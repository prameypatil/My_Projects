
public class DeleteData {

	public DeleteData(DBConnection db)
	{
		String sql = "TRUNCATE Seats_Booking";
	    db.executeUpdate(sql);
	    
	    String sql1 = "TRUNCATE Multiple_Flights";
	    db.executeUpdate(sql1);
	    
	    String sql2 = "TRUNCATE Multiple_Bookings";
	    db.executeUpdate(sql2);
	    
	    String sql3 = "DELETE from Reservations";
	    db.executeUpdate(sql3);
	    
	    String sql4 = "Update Flight_Schedule set Available_Seats = 100";
	    db.executeUpdate(sql4);
	    
	    String sql5 = "Update Flight_Schedule set BClass_Seats_Available = 40";
	    db.executeUpdate(sql5);
	    
	    String sql6 = "Update Flight_Schedule set EClass_Seats_Available = 60";
	    db.executeUpdate(sql6);
	}
}
