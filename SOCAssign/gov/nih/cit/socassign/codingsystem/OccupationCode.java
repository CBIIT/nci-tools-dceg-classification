package gov.nih.cit.socassign.codingsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * A code from a Hierarchical Coding System.  Since the CodingSystem is a tree, the code is a node in the tree.  
 * The root of the tree should be a {@link CodingSystem} object.
 * 
 * 
 * @see @link CodingSystem
 * 
 * @author Daniel Russ
 *
 */
public class OccupationCode {

	private final String name;
	private final String title;
	private final String level;
	private final String description;
	private final OccupationCode parent;
	private List<OccupationCode> children;

	public OccupationCode(OccupationCode parent,String name,String title,String level) {
		this(parent,name,title,title,level);
	}

	public OccupationCode(OccupationCode parent,String name,String title,String description, String level) {
		this.parent=parent;
		this.name=name;
		this.title=title;
		this.level=level;
		this.description=description;
		children=new ArrayList<OccupationCode>();
	}
	public boolean addChild(OccupationCode e) {
		return children.add(e);
	}

	public String getName() {
		return name;
	}
	public String getTitle() {
		return title;
	}
	public String getLevel() {
		return level;
	}
	public OccupationCode getParent() {
		return parent;
	}
	public List<OccupationCode> getChildren() {
		return children;
	}
	public boolean isLeaf(){
		return children.size()==0;
	}
	public String getDescription() {
		return description;
	}
	
	/** The root should be a CodingSystem @see @link {CodingSystem}
	 * @return The @link{CodingSystem} of this OccupationCode.
	 * */
	public OccupationCode getRoot(){
		if (parent == null) return this;
		return parent.getRoot();
	}

	/**
	 * Returns the coding system used by this code.  If the 
	 * System Tree is not setup correctly (i.e. the root is not a CodingSystemRoot) it is possible to return a
	 * null value.  Use with care.
	 * 
	 * @return CodingSystem for this code.
	 */
	public CodingSystem getCodingSystem(){
		if (parent == null) return null;
		return parent.getCodingSystem();
	}
	
	@Override
	public String toString() {
		return name+"\1 "+title;
	}
	
	public static String getCodeFromString(String occCodeString){
		int len=occCodeString.indexOf('\1');
		if (len==-1) len=occCodeString.length();
		return occCodeString.substring(0, len);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OccupationCode other = (OccupationCode) obj;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	

}
