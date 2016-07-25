import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * 
 */

public class NFAnalyzer {
	
	private Table originalTable;
	private DBConnection dbconn;
	
	private String select = "select * ";
//	private String from = "from cosc6340.";
	private String where = " where ";
	private int numKeyAtts;
	private int numNonKeyAtts;
	private ArrayList<String> keyAttNames;
	private ArrayList<String> nonKeyAttNames;
	private String tableName;
	boolean oneNF = false;
	boolean nullKeyAtts = true;
	int distinctKeys;
	public ArrayList<String> sqlScript;
	
	int noOfRows =0;
	private List<ArrayList<Attribute>> keySubsets = new ArrayList<ArrayList<Attribute>>();
	

	public NFAnalyzer(Table table, DBConnection dbconn){
		originalTable = table;
		this.dbconn = dbconn;
		keyAttNames = table.getKeyNames();
		nonKeyAttNames = table.getNonKeyNames();
		numKeyAtts = keyAttNames.size();
		numNonKeyAtts = nonKeyAttNames.size();
		tableName = table.getTableName();
		sqlScript = new ArrayList<String>();
	}
//	
	
	public boolean analyzeDecomposition(){
		boolean worked = false;
		int size1 = 0,size2 = 0;
		String startTrans = "start transaction isolation level read committed read write;";
		String commit = "commit;";
		String end = "end;";
		ArrayList<String> dropnames = new ArrayList<String>();

		ArrayList<Table> tables;
		if (originalTable.getNormForm().equals("2NF"))
			tables = originalTable.getTwoNFDecomposition();
		else if (originalTable.getNormForm().equals("1NF"))
			tables = originalTable.getOneNFDecomposition();
		else
			return false;
		
		int numtables = tables.size();
		ArrayList<String> tnames = new ArrayList<String>();
		// create tables in db

		for (Table d : tables){
			String name = d.getTableName();
			tnames.add(name);
			ArrayList<String> attNames = d.getAllAttributeNames();
			String list = attNames.get(0);
			for (int i = 1; i < attNames.size(); i++)
				list += ", " + attNames.get(i);

			String create = "create table " + name + " as \n" +
					"select " + list + " from " + originalTable.getTableName();

			try {
				boolean works;
//				ResultSet trans = dbconn.execute(startTrans);
//				formatSQL(startTrans);
				formatSQL(create);
				sqlScript.add(create);
				works = dbconn.execute(create);

				// if created ok, commit
				if (works){  // is this true on successful create?
//					System.out.println("Table " + name + " created");
					try{
					works  = dbconn.execute(commit);
					}
					catch(Exception e){
						
					}
//					if (works)
//						System.out.println("Table " + name + " committed");
				}
			}
			catch (Exception e){
				System.err.println("SQL Error: " + e.getMessage());
			}
		} // end for tables

		// join tables in tnames:
		dropnames.addAll(tnames);
		String joinname = "";
		String previousjoin = "";
		int counter = 0;
		boolean done = false;
		while (!done){
			String t1name;
			String t2name;
			ArrayList<String> joinstrings = new ArrayList<String>();
		
			joinname = tables.get(counter).getTableName() + "join";

			ArrayList<String> t1names;
			ArrayList<String>tcombonames = new ArrayList<String>();
			if (counter >= 1){
				t1name = previousjoin;
				t1names = tables.get(counter).getAllAttributeNames();
			}
			else{
				t1names = tables.get(counter).getAllAttributeNames();
				t1name = tables.get(counter).getTableName();
			}
			t2name = tables.get(counter+1).getTableName();
			ArrayList<String> tcommon = new ArrayList<String>();
			ArrayList<String> t2names = tables.get(counter+1).getAllAttributeNames();
			for (int k = 0; k < t1names.size(); k++)
				for (int z = 0; z < t2names.size(); z++)
					if (t2names.get(z).equals(t1names.get(k))){
							tcommon.add(t2names.get(z));
					}
			

			tcombonames.addAll(t1names);
			tcombonames.addAll(t2names);
			String joinselectlist = "";
			for (String s1 : t1names)
				joinselectlist += s1 + ",";
			for (String s2 : t2names){
				if (!tcommon.contains(s2))
					joinselectlist += s2 + ",";
			}
			joinselectlist = joinselectlist.substring(0,joinselectlist.length()-1);
//			System.out.println("joinselectlist: " + joinselectlist);
			
			String joinatts = "";
			for (String c : tcommon)
				joinatts += t1name + "." + c + "=" + t2name + "." + c + " and ";
			joinatts = joinatts.substring(0,joinatts.length()-5);
//			System.out.println("joinatts: " + joinatts);
				
			
			String jointable = "create table " + joinname + " as \n" +
					"select " + joinselectlist + " from " + t1name + " inner join " + t2name +
					"\n on (" + joinatts + ");";
//			System.out.println("jointable: " + jointable);			
			
			String size1s, size2s;
			size1s = "select count(*) from " + originalTable.getTableName() + ";";
			size2s = "select count(*) from " + jointable + ";";
			formatSQL(size1s);
			sqlScript.add(size1s);

			formatSQL(size2s);				
			sqlScript.add(size2s);


			counter += 1;
			if (tables.size() <= counter+1){
				done = true;
			}
			else {
				dropnames.add(joinname);
				previousjoin = joinname;
			}
			
			// if done
			if (done){
				//check originalTable count rows against new table count rows.
				try {
					ResultSet rs1 = dbconn.executeQuery(size1s);
					while (rs1.next())
						size1 = rs1.getInt(1);
					ResultSet rs2 = dbconn.executeQuery(size1s);
					while (rs2.next())
						size2 = rs2.getInt(1);	
				}
				catch (SQLException e){
					System.err.println("SQL Error: " + e.getMessage());
				}
//				System.out.println("size1: "+size1);
//				System.out.println("size2: " +size2);

				if (size1 == size2){
					worked = true;
				
				}
			}
		} // end while not done
		

		try {
//			ResultSet trans = dbconn.execute(startTrans);
//			formatSQL(startTrans);
			
			for (String nm : tnames){
//				System.out.println("drop:");
				dbconn.execute("drop table " + nm + ";");
			}
	
		}
			catch (Exception e){
				;
			}
		if (worked){
			originalTable.setDecompverified(true);
			return true;
		}
		else{
			originalTable.setDecompverified(false);
			return false;
		}
}
//	
	// EXPERIMENT *********************************************************
	
