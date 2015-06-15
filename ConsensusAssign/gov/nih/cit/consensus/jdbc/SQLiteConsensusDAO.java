package gov.nih.cit.consensus.jdbc;

import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.codingsysten.OccupationCode;
import gov.nih.cit.soccer.input.SoccerInput;
import gov.nih.cit.soccer.input.ValidJobDescriptionTypes;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class SQLiteConsensusDAO implements Closeable{
	private static Properties sqlCommands=new Properties();
	static{
		try {
			Class.forName("org.sqlite.JDBC");
			sqlCommands.load( SQLiteConsensusDAO.class.getResourceAsStream("sqlite-jdbc.properties") );
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private AssignmentCodingSystem assCodingSys;
	public SQLiteConsensusDAO() {
		this(AssignmentCodingSystem.SOC2010);
	}
	
	public SQLiteConsensusDAO(AssignmentCodingSystem assCodingSys) {
		this.assCodingSys = assCodingSys;
	}

	Connection connection;

	public void connect(String filename) throws SQLException{
		if (connection!=null){
			connection.close();
		}
		connection=DriverManager.getConnection("jdbc:sqlite:"+filename);
	}


	public void fillSoccerInputTable(Map<Integer, SoccerInput> currentReviewerInput,Map<Integer, SoccerInput> previousInput) throws SQLException{		
		PreparedStatement createHeaderTable=connection.prepareStatement(sqlCommands.getProperty("create.soccer.header.table"));
		createHeaderTable.execute();
		createHeaderTable.close();
		PreparedStatement createInputTable=connection.prepareStatement(sqlCommands.getProperty("create.soccer.input.table"));
		createInputTable.execute();
		createInputTable.close();

		PreparedStatement fillSoccerInputRow = connection.prepareStatement(sqlCommands.getProperty("set.soccer.input.value"));

		boolean firstRow=previousInput.isEmpty();
		for (Integer rowId:currentReviewerInput.keySet()){
			// don't write the value of the SOCcerInput to the database if it already is there!
			if (previousInput.containsKey(rowId)) continue;
			
			SoccerInput input=currentReviewerInput.get(rowId);
			if (firstRow){
				PreparedStatement fillHeaderPS = connection.prepareStatement(sqlCommands.getProperty("set.soccer.input.headers"));

				// Fill the header...
				String[] header=input.headers();
				for (int indx=0;indx<header.length;indx++){
					fillHeaderPS.setInt(1, indx+1); // fill the columnIndx.  Column 0 is the RowId, so we need to shift over by 1
					fillHeaderPS.setString(2, header[indx]); // fill the value of the header...
					fillHeaderPS.addBatch();
				}
				fillHeaderPS.executeBatch();

				// don;t fill the header next time through the loop...
				firstRow=false;

				fillHeaderPS.close();
			}

			Iterator<ValidJobDescriptionTypes> iterator=input.typeIterator();
			int columnId=1;
			int counter=0;
			while ( iterator.hasNext()){
				fillSoccerInputRow.setInt(1, rowId);
				fillSoccerInputRow.setInt(2, columnId++);
				fillSoccerInputRow.setString(3, input.get(iterator.next()));
				fillSoccerInputRow.addBatch();

				if (++counter % 100 == 0){
					fillSoccerInputRow.executeBatch();
				}
			}
		}
		fillSoccerInputRow.executeBatch();
		fillSoccerInputRow.close();
	}
	
	public void fillAssignmentsTable(TreeMap<Integer,TreeMap<Integer,Assignments>> map) throws SQLException{
		PreparedStatement createAssignmentsPS=connection.prepareStatement(sqlCommands.getProperty("create.assignments.table"));
		createAssignmentsPS.execute();
		createAssignmentsPS.close();
		PreparedStatement fillAssignmentRow=connection.prepareStatement(sqlCommands.getProperty("fill.assignment.table"));
		
		int counter=0;
		for (Integer rowId:map.keySet()){
			TreeMap<Integer, Assignments> assignmentMap=map.get(rowId);
			for (Integer coderId:assignmentMap.keySet()){
				Assignments assignment=assignmentMap.get(coderId);
				fillAssignmentRow.setInt(1, rowId);
				fillAssignmentRow.setInt(2, coderId);
				for (int i=0;i<3;i++){
					if (assignment.getCode(i)==null) break;
					fillAssignmentRow.setInt(3, i);
					fillAssignmentRow.setString(4, assignment.getCode(i).getName());
					fillAssignmentRow.addBatch();
					
					if ( ++counter % 100 == 0 ) fillAssignmentRow.executeBatch();
				}
			}
		}
		fillAssignmentRow.executeBatch();
		fillAssignmentRow.close();
	}
	
	public void addAssignments(TreeMap<Integer,TreeMap<Integer,Assignments>> map, int coderId) throws SQLException {
		PreparedStatement fillAssignmentRow=connection.prepareStatement(sqlCommands.getProperty("fill.assignment.table"));
		int counter=0;
		for (Integer rowId:map.keySet()) {
			TreeMap<Integer, Assignments> assignmentMap=map.get(rowId);
			if (assignmentMap.containsKey(coderId)) {
				Assignments assignment=assignmentMap.get(coderId);
				fillAssignmentRow.setInt(1, rowId);
				fillAssignmentRow.setInt(2, coderId);
				for (int i=0;i<3;i++){
					if (assignment.getCode(i)==null) break;
					fillAssignmentRow.setInt(3, i);
					fillAssignmentRow.setString(4, assignment.getCode(i).getName());
					fillAssignmentRow.addBatch();
					
					if ( ++counter % 100 == 0 ) fillAssignmentRow.executeBatch();
				}
			}
		}
		fillAssignmentRow.executeBatch();
		fillAssignmentRow.close();
	}

	public void fillConsensusTable(TreeMap<Integer,String[]> map) throws SQLException {
		PreparedStatement createConsensusPS = connection.prepareStatement(sqlCommands.getProperty("create.consensus.table"));
		createConsensusPS.execute();
		createConsensusPS.close();
		
		int counter = 0;
		PreparedStatement fillConsensus = connection.prepareStatement(sqlCommands.getProperty("fill.consensus.table"));
		for (Integer rowId: map.keySet()) {
			fillConsensus.setInt(1, rowId);
			for (int i=1; i<=Math.min(2, map.get(rowId).length); i++) {
				fillConsensus.setInt(2, i-1);
				fillConsensus.setString(3, (map.get(rowId)[i-1]!=null)?map.get(rowId)[i-1]:"");
				fillConsensus.addBatch();
				
				if (++counter % 100 == 0) fillConsensus.executeBatch();
			}
		}
		fillConsensus.executeBatch();
		fillConsensus.close();
	}

	public void fillCodingSystemTable(AssignmentCodingSystem assCodingSys) throws SQLException {
		// drop the table if it exists
		PreparedStatement dropTableStmt = connection.prepareStatement(sqlCommands.getProperty("drop.codingsystem.table"));
		dropTableStmt.execute();
		dropTableStmt.close();
		
		// re-create the table
		PreparedStatement createTableStmt = connection.prepareStatement(sqlCommands.getProperty("create.codingsystem.table"));
		createTableStmt.execute();
		createTableStmt.close();
		
		// fill the table
		PreparedStatement fillStmt = connection.prepareStatement(sqlCommands.getProperty("fill.codingsystem"));
		fillStmt.setString(1, assCodingSys.name());
		fillStmt.addBatch();
		fillStmt.executeBatch();
		fillStmt.close();
	}
	
	public AssignmentCodingSystem readCodingSystem() throws SQLException {
		PreparedStatement getCodeSysStmt = connection.prepareStatement(sqlCommands.getProperty("get.codingsystem"));
		ResultSet rs = getCodeSysStmt.executeQuery();
		if (rs.next()) {
			AssignmentCodingSystem assCodingSys = AssignmentCodingSystem.valueOf(rs.getString(1));
			return assCodingSys;
		} else {
			throw new RuntimeException("No coding system exists!");
		}
	}

	public void addReviewer(List<String> reviews) throws SQLException{
		Statement statement=connection.createStatement();
		statement.executeUpdate(sqlCommands.getProperty("drop.coder.table"));
		statement.executeUpdate(sqlCommands.getProperty("create.coder.table"));
		statement.close();

		PreparedStatement addReviewPS=connection.prepareStatement(sqlCommands.getProperty("fill.coder.table"));
		for (int i=0;i<reviews.size();i++){
			addReviewPS.setInt(1, i);
			addReviewPS.setString(2, reviews.get(i));
			addReviewPS.addBatch();
		}
		addReviewPS.executeBatch();
	}

	@Override
	public void close() throws IOException {
		try {
			if (connection!=null) connection.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	public TreeMap<Integer, SoccerInput> readSoccerInputTable() throws SQLException {
		TreeMap<Integer, SoccerInput> soccerInputMap = new TreeMap<Integer, SoccerInput>();
		
		PreparedStatement getRowIDsStmt = connection.prepareStatement(sqlCommands.getProperty("get.soccer.input.uniqueRowID"));
		ResultSet rsRowID = getRowIDsStmt.executeQuery();
		
		PreparedStatement getSoccerInputStmt = connection.prepareStatement(sqlCommands.getProperty("get.soccer.input.specifyRowID"));
		while (rsRowID.next()) {
			int rowID = rsRowID.getInt(1);
			getSoccerInputStmt.setInt(1, rowID);
			ResultSet rs = getSoccerInputStmt.executeQuery();
			EnumMap<ValidJobDescriptionTypes, String> jobDecpMap = new EnumMap<ValidJobDescriptionTypes, String>(ValidJobDescriptionTypes.class);
			while (rs.next()) {
				jobDecpMap.put(ValidJobDescriptionTypes.valueOf(rs.getString(2)), rs.getString(3));
			}
			SoccerInput input = new SoccerInput(jobDecpMap);
			soccerInputMap.put(rowID, input);
			rs.close();
		}
		rsRowID.close();
		getSoccerInputStmt.close();
		getRowIDsStmt.close();
		return soccerInputMap;
	}
	
	private List<Integer> getIntList(String sqlStmt) throws SQLException {
		List<Integer> integers = new ArrayList<Integer>();
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			integers.add(rs.getInt(1));
		}
		rs.close();
		stmt.close();
		return integers;
	}
	
	public TreeMap<Integer,TreeMap<Integer,Assignments>> readAssignmentsTable() throws SQLException {
		TreeMap<Integer, TreeMap<Integer, Assignments>> rowAssignmentMap = new TreeMap<Integer, TreeMap<Integer, Assignments>>();
		List<Integer> allRowIDs = getIntList(sqlCommands.getProperty("get.assignment.uniqueRowID"));
		List<Integer> allCoders = getIntList(sqlCommands.getProperty("get.assignment.uniqueCoder"));
		PreparedStatement queryStmt = connection.prepareStatement(sqlCommands.getProperty("get.assignments.specific"));
		for (Integer rowID: allRowIDs) {
			if (!rowAssignmentMap.containsKey(rowID)) {
				rowAssignmentMap.put(rowID, new TreeMap<Integer, Assignments>());
			}
			TreeMap<Integer, Assignments> assignmentMap = rowAssignmentMap.get(rowID);
			for (Integer coder: allCoders) {
				List<OccupationCode> assignedOccCodes = new ArrayList<OccupationCode>();
				queryStmt.setInt(1, rowID);
				queryStmt.setInt(2, coder);
				ResultSet rs = queryStmt.executeQuery();
				while (rs.next()) {
					assignedOccCodes.add(assCodingSys.getOccupationalCode(rs.getString(2).trim()));
				}
				rs.close();
				if (assignedOccCodes.size()>0) {
					assignmentMap.put(coder, new Assignments(rowID, assignedOccCodes));	
				}
			}
			rowAssignmentMap.put(rowID, assignmentMap);
		}
		return rowAssignmentMap;
	}
	
	public List<String> readReviewers() throws SQLException {
		List<String> reviewers = new ArrayList<String>();
		PreparedStatement getCoderStmt = connection.prepareStatement(sqlCommands.getProperty("get.coder.table"));
		ResultSet rs = getCoderStmt.executeQuery();
		while (rs.next()) {
			reviewers.add(rs.getString(1).trim());
		}
		rs.close();
		getCoderStmt.close();
		return reviewers;
	}
	
	public TreeMap<Integer, String[]> readConsensusTable() throws SQLException {
		TreeMap<Integer, String[]> consensusMap = new TreeMap<Integer, String[]>();
		
		PreparedStatement getConsensusStmt = connection.prepareStatement(sqlCommands.getProperty("get.consensus.table"));
		ResultSet rs = getConsensusStmt.executeQuery();
		while (rs.next()) {
			int rowID = rs.getInt(1);
			int consensusNum = rs.getInt(2);
			String consensusCode = rs.getString(3);
			
			if (!consensusMap.containsKey(rowID)) {
				String[] emptyArray = new String[]{null, null};
				consensusMap.put(rowID, emptyArray);
			} else {
				String[] existingArray = consensusMap.get(rowID);
				if ((existingArray.length==1) && (consensusNum==1)) {
					String[] newArray = new String[]{existingArray[0], null};
					consensusMap.put(rowID, newArray);
				}
			}
			consensusMap.get(rowID)[consensusNum] = consensusCode;
		}
		
		return consensusMap;
	}
	
	/**
	 * Update the consensus value. the consensusIndex MUST be 0 or 1. Corresponding
	 * to the first and second choice for the consensus.
	 * @throws SQLException  
	 */
	public void updateConsensusValue(int rowId,int consensusIndex, String value) throws SQLException {
		if (value==null) value="";
		
		PreparedStatement checkExistsStmt = connection.prepareStatement(sqlCommands.getProperty("check.consensus.exists"));
		checkExistsStmt.setInt(1, rowId);
		checkExistsStmt.setInt(2, consensusIndex);
		ResultSet existsRS = checkExistsStmt.executeQuery();
		boolean exists = existsRS.next();
		existsRS.close();
		checkExistsStmt.close();
		
		PreparedStatement updateStmt;
		if (exists) {
			updateStmt = connection.prepareStatement(sqlCommands.getProperty("update.consensus"));
			updateStmt.setString(1, value);
			updateStmt.setInt(2, rowId);
			updateStmt.setInt(3, consensusIndex);
			updateStmt.addBatch();
		} else {
			updateStmt = connection.prepareStatement(sqlCommands.getProperty("insert.consensus"));
			updateStmt.setInt(1, rowId);
			updateStmt.setInt(2, consensusIndex);
			updateStmt.setString(3, value);
			updateStmt.addBatch();
		}
		updateStmt.executeBatch();
		updateStmt.close();
		
		PreparedStatement removeBlankStmt = connection.prepareStatement(sqlCommands.getProperty("remove.blank.consensus"));
		removeBlankStmt.execute();
		removeBlankStmt.close();
	}
}
