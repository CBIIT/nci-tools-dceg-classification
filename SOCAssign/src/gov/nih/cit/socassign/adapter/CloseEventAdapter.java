package gov.nih.cit.socassign.adapter;

import java.awt.event.*;

import gov.nih.cit.socassign.SOCAssignModel;

public class CloseEventAdapter extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent e) {
		SOCAssignModel.getInstance().onExit();
	}
}
