package gov.nih.cit.socassign.listener;

import java.awt.Rectangle;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.cit.socassign.*;
import gov.nih.cit.socassign.codingsystem.*;

public class AssignmentTextFieldListener implements DocumentListener {
	@Override
	public void insertUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		onChange(e);
	}

	private void onChange(DocumentEvent event) {
		Document doc = event.getDocument();
		try {
			String newValue = doc.getText(doc.getStartPosition().getOffset(),doc.getLength());
			JScrollPane autocompleteScroll = SOCAssignGlobals.getAutocompleteScroll();
			@SuppressWarnings("unchecked")
			JList<String> autocompleteField = (JList<String>)((JViewport)autocompleteScroll.getComponent(0)).getComponent(0);
			if (newValue.isEmpty()) {
				autocompleteScroll.setVisible(false);
			} else {
				DefaultListModel<String> autocompleteList = (DefaultListModel<String>)autocompleteField.getModel();
				autocompleteList.clear();
				CodingSystem system = SOCAssignModel.getInstance().getCodingSystem().getCodingSystem();
				for (OccupationCode occupationCode : system.getListOfCodesAtLevel("detailed")) {
					if (occupationCode.getName().contains(newValue) || occupationCode.getTitle().contains(newValue) || occupationCode.getDescription().contains(newValue)) {
						autocompleteList.addElement(occupationCode.getName()+"    "+occupationCode.getTitle());
					}
				}
				if (autocompleteList.size() > 0) {
					Rectangle scrollPosition = autocompleteField.getVisibleRect();
					scrollPosition.y = 0;
					autocompleteField.scrollRectToVisible(scrollPosition);
					autocompleteScroll.setVisible(true);
				} else  {
					autocompleteScroll.setVisible(false);
				}
			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
}
