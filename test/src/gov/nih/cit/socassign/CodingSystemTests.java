
package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsystem.CodingSystem;

import org.junit.Assert;
import org.junit.Test;

public class CodingSystemTests {

	@Test
	public void testCodingSystemLoad() throws Exception{
		CodingSystem codingSystem=CodingSystem.loadSystem(CodingSystem.class.getResourceAsStream("soc2010.xml"));
		Assert.assertNotNull(codingSystem);
		
		Assert.assertEquals("SOC2010",codingSystem.getName());
	}

	
	public void testAssignmentCodingSystem() throws Exception{
		AssignmentCodingSystem soc2010=AssignmentCodingSystem.SOC2010;
		Assert.assertEquals("11-1011", soc2010.getOccupationalCode("11-1011").getName());
		Assert.assertEquals("SOC2010", soc2010.getCodingSystem().getName());
	}
}
