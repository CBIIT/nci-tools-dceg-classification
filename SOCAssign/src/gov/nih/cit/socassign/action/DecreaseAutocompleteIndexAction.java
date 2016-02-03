package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

public class DecreaseAutocompleteIndexAction extends AbstractAction {
	private static final long serialVersionUID = -6486100912494147699L;

	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JList<String> autocompleteField = (JList<String>)e.getSource();
		int index = autocompleteField.getSelectedIndex();
		if (index == 0) {
			SOCAssignGlobals.getAssignmentTF().requestFocusInWindow();
		} else if (index > 0) {
			autocompleteField.setSelectedIndex(index - 1);
		}
	}
}
