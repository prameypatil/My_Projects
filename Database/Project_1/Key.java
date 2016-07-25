import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

public class Key {

	private ArrayList<Attribute> attributes;
	
	boolean isForeign = false;
	
	private List<ArrayList<Attribute>> subsets = new ArrayList<ArrayList<Attribute>>();

	public Key(){
		attributes = new ArrayList<Attribute>();
	}
	
	public Key(ArrayList<Attribute> attributes){
		this.attributes = attributes;
		if (attributes.size() > 1){
			generateSubsets();
		}
	}

	/**
	 * 
	 */
	public void generateSubsets(){

		if (attributes.size() > 1){
			ArrayList<Attribute> hs1 = new ArrayList<Attribute>();
				ArrayList<Attribute> hs2 = new ArrayList<Attribute>();
				ArrayList<Attribute> hs3 = new ArrayList<Attribute>();
				ArrayList<Attribute> hs4 = new ArrayList<Attribute>();
				ArrayList<Attribute> hs5 = new ArrayList<Attribute>();
				ArrayList<Attribute> hs6 = new ArrayList<Attribute>();
		
			if (attributes.size() == 3){
				hs1.add(attributes.get(0));
				hs1.add(attributes.get(1));
				subsets.add(hs1);
				hs2.add(attributes.get(1));
				hs2.add(attributes.get(2));
				subsets.add(hs2);
				hs3.add(attributes.get(0));
				hs3.add(attributes.get(2));
				subsets.add(hs3);
				hs4.add(attributes.get(0));
				subsets.add(hs4);
				hs5.add(attributes.get(1));
				subsets.add(hs5);
				hs6.add(attributes.get(2));		
				subsets.add(hs6);
			}
			else {
				hs1.add(attributes.get(0));
				subsets.add(hs1);
				hs2.add(attributes.get(1));
				subsets.add(hs2);	
			}		
		}	// end if att > 1
	}
	
	public List<ArrayList<Attribute>> getKeySubsets(){
		if (subsets.isEmpty() && (attributes.size() > 1)){
			generateSubsets();
		}
		return subsets;
	}
	
	public void addAttribute(String attributeName){
		Attribute a = new Attribute(attributeName);
		attributes.add(a);
	}
	
	
	/**
	 * @return the attributeNames
	 */
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributeNames the attributeNames to set
	 */
	public void setAttributes(ArrayList<Attribute> attributeNames) {
		this.attributes = attributeNames;
	}
	
	public void setIsForeign(boolean foreign){
		this.isForeign = foreign;
	}
	
	public boolean isForeign(){
		return isForeign;
	}
	
	
}