	public ArrayList<Table> decomposeTable(int nflevel){
		
	
		Collection<FunctionalDependency> fds = null;
		if (nflevel == 1)
			fds = originalTable.getKsubToNkaDeps().values();
		else if (nflevel == 2)
			fds = originalTable.getIntraNkaDeps().values();

		ArrayList<FunctionalDependency> fdarray = new ArrayList<FunctionalDependency>();
		
		for (FunctionalDependency fa : fds){
			fdarray.add(fa);
		}
		
//		System.out.println("FDs for this table: ");
//		for (FunctionalDependency fb : fds)
//			System.out.println("fd: " + fb);
		
		int numfds = fds.size();
		
//		System.out.println("array size: " + fdarray.size());
		
		int maxi = 0;
		int maxval = 0;
		int sizel = 0;
		for (int c = 0; c < fdarray.size(); c++){
			int sizerc = fdarray.get(c).getRight().size();
			if (sizerc > maxval){
				maxval = sizerc;
				maxi = c;
				sizel = fdarray.get(maxi).getLeft().size();
			}
			else if (sizerc == maxval){
				if (fdarray.get(c).getLeft().size() < sizel){
					maxi = c;
					sizel = fdarray.get(maxi).getLeft().size();
					
				}
			}
		}
		
		// EXPERIMENT1 ================
		ArrayList<FunctionalDependency> two = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> three = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> four = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> five = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> six = new ArrayList<FunctionalDependency>();

		// STEP 1 ------------------------------------
		// separate fds by set size
		for (FunctionalDependency afd : fds){
			int setsize = afd.getSet().size();
			switch (setsize) {
				case 2: two.add(afd);
						break;
				case 3: three.add(afd);
						break;
				case 4: four.add(afd);
						break;
				case 5: five.add(afd);
						break;
				case 6: six.add(afd);
						break;
				default:break;
			}
		}
		
		// END STEP 1 -----------------------------------------------
		
		// STEP 2-------------------------------------------------
		
		// left min, right max # atts
		
//		int twomax = 0;
//		int twomin = 0;		
		ArrayList<FunctionalDependency> twoMinMax = new ArrayList<FunctionalDependency>();

		for (FunctionalDependency fd : two)
//			if (fd.getLeft().size() == twomin && fd.getRight().size() == twomax)
				twoMinMax.add(fd);
		
//		System.out.println("twoMinMax: " + twoMinMax);
		
		int threemax = 0;
		int threemin = 0;		
		ArrayList<FunctionalDependency> threeMinMax = new ArrayList<FunctionalDependency>();
		for (int c = 0; c < three.size(); c++){
			int sizerc = three.get(c).getRight().size();
			if (sizerc > threemax){
				threemax = sizerc; 
				threemin = three.get(c).getLeft().size();
			}
			else if (sizerc == threemax){
				if (three.get(c).getLeft().size() < threemin){
					threemin = three.get(c).getLeft().size();
				}
			}
		}
		for (FunctionalDependency fd : three)
			if (fd.getLeft().size() == threemin && fd.getRight().size() == threemax)
				threeMinMax.add(fd);
		
		int fourmax = 0;
		int fourmin = 0;		
		ArrayList<FunctionalDependency> fourMinMax = new ArrayList<FunctionalDependency>();
		for (int c = 0; c < four.size(); c++){
			int sizerc = four.get(c).getRight().size();
			if (sizerc > fourmax){
				fourmax = sizerc; 
				fourmin = four.get(c).getLeft().size();
			}
			else if (sizerc == fourmax){
				if (four.get(c).getLeft().size() < fourmin){
					fourmin = four.get(c).getLeft().size();
				}
			}
		}
		for (FunctionalDependency fd : four)
			if (fd.getLeft().size() == fourmin && fd.getRight().size() == fourmax)
				fourMinMax.add(fd);
		
		int fivemax = 0;
		int fivemin = 0;		
		ArrayList<FunctionalDependency> fiveMinMax = new ArrayList<FunctionalDependency>();
		for (int c = 0; c < five.size(); c++){
			int sizerc = five.get(c).getRight().size();
			if (sizerc > fivemax){
				fivemax = sizerc; 
				fivemin = five.get(c).getLeft().size();
			}
			else if (sizerc == fivemax){
				if (five.get(c).getLeft().size() < fivemin){
					fivemin = five.get(c).getLeft().size();
				}
			}
		}
		for (FunctionalDependency fd : five)
			if (fd.getLeft().size() == fivemin && fd.getRight().size() == fivemax)
				fiveMinMax.add(fd);
		
		int sixmax = 0;
		int sixmin = 0;		
		ArrayList<FunctionalDependency> sixMinMax = new ArrayList<FunctionalDependency>();
		for (int c = 0; c < sixMinMax.size(); c++){
			int sizerc = sixMinMax.get(c).getRight().size();
			if (sizerc > sixmax){
				sixmax = sizerc; 
				sixmin = sixMinMax.get(c).getLeft().size();
			}
			else if (sizerc == sixmax){
				if (sixMinMax.get(c).getLeft().size() < sixmin){
					sixmin = sixMinMax.get(c).getLeft().size();
				}
			}
		}
		for (FunctionalDependency fd : six)
			if (fd.getLeft().size() == sixmin && fd.getRight().size() == sixmax)
				sixMinMax.add(fd);
	
		// END STEP 2 ----------------------------------------------
		
		// STEP 3 -------------------------------------------------
		
		// find/mark/remove duplicates but one,  where set S1 == S2. within level
		
		ArrayList<FunctionalDependency> twoNoMatches = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> threeNoMatches = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> fourNoMatches = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> fiveNoMatches = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> sixNoMatches = new ArrayList<FunctionalDependency>();
		
//		System.out.println("step 3 twoMinMax: " + twoMinMax);
		if (!twoMinMax.isEmpty()){
			if (twoMinMax.size() > 1){
				for (int i = 0; i < twoMinMax.size(); i++){
					if (!twoMinMax.get(i).isSixOfOne())
						twoNoMatches.add(twoMinMax.get(i));
					for (int j = i + 1; j < twoMinMax.size(); j++){
						if (!twoMinMax.get(j).isSixOfOne()){
							if (twoMinMax.get(i).matches(twoMinMax.get(j))){
								twoMinMax.get(j).setSixOfOne(true);
							}
						}
					}
				}
			}
			else {
				twoNoMatches.add(twoMinMax.get(0));
			}
		}
		
		if (!threeMinMax.isEmpty()){
			if (threeMinMax.size() > 1){
				for (int i = 0; i < threeMinMax.size(); i++){
					if (!threeMinMax.get(i).isSixOfOne())
						threeNoMatches.add(threeMinMax.get(i));
					for (int j = i + 1; j < threeMinMax.size(); j++){
						if (!threeMinMax.get(j).isSixOfOne()){
//							System.out.println("threeMinMax(" + i + "): " + threeMinMax.get(i));
//							System.out.println("threeMinMax(" + j + "): " + threeMinMax.get(j));

							if (threeMinMax.get(i).matches(threeMinMax.get(j))){
								threeMinMax.get(j).setSixOfOne(true);
//								System.out.println("threeMinMax(" + i + "): " + threeMinMax.get(i));
//								System.out.println("threeMinMax(" + j + "): "+ threeMinMax.get(j));
							}
						}
					}
				}
			}
			else {
				threeNoMatches.add(threeMinMax.get(0));
			}
		}
		
		if (!fourMinMax.isEmpty()){
			if (fourMinMax.size() > 1){
				for (int i = 0; i < fourMinMax.size(); i++){
					if (!fourMinMax.get(i).isSixOfOne())
						fourNoMatches.add(fourMinMax.get(i));
					for (int j = i + 1; j < fourMinMax.size(); j++){
						if (!fourMinMax.get(j).isSixOfOne()){
//							System.out.println("fourMinMax(" + i + "): " + fourMinMax.get(i));
//							System.out.println("fourMinMax(" + j + "): " + fourMinMax.get(j));

							if (fourMinMax.get(i).matches(fourMinMax.get(j))){
								fourMinMax.get(j).setSixOfOne(true);
//								System.out.println("fourMinMax(" + i + "): " + fourMinMax.get(i));
//								System.out.println("fourMinMax(" + j + "): " + fourMinMax.get(j));

							}
						}
					}
				}
			}
			else {
				fourNoMatches.add(fourMinMax.get(0));
			}
		}
		
		if (!fiveMinMax.isEmpty()){
			if (fiveMinMax.size() > 1){
				for (int i = 0; i < fiveMinMax.size(); i++){
					if (!fiveMinMax.get(i).isSixOfOne())
						fiveNoMatches.add(fiveMinMax.get(i));
					for (int j = i + 1; j < fiveMinMax.size(); j++){
						if (!fiveMinMax.get(j).isSixOfOne()){
							if (fiveMinMax.get(i).matches(fiveMinMax.get(j))){
								fiveMinMax.get(j).setSixOfOne(true);
							}
						}
					}
				}
			}
			else {
				fiveNoMatches.add(fiveMinMax.get(0));
			}
		}
		
		if (!sixMinMax.isEmpty()){
			if (sixMinMax.size() > 1){
				for (int i = 0; i < sixMinMax.size(); i++){
					if (!sixMinMax.get(i).isSixOfOne())
						sixNoMatches.add(sixMinMax.get(i));
					for (int j = i + 1; j < sixMinMax.size(); j++){
						if (!sixMinMax.get(j).isSixOfOne()){
							if (sixMinMax.get(i).matches(sixMinMax.get(j))){
								sixMinMax.get(j).setSixOfOne(true);
							}
						}
					}
				}
			}
			else {
				sixNoMatches.add(sixMinMax.get(0));
			}
		}

		// END STEP 3 ---------------------------------------------------
		
		// STEP 4 -------------------------------------------------------
		
		ArrayList<ArrayList<FunctionalDependency>> remainder = new ArrayList<ArrayList<FunctionalDependency>>();
		
		if (!twoNoMatches.isEmpty()) remainder.add(twoNoMatches);
		if (!threeNoMatches.isEmpty()) remainder.add(threeNoMatches);
		if (!fourNoMatches.isEmpty()) remainder.add(fourNoMatches);
		if (!fiveNoMatches.isEmpty()) remainder.add(fiveNoMatches);
		if (!sixNoMatches.isEmpty()) remainder.add(sixNoMatches);
		
//		System.out.println("remainder.size(): " + remainder.size());
		for (ArrayList<FunctionalDependency> af : remainder){
//			System.out.println("LIST");
//			System.out.println("how many lists " + remainder.size());
//			System.out.println("size of list: " + af.size());
			for (FunctionalDependency fd : af){
//				System.out.println("FD: " + fd);
			}
		}
		
		// STEP 5---------------------------
		
		//?????
		
		// END STEP 5------------------------------
		
		// STEP 6------------------------------
		// set up tables
		ArrayList<Table> ts = new ArrayList<Table>();
		Table t0 = new Table(originalTable);
		t0.setName(originalTable.getTableName() + "1");
		ts.add(t0);
		//------------------------------
		
		for (int shit=0; shit < remainder.size(); shit++)
//			System.out.println("shit: " + remainder.get(shit));
		
//		System.out.println("entire f@# remainder: "+remainder);

		for (int i = 0; i < remainder.size(); i++){
			ArrayList<FunctionalDependency> l = remainder.get(i);
			for (int j = i+1; j < remainder.size(); j++){

				ArrayList<FunctionalDependency> h = remainder.get(j);

				for (int k = 0; k < l.size(); k++){
					FunctionalDependency sub = l.get(k);
					for (int m = 0; m < h.size(); m++){
						FunctionalDependency sup = h.get(m);
						if (!sup.isSuperset()){
//							System.out.println("sup is super? " + sup.isSuperset());
							if (sub.isContainedIn(sup)){

								ArrayList<String> leftover = sup.minus(sub);
								for (String v : leftover){
									ts.get(0).addNonKeyAttribute(v);
									sup.setSuperset(true);
								}
							}
						}
					}
				}
			}
		}
		
		ArrayList<FunctionalDependency> finalset = new ArrayList<FunctionalDependency>();
		for (ArrayList<FunctionalDependency> list : remainder)
			for (FunctionalDependency remnant : list)
				if (!remnant.isSuperset()){
					finalset.add(remnant);
//					System.out.println("remnant: " + remnant);

				}
				
		int tnum = 2;
//		System.out.println("t0): "+ts.get(0));
		for (int yo = 0; yo < finalset.size(); yo++){
			String name = originalTable.getTableName() + tnum;
			Table t = new Table(name);
		
//			for (FunctionalDependency yay : finalset){
				FunctionalDependency yay = finalset.get(yo);
				for (String woo : yay.getLeft()){
					t.addKeyAttribute(woo);
//					System.out.println("t1: "+t + "woo "+woo);
					if (!keyAttNames.contains(woo)){
						ts.get(0).removeAttribute(woo);
//					System.out.println("t0): "+ts.get(0));
					}

				}
				for (String wah : yay.getRight()){
					if (!t.getAllAttributeNames().contains(wah)){
						t.addNonKeyAttribute(wah);
//						System.out.println("t2: "+t + "wah "+wah);

//						System.out.println("t0): "+ts.get(0));

						ts.get(0).removeAttribute(wah);
//						System.out.println("t0): "+ts.get(0));

					}
				}
				for (String yoohoo : keyAttNames){
					if (!t.getAllAttributeNames().contains(yoohoo))
						t.addFKeyAttribute(yoohoo);
				}
//				System.out.println("t3: "+t);

				ts.add(t);
				tnum++;
//			} // end for yay
		}
		
//		System.out.println("DECOMP");
		for (Table ble : ts){
//			System.out.println(ble);
		}
		
		
		// END STEP 6 -------------------------------

		return ts;

	}
	
