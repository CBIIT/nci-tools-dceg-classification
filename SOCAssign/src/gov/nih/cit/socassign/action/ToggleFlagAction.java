package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.Assignments.FlagType;
import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

public class ToggleFlagAction extends AbstractAction {
	private static final long serialVersionUID = 1367000198586462972L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		int row = resultsTable.getSelectedRow();
		if (row >= 0) {
			int selectedRow = resultsTable.convertRowIndexToModel(row);
			boolean flagValue = (Boolean)resultsTable.getValueAt(row, 0);
			int rowID = (Integer)resultsTable.getValueAt(row, 1);
			//System.out.println("FLAG TOGGLER: (selected row) " + selectedRow +  " (rowID) " + rowID + " (current value) " + flagValue+" (row) "+ row + " " +resultsTable.getValueAt(row, 1) + " " +resultsTable.getValueAt(row, 0));
			testModel.updateFlag(selectedRow, flagValue?FlagType.NOT_FLAGGED:FlagType.FLAGGED);
			testModel.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
		} else {
			System.out.println("bad row selected..." + row);
		}
	}
}
