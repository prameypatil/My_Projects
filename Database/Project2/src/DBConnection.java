import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection{
	
	private Connection conn;
	private Statement stmt;
	    
    public DBConnection(){	
    }
    
    public void loadDriver() throws ClassNotFoundException{
    	try {
    		Class.forName("org.postgresql.Driver");
    	} catch (ClassNotFoundException e) {
    		// Could not find the driver class. Likely an issue
    		// with finding the .jar file.
    		//System.err.println("Could not find the JDBC driver class.");
    		//e.printStackTrace();
    		throw e;
    	}

    }

    public Connection initConnection() throws SQLException{
    	// Create property object to hold username & password
    	
//    	user:pramey
//    	database: pramey
//    	password: 2016p-Y
//    	   "jdbc:postgresql://hostname:port/dbname","username", "password");


    	try {
    		conn = DriverManager.getConnection(
    				"jdbc:postgresql://129.7.243.243:5432/pramey","pramey", "2016p-Y");
    		
    		//conn.setAutoCommit(false);

    	} catch (SQLException e) {
    		// Could not connect to database.
    		//System.err.println("Could not connect to database.");
    		//e.printStackTrace();
    		throw e;
    	}
    	return conn;
    }
    
    public Connection getConnection(){
    	return conn;
    }
    
       
    public void testConnection() throws SQLException {
        // Connection is established, do something with it here or
        // return it to a calling method
        try {
        	stmt = conn.createStatement();

            ResultSet rs = executeQuery("select Route_id from Flight_Schedule;");

            while(rs.next()){
            	System.out.println( rs.getString(1));
            }
        } catch (SQLException e) {
//            System.err.println("Could not create statement");
//            e.printStackTrace();
        	throw e;
        }
    }

    public ResultSet executeQuery(String sqlStatement) {

    	try {
        	stmt = conn.createStatement();
        	

    		return stmt.executeQuery(sqlStatement);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public void executeUpdate(String sql)
    {
    	try {
        	stmt = conn.createStatement();

    		stmt.executeUpdate(sql);
    	} catch (SQLException e) {
    		e.printStackTrace();	
    	}	
    }
    
    public void closeConnection() throws SQLException{
    	try {
    		conn.close();
    	}
    	catch (SQLException e){
    		throw e;
    	}
    }
}
