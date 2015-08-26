package gov.nih.cit.socassign.listener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.CodingSystem;

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
			JList<String> autocompleteField = SOCAssignGlobals.getAutocompleteField();
			if (newValue.isEmpty()) {
				autocompleteField.setVisible(false);
			} else {
				DefaultListModel<String> autocompleteList = (DefaultListModel<String>)autocompleteField.getModel();
				autocompleteList.clear();
				CodingSystem system = SOCAssignModel.getInstance().getCodingSystem().getCodingSystem();
				for (String occupationCode : system.getListOfCodesAsStrings()) {
					if (occupationCode.contains(newValue)) {
						autocompleteList.addElement(occupationCode+"    "+system.getOccupationalCode(occupationCode).getTitle());
					}
				}
				autocompleteField.setVisible(true);
			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
}
