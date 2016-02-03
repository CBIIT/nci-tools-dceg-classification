package gov.nih.cit.socassign.adapter;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class SelectAnotherSoccerResultAdapter extends MouseAdapter {
	@Override
	public void mouseClicked(MouseEvent event) {
		JTable singleJobDescriptionTable = SOCAssignGlobals.getSingleJobDescriptionTable();
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		int row = singleJobDescriptionTable.rowAtPoint(event.getPoint());
		row = singleJobDescriptionTable.convertRowIndexToModel(row);
		OccupationCode code = testModel.getOccupationCodeForTop10Row(row);
		SOCAssignGlobals.getCodingSystemPanel().selectOccupation(code);			
		if (event.getClickCount() >= 2) {
			testModel.addSelection(code.getName());
		}
	}
}
