package gov.nih.cit.socassign.listener;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gov.nih.cit.socassign.CodingSystemPanel;
import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

public class AssignmentSelectionListener implements ListSelectionListener {
	@Override
	public void valueChanged(ListSelectionEvent event) {
		JList<OccupationCode> assignmentList = SOCAssignGlobals.getAssignmentList();
		CodingSystemPanel codingSystemPanel = SOCAssignGlobals.getCodingSystemPanel();
		int row = event.getFirstIndex();
		if (row < 0 || row >= assignmentList.getModel().getSize()) {
			assignmentList.clearSelection();
			codingSystemPanel.clearSelection();
			return;
		}
		OccupationCode code = (OccupationCode)assignmentList.getModel().getElementAt(row);
		codingSystemPanel.selectOccupation(code);
	}
}
