package gov.nih.cit.consensus;

import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.soccer.input.SoccerInput;

public class DataRow {
	final SoccerInput input;
	final Assignments assignment;
	
	public DataRow(SoccerInput input, Assignments assignment) {
		this.input = input;
		this.assignment = assignment;
	}

	public SoccerInput getInput() {
		return input;
	}

	public Assignments getAssignments() {
		return assignment;
	}
	
	public int getRowId() {
		return assignment.getRowId();
	}
}
