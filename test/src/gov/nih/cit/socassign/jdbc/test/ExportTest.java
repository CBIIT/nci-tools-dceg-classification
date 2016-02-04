package gov.nih.cit.socassign.jdbc.test;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.SOCAssignResultsExporter;
import gov.nih.cit.socassign.SOCcerResults;
import gov.nih.cit.socassign.codingsystem.OccupationCode;
import gov.nih.cit.socassign.jdbc.SOCAssignDAO;
import gov.nih.cit.socassign.jdbc.SOCAssignDAOFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class ExportTest {

	private SOCcerResults loadTestData() throws IOException{
		return loadTestData( new File("/Users/druss/Documents/workspace-fr/SOCAssign/test/gov/nih/cit/socassign/jdbc/TestIO.csv"));	
	}
	private SOCcerResults loadTestData(File f) throws IOException{
		return SOCcerResults.readSOCcerResultsFile(f);
	}

	@Test
	public void exportAndImportWorks() throws IOException{
		SOCcerResults results=loadTestData();
		
		File f1=File.createTempFile("exportImportTest", ".csv");
		f1.deleteOnExit();

		
		Map<Integer, Assignments> assignedResults=new TreeMap<Integer, Assignments>();
		
		AssignmentCodingSystem cs=AssignmentCodingSystem.SOC2010;
		

		assignedResults.put(3, new Assignments(1, cs.getOccupationalCode("41-2031"),cs.getOccupationalCode("41-4012")) );
		assignedResults.put(4, new Assignments(2, cs.getOccupationalCode("53-4021")) );
		
		SOCAssignResultsExporter.exportResultsToCSV(results, assignedResults, f1);
		
		
		Map<Integer,List<OccupationCode>> readResults=SOCAssignResultsExporter.importResultsFromCSV(f1);
		
		List<OccupationCode> codes=readResults.get(3);
		Assert.assertEquals(cs.getOccupationalCode("41-2031"), codes.get(0));
		Assert.assertEquals(cs.getOccupationalCode("41-4012"), codes.get(1));
		
		codes=readResults.get(4);
		Assert.assertEquals(cs.getOccupationalCode("53-4021"), codes.get(0));
		
	}
	@Test
	public void daoWorks() throws Exception{
		SOCcerResults results=loadTestData();
		
		SOCAssignDAO dao = SOCAssignDAOFactory.getDAO();
		File f1=File.createTempFile("DAOTest", ".db");
		f1.deleteOnExit();
		
		
		dao.connect(f1.getAbsolutePath());
		dao.fillResultsTable(results);
		SOCcerResults daoResults=dao.readResults();
		dao.close();
		
		List<String[]> origList=results.getData();
		List<String[]> daoList=daoResults.getData();
		Assert.assertEquals(origList.size(), daoList.size());
		for (int i=0;i<origList.size();i++){
			String[] origValue=origList.get(i);
			String[] daoValue=daoList.get(i);
			Assert.assertEquals(origValue.length, daoValue.length);
			for (int j=0;j<origValue.length;j++){
				Assert.assertEquals(origValue[j], daoValue[j]);
			}
		}
	}

}

