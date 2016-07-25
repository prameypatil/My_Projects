import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AvailableData {

	public ArrayList<Integer> routes = new ArrayList<Integer>();
	public ArrayList<Integer> customers = new ArrayList<Integer>();
	public ArrayList<String> dates = new ArrayList<String>();
	public ArrayList<String> depTimes = new ArrayList<String>();
	
	public int totalReservations = 0;
	
	public void getAvailableData(DBConnection dbconn)
	{
		
		String sql = "select Route_id from Flight_Schedule where Flight_id < 5;";

		try{
			ResultSet res = dbconn.executeQuery(sql);
			while (res.next()){
				routes.add(res.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}

		
		String sqlC = "select Customer_id from Customers;";

		try{
			ResultSet res = dbconn.executeQuery(sqlC);
			while (res.next()){
				customers.add(res.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}
		
		String sql1 = "select Departure_Date from Flight_Schedule;";
		
		try{
			ResultSet res = dbconn.executeQuery(sql1);
			while (res.next()){
				dates.add(res.getString(1));
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}
		
		String sql2 = "select Departure_Time from Flight_Schedule;";
		
		try{
			ResultSet res = dbconn.executeQuery(sql2);
			while (res.next()){
				depTimes.add(res.getString(1));
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}
		
		/*String sql3 = "select * from Reservations;";
		
		try{
			ResultSet res = dbconn.executeQuery(sql3);
			
			res.afterLast();
			while (res.previous()) {
				
				totalReservations = res.getInt(1);
				System.out.println("last res id: " + totalReservations);
			//while (res.next()){
				
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}*/
		
	}
}
