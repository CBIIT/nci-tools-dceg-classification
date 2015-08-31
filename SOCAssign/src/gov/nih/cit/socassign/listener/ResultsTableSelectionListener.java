package gov.nih.cit.socassign.listener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

public class ResultsTableSelectionListener implements ListSelectionListener {
	@Override
	public void valueChanged(ListSelectionEvent event) {
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		if (!SOCAssignGlobals.validResultSelected()) return;
		SOCAssignGlobals.getAssignmentTF().setText("");
		SOCAssignGlobals.getAssignmentList().clearSelection();
		SOCAssignGlobals.getCodingSystemPanel().clearSelection();
		int selectRow = resultsTable.getSelectedRow();
		selectRow = resultsTable.convertRowIndexToModel(selectRow);
		SOCAssignModel.getInstance().setSelectedResult(selectRow);
	}
};
