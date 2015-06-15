package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsysten.CodingSystemTreeModel;
import gov.nih.cit.socassign.codingsysten.OccupationCode;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This panel holds a JTree displaying a CodingSystem.
 * 
 * @author Daniel Russ
 *
 */
public class CodingSystemPanel extends JPanel {

	JTree codingSystemTree=new JTree();

	public CodingSystemPanel() {
		setLayout(new BorderLayout());
		add(new JScrollPane(codingSystemTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		codingSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public void updateCodingSystem(AssignmentCodingSystem codingSystem){
		codingSystemTree.clearSelection();
		codingSystemTree.setModel(new CodingSystemTreeModel(codingSystem.getCodingSystem()));
	}

	public void addTreeSelectionListener(TreeSelectionListener treeSelectionListener) {
		codingSystemTree.addTreeSelectionListener(treeSelectionListener);
	}
	
	public void addMouseListenerToJTree(MouseListener mouseListener){
		codingSystemTree.addMouseListener(mouseListener);
	}

	public void removeTreeSelectionListener(TreeSelectionListener tsl) {
		codingSystemTree.removeTreeSelectionListener(tsl);
	}

	public void selectOccupation(OccupationCode code){
		TreePath path=makePath(code);
		codingSystemTree.expandPath(path);
		codingSystemTree.scrollPathToVisible(path);
		codingSystemTree.setSelectionPath(path);
	}
	
	private TreePath makePath(OccupationCode code){
		LinkedList<OccupationCode> pathBuilder=new LinkedList<OccupationCode>();
		
		OccupationCode currentCode=code;
		while(currentCode!=null){
			pathBuilder.addFirst(currentCode);
			currentCode=currentCode.getParent();
		}
		return new TreePath(pathBuilder.toArray(new OccupationCode[pathBuilder.size()]));
	}
	
	public int getSelectedPathCount(){
		if (codingSystemTree.getSelectionPath() == null) return -1;
		
		return codingSystemTree.getSelectionPath().getPathCount();
	}
	public OccupationCode getLastSelectedPathComponent() {
		return (OccupationCode)codingSystemTree.getLastSelectedPathComponent();
	}

	public void clearSelection() {
		codingSystemTree.clearSelection();
	}
	
	public void setSelection(OccupationCode code){
		// build the TreePath
		List<OccupationCode> list=new ArrayList<OccupationCode>();
		OccupationCode currentCode=code;
		while (currentCode!=null){
			list.add(currentCode);
			currentCode=currentCode.getParent();
		}
		Collections.reverse(list);

		TreePath path = new TreePath(list.toArray()); 
		codingSystemTree.setSelectionPath(path);
		codingSystemTree.scrollPathToVisible( path);
	}
}
