package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import gov.nih.cit.socassign.*;
import gov.nih.cit.util.AppProperties;

public class ExportAnnotationAction extends AbstractAction {
	private static final long serialVersionUID = -8247847955069036181L;
	private static final String ACTION_NAME = "Export Annotation to CSV";

	public ExportAnnotationAction() {
		super(ACTION_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser jfc = SOCAssignGlobals.getJFC();
		AppProperties appProperties = SOCAssignGlobals.getAppProperties();
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		if (SOCAssignGlobals.getResultsTable().getRowCount()==0) return;
		jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
		jfc.setFileFilter(new FileNameExtensionFilter("Annotation Results Files (.csv)","csv"));
		int res=jfc.showSaveDialog(applicationFrame);
		if (res==JFileChooser.APPROVE_OPTION){
			try {
				SOCAssignModel.getInstance().exportAssignments(jfc.getSelectedFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(applicationFrame, "Warning could not write out the annotation: "+e.getMessage());
				e.printStackTrace();
			}
			appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
		}

	}
}
