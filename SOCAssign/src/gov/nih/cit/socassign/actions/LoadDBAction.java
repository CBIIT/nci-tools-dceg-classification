package gov.nih.cit.socassign.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import gov.nih.cit.socassign.*;
import gov.nih.cit.util.AppProperties;

public class LoadDBAction extends AbstractAction {
	private static final long serialVersionUID = 7770717890150032118L;

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		JFileChooser jfc = SOCAssignGlobals.getJFC();
		jfc.setFileFilter(SOCAssignGlobals.getDBFF());
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		SelectCodingSystemAction selectCodingSystemAction = new SelectCodingSystemAction();
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
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
		int res=jfc.showOpenDialog(applicationFrame);
		if (res==JFileChooser.APPROVE_OPTION){
			appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
			return jfc.getSelectedFile();
		}
		return null;
	}
}
