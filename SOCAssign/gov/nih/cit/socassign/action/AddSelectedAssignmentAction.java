package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AddSelectedAssignmentAction extends AbstractAction {
	private static final long serialVersionUID = -3841222759101461690L;

	@Override
	public void actionPerformed(ActionEvent e) {
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		if (!SOCAssignGlobals.validResultSelected()) return;
		JTextField assignmentTF = SOCAssignGlobals.getAssignmentTF();
		String txt = assignmentTF.getText();
		if (testModel.getCodingSystem().matches(txt)) {
			testModel.addSelection(txt);
		} else {
			JOptionPane.showMessageDialog(SOCAssignGlobals.getApplicationFrame(), "Assignment is not formatted appropriately " + txt, "SOCassign Error", JOptionPane.ERROR_MESSAGE);
		}
		assignmentTF.setText("");
	}
}
