package gov.nih.cit.consensus;

import gov.nih.cit.consensus.jdbc.SQLiteConsensusDAO;
import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.soccer.input.SoccerInput;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class SQLiteDAOTest {

	private List<String> createReviewers(){
		return Arrays.asList("Dan","Stephen");
	}
	
	private TreeMap<Integer,SoccerInput> createSoccerInputMap(){
		TreeMap<Integer,SoccerInput> soccerInputMap=new TreeMap<Integer, SoccerInput>();
		soccerInputMap.put(1, SoccerInput.newBuilder().jobTitle("Job Title-1878").industryCode("code-1878").build());
		soccerInputMap.put(3, SoccerInput.newBuilder().jobTitle("Job Title-2000").industryCode("code-2000").build());
		soccerInputMap.put(5, SoccerInput.newBuilder().jobTitle("Job Title-2010").industryCode("code-2010").build());
		soccerInputMap.put(7, SoccerInput.newBuilder().jobTitle("Job Title-2012").industryCode("code-2012").build());
		return soccerInputMap;
	}
	
	private TreeMap<Integer,TreeMap<Integer,Assignments>> createAssignmentMap() {
		AssignmentCodingSystem cs=AssignmentCodingSystem.SOC2010;
		
		TreeMap<Integer,TreeMap<Integer,Assignments>> rowAssignmentsMap=new TreeMap<Integer, TreeMap<Integer,Assignments>>();
		// both coder 1 and 2 assigned values to rowId 1878
		TreeMap<Integer, Assignments> rowId1=new TreeMap<Integer, Assignments>();
		rowId1.put(1, new Assignments(1878, cs.getOccupationalCode("13-1111"),cs.getOccupationalCode("19-1013")));
		rowId1.put(2, new Assignments(1878,cs.getOccupationalCode("19-1041")));
		rowAssignmentsMap.put(1878, rowId1);
	
		// both coder 1 and 2 assigned values to rowId 2000
		TreeMap<Integer, Assignments> rowId2000=new TreeMap<Integer, Assignments>();
		rowId2000.put(1, new Assignments(2000,cs.getOccupationalCode("29-1021")));
		rowId2000.put(2, new Assignments(2000,cs.getOccupationalCode("27-1021"), cs.getOccupationalCode("29-1021") ));
		rowAssignmentsMap.put(2000, rowId2000);
		
		// coder 2 assigned a value to row 2010
		TreeMap<Integer, Assignments> rowId2010=new TreeMap<Integer, Assignments>();
		rowId2010.put(2, new Assignments(2010,cs.getOccupationalCode("21-2099")));
		rowAssignmentsMap.put(2010, rowId2010);
		
		// coder 1 and 2 assigned a value to row 2012
		TreeMap<Integer, Assignments> rowId2012=new TreeMap<Integer, Assignments>();
		rowId2012.put(1, new Assignments(2012,cs.getOccupationalCode("17-2071")));
		rowId2012.put(2, new Assignments(2012,cs.getOccupationalCode("17-2071")));
		rowAssignmentsMap.put(2012, rowId2012);
		
		return rowAssignmentsMap;
	}

	private TreeMap<Integer,String[]> createConsensusMap(){
		TreeMap<Integer,String[]> consensusMap=new TreeMap<Integer, String[]>();
		consensusMap.put(1878, new String[]{"13-1111"});
		consensusMap.put(2000, new String[]{"29-1021","27-1021"});
		consensusMap.put(2010, new String[]{"21-2099"});
		return consensusMap;
	}

	@Test
	public void testSoccerInputIO() throws Exception{
		File db=File.createTempFile("testDB",".sqlite-db");
		db.deleteOnExit();
		
		TreeMap<Integer,SoccerInput> soccerInputMap=createSoccerInputMap();
		TreeMap<Integer, SoccerInput> priorMap=new TreeMap<Integer, SoccerInput>();
		
		SQLiteConsensusDAO dao=new SQLiteConsensusDAO();
		dao.connect(db.getAbsolutePath());
		dao.fillSoccerInputTable(soccerInputMap,priorMap);
		
		TreeMap<Integer, SoccerInput> returnedMap=dao.readSoccerInputTable();
		
		Assert.assertNotNull("The SOCcerInput map returned from the DAO is null", returnedMap);
		Assert.assertEquals(4, returnedMap.size());
		for (Integer key:soccerInputMap.keySet()){
			SoccerInput original=soccerInputMap.get(key);
			SoccerInput returned=returnedMap.get(key);
			Assert.assertNotNull("RowID "+key+" not saved",returned);
			Assert.assertEquals("The number of features are not the same in RowId "+key,original.getSize(), returned.getSize());			
			Assert.assertEquals("The JobTitles are not the same in RowID "+key,original.getJobTitle(), returned.getJobTitle());
			Assert.assertEquals("The industryCode are not the same in RowID "+key,original.getIndustryCode(), returned.getIndustryCode());
		}
		
		
		dao.close();
		db.delete();
	}
	
	@Test 
	public void testReviewerIO() throws Exception{	
		File db=File.createTempFile("testDB",".sqlite-db");
		db.deleteOnExit();
		
		List<String> reviewers=createReviewers();
		SQLiteConsensusDAO dao=new SQLiteConsensusDAO();

		dao.connect(db.getAbsolutePath());
		dao.addReviewer(reviewers);
		
		List<String> returnedList=dao.readReviewers();
		
		Assert.assertNotNull("The reviewer list returned by the DAO is null",returnedList);
		Assert.assertEquals("the number of reviewers returned by the DAO is not the same as the number of reviewers.",reviewers.size(), returnedList.size());
		for (int i=0;i<reviewers.size();i++){
			Assert.assertEquals("Reviewer "+i+" is not the same in the original list and the returned list",reviewers.get(i), returnedList.get(i));
		}
		
		dao.close();
		db.delete();
	}
	
	@Test
	public void testRowAssignmentIO() throws Exception{
		File db=File.createTempFile("testDB",".sqlite-db");
		db.deleteOnExit();

		TreeMap<Integer,TreeMap<Integer,Assignments>> rowAssignmentsMap=createAssignmentMap();
	
		SQLiteConsensusDAO dao=new SQLiteConsensusDAO();

		dao.connect(db.getAbsolutePath());
		dao.fillAssignmentsTable(rowAssignmentsMap);

		TreeMap<Integer,TreeMap<Integer,Assignments>> returnedMap=dao.readAssignmentsTable();
		Assert.assertNotNull("The assignment map returned from the DAO is null");
		for (Integer rowId:rowAssignmentsMap.keySet()){
			TreeMap<Integer, Assignments> originalReviewers=rowAssignmentsMap.get(rowId);
			TreeMap<Integer, Assignments> returnedReviewers=returnedMap.get(rowId);
			Assert.assertEquals("For row ID "+rowId+" the number of reviewers returned is different",originalReviewers.size(), returnedReviewers.size());
			for (Integer reviewID:originalReviewers.keySet()){
				Assignments orignialAssignments=originalReviewers.get(reviewID);
				Assignments returnedAssignment=returnedReviewers.get(reviewID);
				Assert.assertEquals("Row Id "+rowId+" reviewer "+reviewID+ " Assignment 0 is different",orignialAssignments.getCode(0), returnedAssignment.getCode(0));
				Assert.assertEquals("Row Id "+rowId+" reviewer "+reviewID+ " Assignment 1 is different",orignialAssignments.getCode(1), returnedAssignment.getCode(1));
				Assert.assertEquals("Row Id "+rowId+" reviewer "+reviewID+ " Assignment 2 is different",orignialAssignments.getCode(2), returnedAssignment.getCode(2));
			}
		}
		
		dao.close();
		db.delete();
	}
	
	@Test
	public void testConsensusIO() throws Exception{
		File db=File.createTempFile("testDB",".sqlite-db");
		db.deleteOnExit();
		
		TreeMap<Integer,String[]> consensusMap=createConsensusMap();
		
		SQLiteConsensusDAO dao=new SQLiteConsensusDAO();

		dao.connect(db.getAbsolutePath());
		dao.fillConsensusTable(consensusMap);
		
		TreeMap<Integer, String[]> returnedMap=dao.readConsensusTable();
		Assert.assertNotNull("The consensus map returned from the DAO is null",returnedMap);
		for (Integer rowId:consensusMap.keySet()){
			String[] original=consensusMap.get(rowId);
			String[] returned=returnedMap.get(rowId);
			Assert.assertArrayEquals("The consensus map for RowId: "+rowId+" is different.",original, returned);
		}
		
		dao.close();
		db.delete();
	}

	@Test
	public void testUpdateConsensusValue() throws Exception{
		File db=File.createTempFile("testDB",".sqlite-db");
		db.deleteOnExit();

		
		// setup my objects
		TreeMap<Integer,SoccerInput> soccerInputMap=createSoccerInputMap();
		
		TreeMap<Integer,TreeMap<Integer,Assignments>> rowAssignmentsMap=new TreeMap<Integer, TreeMap<Integer,Assignments>>();
		TreeMap<Integer,String[]> consensusMap=createConsensusMap();
		TreeMap<Integer, SoccerInput> priorMap=new TreeMap<Integer, SoccerInput>();
		
		//connect to the db..
		SQLiteConsensusDAO dao=new SQLiteConsensusDAO();
		dao.connect(db.getAbsolutePath());
		dao.fillSoccerInputTable(soccerInputMap,priorMap);
		dao.fillAssignmentsTable(rowAssignmentsMap);
		dao.fillConsensusTable(consensusMap);
		
		// Row 2012 does not have a value in the consensus table...
		// add one.
		dao.updateConsensusValue(2012, 0, "17-2071");
		// and a second value to row 1878 "19-1013" 
		dao.updateConsensusValue(1878, 1, "19-1013");
		
		// check the value are what we expect.
		TreeMap<Integer, String[]> newConsensusMap=dao.readConsensusTable();
		String[] values=newConsensusMap.get(2012);
		Assert.assertArrayEquals(new String[]{"17-2071"}, values);
		values=newConsensusMap.get(1878);
		Assert.assertArrayEquals(new String[]{"13-1111","19-1013"}, values);

		// one last test check that nulls are handled ok...
		dao.updateConsensusValue(2012, 0, null);
		dao.updateConsensusValue(1878, 0, "");
		dao.updateConsensusValue(1878, 0, "19-1013");
		dao.updateConsensusValue(1878, 1, "");
		newConsensusMap=dao.readConsensusTable();
		values=newConsensusMap.get(2012);
		// either: values is null OR length=0 OR (length=1 and is Null or blank)		
		Assert.assertTrue("No Value should be in rowId==2012 either the value should be null or length 0 or "+
				"length 1 AND the consensus value is null or empty"
				, values==null || values.length==0 || (values.length==1 && (values[0]==null||values[0].length()==0) ));
		// for 1878 either the length is 1 OR the length is 2, but the second value is blank or null.  The first value is 19-1013.
		values=newConsensusMap.get(1878);
		Assert.assertEquals("19-1013", values[0]);
		if (values.length==2){
			Assert.assertTrue(values[1]==null || values[1].length()==0);
		}
		
		
		
		//shutdown...
		dao.close();		
		db.delete();
	}
}
