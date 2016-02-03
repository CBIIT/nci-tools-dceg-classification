package gov.nih.cit.consensus.test;

import gov.nih.cit.consensus.DataRow;
import gov.nih.cit.consensus.SOCConsensusDAO;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LoadSOCAssignCSVTest {

	@Test
	public void test() throws Exception{
		List<String[]> results=SOCConsensusDAO.loadCSV(new File("/tmp/test.csv"));
		for (String[] result:results){
			System.out.println(Arrays.toString(result));
		}
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void test1() throws Exception{
		List<String[]> results=SOCConsensusDAO.loadCSV(new File("/tmp/test1.csv"));
		Assert.assertEquals(6, results.size());
	}
	@Test
	public void test1a() throws Exception{
		List<DataRow> results=SOCConsensusDAO.loadAssignmentsFromCSV(new File("/tmp/test1.csv"));
		
		Assert.assertEquals(5, results.size());
		Assert.assertEquals("51-2022",results.get(0).getAssignments().getCode(0).getName());
		Assert.assertNull(results.get(0).getAssignments().getCode(1));
		
	}

}
