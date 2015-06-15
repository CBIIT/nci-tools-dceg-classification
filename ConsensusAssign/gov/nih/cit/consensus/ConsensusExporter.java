package gov.nih.cit.consensus;

import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.soccer.input.SoccerInput;
import gov.nih.cit.soccer.input.ValidJobDescriptionTypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class ConsensusExporter {

	private List<ValidJobDescriptionTypes> getFields(SoccerInput input) {
		List<ValidJobDescriptionTypes> fields = new ArrayList<ValidJobDescriptionTypes>(input.getSize());
		for (ValidJobDescriptionTypes type:ValidJobDescriptionTypes.values()){
			if (input.has(type)) fields.add(type);
		}
		return fields;
	}
	
	public void export(List<DataRow> exportedRows, File outputFile) throws IOException {
		if (exportedRows==null || exportedRows.size()==0) return;
		
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile));

		// Get the head from the first line. and write the line to the file..
		List<ValidJobDescriptionTypes> fieldType = getFields(exportedRows.get(0).getInput());
		List<String> headers = new ArrayList<String>();
		headers.add("RowID"); 		   
		for (ValidJobDescriptionTypes type:fieldType){
			headers.add(type.toString());
		}		
		headers.add("Consensus_1");    headers.add("Consensus_2");
		writer.writeNext(headers.toArray(new String[0]));
		
		// for each datarow ... write out the data...
		for (DataRow dataRow: exportedRows) {
			// put everything into a list that we will write out to file.
			List<String> rowToWrite = new ArrayList<String>();

			// first the SoccerInput...
			SoccerInput data = dataRow.getInput();
			rowToWrite.add(Integer.toString(dataRow.getAssignments().getRowId()));
			for (ValidJobDescriptionTypes field: fieldType) {
				rowToWrite.add(data.get(field));
			}
			
			// then the consensus assignments...
			Assignments selectedCodes = dataRow.getAssignments();
			rowToWrite.add(selectedCodes.getCode(0).getName());
			if (selectedCodes.getCode(1)==null) {
				rowToWrite.add("");
			} else {
				rowToWrite.add(dataRow.getAssignments().getCode(1).getName());
			}
			
			// everything is ready... write it out... next...
			writer.writeNext(rowToWrite.toArray(new String[0]));
		}
		
		writer.close();
	}
}
