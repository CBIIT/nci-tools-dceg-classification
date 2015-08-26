package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import gov.nih.cit.socassign.SOCAssignGlobals;

public class LastJobDescriptionAction extends AbstractAction {
	private static final long serialVersionUID = 2447684673608109461L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		if (!SOCAssignGlobals.validResultSelected()) return;
		int row = resultsTable.getRowCount()-1;
		resultsTable.setRowSelectionInterval(row, row);
		resultsTable.scrollRectToVisible(resultsTable.getCellRect(row, 0, true));
	}
}
