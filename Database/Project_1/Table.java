import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 * 
 */

public class Table {
  
	private String name;
	private boolean verified;
	
	private Key primaryKey;
	private Key foreignKey;
	
	private ArrayList<Attribute> nonKeyAttributes;

	ArrayList<String> oneNFDependencies = null;  
	ArrayList<String> twoNFDependencies = null;  
	
	private boolean oneNF = false, 
					twoNF = false, 
					threeNF = false;
	
	private String notOneNF_Cause;  // nulls or duplicates
	private String notTwoNF_Cause;  // unallowed dependencies, e.g., K1->B
	private String notThreeNF_Cause;  // unallowed dependencies, e.g., list of deps
	
	//Well, I guess we'll store here because it doesn't make sense to store in Attribute
	// This is two to one FDs among nonkey attributes from 3NF test
	// Could TODO Actually could rewrite 3NF checks now to use intraNkaDeps
	ArrayList<ArrayList<String>> nkaTwoToOneFDs = null;  
	
	//New approach to storing dependencies.  Uses a new class, 
	// FunctionalDependency. Keys to the hashtable are LHS of dependencies.
	Hashtable<String,FunctionalDependency> ksubToNkaDeps;   // for 2NF test
	Hashtable<String,FunctionalDependency> intraNkaDeps;	// for 3NF test
	
	ArrayList<String> sqlScript;
	String normForm = "";
	
	ArrayList<Table> oneNFDecomposition;
	ArrayList<Table> twoNFDecomposition;
	
	boolean decompverified;

//********************************************************

	// Table Class
	public Table(String name){
		this.name = name;
		nonKeyAttributes = new ArrayList<Attribute>();
//		nkaTwoToOneFDs = new ArrayList<ArrayList<String>>();
		oneNFDependencies = new ArrayList<String>();
		twoNFDependencies = new ArrayList<String>();
		this.primaryKey = new Key();
		intraNkaDeps = new Hashtable<String,FunctionalDependency>();
		ksubToNkaDeps = new Hashtable<String,FunctionalDependency>();
		
	}
	
	public Table(String name, Key pk, Key fk, ArrayList<Attribute> nka){
		this(name);
		for (Attribute a : pk.getAttributes()){
			this.addKeyAttribute(a.getName());
			this.primaryKey.setIsForeign(false);	
		}
		if (fk != null){
			for (Attribute fa : fk.getAttributes()){
				this.addFKeyAttribute(fa.getName());
				this.foreignKey.setIsForeign(true);
			}
		}
		for (Attribute nk : nka)
			this.addNonKeyAttribute(nk.getName());
	}
	
	public Table(Table t){
		this(t.getTableName(),t.getKey(),t.getForeignKey(),t.getNonKeyAttributes());
	}
	
//	public Table(String name, Key key){
//		this(name);
//		this.primaryKey = key;
//	}
	
	public boolean containsPKeyAttribute(String s){
		boolean found = false;
		for (Attribute a : primaryKey.getAttributes())
			if (a.getName().equals(s))
				found = true;
		return found;
	}
	
	public boolean containsNonkeyAttribute(String s){
		boolean found = false;
		for (Attribute a : nonKeyAttributes)
			if (a.getName().equals(s))
				found = true;
		return found;
	}
	
	public boolean containsFKeyAttribute(String s){
		boolean found = false;
		for (Attribute a : foreignKey.getAttributes())
			if (a.getName().equals(s))
				found = true;
		return found;
	}
	
	public void removePKeyAttribute(String s){
		int i=-1;
		for (int j = 0; j < primaryKey.getAttributes().size(); j++)
			if (primaryKey.getAttributes().get(j).getName().equals(s)){
				i = j;
				break;
			}
		if (i != -1)
			primaryKey.getAttributes().remove(i);
	}
	
	public void removeFKeyAttribute(String s){
		int i=-1;
		for (int j = 0; j < foreignKey.getAttributes().size(); j++)
			if (foreignKey.getAttributes().get(j).getName().equals(s)){
				i = j;
				break;
			}
		if (i != -1)
			foreignKey.getAttributes().remove(i);
	}
	
