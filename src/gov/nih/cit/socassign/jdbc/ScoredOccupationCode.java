package gov.nih.cit.socassign.jdbc;

import java.util.ArrayList;
import java.util.List;

public class ScoredOccupationCode {

	String code;
	double score;


	public ScoredOccupationCode(String code, double score) {
		this.code=code;
		this.score=score;
	}

	public String getCode() {
		return code;
	}
	public double getScore() {
		return score;
	}

	static List<ScoredOccupationCode> makeListOfScoredOccupationCodes(String[] data){
		List<ScoredOccupationCode> codes=new ArrayList<ScoredOccupationCode>(10);
		for (int i=0;i<data.length;i+=2){
			codes.add(new ScoredOccupationCode(data[i], Double.parseDouble( data[i+1] )));
		}
		return codes;
	}
}


