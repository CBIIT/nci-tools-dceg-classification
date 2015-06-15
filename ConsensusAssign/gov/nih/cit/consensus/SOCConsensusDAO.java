package gov.nih.cit.consensus;

import gov.nih.cit.socassign.Assignments;
import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.codingsysten.OccupationCode;
import gov.nih.cit.soccer.input.SoccerInput;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import au.com.bytecode.opencsv.CSVReader;

public class SOCConsensusDAO {

	public SOCConsensusDAO() {}

	public static AssignmentCodingSystem getCodingSystem(File file) throws IOException{
		String[] header = getHeader(file);
		String cs = header[header.length-2];
		return AssignmentCodingSystem.valueOf(cs.substring(0, cs.indexOf('_')));
	}
	
	public static String[] getHeader(File file) throws IOException {
		CSVReader reader=new CSVReader(new FileReader(file));
		String[] header=reader.readNext();
		reader.close();
		return Arrays.copyOfRange(header, 1, header.length);
	}
	
	public static List<DataRow> loadAssignmentsFromCSV(File file) throws IOException{
		CSVReader reader=new CSVReader(new FileReader(file));
		List<String[]> results=reader.readAll();
		reader.close();
		
		ListIterator<String[]> iterator=results.listIterator();
		// read the header...
		String[] row=iterator.next();
		int startIndex = row.length-3;
		String cs = row[row.length-2];
		System.out.println("loading CS: "+cs.substring(0, cs.indexOf('_')));
		AssignmentCodingSystem codingSystem=AssignmentCodingSystem.valueOf(cs.substring(0, cs.indexOf('_')));
		if (codingSystem==null) throw new IOException("invalid coding system... "+cs.substring(0, cs.indexOf('_')));
		SoccerInput.RowParser parser = new SoccerInput.RowParser(row);
		List<DataRow> dataRows = new ArrayList<DataRow>();
		while (iterator.hasNext()){
			row=iterator.next();
			// not enough items on a row or nothing assigned...
			if (row.length<4 || row[startIndex].trim().length()==0) {
				iterator.remove();
				continue;
			}

			OccupationCode[] codes=new OccupationCode[3];
			int rowId=Integer.parseInt(row[0]);
			for (int i=0;i<3;i++){
				if (row[startIndex+i].trim().length()==0){
					break;
				}
				codes[i]=codingSystem.getOccupationalCode(row[startIndex+i]);
			}
			SoccerInput input = parser.build(row);
			dataRows.add(new DataRow(input, new Assignments(rowId, codes)));
		}
		return dataRows;
	}
	
	public static List<String[]> loadCSV(File file) throws IOException{
		CSVReader reader=new CSVReader(new FileReader(file));
		List<String[]> results=reader.readAll();
		reader.close();
		
		ListIterator<String[]> iterator=results.listIterator();
		// read the header...
		String[] row=iterator.next();
		
		while (iterator.hasNext()){
			row=iterator.next();
			// not enough items on a row or nothing assigned...
			if (row.length!=4 || row[1].trim().length()==0) {
				iterator.remove();
				continue;
			}

		}
		return results;
	}
}
