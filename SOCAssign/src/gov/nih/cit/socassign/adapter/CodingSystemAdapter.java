package gov.nih.cit.socassign.adapter;

import gov.nih.cit.socassign.CodingSystemPanel;
import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CodingSystemAdapter extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
		CodingSystemPanel codingSystemPanel = SOCAssignGlobals.getCodingSystemPanel();
		if (!SOCAssignGlobals.validResultSelected() || codingSystemPanel.getSelectedPathCount() <= 1) return;
		OccupationCode code = codingSystemPanel.getLastSelectedPathComponent();
		if (code.isLeaf() && e.getClickCount() > 1) {
			SOCAssignGlobals.getAssignmentTF().setText("");
			SOCAssignModel.getInstance().addSelection(code.getName());
		}
	};
}
