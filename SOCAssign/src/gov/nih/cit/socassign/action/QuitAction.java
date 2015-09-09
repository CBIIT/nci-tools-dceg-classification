package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gov.nih.cit.socassign.SOCAssignModel;

public class QuitAction extends AbstractAction {
	private static final long serialVersionUID = -7625546279926074427L;

	@Override
	public void actionPerformed(ActionEvent e) {
		SOCAssignModel.getInstance().onExit();
		System.exit(0);
	}
}
