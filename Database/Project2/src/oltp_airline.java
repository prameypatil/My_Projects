import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class oltp_airline {


	private static Connection conn;
	private static DBConnection dbconn;
    private static Random randomGenerator;
	
	public static void main(String[] args) {
		
		String inputArg;
		
		int nthreads = 0;
		
		getDBConnection();
		
		DeleteData delData = new DeleteData(dbconn);
		
		inputArg = new String(args[0]);
		
		try{
			if (inputArg.startsWith("nthreads="))
				nthreads = Integer.parseInt(inputArg.substring(9, inputArg.length()));
			else
				nthreads = Integer.parseInt(inputArg);
		}
		catch(Exception e)
		{
			System.out.println("Please enter valid no of threads");
		}
		
		AvailableData avail = new AvailableData();
		 
		avail.getAvailableData(dbconn);
				
		Reservation reserv = new Reservation();
		
		for(int i = 1; i <= 40; i++)
		{
			int t = getRandomJourneyType();
			reserv.newReservation(avail, i, t );
			
			if(i%50 == 0 )
				
			{
				try{

					System.out.println("wait");
				  Thread.sleep(10000);
				}catch(InterruptedException ex){
				  
				}
			}
		}
		
		
		try{
			dbconn.closeConnection();
		}catch(Exception e){}
	}
	
	private static Connection getDBConnection(){
		dbconn = new DBConnection();
		try {
			dbconn.loadDriver();
		}
		catch (ClassNotFoundException e){
			// Could not find the driver class. Likely an issue
    		// with finding the .jar file.
    		System.err.println("Could not find the JDBC driver class." + e.getMessage());
    		//e.printStackTrace();
    		System.exit(2);
		}
		
		try {
			dbconn.initConnection();
			//dbconn.testConnection();
			
		}
		catch (SQLException e){
			// Could not connect to database.
    		System.err.println("Could not connect to database." + e.getMessage());
    		System.exit(3);
		}
		return conn;
	}
	
	public static int getRandomJourneyType()
	{
		 randomGenerator = new Random();
		 int type = randomGenerator.nextInt((3 - 1) + 1) + 1;
		 return type;
	}
}
