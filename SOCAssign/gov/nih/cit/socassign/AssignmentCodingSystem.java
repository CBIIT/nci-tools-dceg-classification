package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsysten.CodingSystem;
import gov.nih.cit.socassign.codingsysten.OccupationCode;

import java.util.List;
import java.util.regex.Pattern;

/**
 * An enum that contains all the known Coding Systems.  Prevents creation
 * of multiple instances of a CodingSystem.
 * 
 * @author Daniel Russ
 *
 */
public enum AssignmentCodingSystem {
	SOC2010("\\d{2}-\\d{4}","soc2010.xml"),ISCO2008("\\d{0,4}","isco2008.xml"),SIC1987("\\d{4}","sic1987.xml");

	private Pattern pattern;
	private CodingSystem codingSystem;
	
	private AssignmentCodingSystem(String regex,String xmlFilename) {
		pattern=Pattern.compile(regex);
		try {
			this.codingSystem=CodingSystem.loadSystem(xmlFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean matches(String code){
		return pattern.matcher(code).matches();
	}
	
	public CodingSystem getCodingSystem() {
		return codingSystem;
	}
	
	public OccupationCode getOccupationalCode(String code) {
		return codingSystem.getOccupationalCode(code);
	}

	
	public String lookup(String code) {
		return codingSystem.lookup(code);
	}

	public List<OccupationCode> getListOfCodes() {
		return codingSystem.getListofCodes();
	}
	public List<String> getListOfCodesAsStrings(){
		return codingSystem.getListOfCodesAsStrings();
	}

	public List<OccupationCode> getListOfCodesAtLevel(String level) {
		return codingSystem.getListOfCodesAtLevel(level);
	}
	public int getIndexOfCodeAtLevel(OccupationCode code) {
		return codingSystem.getIndexOfCodeAtLevel(code);
	}
	public OccupationCode getRoot(){
		return codingSystem.getRoot();
	}
}
