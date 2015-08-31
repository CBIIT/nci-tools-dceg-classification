package gov.nih.cit.socassign.jdbc;

import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.SOCcerResults;
import gov.nih.cit.socassign.codingsystem.CodingSystem;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Map;

public interface SOCAssignDAO extends Closeable {

	public void connect(String filename) throws SQLException;
	public SOCcerResults readResults() throws SQLException;
	public void fillResultsTable(SOCcerResults results) throws SQLException;
	public void loadAssignments(Map<Integer, Assignments>codeMap,CodingSystem system) throws SQLException;
	public void setAssignment(Assignments assignment) throws SQLException;
	public void updateFlag(Assignments assignments) throws SQLException;

}