	public void removeNonkeyAttribute(String s){
		int i=-1;
		for (int j = 0; j < nonKeyAttributes.size(); j++)
			if (nonKeyAttributes.get(j).getName().equals(s)){
				i = j;
				break;
			}
		if (i != -1)
			nonKeyAttributes.remove(i);
	}
	
	public void removeAttribute(String s){
		boolean found = false;
		int i=-1;
		if (primaryKey != null){
		for (int j = 0; j < primaryKey.getAttributes().size(); j++)
			if (primaryKey.getAttributes().get(j).getName().equals(s)){
				i = j;
				found = true;
				break;
			}
		
		if (i != -1)
			primaryKey.getAttributes().remove(i);
		}
		
		if (!found){
			if (foreignKey != null){
				for (int j = 0; j < foreignKey.getAttributes().size(); j++)
					if (foreignKey.getAttributes().get(j).getName().equals(s)){
						i = j;
						found = true;
						break;
					}
				if (i != -1)
					foreignKey.getAttributes().remove(i);
			}
		}

		if (!found){
			if (nonKeyAttributes != null){
				for (int j = 0; j < nonKeyAttributes.size(); j++)
					if (nonKeyAttributes.get(j).getName().equals(s)){
						i = j;
						break;
					}
				if (i != -1)
					nonKeyAttributes.remove(i);	
			}
		}
	}
	
	public String getTableName() {
		return name;
	}

	public Key getKey() {
		return primaryKey;
	}

	public void setKey(Key key) {
		this.primaryKey = key;
	}

	public void setNormForm(String s)
	{
		this.normForm = s;
	}
	
	public String getNormForm()
	{
		return this.normForm;
	}
	
	public ArrayList<Attribute> getNonKeyAttributes() {
		return nonKeyAttributes;
	}

	public void setNonKeyAttributes(ArrayList<Attribute> nonKeyAttributes) {
		this.nonKeyAttributes = nonKeyAttributes;
	}
	
	public void addNonKeyAttribute(String name){
		Attribute a = new Attribute(name);
		nonKeyAttributes.add(a);
	}
	
	public void addFKeyAttribute(String name){
		if (this.foreignKey == null){
			foreignKey = new Key();
			foreignKey.setIsForeign(true);
		}
		foreignKey.addAttribute(name);
	}

	public void addKeyAttribute(String name){
		if (primaryKey == null){
			primaryKey = new Key();
		}
		primaryKey.addAttribute(name);
	}
	
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the oneNF
	 */
	public boolean isOneNF() {
		return oneNF;
	}

	/**
	 * @param oneNF the oneNF to set
	 */
	public void setOneNF(boolean oneNF) {
		this.oneNF = oneNF;
	}

	/**
	 * @return the twoNF
	 */
	public boolean isTwoNF() {
		return twoNF;
	}

	/**
	 * @param twoNF the twoNF to set
	 */
	public void setTwoNF(boolean twoNF) {
		this.twoNF = twoNF;
	}

	/**
	 * @return the threeNF
	 */
	public boolean isThreeNF() {
		return threeNF;
	}

	/**
	 * @param threeNF the threeNF to set
	 */
	public void setThreeNF(boolean threeNF) {
		this.threeNF = threeNF;
	}


	public ArrayList<String> getNonKeyNames(){

		ArrayList<String> names = new ArrayList<String>();
		for (Attribute at : nonKeyAttributes)
			names.add(at.getName());
		return names;
		
	}
	public ArrayList<String> getKeyNames(){
		ArrayList<Attribute> keyNames = primaryKey.getAttributes();
		ArrayList<String> names = new ArrayList<String>();
		for (Attribute name : keyNames)
			names.add(name.getName());
		return names;
		
	}
	