	// END EXPERIMENT******************************************************
	
	
	private int countNotSixOfOne(ArrayList<FunctionalDependency> afd){
		int count = 0;
		for (FunctionalDependency fd : afd)
			if (fd.isSixOfOne())
				count++;
		return count;
	}
	
	
	public boolean isThreeNF(){
		if (originalTable.isTwoNF()){

			boolean oneToOneFDs = false, twoToOneFDs = false;
			if (numNonKeyAtts > 1){
				// check for single dependencies of the form A -> B
				oneToOneFDs = getOneToOneFDs(originalTable.getNKAttributeHash());
				if (numNonKeyAtts > 2){
					// check for pair dependencies of the form AB -> C
					twoToOneFDs = getTwoToOneFDs(originalTable.getNonKeyAttributes());
				}
				// if no 1:1 or 2:1 FDs, Table is 3NF
				if (!oneToOneFDs && !twoToOneFDs){
					originalTable.setThreeNF(true);
					return true;
				}
			}
			else if ((numNonKeyAtts == 1) || (numNonKeyAtts == 0)){ // only 1 or 0 NKA. Table is 3NF.
				// Is 3NF if 0 nonkey atts?
				originalTable.setThreeNF(true);
				return true;
			}
		}
		else { // not 2NF
			System.err.println("Tried to test for 3NF on table that is not 2NF");
			return false;
		}
		return false;
	}
	
