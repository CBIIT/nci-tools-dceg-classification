package com.java2s.swingx;

import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboPopup;

public class Java2sAutoComboBox<E> extends JComboBox<E> {

	private class AutoTextFieldEditor extends BasicComboBoxEditor {

		
		@SuppressWarnings("unchecked")
		private Java2sAutoTextField<E> getAutoTextFieldEditor() {
			return (Java2sAutoTextField<E>) editor;
		}

		AutoTextFieldEditor(List<E> list) {
			editor = new Java2sAutoTextField<E>(list, Java2sAutoComboBox.this);
		}
	}

	
	@SuppressWarnings("unchecked")
	public Java2sAutoComboBox(List<E> list) {
		isFired = false;
		autoTextFieldEditor = new AutoTextFieldEditor(list);
		setEditable(true);
		setModel(new DefaultComboBoxModel<E>((E[])list.toArray()) {

			protected void fireContentsChanged(Object obj, int i, int j) {
				if (!isFired)
					super.fireContentsChanged(obj, i, j);
			}

		});
		setEditor(autoTextFieldEditor);
	}

	public boolean isCaseSensitive() {
		return autoTextFieldEditor.getAutoTextFieldEditor().isCaseSensitive();
	}

	public void setCaseSensitive(boolean flag) {
		autoTextFieldEditor.getAutoTextFieldEditor().setCaseSensitive(flag);
	}

	public boolean isStrict() {
		return autoTextFieldEditor.getAutoTextFieldEditor().isStrict();
	}

	public void setStrict(boolean flag) {
		autoTextFieldEditor.getAutoTextFieldEditor().setStrict(flag);
	}

	public List<E> getDataList() {
		return autoTextFieldEditor.getAutoTextFieldEditor().getDataList();
	}

	@SuppressWarnings("unchecked")
	public void setDataList(List<E> list) {
		autoTextFieldEditor.getAutoTextFieldEditor().setDataList(list);
		setModel(new DefaultComboBoxModel<E>((E[])list.toArray()));
	}

	void setSelectedValue(Object obj) {
		if (isFired) {
			return;
		} else {
			isFired = true;
			setSelectedItem(obj);
			try {
				BasicComboPopup popup=(BasicComboPopup)getUI().getAccessibleChild(this, 0);				
				@SuppressWarnings("unchecked")
				JList<E> list=(JList<E>)popup.getList();
				int indx=list.getSelectedIndex();
				list.ensureIndexIsVisible(Math.min(list.getModel().getSize()-1, indx+list.getVisibleRowCount()-1));
			} catch (Exception e) {
				e.printStackTrace();
			}

			fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder,
					1));
			isFired = false;
			return;
		}
	}

	protected void fireActionEvent() {
		if (!isFired)
			super.fireActionEvent();
	}

	private AutoTextFieldEditor autoTextFieldEditor;

	private boolean isFired;

}
