import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class DBConnection{
	
	private Connection conn;
	private Statement stmt;
	    
    public DBConnection(){	
    }
    
    public void loadDriver() throws ClassNotFoundException{
    	try {
    		Class.forName("com.vertica.jdbc.Driver");
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
    	Properties myProp = new Properties();
    	myProp.setProperty("User", "cosc6340");
    	myProp.setProperty("Password", "1pmMon-Wed");
    	myProp.setProperty("LoginTimeout", "15");

    	try {
    		conn = DriverManager.getConnection(
    				"jdbc:vertica://129.7.242.19:5433/cosc6340", myProp);
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
    
    public boolean verifyTable(Table t) throws SQLException{
    	boolean verified = false;
    	String tname = t.getTableName();

    	String sqlCheckTName = "select table_name from v_catalog.tables where table_name = " + "'" +tname +"'";
    	String  sqlCheckColNames = "select column_name from columns where table_name = " + "'" +tname + "'";

    	// TODO:  should really also check whether there are *more* columns than in 
    	// the schema (other than the i index)
    	try {
        	stmt = conn.createStatement();
        	ResultSet rs = executeQuery(sqlCheckTName);
        	String tableName = null;
        	while (rs.next()){
        		tableName = rs.getString(1);
        	}
//        	System.out.println("tname: " + tname);
//        	System.out.println("tableName: " + tableName);
        	if (!tableName.equals(tname)){
        		return verified;
        	} // else check column names

        	stmt = conn.createStatement();
        	rs.close();
        	rs = executeQuery(sqlCheckColNames);
        	ArrayList<String> allFromInputSchema = t.getAllAttributeNames();
        	ArrayList<String> actualFromTable = new ArrayList<String>();
//        	System.out.println("schema");
        	for (String schemas : allFromInputSchema)
//        		System.out.println(schemas);
        	
        	while (rs.next()){
        		actualFromTable.add(rs.getString(1));
        	}
//        	System.out.println("table");
//        	for (String schemas : actualFromTable)
//        		System.out.println(schemas);
        	boolean found;
        	for (String cname : allFromInputSchema){
            	found = false;
        		for (String s : actualFromTable){
        			if (s.equals(cname)){
        				found = true;
        				break;
        			}
        		}
        		if (!found){
        			return false;
        		}
        	}
    	}
    	catch (SQLException e){
    		throw e;
    	}
    	return true;
    }
    
    public void testConnection() throws SQLException {
        // Connection is established, do something with it here or
        // return it to a calling method
        try {
        	stmt = conn.createStatement();

            ResultSet rs = executeQuery("select table_name from v_catalog.tables");

//            while(rs.next()){
////            	System.out.println( rs.getString(1));
//            }
        } catch (SQLException e) {
//            System.err.println("Could not create statement");
//            e.printStackTrace();
        	throw e;
        }
    }

    public ResultSet executeQuery(String sqlStatement) {
    	try {
    		return stmt.executeQuery(sqlStatement);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public boolean execute(String sqlStatement){
    	boolean worked = false;
    	try{
    	 worked = stmt.execute(sqlStatement);
    	}
    	catch (SQLException e){
    		System.err.println("SQL Error: " + e.getMessage());
    	}
    	return worked;
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