	// utility method
	public ArrayList<ArrayList<String>> getTwoToOneTestSets(ArrayList<Attribute> alist){
		//	System.out.println("numNonKeyAtts: " + numNonKeyAtts);
		ArrayList<ArrayList<String>> twoToOneTestSets = new ArrayList<ArrayList<String>>();
		ArrayList<String> attNames = new ArrayList<String>();
		int numAtts = alist.size();
		
		for (Attribute a : alist)
			attNames.add(a.getName());

		// get two-> one sets
		if (numAtts > 2){
			for (int i=0; i < numAtts; i++){
				for (int j = i+1; j < numAtts; j++){
					// elements 0, 1 and 2 pf a hold the left pair and right attribute, respectively

					for (int k = 0; k < numAtts; k++){
						if ((k != i) && (k != j)){
							ArrayList<String> twoToOneTest = new ArrayList<String>();
							twoToOneTest.add(attNames.get(i));
							twoToOneTest.add(attNames.get(j));
							twoToOneTest.add(attNames.get(k));
							twoToOneTestSets.add(twoToOneTest);
						}
					} // end for k
				} // end for j
			} // end for i
			//DEBUG
//			for (ArrayList<String> s : twoToOneTestSets)
//				System.out.println(s);
		} // end if
		// will return empty set list if numAtts <= 2
		return twoToOneTestSets;
	}
	
	
	private boolean getTwoToOneFDs(ArrayList<Attribute> attList){
		// Two on the left only
		//		System.out.println("IN getTwoToOneFDs");
		ArrayList<ArrayList<String>> twoToOneTestSets;
		ArrayList<ArrayList<String>> twoToOneFDs;
		
		twoToOneTestSets = getTwoToOneTestSets(attList);
		if (twoToOneTestSets.size() > 0){
			twoToOneFDs = new ArrayList<ArrayList<String>>();

			try {
				for (ArrayList<String> test : twoToOneTestSets){			
					String left1 = test.get(0);
					String left2 = test.get(1);
					String right = test.get(2);
//					System.out.println("right: " + right);

					String sql1 = "select count(*) from " + tableName +
							"\n as a1 inner join " + tableName + " as a2 \non a1." +
							left1 + "= a2." + left1 + " and a1." + left2 + "= a2." + left2 +
							" and a1." + right + " != a2." + right +";";

//					System.out.println("sql1: " + sql1);

					formatSQL(sql1);
					sqlScript.add(sql1);

					
					//sqlScript.add(sql1);
					ResultSet rscount = dbconn.executeQuery(sql1);

					int count = -1;
					while (rscount.next()){
						count = rscount.getInt(1);
					}

					if (count == 0){           // FD found, test is an FD					
						twoToOneFDs.add(test);

						// store it
						String s = "";
						FunctionalDependency fd = new FunctionalDependency();
						fd.addLeft(left1);
						fd.addLeft(left2);
						fd.addRight(right);

						s = s + left1;
						s += left2;
//						s = s + right;
						originalTable.addIntraNkaDep(s, fd);
//						System.out.println("21fds:  " + s + " " +right);

//						originalTable.addTwoNFDependencies(s);
						s = "";
					}
					// set twoToOne FDs in Table
				} // end for
				if (twoToOneFDs.size() > 0){
					originalTable.setNkaTwoToOneFDs(twoToOneFDs);
					return true;
				}
			}
			catch (SQLException e){
				System.err.println("SQL ERROR: " + e.getMessage());
			}
		}
		return false;
	}
	
