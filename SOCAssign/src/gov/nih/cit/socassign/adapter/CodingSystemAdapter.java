package gov.nih.cit.socassign.adapter;

import java.awt.event.*;

import gov.nih.cit.socassign.*;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

public class CodingSystemAdapter extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
		CodingSystemPanel codingSystemPanel = SOCAssignGlobals.getCodingSystemPanel();
		if (!SOCAssignGlobals.validResultSelected() || codingSystemPanel.getSelectedPathCount() <= 1) return;
		OccupationCode code = codingSystemPanel.getLastSelectedPathComponent();
		if (code.isLeaf() && e.getClickCount() > 1) {
			SOCAssignModel.getInstance().addSelection(code.getName());
		}
	};
}
