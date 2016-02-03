package gov.nih.cit.socassign.adapter;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.action.AddAutocompleteAssignmentAction;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

public class AutocompleteDoubleClickAdapter extends MouseAdapter {
	private final AbstractAction addAutocompleteAssignmentAction = new AddAutocompleteAssignmentAction();
	@SuppressWarnings("unchecked")
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() > 1) {
			if (!SOCAssignGlobals.validResultSelected() || ((JList<String>)e.getSource()).getSelectedIndex() == -1) return;
			addAutocompleteAssignmentAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
		}
	};
}
