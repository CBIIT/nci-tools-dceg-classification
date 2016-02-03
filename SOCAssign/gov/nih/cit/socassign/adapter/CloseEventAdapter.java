package gov.nih.cit.socassign.adapter;

import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseEventAdapter extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent e) {
		SOCAssignModel.getInstance().onExit();
	}
}
