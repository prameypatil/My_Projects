import java.util.ArrayList;

public class FunctionalDependency {

	ArrayList<String> left;
	ArrayList<String> right;
	ArrayList<String> set; // contains all attributes on l or r


	boolean sixOfOne = false;
	boolean isSubset = false;
	boolean isSuperset = false;
	
	


	public FunctionalDependency(){
		this.left = new ArrayList<String>();
		this.right = new ArrayList<String>();
		this.set = new ArrayList<String>();
	}

	/**
	 * @return the left
	 */
	public ArrayList<String> getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
//	public void setLeft(ArrayList<String> left){
//		for (String l : left){
//			this.addLeft(l);
//		}
//	}
	
	public void addLeft(String lhs){
		left.add(lhs);
		set.add(lhs);
	}
	
	public void addRight(String rhs){
		right.add(rhs);
		set.add(rhs);
	}
	
	public void addRightList(ArrayList<String> rlist){
		for (String r : rlist)
			this.addRight(r);
	}
	
	public void addLeftList(ArrayList<String> llist){
		for (String l : llist)
			this.addLeft(l);
	}
	
	/**
	 * @return the right
	 */
	public ArrayList<String> getRight() {
		return right;
	}

	/**
	 * @param right the right to set
	 */
//	public void setRight(ArrayList<String> right) {
//		this.right = right;
//	}
	

	/**
	 * @return the sixOfOne
	 */
	public boolean isSixOfOne() {
		return sixOfOne;
	}

	/**
	 * @param sixOfOne the sixOfOne to set
	 */
	public void setSixOfOne(boolean sixOfOne) {
		this.sixOfOne = sixOfOne;
	}
	
	public boolean matches(FunctionalDependency fd){
		ArrayList<String> fds = fd.getSet();
		boolean match = true;
		
		if (fds.size() != this.set.size())
			return false;
		else {
			for (String s : fds){
				if (!set.contains(s)){
					match = false;
					break;
				}
			}
		}
		return match;
	}
	
	/**
	 * @return the set
	 */
	public ArrayList<String> getSet() {
		return set;
	}

	/**
	 * @param set the set to set
	 */
	public void setSet(ArrayList<String> set) {
		this.set = set;
	}
	
	public ArrayList<String> minus(FunctionalDependency fd){
		ArrayList<String> leftover = new ArrayList<String>();
		
		for (String s : fd.getSet()){
			if (!set.contains(s)){
				leftover.add(s);
			}
		}
		return leftover;
	}
	
	public boolean isContainedIn(FunctionalDependency f){
		boolean containedIn = false;
		if (f.getSet().containsAll(this.getSet())){
			containedIn = true;
			this.setSubset(true);
			f.setSuperset(true);
		}
		return containedIn;
	}
	/**
	 * @return the isSubset
	 */
	public boolean isSubset() {
		return isSubset;
	}

	/**
	 * @param isSubset the isSubset to set
	 */
	public void setSubset(boolean isSubset) {
		this.isSubset = isSubset;
	}
	
	public boolean isSuperset(FunctionalDependency fd){
		boolean sup = true;
		for (String s : fd.getSet())
			if (!set.contains(s))
				sup = false;
		setSuperset(false);
		return sup;
	}
	/**
	 * @return the isSuperset
	 */
	public boolean isSuperset() {
		return isSuperset;
	}

	/**
	 * @param isSuperset the isSuperset to set
	 */
	public void setSuperset(boolean isSuperset) {
		this.isSuperset = isSuperset;
	}

	public String toString(){
		String s = "";
		for (String l : left)
			s += l + " ";
		s += "->";
		for (String r : right)
			s += r + " ";
		
		s += " sixOfOne? " + sixOfOne;
		
		return s;
	}
	
	
}
