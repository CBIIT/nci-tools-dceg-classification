package gov.nih.cit.consensus;

import gov.nih.cit.consensus.jdbc.SQLiteConsensusDAO;
import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.codingsysten.OccupationCode;
import gov.nih.cit.soccer.input.SoccerInput;
import gov.nih.cit.soccer.input.ValidJobDescriptionTypes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class ConsensusTableModel extends AbstractTableModel {

	public static final String CODING_SYSTEM_CHANGED_KEY="coding.system";

	List<String> reviewerNames=new ArrayList<String>();

	AssignmentCodingSystem codingSystem=null;
	TreeMap<Integer,TreeMap<Integer,Assignments>> rowAssignmentsMap=new TreeMap<Integer, TreeMap<Integer,Assignments>>();
	TreeMap<Integer,String[]> consensusMap=new TreeMap<Integer, String[]>();
	TreeMap<Integer,SoccerInput> soccerInputMap=new TreeMap<Integer, SoccerInput>();
	@SuppressWarnings("unchecked")
	TreeMap<Integer, Assignments>[] tableData=(TreeMap<Integer,Assignments>[])new TreeMap<?,?>[0];
	private int numInputCol = 0;
	private String[] inputColNames;
	private SQLiteConsensusDAO sqliteDAO=null;

	public ConsensusTableModel(){}
	
	public void loadFromDatabase(File dbFile) throws SQLException {
		// instantiate a new SQLiteDAO, regardless if it is null or not
		sqliteDAO=new SQLiteConsensusDAO();
		sqliteDAO.connect(dbFile.getAbsolutePath());
		
		soccerInputMap = sqliteDAO.readSoccerInputTable();
		rowAssignmentsMap = sqliteDAO.readAssignmentsTable();
		consensusMap = sqliteDAO.readConsensusTable();
		reviewerNames = sqliteDAO.readReviewers();
		
		@SuppressWarnings("unchecked")
		TreeMap<Integer, Assignments>[] tmp=(TreeMap<Integer,Assignments>[])rowAssignmentsMap.values().toArray(new TreeMap<?,?>[rowAssignmentsMap.size()]);
		tableData=tmp;
		if (soccerInputMap.size()>0) {
			SoccerInput firstInput = soccerInputMap.firstEntry().getValue();
			numInputCol = firstInput.getSize();
			inputColNames = firstInput.headers();
		}
		
		codingSystem=sqliteDAO.readCodingSystem();
		pcs.firePropertyChange(CODING_SYSTEM_CHANGED_KEY, null, codingSystem);
		
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	public void addReviewerAssignment(File reviewerAssignmentsFile) throws IOException{
		addReviewerAssignment(null,reviewerAssignmentsFile);
	}
	
	public void addReviewerAssignment(String reviewerName, File reviewerAssignmentsFile) throws IOException{

		TreeMap<Integer,SoccerInput> inputMap=new TreeMap<Integer, SoccerInput>();
		
		if (reviewerName==null) reviewerName="Coder-"+(reviewerNames.size()+1);		
		reviewerNames.add(reviewerName);
		int reviewerIndx=reviewerNames.size();
		System.out.println("Adding reviewer: "+reviewerName+" as reviewer "+reviewerIndx);

		// only do this stuff if we are on the first reviewer...
		if (codingSystem==null){
			inputColNames = SOCConsensusDAO.getHeader(reviewerAssignmentsFile);
			codingSystem=SOCConsensusDAO.getCodingSystem(reviewerAssignmentsFile);
			pcs.firePropertyChange(CODING_SYSTEM_CHANGED_KEY, null, codingSystem);
		}
		
		List<DataRow> reviewerDataRows = SOCConsensusDAO.loadAssignmentsFromCSV(reviewerAssignmentsFile);

		// create a database table

		// for each job title that was assigned by a review
		// get the list of assignments for that job title (or create it if it does not exist)
		// the list (currentList) is the list of all reviewer assignment for a job title
		// add the assignment to the list 
		for (DataRow dataRow:reviewerDataRows){
			Assignments assignment = dataRow.getAssignments();
			TreeMap<Integer,Assignments> currentList=rowAssignmentsMap.get(dataRow.getRowId());
			if (currentList==null) {
				currentList=new TreeMap<Integer, Assignments>();
				rowAssignmentsMap.put(dataRow.getRowId(), currentList);
			}
			currentList.put(reviewerIndx,assignment);

			SoccerInput soccerInput = dataRow.getInput();
			inputMap.put(dataRow.getRowId(), soccerInput);

			if (numInputCol==0) {
				numInputCol = soccerInput.getSize();
			}
		}

		
		@SuppressWarnings("unchecked")
		TreeMap<Integer, Assignments>[] tmp=(TreeMap<Integer,Assignments>[])rowAssignmentsMap.values().toArray(new TreeMap<?,?>[rowAssignmentsMap.size()]);
		tableData=tmp;

		fireTableStructureChanged();
		// create a new dao if it does not already exist.
		if (sqliteDAO==null){
			sqliteDAO=new SQLiteConsensusDAO();
			try {
				File dbFile=new File(reviewerAssignmentsFile.getAbsolutePath()+".db");
				if (dbFile.exists()) dbFile.delete();
				sqliteDAO.connect(dbFile.getAbsolutePath());
				
				sqliteDAO.fillCodingSystemTable(codingSystem);
				sqliteDAO.fillAssignmentsTable(rowAssignmentsMap);
				sqliteDAO.fillConsensusTable(consensusMap);
			} catch (SQLException e) {
				throw new IOException(e);
			}
		} else {
			try {
				sqliteDAO.addAssignments(rowAssignmentsMap, reviewerNames.size());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		try {
			sqliteDAO.fillSoccerInputTable(inputMap,soccerInputMap);
			sqliteDAO.addReviewer(reviewerNames);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		// put everything into the SoccerInputMap.
		soccerInputMap.putAll(inputMap);
	}

	@Override
	public int getColumnCount() {
		return numInputCol+3+3*reviewerNames.size();
	}

	@Override
	public String getColumnName(int column) {
		if (column==0){
			return "Row Id";
		}
		if (column<(numInputCol+1)){
			return inputColNames[column-1];
		}		
		if (column<(numInputCol+1+3*reviewerNames.size())){
			return reviewerNames.get( (column-numInputCol-1)/3 )+"-"+(((column-numInputCol-1) % 3)+1);
		}
		return "consensus-"+(column-numInputCol-3*reviewerNames.size());
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (reviewerNames.size()==0) return false;

		int consensusIndex= columnIndex-3*reviewerNames.size()-1-numInputCol;

		// this is not part of the consensus -- not editable
		if (consensusIndex<0) return false;


		// the first choice is always editable...
		if (consensusIndex==0) return true;

		int rowID = getRowIdFor(rowIndex);
		String[] consensus=consensusMap.get(rowID);
		System.out.println("("+rowIndex+", "+consensusIndex+") "+((consensus!=null)?"{"+consensus[consensusIndex-1]+", "+consensus[consensusIndex]+"}":"consensus is null"));
		return (consensus!= null && consensus[consensusIndex-1]!=null && consensus[consensusIndex-1].length()>0);
	}
	
	private int getRowIdFor(int rowIndex) {
		TreeMap<Integer,Assignments> assignmentMap=tableData[rowIndex];
		// column 0 is the row id...
		return assignmentMap.firstEntry().getValue().getRowId();
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		boolean nullValue=(aValue==null || aValue.toString().length()==0);
		int rowId=getRowIdFor(rowIndex);
		
		if (!nullValue){
			if (codingSystem.getOccupationalCode(aValue.toString())==null) return;
		}	
		

		int consensusIndex=columnIndex-3*reviewerNames.size()-1-numInputCol;
		System.out.println("ConsensusTableModel: setting consensus: "+(consensusIndex+1)+" to "+aValue+" for row: "+rowIndex);
		String[] row=consensusMap.get(rowId);
		if (row==null){
			row=new String[2];
			consensusMap.put(rowId, row);
		}


		if (nullValue) {
			// move consensus value 1 to 0 (can't have a second choice if you dont have a first)
			// right now there are only 2 possible consensus values.  If you add more, you need to move the entire array
			// starting from 1+consensusIndex and set the last to null.
			if (consensusIndex==0){
				row[0]=row[1];
				row[1]=null;
				
				try {
					sqliteDAO.updateConsensusValue(rowId, 0, row[0]);
					sqliteDAO.updateConsensusValue(rowId, 1, null);
				} catch (SQLException e) {
					// not sure what to do if this happens....
					e.printStackTrace();
				}
			}else{
				row[consensusIndex]=null;
			}
		}else{
			row[consensusIndex]=aValue.toString();
			// consensus 1 and 2 cannot be the same.
			if (row[0].equals(row[1])){
				row[1]=null;
				// no need to notify the table listeners that nothing changed
				return;
			}
			try {
				sqliteDAO.updateConsensusValue(rowId, consensusIndex, aValue.toString());
			} catch (SQLException e) {
				// still not sure what to do....
				e.printStackTrace();
			}
		}


		fireTableRowsUpdated(rowIndex, rowIndex+1);
	}

	@Override
	public int getRowCount() {
		return tableData.length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return (columnIndex==0)?Integer.class:String.class;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int rowID = getRowIdFor(rowIndex);
		if (columnIndex==0) return rowID;

		// return the input...
		if ( (columnIndex<numInputCol+1) ) {
			int idx = columnIndex-1;
			if (soccerInputMap.get(rowID)==null ){
				System.out.println("Hey There!!!");
			}
			String value = soccerInputMap.get(rowID).get(ValidJobDescriptionTypes.valueOf(inputColNames[idx]));
//			System.out.println(value);
//			try {
//				throw new Exception("sosad");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return value;
		}

		// return the coders assignment...
		if ( (columnIndex-1)<(numInputCol+3*reviewerNames.size()) ){
			TreeMap<Integer,Assignments> assignmentMap=tableData[rowIndex];
			// the coders start at 1 not 0
			Assignments assignment=assignmentMap.get( (columnIndex-1-numInputCol) /3 + 1);
			if (assignment==null) return "";

			return assignment.getCode( (columnIndex-1-numInputCol)%3 );			
		}

		// return the consensus
		String[] consensus=consensusMap.get(rowID);
		if (consensus==null) return "";
		int consensusIdx = columnIndex-3*reviewerNames.size()-1-numInputCol; 
		return (consensusIdx<consensus.length)?consensus[consensusIdx]:"";
	}

	public String[] getReviewerNames(){
		return reviewerNames.toArray(new String[reviewerNames.size()]);
	}



	PropertyChangeSupport pcs=new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public List<DataRow> getConsensusDataRows() {
		List<DataRow> dataRows = new ArrayList<DataRow>();
		for (int rowID: consensusMap.keySet()) {
			SoccerInput input = soccerInputMap.get(rowID);
			List<OccupationCode> selOccCodes = new ArrayList<OccupationCode>();
			for (String code: consensusMap.get(rowID)) {
				selOccCodes.add(codingSystem.getOccupationalCode(code));
			}
			Assignments assignment = new Assignments(rowID, selOccCodes);
			
			if (input == null ){
				System.out.println( selOccCodes +"\t" + assignment);
			}
			dataRows.add(new DataRow(input, assignment));
		}
		return dataRows;
	}
	
	public void onExit(){
		try {
			if (sqliteDAO!=null){
				System.out.println("... closing the db connection ...");
				sqliteDAO.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
