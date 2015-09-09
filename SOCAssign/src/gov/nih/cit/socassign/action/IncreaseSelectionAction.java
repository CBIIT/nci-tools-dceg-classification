package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;
import gov.nih.cit.socassign.*;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

public class IncreaseSelectionAction extends AbstractAction {
	private static final long serialVersionUID = 4065641788466989249L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JList<OccupationCode> assignmentList = SOCAssignGlobals.getAssignmentList();
		if (!SOCAssignGlobals.validResultSelected()) return;
		int selectedIndex = assignmentList.getSelectedIndex();
		if (selectedIndex > 0) {
			SOCAssignModel.getInstance().increaseSelection(selectedIndex);
			assignmentList.setSelectedIndex(selectedIndex - 1);
		}
	}
}