	private boolean getOneToOneFDs(Hashtable<String,Attribute> attTable){  // of the form A->B
		boolean FDsInTable = false;
		
//		sqlScript.add("\n1-1 FD Analysis\n");
		Set<String> attNames = attTable.keySet();
		
		try{
			for (String left : attNames){ // non-key attribute name
//				System.out.println("left: " + left);
				for (String right : attNames){ // for all other nonkey attribute names
//					System.out.println("left, right: " + left + "," + right);
					if (!right.equals(left)){ 		// don't compare against yourself
				
						String sql1 = "select count(*) from " + tableName +
								"\n as a1 inner join " + tableName + " as a2 \non a1." +
								left + "= a2." + left + " and a1." + right + 
								" != a2." + right +";";
						
						formatSQL(sql1);
						sqlScript.add(sql1);
//						System.out.println("sql1: " + sql1);
						ResultSet size = dbconn.executeQuery(sql1);

						int count = -1;
						while (size.next()){
							count = size.getInt(1);
						}
						
						if (count == 0) {			// there is a FD from left to right
							FDsInTable = true;
							Attribute aleft = attTable.get(left);
							Attribute aright = attTable.get(right);
							aleft.addDetermines(right);
							aright.addDeterminedBy(left);
//							System.out.println("FD: " + left + "->" +right);
							
							// 

							
							String s = "";
							FunctionalDependency fd = new FunctionalDependency();
							fd.addLeft(left);
							fd.addRight(right);
							s = s + left;
							
							originalTable.addIntraNkaDep(s, fd);
//							s = s + right;

//							System.out.println("11fds:  " + s + " " + right);
							// TO DO comment out.
							originalTable.addTwoNFDependencies(s);
							s = "";
						}
						// check next pair 
					} // end if right != left
				} // end for right varying
			} // end for left varying
//			System.out.println("One to One FDs in table " + tableName + "?: " + FDsInTable);
			
		} // end try
		catch(SQLException e){
			System.out.println("SQL Error: " + e.getMessage());
		}
		return FDsInTable;
	}
	

