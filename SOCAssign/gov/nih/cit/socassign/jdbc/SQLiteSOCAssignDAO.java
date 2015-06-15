package gov.nih.cit.socassign.jdbc;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.Assignments.FlagType;
import gov.nih.cit.socassign.SOCcerResults;
import gov.nih.cit.socassign.codingsysten.CodingSystem;
import gov.nih.cit.socassign.codingsysten.OccupationCode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SQLiteSOCAssignDAO implements SOCAssignDAO {

	private static Properties sqlCommands=new Properties();

	static{
		try {
			Class.forName("org.sqlite.JDBC");
			sqlCommands.load( SQLiteSOCAssignDAO.class.getResourceAsStream("sqlite_jdbc.properties") );
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	private Connection connection=null;
	
	/**
	 * create an instance of SOCAssignDAO which uses SQLite as for storage.
	 */
	public SQLiteSOCAssignDAO() {
		
	}

	@Override
	public void connect(String filename) throws SQLException{
		// if the connection is not null, close it
		// before you open a new connection...
		String connectionURL="jdbc:sqlite:"+filename;
		if (connection!=null) {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		connection=DriverManager.getConnection(connectionURL);

		createNewAssignmentsTable();
	}

	private String[] readHeadFromTable() throws SQLException{
		int headLength=-1;
		PreparedStatement getNumberOfColumns=connection.prepareStatement(sqlCommands.getProperty("get.number.of.columns"));
		ResultSet rs=getNumberOfColumns.executeQuery();
		if (rs.next()){
			headLength=rs.getInt(1);
		}
		if (headLength<=0) throw new SQLException("could not determine the HEAD size.");
		getNumberOfColumns.close();
		
		PreparedStatement getValue=connection.prepareStatement(sqlCommands.getProperty("get.head"));
		String[] head=new String[headLength];
		rs=getValue.executeQuery();
		while (rs.next()){
			head[rs.getInt(1)-1]=rs.getString(2);
		}
		getValue.close();
		
		return head;
	}

	@Override
	public SOCcerResults readResults() throws SQLException{
		String[] head=readHeadFromTable();
		List<String[]> results=readResultsFromTable();
		String sysname=head[head.length-2];
		sysname=sysname.substring(0,sysname.indexOf('_'));
		AssignmentCodingSystem codingSystem=AssignmentCodingSystem.valueOf(sysname);
	
		return new SOCcerResults(head, results,codingSystem);
	}


	private List<String[]> readResultsFromTable() throws SQLException{
	
		PreparedStatement getSize=connection.prepareStatement(sqlCommands.getProperty("get.results.size"));
		ResultSet rs=getSize.executeQuery();
		int rowSize=-1;
		int colSize=-1;
		if (rs.next()){
			rowSize=rs.getInt(1);
			colSize=rs.getInt(2);
		}
		if (rowSize<=0 || colSize<=0) throw new SQLException("could not determine the data size.");
		
		List<String[]> list=Arrays.asList(new String[rowSize+1][colSize+1]);
				
		PreparedStatement getValue=connection.prepareStatement(sqlCommands.getProperty("get.all.results"));
		rs=getValue.executeQuery();
		while (rs.next()){
			list.get(rs.getInt(1))[rs.getInt(2)]=rs.getString(3);
		}
		
		return list;
	}

	@Override
	public void fillResultsTable(SOCcerResults results) throws SQLException{
		connection.setAutoCommit(false);
		createHeadTable(results.getHead());
		createResultsTable(results.getData());
		connection.commit();
		connection.setAutoCommit(true);
	}
	
	private void createHeadTable(String[] head) throws SQLException{
		PreparedStatement createHead=connection.prepareStatement(sqlCommands.getProperty("create.head.table"));
		createHead.executeUpdate();
		
		PreparedStatement setValue=connection.prepareStatement(sqlCommands.getProperty("set.head"));


		for (int i=1;i<=head.length;i++){
			setValue.setInt(1, i);setValue.setString(2, head[i-1]);
			setValue.addBatch();
		}
		setValue.executeBatch();

		setValue.close();
	}
	public void createNewAssignmentsTable() throws SQLException{
		PreparedStatement createTable=connection.prepareStatement(sqlCommands.getProperty("create.assignments.table"));
		createTable.executeUpdate();
		createTable.close();
	}


	@Override
	public void loadAssignments(Map<Integer, Assignments>codeMap,CodingSystem system) throws SQLException{
		PreparedStatement getAssignment=connection.prepareStatement(sqlCommands.getProperty("get.all.assignments"));
		ResultSet rs=getAssignment.executeQuery();
		while (rs.next()){
			int row=rs.getInt(1);

			List<OccupationCode> codeList=new ArrayList<OccupationCode>();
			for (int i=2;i<=4;i++){
				OccupationCode code=system.getOccupationalCode(rs.getString(i));
				if (code != null) codeList.add(code);
			}
			
			FlagType flag=FlagType.values()[rs.getInt(5)];
			Assignments assignments=new Assignments(row, codeList,flag);			
			codeMap.put(row, assignments);
		}

		rs.close();
		getAssignment.close();
	}
	
	private void createResultsTable(List<String[]> data) throws SQLException{
		int batchCounter=0;
		int batchSize=100;

		PreparedStatement createTable=connection.prepareStatement(sqlCommands.getProperty("create.results.table"));
		createTable.executeUpdate();
		createTable.close();

		PreparedStatement setValue=connection.prepareStatement(sqlCommands.getProperty("set.results"));

		for (int rowIndx=0;rowIndx<data.size();rowIndx++){
			setValue.setInt(1, rowIndx);
			String[] row=data.get(rowIndx);
			for (int colIndx=0;colIndx<row.length;colIndx++){
				setValue.setInt(2, colIndx);
				setValue.setString(3, row[colIndx]);
				setValue.addBatch();

				if ( ++batchCounter % batchSize == 0) {
					setValue.executeBatch();
					setValue.setInt(1, rowIndx);
				}
			}

		}
		setValue.executeBatch();
		setValue.close();
	}

	@Override
	public void setAssignment(Assignments assignments) throws SQLException{
		String code1=(assignments.size()>0)?assignments.get(0).getName():"";
		String code2=(assignments.size()>1)?assignments.get(1).getName():"";
		String code3=(assignments.size()>2)?assignments.get(2).getName():"";
		
		PreparedStatement rowExists=connection.prepareStatement(sqlCommands.getProperty("find.row.in.assignments.table"));
		rowExists.setInt(1, assignments.getRowId());
		ResultSet rs=rowExists.executeQuery();
		PreparedStatement assignCodes;
		if (rs.next()){
			assignCodes=connection.prepareStatement(sqlCommands.getProperty("update.row.in.assignments.table"));
			assignCodes.setString(1, code1);
			assignCodes.setString(2, code2);
			assignCodes.setString(3, code3);
			assignCodes.setInt(4, assignments.getRowId());
		} else{
			assignCodes=connection.prepareStatement(sqlCommands.getProperty("add.row.to.assignments.table") );
			assignCodes.setInt(1, assignments.getRowId());
			assignCodes.setString(2, code1);
			assignCodes.setString(3, code2);
			assignCodes.setString(4, code3);
			assignCodes.setInt(5, assignments.getFlag().ordinal());
		}
		assignCodes.executeUpdate();		
	}
	
	@Override
	public void updateFlag(Assignments assignments) throws SQLException{
		String code1=(assignments.size()>0)?assignments.get(0).getName():"";
		
		PreparedStatement rowExists=connection.prepareStatement(sqlCommands.getProperty("find.row.in.assignments.table"));
		rowExists.setInt(1, assignments.getRowId());
		ResultSet rs=rowExists.executeQuery();
		PreparedStatement updateFlag;

		// if the assignment is in the database...
		if (rs.next()){
			if (code1.length()==0 && assignments.getFlag()==FlagType.NOT_FLAGGED){
				// if there is no occupational code assigned AND it is NOT FLAGGED (now).  Just remove it from the DB...
				updateFlag=connection.prepareStatement(sqlCommands.getProperty("delete.assignments") );
				updateFlag.setInt(1, assignments.getRowId());
			} else {
				// otherwise update the row.
				updateFlag=connection.prepareStatement(sqlCommands.getProperty("update.flag.for.row.in.assignment.table"));
				updateFlag.setInt(1, assignments.getFlag().ordinal());
				updateFlag.setInt(2, assignments.getRowId());
			}
		}else{
			// No assigment exists, so add a blank assignment with the flag set...
			updateFlag=connection.prepareStatement(sqlCommands.getProperty("add.row.to.assignments.table") );
			updateFlag.setInt(1, assignments.getRowId());
			updateFlag.setString(2, "");
			updateFlag.setString(3, "");
			updateFlag.setString(4, "");
			updateFlag.setInt(5, assignments.getFlag().ordinal());
		}
		updateFlag.executeUpdate();
	}
	
	@Override
	public void close() throws IOException {
		if (connection!=null)
			try {
				connection.close();
			} catch (SQLException e) {
				throw new IOException(e);
			}
	}
}
