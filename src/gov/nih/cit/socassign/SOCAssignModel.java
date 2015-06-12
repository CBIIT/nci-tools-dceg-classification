package gov.nih.cit.socassign;

import gov.nih.cit.socassign.Assignments.FlagType;
import gov.nih.cit.socassign.codingsysten.OccupationCode;
import gov.nih.cit.socassign.jdbc.SOCAssignDAO;
import gov.nih.cit.socassign.jdbc.SOCAssignDAOFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * <p>This class holds the bulk of the information needed by the coders
 * to perform their coding, and the results.  This model holds the JTable
 * and JList models in order to ensure they interoperate smoothly.  Most
 * of the actual setting is performed in anonymous controller classes in
 * SOCAssign</p>
 * 
 * @author Daniel Russ
 *
 *
 */
public class SOCAssignModel {

	public static final String TABLE_MODEL_PROPERTY="table.model";
	/** only one instance of SOCAssign model is needed.  Only bad things can occur if you allow a second*/
	private static SOCAssignModel INSTANCE=new SOCAssignModel();
	private static Logger logger=Logger.getLogger(SOCAssignModel.class.getName());
	
	/** The results of SOCcer  */
	private SOCcerResults results=null;

	/** The table model holds the SOCcer results used by the SOCcerResults table.   */
	private SOCcerResultsTableModel tableModel=new SOCcerResultsTableModel(null);
	/** The list model that holds the coders assignments */
	private AssignmentListModel assignmentListModel=new AssignmentListModel();
	/** the coding system used by SOCcer.  Most likely will by SOC2010. */
	private AssignmentCodingSystem codingSystem=null;
	/** holds the SOCcer results for the selected index. */
	private SingleSOCcerResultTableModel singleJobDescriptionTableModel=new SingleSOCcerResultTableModel();
	/** Maps the RowID to the coders results.  <B>WARNING</B>: this is not necessarily the rowIndex. */
	private Map<Integer, Assignments> assignmentListMap=new HashMap<Integer,Assignments>();
	private DefaultListModel singleJobDescriptionListModel=new DefaultListModel();
	
	/** the selected row NOT the selected rowID (This can get confusing.)*/
	private int selectedResultsRow=0;
	private SOCAssignDAO dao=null;
	
	private SOCAssignModel() {}

	public static SOCAssignModel getInstance() {
		if (INSTANCE==null){
			INSTANCE=new SOCAssignModel();
		}
		return INSTANCE;
	}
	
	/** returns a list of OccupationCodes assigned by the coder for the selected row. */
	private Assignments getAssignmentsForSelectedRow(){
		int rowId=getSelectedRowId();
		Assignments assignments=assignmentListMap.get(rowId);
		if (assignments==null){
			assignments=new Assignments(rowId,new ArrayList<OccupationCode>());
			assignmentListMap.put(rowId, assignments);
		}
		return assignments;
	}
	
	/** Set the occupational coding system used in the analysis.  */
	public void setCodingSystem(AssignmentCodingSystem codingSystem) {
		logger.finer("setting the coding system to "+codingSystem.toString());
		this.codingSystem = codingSystem;
	}
	/** This method checks to see if the coder has assigned any codes to the rowIndex of the resultsTable. 
	 * @return true if at least 1 code has been assigned, false otherwise*/
	public boolean isRowAssigned(int rowIndex){
		Assignments assignment=assignmentListMap.get(getRowIdForRowIndex(rowIndex));
		return ( assignment!=null && assignment.size()>0 );
	}
	
	/** This method returns the coding system
	 *  @return the coding system SOCcer used in the analysis.  Usually {@link AssignmentCodingSystem#SOC2010} */
	public AssignmentCodingSystem getCodingSystem() {
		return codingSystem;
	}
	
	/** @return the TableModel for the soccerResultsTable*/
	public SOCcerResultsTableModel getTableModel(){
		return tableModel;
	}

	/** @return the TableModel for the singleJobDescriptionTable */
	public SingleSOCcerResultTableModel getTop10Model(){
		return singleJobDescriptionTableModel;
	}
	
	/** This method returns the Occupation code for the row in the singleJobDescriptionTable 
	 * @return an OccupationCode for the row index. */
	public OccupationCode getOccupationCodeForTop10Row(int row){
		return codingSystem.getOccupationalCode( singleJobDescriptionTableModel.getSOCforRow(row) );
	}
	
	/** @return the ListModel for the JList assignentList */
	public AssignmentListModel getAssignmentListModel() {
		return assignmentListModel;
	}

