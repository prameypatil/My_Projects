import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;


public class Reservation implements Runnable{

	private DBConnection dbconn;
	private AvailableData avail;
    private Random randomGenerator;
	
    private ArrayList<Integer> flights = new ArrayList<Integer>();
	
	private int flight = 0;
	private int availSeats = 0;
	private int availSeatsB = 0;
	private int availSeatsE = 0;
	
	//private Thread t;
	//private String threadName;
	private int resID;
	private int custID;
	//private String travelDate = "";

	private Connection conn;
	private Statement stmt;
	int seat = 0;
	int type = 1;
	boolean notUpdated = true;
	boolean notUpdatedMBT = true;
	
	public synchronized void newReservation(AvailableData avail2, int resID1, int t) {
		
		resID = resID1;
		
		type = t;
	
		newConnection();
		avail = avail2;		

		getCustID();
		
		notUpdated = true;
		notUpdatedMBT = true;
		run();
		
	}

	
	private void newConnection() {
		
		dbconn = new DBConnection();
		try {
			dbconn.loadDriver();
		}
		catch (ClassNotFoundException e){
			System.err.println("Could not find the JDBC driver class." + e.getMessage());
    		System.exit(2);
		}
		
		try {
			conn = dbconn.initConnection();
		}catch(Exception e)
		{}
		
		
	}

	private void closeConnection() {
	
		try{
			conn.close();
		}catch(Exception e)
		{}
	}


	
	private void getCustID() {
			
	    randomGenerator = new Random();
        int index = randomGenerator.nextInt(avail.customers.size());
        custID = avail.customers.get(index);
	}

	@Override
	public synchronized void run() {
	
		   randomGenerator = new Random();
	        int index = randomGenerator.nextInt(avail.routes.size());
	        int route = avail.routes.get(index);
	        
	        if(type == 1)
	        {
	        	System.out.println("Single flight...");
	        	getFlights(route);
	        	selectFlight();
	        	doReservation();
	        }else if (type == 2)
	        {
	        	System.out.println("Connecting flights...");
	        	int SD = getRandomSD();
	        	for(int i = 0; i < 2; i ++)
	        	{
	        		if(SD == 1)
	        		{
	        			if(i == 0)
	        				flight = 5;
	        			else
	        				flight = 6;
	        		}else{
	        			if(i == 0)
	        				flight = 7;
	        			else
	        				flight = 8;
	        		}
	        		
	        		getTotalAvailableSeats();
	        		if(availSeats == 0)
	        		{
	        			System.out.println("Sorry... there are no flights available for given Source and Destination");
	        		}
	        		else
	        		{
	        			doReservation();
	        		}
	        	}
	        }else if (type == 3)
	        {
	        	System.out.println("Connecting flights...");
	        	int SD3F = getRandomSD();
	        	for(int i = 0; i < 3; i ++)
	        	{
	        		if(SD3F == 1)
	        		{
	        			if(i == 0)
	        			{	flight = 9;}
	        			else if (i == 1)
	        			{	flight = 10;}
	        			else
	        			{	flight = 11;}
	        		}else
	        		{
	        			if(i == 0)
	        			{	flight = 12;}
	        			else if (i == 1)
	        			{	flight = 13;}
	        			else
	        			{	flight = 14;}
	        		}
	        		
	        		getTotalAvailableSeats();
	        		if(availSeats == 0)
	        		{
	        			System.out.println("Sorry... there are no flights available for given Source and Destination");
	        		}
	        		else
	        		{
	        			doReservation();
	        		}
	        	}
	        }
	        
	        closeConnection();
	}
	
	private int getRandomSD()
	{
		randomGenerator = new Random();
		 int SD = randomGenerator.nextInt((2 - 1) + 1) + 1;
		 return SD;
	}
	