	public boolean isOneNF(){
		//	System.out.println("IN isOneNF");
		boolean distinct = false;

		String n = "select count(*) from " + tableName + ";";
//		System.out.println(tableName);
		int noOfRows = 0;
		try{
			ResultSet nors = dbconn.executeQuery(n);
			while (nors.next()){
				noOfRows = nors.getInt(1);
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}

//		System.out.println("noOfRows: " + noOfRows);
		if (noOfRows > 0){
			for (String key : keyAttNames){

				String statement = select + " from " + originalTable.getTableName() + where + key + " is null";
				formatSQL(statement);
				sqlScript.add(statement);
				ResultSet rs = dbconn.executeQuery(statement);

				try{
					//				System.out.println("rs.next: " + rs.next());

					if(!rs.next())//no null values, then check for duplicates
					{
//						System.out.println("numKeyAtts: "+numKeyAtts);
						if (numKeyAtts == 1){ // because you check > 1 case below
							// and because it's an error if you check each col separately for distinct vals == # rows
							statement = "select count(distinct "+ key +") as count from " + originalTable.getTableName();
							formatSQL(statement);
							//sqlScript.add(statement);
							rs = dbconn.executeQuery(statement);
							int count = 0;
							while (rs.next()) {
								count = rs.getInt("count");
//									System.out.println("count?? " + count);
							}
							if(noOfRows == count)
							{
								distinct = true;
								originalTable.setOneNF(true);
//								return true;
							}
							else {
								// distinct is already false
								originalTable.setOneNF(false);
								originalTable.setNotOneNF_Cause("duplicates and/or nulls in key attribute");
//								return false;
							}
						} // end if numKeyAtts == 1
					} // end if rs.next()
					else { // there are nulls
						originalTable.setOneNF(false);
						originalTable.setNotOneNF_Cause("duplicates and/or nulls in key attribute");
						return false;  // no point in checking further if there are nulls anywhere in the key
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}

			}//end of for i < numKeyAtts

			if(numKeyAtts > 1)
			{
				boolean is1NF = checkCompositeKey(noOfRows);//check for the 3rd case
//				System.out.println("is1NF after checkComposite: " + is1NF);
				originalTable.setOneNF(is1NF);
				if (!is1NF){
					originalTable.setOneNF(false);
					originalTable.setNotOneNF_Cause("duplicates in composite Key attribute");
				}
				return is1NF;
			}
			else{
				return distinct; // = false
			}
		}
		else{
			originalTable.setOneNF(false);
			originalTable.setNotOneNF_Cause("no data in table");
			return false;
		}
	}
	
	public boolean checkCompositeKey(int noOfRows)
	{
//		System.out.println("IN checkCompositeKey");
		String concatKeys = "";
		String concatKeysSet2 = "";
		int count = 0, totalKeys = 0;
		
		// why are you counting keys in a loop, when you have the number in the condition of the loop!!!!!?????
		for(int i = 0; i < originalTable.getKey().getAttributes().size(); i++)
		{
			if(i < 2)
				concatKeys = concatKeys + originalTable.getKey().getAttributes().get(i).getName();
			else
				concatKeysSet2 = concatKeysSet2 + originalTable.getKey().getAttributes().get(i).getName();
			
			if(i != originalTable.getKey().getAttributes().size() - 1 && i < 1)
			{
				concatKeys = concatKeys + ", ";
			}
			else if(i != originalTable.getKey().getAttributes().size() - 1 && i >= 2)
			{
				concatKeysSet2 = concatKeysSet2 + ", ";
			}
			
			// Why are you counting keys?  You already have the number.
			totalKeys = i + 1;
		}//end of for
//		System.out.println("totalKeys: " + totalKeys);
		
		String stmt = "";
		
		if(totalKeys == 2)
		{
			stmt = "select count(distinct concat(" + concatKeys + ")) as count from " + originalTable.getTableName();
			formatSQL(stmt);
			//sqlScript.add(stmt);
		}
		else if (totalKeys == 3)
		{
			stmt = "select count(distinct concat(concat(" + concatKeys + "), " + concatKeysSet2 + ")) as count from "  + originalTable.getTableName();
			formatSQL(stmt);
		}
		else
		{
			stmt = "select count(distinct concat(concat(" + concatKeys + "), concat(" + concatKeysSet2 + "))) as count from " + originalTable.getTableName();
			formatSQL(stmt);
			//sqlScript.add(stmt);
		}

		try
		{
			ResultSet rs = dbconn.executeQuery(stmt);
			while (rs.next()) {
				count = rs.getInt("count");
//				System.out.println("count?? " + count);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		System.out.println("noOfRows?? " + noOfRows);
        if(noOfRows == count)
        {
        	return true;
        }
		
		return false;
	}

	
	/**
	 * @return the sqlScript
	 */
	public ArrayList<String> getSQLScript() {
		return sqlScript;
	}
	
	
	public boolean isTwoNF(){
		if (originalTable.isOneNF()){
			

			String huh = "select * from " + originalTable.getTableName();

		ArrayList<Attribute> nonKeyAttributes = originalTable.getNonKeyAttributes();
		String query = "";
		int numKeyAtts = keyAttNames.size();
		boolean twoNF;
		
		if (numKeyAtts == 1){  // no subsets, we're automatically 2NF
			originalTable.setTwoNF(true);
			return true;
		}
		
		// num key atts > 1
		
		keySubsets = originalTable.getKey().getKeySubsets();
		int numSubsets = keySubsets.size();  // 2 or 6 for this program
		boolean dependencies = false;
		
		for (ArrayList<Attribute> set : keySubsets){		// for each key subset	
			if (set.size() == 1){	// if the subset is a single attribute
				Attribute att = set.get(0);
				String katt = att.getName();  // LHS
//				System.out.println("katt: " + katt);

				for(String nkatt : nonKeyAttNames)
				{
					query = "select * from " + tableName +"\nas a1"
							+ " inner join "+ tableName +" as a2 \non ( "
							+ "a1." + katt + " = a2." + katt 
							+ " and " + "a1." + nkatt + " != " 
							+ "a2." + nkatt +")";

					try {
						formatSQL(query);
						sqlScript.add(query);
						ResultSet rs = dbconn.executeQuery(query);

						if (!rs.next()){  // empty set => dependency is true
							dependencies = true;
							originalTable.setTwoNF(false);
							String s = "";

							FunctionalDependency fd = new FunctionalDependency();
							fd.addLeft(katt);
							fd.addRight(nkatt);
							s = s + katt;
//							s = s + nkatt;
							originalTable.addKsubToNkaDep(s, fd);

						}  // end if
					}
					catch(SQLException e){
						System.out.println("SQL Error: " + e.getMessage());
					} // end catch
				} // end for nkatt
			} // end if set size == 1
			else { //set size = 2  // composite
				Attribute att1 = set.get(0);
				Attribute att2 = set.get(1);
				String katt1 = att1.getName();
				String  katt2 = att2.getName();

				for (String nkatt : nonKeyAttNames){
					query = "select * from " + tableName +" as\na1"
							+ " inner join "+ tableName +" as a2 \non ( "
							+ "a1." + katt1 + " = a2." + katt1
							+ " and a1." + katt2 + "= a2." + katt2 
							+ " and " + "a1." + nkatt + " != " 
							+ "a2." + nkatt +")";

					try {
						formatSQL(query);
						sqlScript.add(query);
						ResultSet rs = dbconn.executeQuery(query);

						if (!rs.next()){   // there is a dependency
							dependencies = true;
							originalTable.setTwoNF(false);
							String s = "";

							FunctionalDependency fd = new FunctionalDependency();
							fd.addLeft(katt1);
							fd.addLeft(katt2);
							fd.addRight(nkatt);
							
					
//							System.out.println("isTwoNF: " + s);
							s = s + katt1;
							s = s + katt2;
							originalTable.addKsubToNkaDep(s, fd);

						}
					}
					catch(SQLException e){
						System.out.println("SQL Error: " + e.getMessage());
					} // end catch
				} // end for nkatt
			}  // end else

		}  //end for set


		if(dependencies)
		{
			originalTable.setTwoNF(false);
			return false;
		}
		else {
			originalTable.setTwoNF(true);
			return true;
		}
	}
	else {
		System.err.println("Error: tried to check UNF table for 2NF");
		return false;
	}
	} // end isTwoNF
	
	public void formatSQL(String sql1) {
		
		String[] split = sql1.split("from");
		
		String select = split[0];
		String fromWhere = "from " + split[1];
		
		split = fromWhere.split("where");
		
		String from = split[0];
		
		String where = "";
		
		if(split.length > 1)
		{where = "where " + split[1];}
		
		sqlScript.add(select);
		sqlScript.add(from);
		sqlScript.add(where);
		sqlScript.add("");
		
	}
	
	public void setSQL()
	{
		originalTable.setSQLScript(sqlScript);
	}
}
