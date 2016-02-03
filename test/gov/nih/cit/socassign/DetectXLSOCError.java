package gov.nih.cit.socassign;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class DetectXLSOCError {

	@Test
	public void test() {
		URL url=DetectXLSOCError.class.getResource("nebcs-test.csv");
		Assert.assertNotNull("url is null", url);
		File f=new File(url.getFile());
		Assert.assertNotNull("unable to open file"+url.getFile(), f);
				
		try{
			SOCcerResults r=SOCcerResults.readSOCcerResultsFile(f);
			Assert.fail("exception not thrown.");
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

}
