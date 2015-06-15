package gov.nih.cit.socassign;

import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the table model for the table the shows the 
 * top 10 SOCcer results for a selected Job Description.
 * 
 * @author Daniel Russ
 *
 */
public class SingleSOCcerResultTableModel extends AbstractTableModel{
	
	private static Logger logger=Logger.getLogger(SingleSOCcerResultTableModel.class.getName());

	private String[] values;
	
	public SingleSOCcerResultTableModel() {
	}

	public void clear(){
		values=null;
	}
	
	public void changeResults(String[] values){
		this.values=values;
		logger.finer("Job Description changed/new SOCcer results");
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		if (values==null) return 0;
		return 10;
	}

	@Override
	public int getColumnCount() {
		if (values==null) return 0;	
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int indx=values.length-20;
		if (values==null) return "";
		switch (columnIndex){
		case 0:
			return rowIndex+1;
		case 1:
			return values[indx+rowIndex*2];
		case 2:
			return Double.parseDouble( values[indx+rowIndex*2+1] );
		case 3:
			String val=SOCAssignModel.getInstance().getCodingSystem().getOccupationalCode(values[indx+rowIndex*2]).getTitle();
			if (val==null) val="";
			return val;
		}
		return "";
	}
	
	public String getSOCforRow(int selectedRow){
		int indx=values.length-20;
		return values[indx+selectedRow*2];
	}

	String[] colNames={"Rank","Code","Score","Definition"};
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		logger.finer("added a table model listener...");
		super.addTableModelListener(l);
	}

	private Class<?>[] columnClasses={Integer.class,String.class,Double.class,String.class};


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}
}
