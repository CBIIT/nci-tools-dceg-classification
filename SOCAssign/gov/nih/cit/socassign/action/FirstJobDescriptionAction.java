package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

public class FirstJobDescriptionAction extends AbstractAction {
	private static final long serialVersionUID = 6532718564577577441L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		if (!SOCAssignGlobals.validResultSelected()) return;			
		resultsTable.setRowSelectionInterval(0, 0);			
		resultsTable.scrollRectToVisible(resultsTable.getCellRect(0, 0, true));
	}
}
