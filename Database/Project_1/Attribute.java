import java.util.ArrayList;

/**
 * 
 */


public class Attribute {
	//   TEST
	
	private String name;
	ArrayList<String> determines; // 
	ArrayList<String> determinedBy;
	
	public Attribute(String name){
		this.name = name;
		determines = new ArrayList<String>();
		determinedBy = new ArrayList<String>();
	}
	
	public String getName(){
		return name;
	}
	
	public void addDetermines(String s){
		determines.add(s);
//		System.out.println("Attribute: " + name + " determines " + s);
	}
	
	public ArrayList<String> getDetermines(){
			return determines;
	}
	
	public void addDeterminedBy(String s){
		determinedBy.add(s);
//		System.out.println("Attribute: " + name + " determinedBy " + s);

	}
	
	public ArrayList<String> getDeterminedBy(){
		return determinedBy;
	}
}
