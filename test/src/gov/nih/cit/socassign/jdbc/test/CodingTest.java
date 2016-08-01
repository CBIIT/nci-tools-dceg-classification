package gov.nih.cit.socassign.jdbc.test;

import static org.junit.Assert.assertNotNull;
import gov.nih.cit.socassign.AssignmentCodingSystem;

import org.junit.Test;

public class CodingTest {

	@Test
	public void test() {
		AssignmentCodingSystem soc2010=AssignmentCodingSystem.SOC2010;
		assertNotNull(soc2010.getOccupationalCode("99-9999"));
		assertNotNull(soc2010.getOccupationalCode("99-9998"));
	}

}
