
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class Reporter {
 
	private DBConnection dbconn;
	
	private Vector<Table> tables;
	
	PrintWriter writer;
	
	public Reporter(Vector<Table> tables, DBConnection dbconn) {
		
		this.tables = tables;
		this.dbconn = dbconn;
		
		writeSummary();
		
		writer.close();
		
	}

	private void writeToFile(String string) {
		
		writer.println(string);
	}

	private void writeSummary() {

//		System.out.println("IN writeSummary");
		try{
			writer = new PrintWriter("NF.txt", "UTF-8");
			
			for (Table t: tables)
				writer.println(t);
			writer.println("\n");

			writer.println("Table	     Form	    Y_N	            Reason");

			// for each table write results
			for(Table t: tables)
			{
				String nm = t.getTableName();
				int l = nm.length();
				for (int p=l; p<14; p++)
					nm += " ";
				
				if (t.isVerified()){
//					System.out.println("IN for t : tables");
					// pad the name
					
					if(t.getNormForm().equals("3NF"))
					{
//						System.out.println(t.getTableName() + " is in 3NF");

						writeToFile(nm +"   " + " 3NF "  +"      " + " Y "  +" " + " "  );
						//insertIntoTable(i, t.getTableName(), "3NF", "Y", "");
					}
					else if(t.getNormForm().equals("2NF"))
					{  

						Hashtable<String,FunctionalDependency> twoNFfds = t.getIntraNkaDeps();

						String s = getFDString(twoNFfds);

						writeToFile(nm +"	 " + " 3NF "  +"      " + " N "  +"      " + " 2NF but " + s );

					}
					else if (t.getNormForm().equals("1NF"))
					{
						
						Hashtable<String,FunctionalDependency> oneNFfds = t.getKsubToNkaDeps();
						String s = getFDString(oneNFfds);

						writeToFile(nm +"   " + " 3NF "  +"      " + " N "  +"      " + " 1NF but " + s );
						//insertIntoTable(i, t.getTableName(), "3NF", "N", "is in 1NF but " + s);
						//					System.out.println("reason: " + s);
					}// end else
					else if (!t.isOneNF()){
						String s = t.getNotOneNF_Cause();
//						System.out.println(t.getTableName() + " is in UNF");
						writeToFile(nm +"   " + " 3NF "  +"      " + " N "  +"      " + " UNF, " + s );
					}
				} // end if is verified
				else {
					writeToFile(nm +"   " + " 3NF "  +"      " + " N "  +"      " + " Table in database does not match schema ");

				}
			} // end for 

			writeToFile("\nDecomposition into normalized tables:");

			for (Table t : tables){
//				System.out.println("table: " + t.getTableName());

				if (t.isVerified()){

					ArrayList<Table> dc;

					if ((t.isTwoNF()) && !(t.isThreeNF())){

						writeToFile("\n" + t.getTableName() + " Decomposition:");
						dc = t.getTwoNFDecomposition();
						String join = "=join(";
						for (Table deco : dc){
							writeToFile("  " + deco.toString());
							join += deco.getTableName() + ",";
						}
						join = join.substring(0, join.length()-1) + ")? ";
						join += t.isDecompverified();
						writeToFile("Verification:");
						writeToFile("  " + t.getTableName() + join);
					}
					else if ((t.isOneNF()) && (!t.isTwoNF())){
						writeToFile("\n" + t.getTableName() + " Decomposition:");
						dc = t.getOneNFDecomposition();
						String join = "=join(";
						for (Table deco : dc){
							writeToFile("  " + deco.toString());
							join += deco.getTableName() + ",";
						}
						join = join.substring(0, join.length()-1) + ")? ";
						join += t.isDecompverified();

						writeToFile("Verification:");
						writeToFile("  " + t.getTableName() + join);


					}
				} // end if is verified
			} // end for tables t
		} // end try
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getFDString(Hashtable<String,FunctionalDependency> fds){
		String all = "";
		for (FunctionalDependency fd : fds.values()){
			String s = "";
			ArrayList<String> left = fd.getLeft();
			ArrayList<String> right = fd.getRight();
			for (String l : left){
				s +=  l;
			}
			s+= "->";
			
			for (String r : right){
				s += r;
			}
			s += ", ";
			all += s;
		}
		
		return all.substring(0,all.length()-2);
	}
	

}
