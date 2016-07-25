
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class Reporter {

//	private ArrayList<String> reason = null;
 
	private DBConnection dbconn;
	
	private Vector<Table> tables;
	
	PrintWriter writer;
	
	public Reporter(Vector<Table> tables, DBConnection dbconn) {
		
		this.tables = tables;
		this.dbconn = dbconn;
		//createTable();
		
		// Write summary table
//		for (Table tbl : tables)
//			System.out.println(tbl.getTableName());
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

//						System.out.println(t.getTableName() + " is in 2NF");

//						reason = t.getTwoNFDependencies();
						
//						System.out.println("reason: " + reason);

						Hashtable<String,FunctionalDependency> twoNFfds = t.getIntraNkaDeps();
//						String s = parseDependencies(reason);
						String s = getFDString(twoNFfds);

						writeToFile(nm +"	 " + " 3NF "  +"      " + " N "  +"      " + " 2NF but " + s );
						//insertIntoTable(i, t.getTableName(), "3NF", "N", "is in 2NF but " + s);
						//					System.out.println("reason: " + s);
					}
					else if (t.getNormForm().equals("1NF"))
					{
//						System.out.println(t.getTableName() + " is in 1NF");

//						reason = t.getOneNFDependencies(); 
//						System.out.println("writeSummary: reason: " + reason);
						
						Hashtable<String,FunctionalDependency> oneNFfds = t.getKsubToNkaDeps();
						String s = getFDString(oneNFfds);

//						String s = parseDependencies(reason);

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
//					System.out.println("table: " + t.getTableName());
					ArrayList<Table> dc;
//					System.out.println("is1NF: " + t.isOneNF());
//					System.out.println("is2NF: " + t.isTwoNF());
//					System.out.println("is3NF: " + t.isThreeNF());


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

						// TODO One NF verification
						// for each table in dc
						//   create tables in the database using projection
						// join the tables
						// count rows in joined table
						// if t # rows = joined table # rows, they match
					}
					// Test dc tables.   Create in DB. Join new tables. See if # rows in joined = # rows in original
					// Print result. Example:  R = join(R1,R2) ? YES
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
	
//	private String parseDependencies(ArrayList<String> reason2) {
//		System.out.println("reason: " + reason2);
//		
//		String s = "";
//		char left1, left2;
//
//		for (String r : reason2){
//			System.out.println("r: " + r);
//			String k = "";
//			if (r.length() == 3){
//				System.out.println("r = 3: " + r);
//				System.out.println("charAt(0: "+r.charAt(0));
//				System.out.println("charAt(1: "+r.charAt(1));
//				System.out.println("charAt(2: "+r.charAt(2));
//				
//				left1 = r.charAt(0);
//				left2 = r.charAt(1);
//
//				k = k +  left1 + left2 + " -> " + r.charAt(2); 
//			}
//			else if(r.length() == 2)
//			{
//				k = k + r.charAt(0) + " -> " + r.charAt(1);
//			}
//			if(!s.contains(k)) {
//				s += ", " + k;
//			}		
//		}
//		System.out.println("s: " + s);
//		s = s.substring(1);
//		
//		return s;
//	}

}
