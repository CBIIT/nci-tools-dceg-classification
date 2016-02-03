package gov.nih.cit.socassign;

import gov.nih.cit.socassign.action.LoadPreviousWorkAction;
import gov.nih.cit.socassign.codingsystem.OccupationCode;
import gov.nih.cit.util.AppProperties;
import gov.nih.cit.util.RollingList;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SOCAssignGlobals {
	public static final String title = "SOCAssign v0.0.3";
	public static final Color PALE_GREEN = new Color(152, 251, 152);

	private static final LoadPreviousWorkAction loadDBAction = new LoadPreviousWorkAction();
	private static final JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));

	private static JFrame applicationFrame;
	private static JTable resultsTable;
	private static JList<OccupationCode> assignmentList;
	private static JTextField assignmentTF;
	private static JScrollPane autocompleteScroll;
	private static JTable singleJobDescriptionTable;
	private static CodingSystemPanel codingSystemPanel;
	private static RollingList<File> lastWorkingFileList;
	private static AppProperties appProperties;
	private static Font fontAwesome = null;
	private static JMenu fileMenu;

	static {
		try {
			fontAwesome = Font.createFont(Font.TRUETYPE_FONT, SOCAssign.class.getResourceAsStream("fonts/fontawesome-webfont.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		fontAwesome = fontAwesome.deriveFont(20f);
	}

	public static JFrame intializeApplicationFrame(JFrame applicationFrame) {
		SOCAssignGlobals.applicationFrame = applicationFrame;
		return applicationFrame;
	}

	public static JFrame getApplicationFrame() {
		return applicationFrame;
	}

	public static JTable intializeResultsTable(JTable resultsTable) {
		SOCAssignGlobals.resultsTable = resultsTable;
		return resultsTable;
	}

	public static JTable getResultsTable() {
		return resultsTable;
	}

	public static JList<OccupationCode> initializeAssignmentList(JList<OccupationCode> assignmentList) {
		SOCAssignGlobals.assignmentList = assignmentList;
		return assignmentList;
	}

	public static JList<OccupationCode> getAssignmentList() {
		return assignmentList;
	}

	public static JTextField initializeAssignmentTF(JTextField assignmentTF) {
		SOCAssignGlobals.assignmentTF = assignmentTF;
		return assignmentTF;
	}

	public static JTextField getAssignmentTF() {
		return assignmentTF;
	}

	public static JScrollPane initializeAutocompleteScroll(JScrollPane autocompleteScroll) {
		SOCAssignGlobals.autocompleteScroll = autocompleteScroll;
		return autocompleteScroll;
	}

	public static JScrollPane getAutocompleteScroll() {
		return autocompleteScroll;
	}

	public static JTable initializeSingleJobDescriptionTable(JTable singleJobDescriptionTable) {
		SOCAssignGlobals.singleJobDescriptionTable = singleJobDescriptionTable;
		return singleJobDescriptionTable;
	}

	public static JTable getSingleJobDescriptionTable() {
		return singleJobDescriptionTable;
	}

	public static CodingSystemPanel intializeCodingSystemPanel(CodingSystemPanel codingSystemPanel) {
		SOCAssignGlobals.codingSystemPanel = codingSystemPanel;
		return codingSystemPanel;
	}

	public static CodingSystemPanel getCodingSystemPanel() {
		return codingSystemPanel;
	}

	public static RollingList<File> initializeLastWorkingFileList(RollingList<File> lastWorkingFileList) {
		SOCAssignGlobals.lastWorkingFileList = lastWorkingFileList;
		return lastWorkingFileList;
	}

	public static RollingList<File> getLastWorkingFileList() {
		return lastWorkingFileList;
	}

	public static AppProperties initializeAppProperties(AppProperties appProperties) {
		SOCAssignGlobals.appProperties = appProperties;
		return appProperties;
	}

	public static AppProperties getAppProperties() {
		return appProperties;
	}

	public static Font getFontAwesome() {
		return fontAwesome;
	}

	public static JFileChooser getJFC() {
		return jfc;
	}

	public static JMenu initializeFileMenu(JMenu fileMenu) {
		SOCAssignGlobals.fileMenu = fileMenu;
		return fileMenu;
	}

	public static JMenu getFileMenu() {
		return fileMenu;
	}

	public static void updateLastWorkingFileList(File f) {
		// only update the file menu if the file is not on the list...
		if (lastWorkingFileList.add(f)) {
			updateFileMenu();

			List<String> props = new ArrayList<String>();
			for (File file:lastWorkingFileList) {
				props.add(file.getAbsolutePath());
			}
			appProperties.setListOfProperties("last.file", props);
		}
	}

	public static void updateFileMenu() {

		for (int i = fileMenu.getMenuComponentCount() - 4;i >= 3;i--) {
			fileMenu.remove(i);
		}

		for (int i = 0;i < lastWorkingFileList.size();i++) {
			File file = lastWorkingFileList.get(i);
			JMenuItem menuItem = new JMenuItem(loadDBAction);menuItem.setText(file.getName());menuItem.setActionCommand(file.getAbsolutePath());
			fileMenu.insert(menuItem, 3);
		}
		fileMenu.invalidate();
	}

	public static boolean validResultSelected() {
		return resultsTable.getSelectedRow() >= 0;
	}
}