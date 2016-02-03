package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SelectCodingSystemAction extends AbstractAction {
	private static final long serialVersionUID = -8289114472189185465L;

	@Override
	public void actionPerformed(ActionEvent event) {
		SOCAssignModel testModel = SOCAssignModel.getInstance();

		AssignmentCodingSystem codingSystem = AssignmentCodingSystem.valueOf(event.getActionCommand());
		if (testModel.getCodingSystem() != codingSystem) {
			testModel.setCodingSystem(codingSystem);
			SOCAssignGlobals.getCodingSystemPanel().updateCodingSystem(codingSystem);
		}
	}
}
