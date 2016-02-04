package gov.nih.cit.socassign.jdbc.test;

import gov.nih.cit.socassign.SOCcerResults;
import gov.nih.cit.socassign.jdbc.SOCAssignDAO;
import gov.nih.cit.socassign.jdbc.SOCAssignDAOFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class IOTest {

	private static Connection connection;
	private static File dbFile;
	private static Properties sqlCommands=new Properties();
	
	@BeforeClass
	/*
	 * Create a connection to the file /tmp/temp.db...
	 */
	public static void connect() throws Exception{
		Class.forName("org.sqlite.JDBC");
		sqlCommands.load(IOTest.class.getResourceAsStream("sqlite_jdbc.properties") );
		dbFile=File.createTempFile("IOTest", ".db");
		connection=DriverManager.getConnection("jdbc:sqlite:"+dbFile.getAbsolutePath());
	}
	
	/*
	 * close the connection and delete the file...
	 */
	@AfterClass
	public static void close() throws Exception{
		connection.close();
		dbFile.delete();
	}
	
	@Test
	public void assignmentsTest() throws Exception{
		PreparedStatement createTable=connection.prepareStatement(sqlCommands.getProperty("create.assignments.table"));
		createTable.executeUpdate();
		createTable.close();
		
		PreparedStatement addRow=connection.prepareStatement(sqlCommands.getProperty("add.row.to.assignments.table"));
		addRow.setInt(1, 4);
		addRow.setString(2, "11-1011");
		addRow.setString(3, "11-1000");
		addRow.setString(4, "11-0000");
		addRow.executeUpdate();
		addRow.close();
		
		PreparedStatement getRow=connection.prepareStatement(sqlCommands.getProperty("find.row.in.assignments.table"));
		getRow.setInt(1, 1);
		ResultSet rs=getRow.executeQuery();
		if (rs.next()){
			Assert.fail("Did not place row 1.");
		}
		getRow.setInt(1, 4);
		rs=getRow.executeQuery();
		Assert.assertTrue("Could not find row 4, which we filled!!!",rs.next());
		Assert.assertEquals("11-1011",rs.getString(2));
		Assert.assertEquals("11-1000",rs.getString(3));
		Assert.assertEquals("11-0000",rs.getString(4));
		getRow.close();
		
	}

	@Test
	public void resultsTest() throws Exception{
		PreparedStatement createTable=connection.prepareStatement(sqlCommands.getProperty("create.results.table"));
		createTable.executeUpdate();
		createTable.close();
		
		String[] s={"","1","assembler","3540","stuff"};
		PreparedStatement setValue=connection.prepareStatement(sqlCommands.getProperty("set.results"));
		setValue.setInt(1, 1);
		for (int i=1;i<s.length;i++){
			setValue.setInt(2, i);
			setValue.setString(3, s[i]);
			setValue.addBatch();
		}

		setValue.executeBatch();
		setValue.close();
		PreparedStatement getValue=connection.prepareStatement(sqlCommands.getProperty("get.results"));
		getValue.setInt(1,1);
		ResultSet rs=getValue.executeQuery();

		while(rs.next()){
			int col=rs.getInt(1);String value=rs.getString(2);
			Assert.assertEquals(s[col],value);
		}
		
		getValue.close();
	}
	
	@Test
	public void headTest() throws Exception{
		PreparedStatement createTable=connection.prepareStatement(sqlCommands.getProperty("create.head.table"));
		createTable.executeUpdate();
		createTable.close();

		String[] s={"","Id","JobTitle","SIC","JobTask"};
		PreparedStatement setValue=connection.prepareStatement(sqlCommands.getProperty("set.head"));
		
		for (int i=1;i<s.length;i++){
			setValue.setInt(1, i);setValue.setString(2, s[i]);
			setValue.addBatch();
		}
		
		setValue.executeBatch();
		setValue.close();
		
		PreparedStatement getValue=connection.prepareStatement(sqlCommands.getProperty("get.head"));
		ResultSet rs=getValue.executeQuery();
		while (rs.next()){
			int col=rs.getInt(1);
			Assert.assertEquals(s[col], rs.getString(2));
		}
		getValue.close();
	}
	
	
	@Test
	public void testDAO() throws Exception {
		SOCcerResults results=SOCcerResults.readSOCcerResultsFile(new File(this.getClass().getResource("testResults.csv").toURI()));
		String[] head1=results.getHead();
		
		SOCAssignDAO dao=SOCAssignDAOFactory.getDAO();
		File dbFile=new File("/tmp/mydb.db");
		if (dbFile.exists()) dbFile.delete();
		
		dao.connect(dbFile.getAbsolutePath());
		dao.fillResultsTable(results);
		
		SOCcerResults res2=dao.readResults();
		String[] head2=res2.getHead();
		Assert.assertArrayEquals(head1, head2);
		
		List<String[]> resultsList=results.getData();
		List<String[]> res2List=res2.getData();
		Assert.assertEquals(resultsList.size(), res2List.size());
		for (int row=0;row<resultsList.size();row++){
			Assert.assertArrayEquals(resultsList.get(row), res2List.get(row));
		}
		dao.close();
	}
	
	@Test
	public void testRowCount() throws Exception{
		/*
		String[][] codes={
				{"41-2031","41-4012","41-9011","41-1011","41-9091","43-4181","41-9022","43-5111","41-9099","41-2022"},
				{"41-2021","41-2022","49-9096","27-2021","49-9098","49-9041","37-3011","35-3011","43-3061","35-3041"},
				{"53-4041","27-2042","33-2021","51-9199","27-3012","39-2011","27-2011","27-2012","11-3121","33-3051"},
				{"41-3021","43-9041","13-2052","43-4181","13-1031","43-3011","41-3011","13-1011","13-2061","43-4131"}
		};
		double[][] score={
				{0.56,0.45,0.3,0.204,0.060,0.059,0.0052,0.0514,0.045,0.041},
				{0.0340,0.0308,0.02689,0.02536,0.019,0.01927,0.01775,0.0177,0.014,0.0140},
				{0.00657,0.0050,0.005,0.0048,0.00479,0.00479,0.00436,0.0043,0.0042,0.004197},
				{0.650911494686326,0.0519,0.0344,0.032,0.0317,0.026342,0.02615,0.0261548,0.0261,0.0253}
		};
		String[][] jobDescription={
				{"5","salesman","5139","travelling sales"},
				{"10","outside cartman","7992","keep track of golf carts"},
				{"15","traning for ins","6300","learn underwritting and general"},
				{"20","ins agent","6411","insurance agent"}
		};
		*/	
	}
}
