package gov.nih.cit.socassign.actions;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;

public class AssignmentTextFieldListener implements KeyListener {
	private DefaultListModel<String> autocompleteList;
	private JList<String> autocompleteField;
	public AssignmentTextFieldListener(JList<String> autocompleteField, DefaultListModel<String> autocompleteList) {
		this.autocompleteField = autocompleteField;
		this.autocompleteList = autocompleteList;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		JTextField textField = (JTextField)e.getSource();
		String newValue = textField.getText();
		if (e.getKeyChar() != '\b') {
			newValue += e.getKeyChar();
		}
		if (newValue.isEmpty()) {
			autocompleteField.setVisible(false);
		} else {
			autocompleteList.clear();
			autocompleteList.addElement(newValue);
			autocompleteField.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
