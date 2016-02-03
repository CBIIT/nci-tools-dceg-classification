package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JViewport;

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
