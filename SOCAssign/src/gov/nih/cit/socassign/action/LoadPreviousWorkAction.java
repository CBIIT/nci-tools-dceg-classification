package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import gov.nih.cit.socassign.*;
import gov.nih.cit.util.AppProperties;

public class LoadPreviousWorkAction extends AbstractAction {
	private static final long serialVersionUID = -4547819368944091507L;

	private static final String ACTION_NAME = "Load Previous Work";

	public LoadPreviousWorkAction() {
		super(ACTION_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		SelectCodingSystemAction selectCodingSystemAction = new SelectCodingSystemAction();
		SOCAssignGlobals.getJFC().setFileFilter(new FileNameExtensionFilter("Working Files (.db)","db"));
		// if the user selected a file from the menu ... load the file
		// else get the file from a JFileChooser...
		File dbFile= (actionEvent.getActionCommand().length() == 0) ? getFile() : new File(actionEvent.getActionCommand());
		if (dbFile == null ) return;

		// if somehow the file does not exist delete it from the lastWorkingFileList...
		if (!dbFile.exists()) {
			SOCAssignGlobals.getLastWorkingFileList().remove(dbFile);
			SOCAssignGlobals.getAppProperties().remove(dbFile.getAbsolutePath());
			SOCAssignGlobals.updateFileMenu();
			return;
		}

		// load the db...
		try {
			testModel.loadPreviousWork(dbFile);
			SOCAssignGlobals.updateLastWorkingFileList(dbFile);
			SOCAssignGlobals.getResultsTable().invalidate();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(applicationFrame, "Error trying to Open database: (Did you select results instead of a working file?) "+dbFile.getAbsolutePath(), "SOCassign Error", JOptionPane.ERROR_MESSAGE);
		}
		applicationFrame.setTitle(SOCAssignGlobals.title+" ("+dbFile.getAbsolutePath()+")");
		boolean systemSpecified=testModel.isCodingSystemSpecifiedInResults();
		if (systemSpecified){
			selectCodingSystemAction.setEnabled(false);
			SOCAssignGlobals.getCodingSystemPanel().updateCodingSystem(testModel.getCodingSystem());
		}else{
			selectCodingSystemAction.setEnabled(true);
		}

	}

	private File getFile(){
		JFileChooser jfc = SOCAssignGlobals.getJFC();
		AppProperties appProperties = SOCAssignGlobals.getAppProperties();
		jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
		int res=jfc.showOpenDialog(SOCAssignGlobals.getApplicationFrame());
		if (res==JFileChooser.APPROVE_OPTION){
			appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
			return jfc.getSelectedFile();
		}
		return null;
	}
}
