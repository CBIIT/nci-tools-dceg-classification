package gov.nih.cit.socassign.action;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.*;

import gov.nih.cit.socassign.SOCAssignGlobals;

public class EnterAutocompleteFieldAction extends AbstractAction {
	private static final long serialVersionUID = 5197909226059549750L;

	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JList<String> autocomplete = (JList<String>)((JViewport)SOCAssignGlobals.getAutocompleteScroll().getComponent(0)).getComponent(0);
		autocomplete.requestFocusInWindow();
		autocomplete.setSelectedIndex(0);
		Rectangle scrollPosition = autocomplete.getVisibleRect();
		scrollPosition.y = 0;
		autocomplete.scrollRectToVisible(scrollPosition);
	}
}
