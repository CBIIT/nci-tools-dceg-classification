package gov.nih.cit.socassign.codingsystem;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Model for a JTree that hold a CodingSystem.
 * 
 * @author Daniel Russ
 */
public class CodingSystemTreeModel implements TreeModel{

	private final CodingSystem codingSystem;
	
	public CodingSystemTreeModel(CodingSystem codingSystem) {
		this.codingSystem=codingSystem;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return getParentFromObject(parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return getParentFromObject(parent).getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		OccupationCode parentCode=getParentFromObject(parent);
		String childCode=OccupationCode.getCodeFromString(child.toString());
		
		List<OccupationCode> children=parentCode.getChildren();
		for (int i=0;i<parentCode.getChildren().size();i++){
			if (children.get(i).getName().equals(childCode)) return i;
		}

		return -1;
	}

	private OccupationCode getParentFromObject(Object parent){		
		return codingSystem.getOccupationalCode( OccupationCode.getCodeFromString(parent.toString()) );
	}
	
	@Override
	public Object getRoot() {
		return codingSystem.getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		return codingSystem.getOccupationalCode(OccupationCode.getCodeFromString(node.toString())).isLeaf();
	}

	// the treemodel is static, so there is no point in adding model listeners.  It cannot change.
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// thank you for adding yourself to the treeModelListenerList...
		// I'll just write your name down and notify you of any changes. (click) 
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// thank you, I'll just take you off the list ...  
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// anyone listening... no point doing anything...
	}
	
	
}
