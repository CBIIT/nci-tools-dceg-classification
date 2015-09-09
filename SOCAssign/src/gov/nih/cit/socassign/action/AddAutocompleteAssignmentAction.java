package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

@SuppressWarnings("serial")
public class AddAutocompleteAssignmentAction extends AbstractAction {
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!SOCAssignGlobals.validResultSelected()) return;
		@SuppressWarnings("unchecked")
		String value = ((JList<String>)e.getSource()).getSelectedValue().substring(0,7);
		SOCAssignGlobals.getAssignmentTF().setText("");
		SOCAssignGlobals.getAutocompleteScroll().setVisible(false);
		SOCAssignModel.getInstance().addSelection(value);
	}
}
