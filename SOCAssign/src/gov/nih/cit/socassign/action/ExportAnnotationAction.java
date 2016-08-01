package gov.nih.cit.socassign.action;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.util.AppProperties;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportAnnotationAction extends AbstractAction {
	private static final long serialVersionUID = -8247847955069036181L;
	private static final String ACTION_NAME = "Export Annotation to CSV";

	// Have a local instance of a JFC to prevent allowing multiple instance of the FileFilter being added to the JFC
	private JFileChooser jfc;
	
	public ExportAnnotationAction() {
		super(ACTION_NAME);
		jfc=new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("Annotation Results Files (.csv)","csv"));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		AppProperties appProperties = SOCAssignGlobals.getAppProperties();
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		if (SOCAssignGlobals.getResultsTable().getRowCount() == 0) return;
		jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
		int res = jfc.showSaveDialog(applicationFrame);
		if (res == JFileChooser.APPROVE_OPTION) {
			try {
				SOCAssignModel.getInstance().exportAssignments(jfc.getSelectedFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(applicationFrame, "Warning could not write out the annotation: " + e.getMessage());
				e.printStackTrace();
			}
			appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
		}

	}
}
