package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import gov.nih.cit.socassign.SOCAssignGlobals;

public class NextJobDescriptionAction extends AbstractAction {
	private static final long serialVersionUID = -7908790272509895926L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		if (!SOCAssignGlobals.validResultSelected()) return;
		int nextRow = (resultsTable.getSelectedRow() + 1) % resultsTable.getRowCount();
		resultsTable.setRowSelectionInterval(nextRow, nextRow);
		resultsTable.scrollRectToVisible(resultsTable.getCellRect(nextRow, 0, true));
	}
}
