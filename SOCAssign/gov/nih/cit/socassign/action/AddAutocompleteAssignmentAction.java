package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

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
