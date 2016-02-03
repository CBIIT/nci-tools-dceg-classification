package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

public class DecreaseSelectionAction extends AbstractAction {
	private static final long serialVersionUID = 4585924452138201864L;

	@Override
	public void actionPerformed(ActionEvent e) {
		JList<OccupationCode> assignmentList = SOCAssignGlobals.getAssignmentList();
		if (!SOCAssignGlobals.validResultSelected()) return;
		int selectedIndex = assignmentList.getSelectedIndex();
		if (selectedIndex < 0) return;
		SOCAssignModel.getInstance().decreaseSelection(selectedIndex);
		if (selectedIndex < assignmentList.getModel().getSize() - 1) {
			assignmentList.setSelectedIndex(selectedIndex + 1);
		}
	}
}