	public ArrayList<String> getFKeyNames(){
		ArrayList<Attribute> atts;
		ArrayList<String> names = new ArrayList<String>();
		if (foreignKey != null){
			atts = foreignKey.getAttributes();
			for (Attribute att : atts)
				names.add(att.getName());
		}
		return names;
	}
	
	
	public ArrayList<String> getAllAttributeNames(){
		ArrayList<Attribute> keyNames = primaryKey.getAttributes();
		ArrayList<Attribute> fkeyNames;
		ArrayList<String> names = new ArrayList<String>();
		if (foreignKey != null){
			fkeyNames = foreignKey.getAttributes();
			for (Attribute fname : fkeyNames)
				names.add(fname.getName());
		}
		for (Attribute name : keyNames)
			names.add(name.getName());
		for (Attribute nkname : nonKeyAttributes)
			names.add(nkname.getName());
		return names;
	}
	
	public Hashtable<String,Attribute> getNKAttributeHash(){
		Hashtable<String,Attribute> hash = new Hashtable<String,Attribute>();
		ArrayList<Attribute> alist = getNonKeyAttributes();
		for (Attribute a : alist)
			hash.put(a.getName(),a);
		return hash;
	}
	
	/**
	 * @return the notOneNF_Cause
	 */
	public String getNotOneNF_Cause() {
		return notOneNF_Cause;
	}

	/**
	 * @param notOneNF_Cause the notOneNF_Cause to set
	 * Set to "nulls" or "duplicates" by NFAnalyzer
	 */
	public void setNotOneNF_Cause(String notOneNF_Cause) {
		this.notOneNF_Cause = notOneNF_Cause;
	}
	
	/**
	 * @return the notTwoNF_Cause
	 */
	public String getNotTwoNF_Cause() {
		return notTwoNF_Cause;
	}

	/**
	 * @param notTwoNF_Cause the notTwoNF_Cause to set
	 */
	//TODO
//	public void setNotTwoNF_Cause(dependencies from somewhere) {
//	}

	/**
	 * @return the notThreeNF_Cause
	 */
	public String getNotThreeNF_Cause() {
		//Could TODO - rewrite to use intraNkaDeps
		if ((!notThreeNF_Cause.equals(null)) || (!notThreeNF_Cause.equals(""))){
			String s = "";
			for (Attribute att : nonKeyAttributes){
				s += att.getName() + "->";
				for (String d : att.getDetermines()){
					s += d;
				}
			}
			for (ArrayList<String> fdset : nkaTwoToOneFDs){
				s += fdset.get(0) + fdset.get(1) + "->";
				for (int i = 2; i < fdset.size(); i++){
					s += fdset.get(i);
				}
			}
			notThreeNF_Cause = s;
		}
		return notThreeNF_Cause;
	}

	public ArrayList<ArrayList<String>> getNkaTwoToOneFDs(){
		return nkaTwoToOneFDs;
	}
	
	public void addTwoToOneFD(ArrayList<String> fd){
		nkaTwoToOneFDs.add(fd);
	}
	/**
	 * @param notThreeNF_Cause the notThreeNF_Cause to set
	 */
	// probably done here
	public void setNkaTwoToOneFDs(ArrayList<ArrayList<String>> deps ) {
		nkaTwoToOneFDs = deps;
	}

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * @param verified the verified to set
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * @return the sqlScript
	 */
	public ArrayList<String> getSQLScript() {
		return sqlScript;
	}

	/**
	 * @param sqlScript the sqlScript to set
	 */
	public void setSQLScript(ArrayList<String> sqlScript) {
		this.sqlScript = sqlScript;
	}

	


	public void outputSQLScript(Table t) {

//		String s = name + "SQL.txt";
		String s = "./" + name + "-SQL.txt";
		
		try {
			PrintWriter writer = new PrintWriter(s,"UTF-8");
			writer.println(t.toString());
			writer.println();
			for (String st : sqlScript)
				writer.println(st);
			
			writer.close();
			
			//Files.write(Paths.get(s), sqlScript);
			
		}
		catch (Exception e){
			System.err.println("Error writing to file: " + s);
		}
	}
	

	
	public void addOneNFDependencies(String s)
	{
		this.oneNFDependencies.add(s);
	}
	
	public ArrayList<String> getOneNFDependencies()
	{
		return oneNFDependencies;
	}
	public void addTwoNFDependencies(String s)
	{
		this.twoNFDependencies.add(s);
	}
	
