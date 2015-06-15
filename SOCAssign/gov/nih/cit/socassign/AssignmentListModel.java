package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsysten.OccupationCode;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;

/**
 * The list model for the assignmentsList (list of Coder Selected Occupation codes).
 * Currently the Coder is only allow to make 3 selections.
 * 
 * @author Daniel Russ
 *
 */
public class AssignmentListModel extends AbstractListModel<OccupationCode> {

	private static Assignments NO_ASSIGNMENT=new Assignments(-1,new ArrayList<OccupationCode>());

	private static Logger logger=Logger.getLogger(AssignmentListModel.class.getName());
	private Assignments assignments=NO_ASSIGNMENT;
	
	public AssignmentListModel() {
	}

	public void clear(){
		int sz=assignments.size();
		assignments=NO_ASSIGNMENT;
		fireContentsChanged(this, 0, sz);
	}
	public void resetList(Assignments assignments){
		int sz=0;
		if (this.assignments!=null) sz=assignments.size();
		this.assignments=assignments;
		
		fireContentsChanged(this, 0, sz);
	}
	
	public void addAssignment(OccupationCode codeAssigned){
		if (codeAssigned==null || assignments.contains(codeAssigned)) return;

		if (assignments.size()==3){
			assignments.remove(2);
			assignments.add(codeAssigned);
			fireContentsChanged(this, 2, 2);
			logger.finer("adding "+codeAssigned.getName()+" to assignment list ivChange: (2,2)");
		}else{
			assignments.add(codeAssigned);
			fireIntervalAdded(this, assignments.size(), assignments.size());
			logger.finer("adding "+codeAssigned.getName()+" to assignment list");
		}
	}
	
	public void addSelection(String socAssignment) {
		addAssignment( getOccupationCode(socAssignment) );
	}

	public boolean containsSOC(OccupationCode code){
		return assignments.contains(code);
	}
	
	public boolean containsSOC(String assignment){
		return containsSOC( getOccupationCode(assignment) );
	}
	
	public void decreaseSelection(int indx){
		if ( (indx+1)>=assignments.size() ) return;
		
		OccupationCode tmp=assignments.get(indx+1);
		assignments.set(indx+1, assignments.get(indx));
		assignments.set(indx, tmp);
		fireContentsChanged(this, indx, indx+1);
	}
	
	public void increaseSelection(int indx){
		if ( indx>=assignments.size() || indx==0) return;
		
		OccupationCode tmp=assignments.get(indx-1);
		assignments.set(indx-1, assignments.get(indx));
		assignments.set(indx, tmp);
		fireContentsChanged(this, indx-1, indx);
	}
	
	public void removeElementAt(int indx){
		if (indx>=assignments.size()) return;
		assignments.remove(indx);
		fireIntervalRemoved(this, indx, indx);
	}
	
	@Override
	public OccupationCode getElementAt(int indx) {
		return assignments.get(indx);
	}

	@Override
	public int getSize() {
		return assignments.size();
	}
	
	private OccupationCode getOccupationCode(String code){
		return SOCAssignModel.getInstance().getCodingSystem().getOccupationalCode(code);
	}

	public Assignments getAssignments() {
		return assignments;
	}
}
