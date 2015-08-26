package gov.nih.cit.socassign;

import gov.nih.cit.socassign.Assignments.FlagType;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * The table model used by the SOCcer Results table.  The 
 * 
 * @author Daniel Russ
 *
 */
public class SOCcerResultsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -7462382712712342515L;
	private static final Logger logger = Logger.getLogger(SOCcerResultsTableModel.class.getName());
	private SOCcerResults results;

	
	public SOCcerResultsTableModel(SOCcerResults results) {
		this.results=results;
	}

	public void changeResults(SOCcerResults results){
		this.results=results;
		logger.warning("results changed");
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		if (results==null) return 0;
		return results.size();
	}

	@Override
	public int getColumnCount() {
		if (results==null) return 1;	
		return results.rowSize()+1;
	}

	public static DecimalFormat df=new DecimalFormat("0.0000");
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SOCAssignModel socAssignModel=SOCAssignModel.getInstance();
		if (results==null) return "";
		if (columnIndex==0) {
			Assignments assignments=socAssignModel.getAssignmentsForRow(rowIndex);
			if (assignments==null) return false;
			return assignments.getFlag()==FlagType.FLAGGED;
		}

		columnIndex--;
		if (columnIndex==0){
			return Integer.parseInt(results.getRow(rowIndex)[columnIndex]);
		}
		if (columnIndex==5){
			//return Double.parseDouble(results.getRow(rowIndex)[columnIndex]);
			return df.format(Double.parseDouble( results.getRow(rowIndex)[columnIndex] ));
		}
		return results.getRow(rowIndex)[columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SOCAssignModel socAssignModel=SOCAssignModel.getInstance();
		// only the flogged column is editable....
		if (columnIndex==0) {
			FlagType flag=((Boolean)aValue)?FlagType.FLAGGED:FlagType.NOT_FLAGGED;
			socAssignModel.updateFlag(rowIndex,flag);
			fireTableDataChanged();
		}
	}

	@Override
	public String getColumnName(int column) {
		return (column==0)?"Flag":results.getHead()[column-1];
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		logger.finer("added a table model listener...");
		super.addTableModelListener(l);
	}

	private Class<?>[] columnClasses={Boolean.class,Integer.class,String.class,String.class,String.class,String.class,Double.class};


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}


}
