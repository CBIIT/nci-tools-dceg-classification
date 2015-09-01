package gov.nih.cit.soccer.input;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;

public class SoccerInput {
	
	private double[] jaccardScores;
	
	private final EnumMap<ValidJobDescriptionTypes, String> inputMap;
	
	public SoccerInput(EnumMap<ValidJobDescriptionTypes, String> jobDescriptionMap){
		this.inputMap=jobDescriptionMap;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		//private String jobTitle, industryCode, jobTasks, tools;
		EnumMap<ValidJobDescriptionTypes, String> map=new EnumMap<ValidJobDescriptionTypes, String>(ValidJobDescriptionTypes.class);
		
		public Builder() {}
		
		public Builder jobTitle(String jobTitle) {
			map.put(ValidJobDescriptionTypes.JobTitle, jobTitle);
			return this;
		}
		
		public Builder industryCode(String industryCode) {
			map.put(ValidJobDescriptionTypes.SIC, industryCode);
			return this;
		}
		
		public Builder jobTasks(String jobTasks) {
			map.put(ValidJobDescriptionTypes.JobTask, jobTasks);
			return this;
		}
		
		public Builder tools(String tools) {
			map.put(ValidJobDescriptionTypes.Tool, tools);
			return this;
		}

		public SoccerInput build() {
			return new SoccerInput(map);
		}
	}
	
	public static class RowParser {
		
		private ValidJobDescriptionTypes[] jobDescriptionType;
		
		public RowParser(String[] headers) {
			jobDescriptionType=new ValidJobDescriptionTypes[headers.length-3];
			for (int i=1;i<headers.length-3;i++){
				jobDescriptionType[i]=ValidJobDescriptionTypes.valueOf(headers[i]);
			}
		}
		
		public SoccerInput build(String[] row) {
			EnumMap<ValidJobDescriptionTypes, String> soccerInputMap=new EnumMap<ValidJobDescriptionTypes, String>(ValidJobDescriptionTypes.class);
			for (int i=1; i<row.length-3; i++) {
				soccerInputMap.put(jobDescriptionType[i], row[i]);
			}
			return new SoccerInput(soccerInputMap);
		}
	}

	public double[] getJaccardScores() {
		return jaccardScores;
	}

	public void setJaccardScores(double[] jaccardScores) {
		this.jaccardScores = jaccardScores;
	}

	public String getJobTitle() {
		return inputMap.get(ValidJobDescriptionTypes.JobTitle);
	}

	public String getIndustryCode() {
		return inputMap.get(ValidJobDescriptionTypes.SIC);
	}

	public String getJobTasks() {
		return inputMap.get(ValidJobDescriptionTypes.JobTask);
	}

	public String getTools() {
		return inputMap.get(ValidJobDescriptionTypes.Tool);
	}

	public int getSize() {
		return inputMap.size();
	}

	public String get(ValidJobDescriptionTypes jobDescriptionType){
		return inputMap.get(jobDescriptionType);
	}
	public boolean has(ValidJobDescriptionTypes jobDescriptionType){
		return inputMap.containsKey(jobDescriptionType);
	}
	public String[] headers(){
		String[] headers=new String[inputMap.size()];
		int indx=0;
		for (ValidJobDescriptionTypes type:inputMap.keySet()){
			headers[indx++]=type.toString();
		}
		return headers;
	}
	public Iterator<ValidJobDescriptionTypes> typeIterator(){
		return inputMap.keySet().iterator();
	}
	
	@Override
	public String toString() {
		return "SoccerInput [" + inputMap+ "  "+ Arrays.toString(jaccardScores) + "]";
	}
	
}
