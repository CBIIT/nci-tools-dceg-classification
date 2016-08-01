package gov.nih.cit.socassign.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.cit.socassign.jdbc.SQLiteSOCAssignDAO;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.junit.Test;

public class TestDBUpdate {

	@Test
	public void test() throws Exception{
		Properties sqlCommands=new Properties();
		
		File dbFile = new File("/Volumes/druss$/Dan-today.1.db");
		assertTrue("The db file doesn't exist....",dbFile.exists());

		String connectionURL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
		Connection connection= null;
		try{
			Class.forName("org.sqlite.JDBC");
			sqlCommands.load(SQLiteSOCAssignDAO.class.getResourceAsStream("sqlite_jdbc.properties"));
			
			connection=DriverManager.getConnection(connectionURL);
			assertNotNull("The connection is null! ",connection);


			
			String sql=sqlCommands.getProperty("assignment.table.column.names");
			PreparedStatement ps=connection.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();			
			boolean old=true;
			while (rs.next()){
				if (rs.getString(2).equalsIgnoreCase("comment")) old=false;
			}
			if (old){
				sql=sqlCommands.getProperty("add.comments.column");
				connection.prepareStatement(sql).executeUpdate();
			}
		
		} finally{
			if (connection!=null) connection.close();
		}
	}

}
