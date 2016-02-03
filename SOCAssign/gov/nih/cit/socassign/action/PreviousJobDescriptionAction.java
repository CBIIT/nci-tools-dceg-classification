package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

public class PreviousJobDescriptionAction extends AbstractAction {
	private static final long serialVersionUID = -2066708696929261752L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		if (!SOCAssignGlobals.validResultSelected()) return;			
		int nextRow = (resultsTable.getRowCount() + resultsTable.getSelectedRow() - 1) % resultsTable.getRowCount();
		resultsTable.setRowSelectionInterval(nextRow, nextRow);
		resultsTable.scrollRectToVisible(resultsTable.getCellRect(nextRow, 0, true));
	}
}
