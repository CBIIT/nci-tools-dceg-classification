package gov.nih.cit.socassign.listener;

import java.awt.event.*;

import javax.swing.*;

import gov.nih.cit.socassign.SOCAssignGlobals;

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