	public Assignments getAssignmentsForRow(int rowIndex){
		return assignmentListMap.get(getRowIdForRowIndex(rowIndex)) ;
	}

	/** @return the ListModel for the JList  */
	public ListModel getSingleJobDescriptionListModel() {
		return singleJobDescriptionListModel;
	}

	/** sets the SOCcerResults and notifies all the components that the results were updated. */
	public void setResults(SOCcerResults results){
		resetModel();
		this.results=results;

		
		// the results should have changed or else an exception was thrown
		// and we would not reach this point.
		// the user could have re-selected the file, if that is the case,  the
		// data is reloaded.
		
		logger.finer("resets updated..");
		tableModel.changeResults(results);
		
		setCodingSystem(results.getCodingSystem());
	}

	public void updateFlag(int rowIndex,FlagType flag) {
		try {
			Assignments assignments=assignmentListMap.get(getRowIdForRowIndex(rowIndex));
			assignments.setFlag(flag);
			dao.updateFlag(assignments);
		} catch (SQLException e) {

		}
	}
	
	public void setNewDB(String fileName) throws IOException{
		File dbFile=new File(fileName);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		if (dao==null) dao=SOCAssignDAOFactory.getDAO();
		try{
			dao.connect(fileName);
			dao.fillResultsTable(results);
		}catch (SQLException sqle){
			throw new IOException(sqle);
		}
	}

	public void loadPreviousWork(File dbFile) throws IOException{
		resetModel();
		
		if (dao==null) dao=SOCAssignDAOFactory.getDAO();
		try{
			dao.connect(dbFile.getAbsolutePath());
			results=dao.readResults();
			// now the assignments...
			dao.loadAssignments(assignmentListMap, codingSystem.getCodingSystem());
			
			logger.finer("results updated from database...");
			tableModel.changeResults(results);
			setCodingSystem(results.getCodingSystem());
			


		}catch (SQLException sqle){
			throw new IOException(sqle);
		}
	}
	public boolean isCodingSystemSpecifiedInResults(){
		if (results==null) return false;

		return (results.getCodingSystem()!=null);
	}
	
	public void setSelectedResult(int selectedResults){
		this.selectedResultsRow=selectedResults;
		String[] row=results.getRow(selectedResults);
		singleJobDescriptionTableModel.changeResults(row);
		singleJobDescriptionListModel.clear();
		singleJobDescriptionListModel.addElement("Row: "+row[0]);
		singleJobDescriptionListModel.addElement("Job Title: "+row[1]);
		singleJobDescriptionListModel.addElement("SIC: "+row[2]+" ("+AssignmentCodingSystem.SIC1987.lookup(row[2])+")" );
		singleJobDescriptionListModel.addElement("Job Task: "+row[3]);
		assignmentListModel.resetList(getAssignmentsForSelectedRow());
	}
	
	public void addAssignment(OccupationCode codeAssigned){
		assignmentListModel.addAssignment(codeAssigned);
		updateAssignmentTable();
	}
	public void addSelection(String socAssignment){
		assignmentListModel.addSelection(socAssignment);
		updateAssignmentTable();
	}
	public void removeElementAt(int indx){
		assignmentListModel.removeElementAt(indx);
		updateAssignmentTable();
	}
	public void increaseSelection(int selectedIndex){
		assignmentListModel.increaseSelection(selectedIndex);;
		updateAssignmentTable();
	}
	public void decreaseSelection(int selectedIndex){
		assignmentListModel.decreaseSelection(selectedIndex);;
		updateAssignmentTable();
	}
	
	public void resetModel(){
		assignmentListMap.clear();
		singleJobDescriptionListModel.clear();
		singleJobDescriptionTableModel.clear();
		assignmentListModel.clear();
	}
	
	private void updateAssignmentTable(){
		try {
			dao.setAssignment(assignmentListModel.getAssignments());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int getRowIdForRowIndex(int rowIndex){
//		System.out.println("ROWINDEX -> "+rowIndex+"  "+tableModel.getValueAt(rowIndex, 1));
		return (Integer)tableModel.getValueAt(rowIndex, 1);
	}
	private int getSelectedRowId(){
		return (Integer)tableModel.getValueAt(selectedResultsRow, 1);
	}
	public void exportAssignments(File file) throws IOException{
		SOCAssignResultsExporter.exportResultsToCSV(results, assignmentListMap, file);
	}
	
	public void onExit(){
		try {
			logger.finer("closing the database.");
			if (dao!=null) {
				dao.close();
			}
		} catch (IOException e) {

		}
	}	
}