	private void getFlights(int route) {
		
		String sql = "select Flight_id from Flight_Schedule where Route_id = " + route + ";";
		
		try{
			ResultSet res = dbconn.executeQuery(sql);
			while (res.next()){
				flights.add(res.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}
	
	
	public void getTotalAvailableSeats()
	{
		String sql = "select Available_Seats from Flight_Schedule where Flight_id = " + flight + ";";
		String sqlB = "select BClass_Seats_Available from Flight_Schedule where Flight_id = " + flight + ";";
		String sqlE = "select EClass_Seats_Available from Flight_Schedule where Flight_id = " + flight + ";";
		
		try{
			ResultSet res = dbconn.executeQuery(sql);
			ResultSet resB = dbconn.executeQuery(sqlB);
			ResultSet resE = dbconn.executeQuery(sqlE);
			
			while (res.next()){
				availSeats = res.getInt(1);
			}
			
			while (resB.next()){
				availSeatsB = resB.getInt(1);
			}
			
			while (resE.next()){
				availSeatsE = resE.getInt(1);
			}
			
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public int getRandomSeatE()
	{
		randomGenerator = new Random();
        
	    int seatIDE = randomGenerator.nextInt((100 - 41) + 1) + 41;
	    
	    return seatIDE;
	}
	
	public int getRandomSeatB()
	{
	    randomGenerator = new Random();
	    int seatIDB = randomGenerator.nextInt((40 - 1) + 1) + 1;
	    return seatIDB;
	}
	
	public void getRandomFlight()
	{
		randomGenerator = new Random();
        int index = randomGenerator.nextInt(flights.size());
        flight = flights.get(index);
	}
	
	public synchronized int selectSeat()
	{
		String availability = "xyz";
		
	    int available = 0;
		int seat = 0;
		int found = 0;
	    
	   	while(available == 0)
	   	{
	    
	   		if(availSeatsE > 0)
	   		{
    			seat = getRandomSeatE();
    		}
	    	else{
	    			seat = getRandomSeatB();
	   		}
	    		
	  		//System.out.println("CustID: " + custID + " ResID: " + resID + "	Booking seat " + seat + " for flight " + flight );
	    
	   		System.out.println();
		    
    		String sql  = "select Availability from Seats_Booking where Flight_id = " 
	    				+ flight + "and Seat_id = " + seat +";";  
		    
    		try{
	    		ResultSet res = dbconn.executeQuery(sql);
	    		
	   			if(!res.isBeforeFirst())
	   			{
	   				available = 1;
	   				found = 0;
	   			}else{
    				while (res.next()){
    					found = 1;
    					availability = res.getString(1);
	    			}
	    		}
	    	}
	    	catch(SQLException e){
	   			System.out.println(e);
	   		}
	    		
    		if(found == 1)
    		{
    			if(availability.equals("YES"))
    			{
    				available = 1;
    			}
    			else if(availability.equals("NO") )
    			{
    				available = 0;
    				System.out.println("Customer: " + custID + " Reservation: " + resID 
    					+ " :- Selected seat no " + seat + " is not available, booking a different seat...");
    			}
    		}
	    		found = 0;
	    		availability = "x";
    	}
	    
	    return seat;
	}
	
	public void selectFlight()
	{
		
	
		boolean flightIsFull = false;

    	do{
    		getRandomFlight();
    		getTotalAvailableSeats();
    		
    		if(availSeats > 0)
    			flightIsFull = false;
    		else
    		{
    			flightIsFull = true;
    			
    	    	System.out.println("Dear Customer: " + custID + ", Reservation: " + resID
    	    			+ " :- No seats available in this flight " + flight + " ... Booking another flight");
    		}
    		
    	}while(flightIsFull);
		
    	
    	
	 }
	
	
	public synchronized void doReservation()
	{
		
		boolean readyToBook = false;

		do{
	        
        	seat = selectSeat();
        	
        	if(seat != 0)
        	{
        		readyToBook = true;
        	}
        }
        while(!readyToBook);
		
		//System.out.println("Booking... for Cust: " + custID + " res: " + resID + " seat: " + seat );

	    try{
    	   
    	   conn.setAutoCommit(false);
    	   stmt = conn.createStatement();
    	   
    	   String SQL = "INSERT INTO Seats_Booking (Flight_id, Seat_id, Availability) " +
    	                "VALUES ( " + flight + ", " + seat + ", 'NO');";
    	   stmt.executeUpdate(SQL);      		

    	   if(notUpdated)
    	   {
    		   String SQL1 = "INSERT INTO Reservations (Reservation_id, ReservationDate, Trip_Type,"
    	   				+ "Trip_Status, Payment_Type, Airfare, Taxes, Total_Amount)  " +
    	                "VALUES (" + resID + ", Now(), 'Single', 'Incomplete', 'Credit', "
    	   				+ " 1000.00, 250.00, 1250.00 );";
    	   
    		   stmt.executeUpdate(SQL1);
    		   notUpdated = false;
    	   }
    	   
    	   String SQL2 = "INSERT INTO Multiple_Flights (Reservation_id, Flight_id, Seat_id, Flightfare) " +
	                "VALUES (" + resID + ", " + flight + ", " + seat + ", " + "1000.00 );";
    	   
    	   stmt.executeUpdate(SQL2);
    	   
    	   if(notUpdatedMBT)
    	   {
    		   String SQL3 = "INSERT INTO Multiple_Bookings (Customer_id, Reservation_id) " +
	                "VALUES (" + custID + ", " + resID + ");";
    	   
    		   stmt.executeUpdate(SQL3);
    		   notUpdatedMBT = false;
    	   }
    	   int newAvailSeats = availSeats - 1;
    	   int newAvailSeatsE = availSeatsE - 1;
    	   int newAvailSeatsB = availSeatsB - 1;
    	   
    	   String SQL4 = "UPDATE Flight_Schedule  " +
	                "set Available_Seats = " + newAvailSeats +" "
	                + "where Flight_id = " + flight + ";";
    	   stmt.executeUpdate(SQL4);
    	   
    	   if(seat > 40)
    	   {
    		   String SQL5 = "UPDATE Flight_Schedule set EClass_Seats_Available = " + newAvailSeatsE 
    		   					+ " where Flight_id = " + flight + ";";
    		   stmt.executeUpdate(SQL5);
    	   }
    	   else
    	   {
    		   String SQL6 = "UPDATE Flight_Schedule set BClass_Seats_Available = " + newAvailSeatsB 
								+ " where Flight_id = " + flight + ";";
    		   stmt.executeUpdate(SQL6);
    	   }
	   
	   
    	   // If there is no error.
    	   conn.commit();
    	   
    	   System.out.println("Successfully booked ticket for custumer id: " + custID 
    			   				+ " Your reservation id is: " + resID + " Flight: " + flight + " Seat No: " + seat);
    	   System.out.println();
    	   
    	}catch(SQLException se){
    	   // If there is any error.
    		se.printStackTrace();
    	   try {
    		   conn.rollback();
    	   } catch (SQLException e) {
    		
    		   e.printStackTrace();
    	   }
    	
    	}

	}
}
