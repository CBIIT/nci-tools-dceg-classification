package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


/**
 * This class consumes events so that parent action maps wont perform.
 * Needed because if you typed a period in the Comment box, it would move
 * down a job description.
 * 
 * @author Daniel Russ
 *
 */
public class EmptyAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {}
}
