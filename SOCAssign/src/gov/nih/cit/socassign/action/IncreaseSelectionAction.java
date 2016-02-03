package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

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
