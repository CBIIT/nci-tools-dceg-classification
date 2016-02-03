package gov.nih.cit.socassign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.opencsv.CSVReader;

/**
 * This class holds the results of SOCcer along with the components of the JobDescription,
 * and the coding system used by SOCcer.  Currently, only SOC2010 is supported, however, the codes is
 * ready to handle ISCO when data is available.
 *
 * @author Daniel Russ
 *
 */
public class SOCcerResults {

	private static Logger logger = Logger.getLogger(SOCcerResults.class.getName());
	private static Comparator<String[]> idComparator = new Comparator<String[]>() {
		@Override
		public int compare(String[] o1, String[] o2) {
			return (Integer.parseInt(o1[0]) - Integer.parseInt(o2[0]));
		}
	};

	private final AssignmentCodingSystem codingSystem;
	private final String[] head;
	private final List<String[]> data;
	private Map<Integer, Integer> keyMap = new TreeMap<Integer, Integer>();
	private boolean[] flagged;

	/**
	 * A class that contains the results for SOCcer.  The job descriptions are also kept in the kept in
	 * the data.
	 */
	public SOCcerResults(String[] head,List<String[]> data, AssignmentCodingSystem codingSystem) {
		this(head, data, codingSystem, null);
	}

	public SOCcerResults(String[] head,List<String[]> data, AssignmentCodingSystem codingSystem, boolean[] flagged) {
		this.head = head;
		this.data = data;
		this.codingSystem = codingSystem;
		this.flagged = flagged;
		if (flagged == null) {
			this.flagged = new boolean[data.size()];
			Arrays.fill(this.flagged, false);
		}

		// fill a map to lookup results by Row Id instead of row number from the table..
		for(int indx = 0;indx < data.size();indx++) {
			keyMap.put(Integer.parseInt(data.get(indx)[0]), indx);
		}
	}
	private static AssignmentCodingSystem processHead(String[] head) {
		/* SOCcer provides the top 10 scoring results...
		 * the last 20 are the Soccer Results SOC1 Prob1 SOC2 Prob2 ... SOC10 Prob10
		 *
		 * Detect the coding system...
		 * */
		String sys = head[head.length - 2];
		sys = sys.substring(0, sys.indexOf('_'));
		AssignmentCodingSystem codingSystem = AssignmentCodingSystem.valueOf(sys);
		if (codingSystem != null) {
			logger.finer("Detected " + codingSystem.toString() + " coding");
		}
		return codingSystem;
	}

	/**
	 * The head is the first line of the SOCcer results.  Describing the input and result columns
	 * @return
	 */
	public String[] getHead() {
		return head;
	}

	/**
	 * The input data to SOCcer and the top 10 SOCcer assignments and scores.
	 * The list contain a string[] for every job description, and the String[] are
	 * the input/results
	 * @return
	 */
	public List<String[]> getData() {
		return data;
	}

	/**
	 * The Coding system for the results.
	 * @return
	 */
	public AssignmentCodingSystem getCodingSystem() {
		return codingSystem;
	}

	/**
	 * The number of rows in the table.
	 * @return
	 */
	public int size() {
		if (data == null) return 0;
		return data.size();
	}

	/**
	 * The number of columns for a row in the SOCcerResults table.
	 * There are currently 10 SOC codes and scores (total 20).  The
	 * table has keep one result, so the the other 18 element of the
	 * row are dropped.
	 */
	public int rowSize() {
		if (head == null) return 0;
		return head.length - 18;
	}

	/**
	 *  Returns the row given the row number of the SOCcerResults data (0 counting).  This may or may not have rowID i!!
	 * @param row number.
	 * @return
	 */
	public String[] getRow(int rowNumber) {
		if ((data == null) || (rowNumber >= data.size())) return new String[0];
		return data.get(rowNumber);
	}

	/**
	 * Returns the row with row id = rowId.  The row id does not exist, it returns an empty String[].
	 * @param rowId
	 * @return
	 */
	public String[] getRowId(int rowId) {
		if (!keyMap.containsKey(rowId)) return new String[0];
		return getRow(keyMap.get(rowId));
	}

	public boolean isFlagged(int row) {
		return flagged[row];
	}

	public void setFlag(int row,boolean flag) {
		this.flagged[row] = flag;
	}

	public void checkForXLSOCError() throws IOException{
		int start=0;
		for(String word:head){
			if (word.endsWith("_1")) break;
			start++;
		}
		for(String[] row:data){
			for (int col=start;col<row.length;col+=2){
				if (row[col].startsWith("Nov")) 
					throw new IOException("Bad SOC code: Look like the file has been modified by Excel.\nRow: "+Arrays.toString(row));
			}
		}
	}
	
	/**
	 * Read the CSV file from SOCcer.  The file can be filtered so the row id 1 does not have to be on the first line
	 * @param soccerResultsFile
	 * @return
	 * @throws IOException
	 */
	public static SOCcerResults readSOCcerResultsFile(File soccerResultsFile) throws IOException {
		CSVReader reader = null;
		// read the header...
		String[] head = null;
		// read the data...
		List<String[]> data = null;
		AssignmentCodingSystem codingSystem = null;
		SOCcerResults results=null;
		
		try {
			reader = new CSVReader(new BufferedReader(new FileReader(soccerResultsFile)));

			head = reader.readNext();
			logger.finer("read header..");
			if (head.length < 21) throw new IOException(soccerResultsFile.getAbsolutePath() + " not formatted appropriately");

			data = reader.readAll();
			Collections.sort(data, idComparator);
			logger.finer("finished reading data ..");
			
			codingSystem = processHead(head);
			results=new SOCcerResults(head,data,codingSystem);
			results.checkForXLSOCError();
		} catch (StringIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(SOCAssignGlobals.getApplicationFrame(), "CSV File appears to be using old header names.");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(SOCAssignGlobals.getApplicationFrame(), "CSV File appears to be missing the ID column.");
		} finally {
			if (reader != null) reader.close();
			if (codingSystem == null) return null;
		}

		return results;
	}

}
