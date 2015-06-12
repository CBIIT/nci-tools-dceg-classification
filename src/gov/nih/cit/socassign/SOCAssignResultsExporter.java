package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsysten.OccupationCode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * This class exports the results of SOCassign to a CSV File.
 * 
 * @author Daniel Russ, Stephen Ho
 *
 */
public class SOCAssignResultsExporter {
	
	private static Logger logger=Logger.getLogger(SOCAssignResultsExporter.class.getName());
	
	private static void writeHead(SOCcerResults results, CSVWriter writer){

		String[] soccerResultsHead=results.getHead();
		int soccerJobDescriptionLength=soccerResultsHead.length-20;
		String[] socassignResultsHead=new String[soccerJobDescriptionLength+3];
		System.arraycopy(soccerResultsHead, 0, socassignResultsHead, 0, soccerJobDescriptionLength);
		for (int i=1;i<=3;i++){
			socassignResultsHead[soccerJobDescriptionLength+i-1]=results.getCodingSystem().toString()+"_"+i;
		}
		
		logger.finer(Arrays.toString(socassignResultsHead));
		writer.writeNext(socassignResultsHead);
	}


	private static void writeData(String[] inputData, Assignments codes,CSVWriter writer){
		String[] line=new String[inputData.length+3];
		System.arraycopy(inputData, 0, line, 0, inputData.length);
		Arrays.fill(line, inputData.length, line.length, "");
		
		for (int i=0;i<Math.min(codes.size(), 3);i++){
			if (codes.get(i)!=null){
				line[i+inputData.length]=codes.get(i).getName();
			} 
		}
		writer.writeNext(line);
		logger.finer("Writing "+Arrays.toString(line));
	}
	
	public static void exportResultsToCSV(SOCcerResults results, 
			Map<Integer, Assignments> assignedResults, File file) throws IOException{ 
		
		CSVWriter writer=new CSVWriter(new FileWriter(file));		

		int soccerJobDescriptionLength=results.getHead().length-20;
		String[] inputData=new String[soccerJobDescriptionLength];
		
		// write the head..
		writeHead(results, writer);

		// write the data ONLY if there are SOCassign Results...
		List<Integer> keys=new ArrayList<Integer>(assignedResults.keySet());
		for (Integer key:keys){
			Assignments assignedCodes=assignedResults.get(key);
			if (assignedCodes.size()>0 && assignedCodes.get(0)!=null){
				System.out.println(" ==== "+ Arrays.toString(results.getRowId(key) ) +"====");
				System.arraycopy( results.getRowId(key), 0, inputData, 0, soccerJobDescriptionLength);
				writeData(inputData,assignedCodes,writer);
			}
		}
		
		writer.close();
	}
	
	public static Map<Integer, List<OccupationCode>> importResultsFromCSV(File file) throws IOException{
		Map<Integer, List<OccupationCode>> map=new TreeMap<Integer, List<OccupationCode>>();
		CSVReader reader=new CSVReader(new FileReader(file));
		String[] header=reader.readNext();
		String sysname=header[header.length-1];
		sysname=sysname.substring(0,sysname.indexOf('_'));
		AssignmentCodingSystem codingSystem=AssignmentCodingSystem.valueOf(sysname);
		
		List<String[]> rows=reader.readAll();

		
		for (String[] row:rows){
			List<OccupationCode> codes=new ArrayList<OccupationCode>();
			for (int i=row.length-3;i<row.length;i++){
				if (row[i].length()>0){
					codes.add(codingSystem.getOccupationalCode(row[i]));
				}
				map.put(Integer.parseInt(row[0]), codes);
			}
		}
		reader.close();
		return map;
	}
}
