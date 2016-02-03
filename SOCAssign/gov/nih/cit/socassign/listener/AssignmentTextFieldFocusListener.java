package gov.nih.cit.socassign.listener;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class AssignmentTextFieldFocusListener implements FocusListener {
	@Override
	public void focusGained(FocusEvent e) {
		if (!((JTextField)e.getSource()).getText().isEmpty()) {
			SOCAssignGlobals.getAutocompleteScroll().setVisible(true);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		JScrollPane scrollPane = SOCAssignGlobals.getAutocompleteScroll();
		if (!scrollPane.isAncestorOf(e.getOppositeComponent())) {
			scrollPane.setVisible(false);
		}
	}
}