	public ArrayList<String> getTwoNFDependencies()
	{
		return twoNFDependencies;
	}

	/**
	 * @return the twoNFDecomposition
	 */
	public ArrayList<Table> getTwoNFDecomposition() {
		return twoNFDecomposition;
	}

	/**
	 * @param twoNFDecomposition the twoNFDecomposition to set
	 */
	public void setTwoNFDecomposition(ArrayList<Table> twoNFDecomposition) {
//		System.out.println("in setTwoNFDecomp: ");
//		for (Table t : twoNFDecomposition)
//			System.out.println(t);
		this.twoNFDecomposition = twoNFDecomposition;
	}
	
	/**
	 * @return the foreignKey
	 */
	public Key getForeignKey() {
		return foreignKey;
	}

	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(Key foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	public void addKsubToNkaDep(String s, FunctionalDependency fd){
		if (ksubToNkaDeps.containsKey(s)){
			FunctionalDependency fdep = ksubToNkaDeps.get(s);
			fdep.addRightList(fd.getRight());
		}
		else {
			ksubToNkaDeps.put(s, fd);
		}
	}
	
	/**
	 * @return the ksubToNkaDeps
	 */
	public Hashtable<String, FunctionalDependency> getKsubToNkaDeps() {
		return ksubToNkaDeps;
	}

	/**
	 * @param ksubToNkaDeps the ksubToNkaDeps to set
	 */
	public void setKsubToNkaDeps(Hashtable<String, FunctionalDependency> ksubToNkaDeps) {
		this.ksubToNkaDeps = ksubToNkaDeps;
	}

	public void addIntraNkaDep(String s, FunctionalDependency fd){
		if (intraNkaDeps.containsKey(s)){
//			System.out.println("intra " + intraNkaDeps.containsKey(s));
			FunctionalDependency fdep = intraNkaDeps.get(s);
			// I forget. Does get return a pointer to the object or a copy?  I'm going to assume a pointer for now.
			fdep.addRightList(fd.getRight());
		}
		else {
			intraNkaDeps.put(s, fd);
		}
	}
	
	/**
	 * @return the intraNkaDeps
	 */
	public Hashtable<String, FunctionalDependency> getIntraNkaDeps() {
		return intraNkaDeps;
	}

	/**
	 * @param intraNkaDeps the intraNkaDeps to set
	 */
	public void setIntraNkaDeps(Hashtable<String, FunctionalDependency> intraNkaDeps) {
		Collection<FunctionalDependency> fds = intraNkaDeps.values();
		for (FunctionalDependency fd : fds){
			
		}
		this.intraNkaDeps = intraNkaDeps;
	}

	/**
	 * @return the oneNFDecomposition
	 */
	public ArrayList<Table> getOneNFDecomposition() {
		return oneNFDecomposition;
	}

	/**
	 * @param oneNFDecomposition the oneNFDecomposition to set
	 */
	public void setOneNFDecomposition(ArrayList<Table> oneNFDecomposition) {
		this.oneNFDecomposition = oneNFDecomposition;
	}

	public String toString(){
		String k = "";
		String a = "";
		String fk = "";
		
//		System.out.println("HEEEERE");
		if (primaryKey != null && primaryKey.getAttributes().size() > 0){
			for (String key : getKeyNames()){
				k += key + "(k),";
			}
		}
		
		if (foreignKey != null && (foreignKey.getAttributes().size() > 0)){
			for (String fkey : getFKeyNames()){
				fk += fkey + "(fk),";
			}
		}

		if (nonKeyAttributes != null && nonKeyAttributes.size() > 0){
			for (String nonkey : getNonKeyNames()){
				a += nonkey + ",";
			}
			
//			System.out.println("a: " +a);
			a = a.substring(0, a.length()-1);
		}
		
		return name + "(" + k + fk + a + ")";
		
	}

	/**
	 * @return the decompverified
	 */
	public boolean isDecompverified() {
		return decompverified;
	}

	/**
	 * @param decompverified the decompverified to set
	 */
	public void setDecompverified(boolean decompverified) {
		this.decompverified = decompverified;
	}
	
	
}
