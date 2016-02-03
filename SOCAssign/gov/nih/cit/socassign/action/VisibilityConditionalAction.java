package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

public class VisibilityConditionalAction extends AbstractAction {
	private static final long serialVersionUID = 1368345634417365927L;
	private JComponent component;
	private AbstractAction whenVisible;
	private AbstractAction whenHidden;

	public VisibilityConditionalAction(JComponent component, AbstractAction whenVisible, AbstractAction whenHidden) {
		this.component = component;
		this.whenVisible = whenVisible;
		this.whenHidden = whenHidden;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (component.isVisible()) {
			whenVisible.actionPerformed(e);
		} else {
			whenHidden.actionPerformed(e);
		}
	}
}
