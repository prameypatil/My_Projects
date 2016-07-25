
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class CertifyNF {

	private static Vector<Table> tables; 
	private static Connection conn;
	private static DBConnection dbconn;
	private static Vector<NFAnalyzer> analyzers;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// hmm. Going to store a new instance of NFAnalyzer for each table (for now).
		analyzers = new Vector<NFAnalyzer>();
		// get filename argument from command line
		String inputArg;
		String inputFilename;
		
		// Test for input args
		if ((args[0] == null) || (args[0] == "")){
			System.err.println("ERROR: You must specify an input argument "
					+ "of the form 'database=filename.txt'");
			System.exit(1);
		}
		
		// get/test DB connection
		getDBConnection();
		
		// Got connection ok.  Proceed.
		
		// Input arg ok, Strip off prefix if it's there
		inputArg = new String(args[0]);
		if (inputArg.startsWith("database="))
			inputFilename = inputArg.substring(9, inputArg.length());
		else
			inputFilename = inputArg;
		
		//DEBUG
//		System.out.println(inputFilename);
		
		// read the file and create Table list (Vector)
		getInput(inputFilename);
		
		// for each Table, Analyze the table
		boolean verified = false;
		for (Table t : tables){
			//DEBUG
//			System.out.println("Table: " + t.getTableName());
//			System.out.println(t);
			System.out.println("\nVerifying table schema against database: " + t);
			try {
				verified = dbconn.verifyTable(t);
			}
			catch (SQLException e){
				System.out.println("Problem verifying table: " + t.getTableName() + e.getMessage());
			}
			if (verified){ // table exists and schema columns are in table
				t.setVerified(true);
				NFAnalyzer analyzer = new NFAnalyzer(t,dbconn);
				analyzers.add(analyzer);
				//t.setSQLScript(analyzer.getSQLScript()); 
				System.out.println("Testing for 1NF");
				boolean isOneNF = analyzer.isOneNF();

				ArrayList<Table> ts;
				// check for normal forms
				boolean worked = false;
				if(isOneNF)
				{
					t.setNormForm("1NF");
					//TODO
//					System.out.println("Table " + t.getTableName() + " is in 1NF\n");
					
					System.out.println("Testing for 2NF");
					boolean isTwoNF = analyzer.isTwoNF();

				      if (isTwoNF){
				    	  t.setNormForm("2NF");
//				    	  System.out.println("Table " + t.getTableName() + " is in 2NF\n");
				      
				
					//    if 3NF // check 3NF
				    	  
				    	  System.out.println("Testing for 3NF");
					      if (analyzer.isThreeNF()){
					    	  t.setNormForm("3NF");
//					    	  System.out.println("Table " + t.getTableName() + " is in 3NF\n");
					      }
					      else {
//					    	  System.out.println("Table " + t.getTableName() + " is not in 3NF\n");
//					          // TODO
					    	  ts = analyzer.decomposeTable(2);
					    	  //TEST
//					    	  analyzer.decomposeTable(2);
					    	  t.setTwoNFDecomposition(ts);
					    	  t.setDecompverified(analyzer.analyzeDecomposition());
					    	  // test
//					    	  for (Table it : ts)
//					    		  System.out.println(it);
					      } // end if 3NF
					
				      }
				      else //else not 2NF
				      {
//				    	  System.out.println("Table " + t.getTableName() + " is NOT in 2NF\n");
				    	  ts = analyzer.decomposeTable(1);
				    	  t.setOneNFDecomposition(ts);
				    	  t.setDecompverified(analyzer.analyzeDecomposition());

//				    	  for(int i = 0; i < t.getTwoNFDependencies().size(); i++)
//				    	  {
//				    		  System.out.println("dependencies " + t.getTwoNFDependencies().get(i));
//				    	  }
				      } // end if 2NF
					// TODO
					//    decompose 1NF table
				}
				else // not 1NF
				{
//					System.out.println("Table " + t.getTableName() + " is NOT in 1NF");
					//TODO
					//decompose UNF table
				}// end if isOneNF
			
				
				analyzer.setSQL();
				System.out.println("Writing SQL file " + t.getTableName() + "-SQL.txt" +
						" for table analysis of: " + t.getTableName());
				t.outputSQLScript(t);
				
			} // end if verified
			else { //  table is missing or corrupt
				t.setVerified(false);
				System.out.println("Error processing table " + t.getTableName() + ". Table missing or columns don't match.");
			} // end if verified
		} // end for t : tables
	
		System.out.println("Writing report file: NF.txt");
		Reporter reporter = new Reporter(tables, dbconn);
		
		// DEBUG:
//		System.out.println("Table classes");
//		for (Table t : tables){
//			System.out.println(t);
//		}
		
		try {
			dbconn.closeConnection();
		}
		catch (SQLException e){
			System.err.println("Error closing connection. " + e.getMessage());
		}
	} // end main

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
	/**
	 * @param inputFilename
	 */
	private static void getInput(String inputFilename) {
		List<String> lines;
		Path p1 = Paths.get(inputFilename); 
		if (Files.exists(p1)){
			try {
			  lines = Files.readAllLines(p1);
			  if (!lines.isEmpty()){
					parseInput(lines);
			  }
			  else {
				  System.err.println("ERROR: The input file is empty.");
			  }
			}
			catch (IOException e){
				System.err.println(e);
			}
		}
		else {
			System.err.println("ERROR: The input file does not exist.");
		}	
	} // end getInput()
	
	private static void parseInput(List<String> lines){
		
		int i, lastIndex;
		String s1, s2;
		StringTokenizer st;
		// use a Vector for now
		tables = new Vector<Table>(lines.size());
		for (String line : lines){
			// parse the input
			
			//DEBUG, echo input
//			System.out.println(line);
			
			// Get table name
			i = line.indexOf('(');
			s1 = new String(line.substring(0,i));
			s1 = s1.trim();
			
			// Create new table
			Table t = new Table(s1);
			
			// Get table attributes

			lastIndex = line.lastIndexOf(')');
			s2 = line.substring(i+1, lastIndex);
			st = new StringTokenizer(s2,",");
			
			String att;
			while (st.hasMoreTokens()){
				att = st.nextToken();
				int j = att.indexOf("(k)");
				//if att is a key
				if (j != -1)
					t.addKeyAttribute(att.substring(0, j).trim());
				// att is not a key
				else
					t.addNonKeyAttribute(att.trim());	
			} // end while
//			System.out.println("Table: " + t);
			
			tables.addElement(t);
			

		}// end for
//		System.out.println("Table count: " + tables.size());
	} // end parseInput()
} // end Class CertifyNF
