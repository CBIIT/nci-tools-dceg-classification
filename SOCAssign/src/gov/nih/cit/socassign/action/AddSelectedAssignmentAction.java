package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;
import gov.nih.cit.socassign.*;

public class AddSelectedAssignmentAction extends AbstractAction {
	private static final long serialVersionUID = -3841222759101461690L;

	@Override
	public void actionPerformed(ActionEvent e) {
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		if (!SOCAssignGlobals.validResultSelected()) return;
		String txt = SOCAssignGlobals.getAssignmentTF().getText();
		if (testModel.getCodingSystem().matches(txt)){
			testModel.addSelection(txt);
		}else{
			JOptionPane.showMessageDialog(SOCAssignGlobals.getApplicationFrame(), "Assignment is not formatted appropriately "+txt, "SOCassign Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
