package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;
import gov.nih.cit.socassign.*;
import gov.nih.cit.socassign.Assignments.FlagType;

public class ToggleFlagAction extends AbstractAction {
	private static final long serialVersionUID = 1367000198586462972L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		int row= resultsTable.getSelectedRow();
		if (row>=0){
			int selectedRow=resultsTable.convertRowIndexToModel( row );
			boolean flagValue=(Boolean)resultsTable.getValueAt(selectedRow, 0);
			int rowID=(Integer)resultsTable.getValueAt(selectedRow, 1);
			System.out.println("FLAG TOGGLER: (row) "+selectedRow+" (rowID) "+rowID+" (current value) "+flagValue);
			testModel.updateFlag(selectedRow, flagValue?FlagType.NOT_FLAGGED:FlagType.FLAGGED);
			testModel.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
		}else{
			System.out.println("bad row selected..."+row);
		}
	}
}
