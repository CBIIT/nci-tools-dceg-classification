package gov.nih.cit.socassign.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.*;

import gov.nih.cit.socassign.*;

public class LoadSoccerResultsAction extends AbstractAction {
	private static final long serialVersionUID = 8572841906487942011L;
	private static final String ACTION_NAME = "Load SOCcer Results";
	private static final FileFilter CSV_FF = new FileNameExtensionFilter("SOCcer Results Files (.csv)","csv");

	public LoadSoccerResultsAction() {
		super(ACTION_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser jfc = SOCAssignGlobals.getJFC();
		JFrame applicationFrame = SOCAssignGlobals.getApplicationFrame();
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		JTable resultsTable = SOCAssignGlobals.getResultsTable();
		CodingSystemPanel codingSystemPanel = SOCAssignGlobals.getCodingSystemPanel();
		SelectCodingSystemAction selectCodingSystemAction = new SelectCodingSystemAction();
		
		jfc.setCurrentDirectory(new File(SOCAssignGlobals.getAppProperties().getProperty("last.directory", System.getProperty("user.home"))));
		jfc.setFileFilter(CSV_FF);
		int res=jfc.showOpenDialog(applicationFrame);
		if (res==JFileChooser.APPROVE_OPTION){
			System.out.println("Selected file: "+jfc.getSelectedFile().getAbsolutePath());
			try {
				testModel.resetModel();
				SOCcerResults results=SOCcerResults.readSOCcerResultsFile(jfc.getSelectedFile());
				testModel.setResults(results);
				resultsTable.invalidate();

				boolean systemSpecified=testModel.isCodingSystemSpecifiedInResults();
				if (systemSpecified){
					selectCodingSystemAction.setEnabled(false);
					codingSystemPanel.updateCodingSystem(testModel.getCodingSystem());
				}else{
					selectCodingSystemAction.setEnabled(true);
				}

				String fileName=jfc.getSelectedFile().getAbsolutePath();
				int indx=fileName.lastIndexOf('.');
				if (indx<0 || fileName.substring(indx)==".db"){
					fileName=fileName+".db";
				}else{
					fileName=fileName.substring(0,indx)+".db";
				}					
				testModel.setNewDB(fileName);
				SOCAssignGlobals.updateLastWorkingFileList(new File(fileName));

				applicationFrame.setTitle(SOCAssignGlobals.title+" ("+fileName+")");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(applicationFrame, "Error trying to Open File "+jfc.getSelectedFile().getAbsolutePath(), "SOCassign Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
