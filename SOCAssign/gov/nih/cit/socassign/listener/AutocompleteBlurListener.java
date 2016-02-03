package gov.nih.cit.socassign.listener;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JList;

public class AutocompleteBlurListener implements FocusListener {
	@Override
	public void focusGained(FocusEvent e) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void focusLost(FocusEvent e) {
		((JList<String>)e.getSource()).clearSelection();
		if (e.getOppositeComponent() != null && !e.getOppositeComponent().equals(SOCAssignGlobals.getAssignmentTF())) {
			SOCAssignGlobals.getAutocompleteScroll().setVisible(false);
		}
	}
}
