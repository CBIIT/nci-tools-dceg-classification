package gov.nih.cit.socassign.jdbc;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.SOCcerResults;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SoccerResultsLoadTest {

	@Test
	public void createAndSaveResults() throws Exception{

		
		String[] head="Id,JobTitle,SIC,JobTask,SOC2010_1,Score_1,SOC2010_2,Score_2,SOC2010_3,Score_3,SOC2010_4,Score_4,SOC2010_5,Score_5,SOC2010_6,Score_6,SOC2010_7,Score_7,SOC2010_8,Score_8,SOC2010_9,Score_9,SOC2010_10,Score_10".split(",");
		String[][] data={
				{"10","salesman","5139","travelling sales","41-2031","0.56","41-4012","0.45","41-4011","0.30","41-1011","0.204","41-9091","0.060","43-4181","0.059","41-9022","0.052","43-5111","0.0514","41-9099","0.045","41-2022","0.045"},
				{"20","outside cartman","7992","keep track of golf carts","41-2021","0.0340","41-2022","0.0308","49-9096","0.02689","27-2021","0.02536","49-9098","0.019","49-9041","0.01927","37-3011","0.01775","35-3011","0.0177","43-3061","0.014","35-3041","0.0140"},				
				{"30","traning for ins","6300","learn underwritting and general","53-4041","0.00657","27-2042","0.0050","33-2021","0.005","51-9199","0.0048","27-3012","0.00479","39-2011","0.00479","27-2011","0.00436","27-2012","0.0043","11-3121","0.0042","33-3051","0.004197"},
				{"40","ins agent","6411","insurance agent","41-3021","0.650911494686326","43-9041","0.0519","13-2052","0.0344","43-4181","0.032","13-1031","0.0317","43-3011","0.026342","41-3011","0.02615","13-1011","0.0261548","13-2061","0.0261","43-4131","0.0253"}
		};
		
		SOCcerResults results=new SOCcerResults(head, Arrays.asList(data), AssignmentCodingSystem.SOC2010);
		Assert.assertEquals(4, results.size());

		SOCAssignDAO dao = SOCAssignDAOFactory.getDAO();
//		File f1=File.createTempFile("DAOTest", ".db");
//		f1.deleteOnExit();
		File f1=new File("/tmp/myTest.db");
		if (f1.exists()) f1.delete();
		
		dao.connect(f1.getAbsolutePath());
		dao.fillResultsTable(results);
		SOCcerResults daoResults=dao.readResults();

		
		List<String[]> res=daoResults.getData();
		Assert.assertEquals(data[0][0], res.get(0)[0]);
		Assert.assertEquals(data[1][0], res.get(1)[0]);
		Assert.assertEquals(data[2][0], res.get(2)[0]);
		Assert.assertEquals(data[3][0], res.get(3)[0]);
		
		Assert.assertEquals(results.size(), daoResults.size());
		
		dao.close();
	}

}
