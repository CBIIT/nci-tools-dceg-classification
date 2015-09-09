package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;
import gov.nih.cit.socassign.*;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

public class RemoveSelectedAssignmentAction extends AbstractAction {
	private static final long serialVersionUID = 1236434466480189965L;

	@Override
	public void actionPerformed(ActionEvent e) {
		JList<OccupationCode> assignmentList = SOCAssignGlobals.getAssignmentList();
		if (!SOCAssignGlobals.validResultSelected() || assignmentList.getSelectedIndex() < 0) return;
		SOCAssignModel.getInstance().removeElementAt(assignmentList.getSelectedIndex());
	}
}
