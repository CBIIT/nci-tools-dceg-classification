package gov.nih.cit.socassign;

import gov.nih.cit.socassign.Assignments.FlagType;
import gov.nih.cit.socassign.codingsysten.OccupationCode;
import gov.nih.cit.util.AppProperties;
import gov.nih.cit.util.RollingList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * SOCAssign is the main class.  
 * 
 * @author Daniel Russ
 *
 */
public class SOCAssign{

	public static Logger logger=Logger.getLogger(SOCAssign.class.getName());

	/** the main application frame.  Held to changed the Title */
	private static JFrame applicationFrame;
	/** holds most of the data required by the gui components */
	private static SOCAssignModel testModel=SOCAssignModel.getInstance();
	/** displays the results of SOCcer */
	private static JTable resultsTable=new JTable(testModel.getTableModel());
	/** display the codes assigned by the coder. */
	private static JList assigmentList=new JList(testModel.getAssignmentListModel());
	/** A text field where coders can type in a code */
	private static JTextField assignmentTF=new JTextField(8);
	/** A table that displays the results of SOCcer for a single job description.  This is filled
	 * when the user selects a row in the resultsTable*/
	private static JTable singleJobDescriptionTable=new JTable(testModel.getTop10Model());
	/** A JPanel that display all the codes for a coding system */
	private static CodingSystemPanel codingSystemPanel=new CodingSystemPanel();
	/** Displays the selected Job Description from the resultsTable */
	private static JList jobDescriptionInfoList=new JList(testModel.getSingleJobDescriptionListModel());
	/** A list that holds the last 3 files used */
	private static RollingList<File> lastWorkingFileList=new RollingList<File>(3);
	/** Stores information (the last files used) in a properties file so it will be remembered next time the program starts*/
	private static AppProperties appProperties;
	/** used in the JFrame title */
	private static final String title="SOCAssign v0.0.2";


	public static Font fontAwesome;
	/**
	 * "Main" method of the application should be run on the Event Dispatch Thread.  
	 */
	public static void createAndShowGUI() {
		// create the application frame ...
		applicationFrame=new JFrame(title);
		applicationFrame.addWindowListener(windowListener);
		applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// format the results table ...
		resultsTable.setAutoCreateRowSorter(true);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(resultsTableSelectionListener);
		resultsTable.setDefaultRenderer(String.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Double.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Integer.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Boolean.class, flagRenderer);

		// and the selected soccer result table...
		singleJobDescriptionTable.setAutoCreateRowSorter(true);
		singleJobDescriptionTable.addMouseListener(selectAnotherSoccerResultListener);
		singleJobDescriptionTable.setDefaultRenderer(Integer.class, selectedResultRenderer);
		singleJobDescriptionTable.setDefaultRenderer(Double.class, selectedResultRenderer);

		// and the assignmentList
		assigmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		assigmentList.addListSelectionListener(assignmentListSelectionListener);

		// if you hit the up/down arrow in the textfield, it switches the selected soccerResult
		assignmentTF.setAction(addSelectedAssignment);
		assignmentTF.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
		assignmentTF.getActionMap().put("DOWN", nextJobDescription);
		assignmentTF.getInputMap().put(KeyStroke.getKeyStroke("UP"), "UP");
		assignmentTF.getActionMap().put("UP", previousJobDescription);
//		assignmentTF.addKeyListener(myKeyListener);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.WEST);


		JPanel centerPanel = new JPanel(new BorderLayout());		
		// add the code assignment box ...
		JPanel inputPanel=new JPanel(new BorderLayout());
		inputPanel.add(assignmentTF,BorderLayout.NORTH);

		// and the button Panel
		//JPanel buttonPanel=new JPanel(new GridLayout(2, 2));
		JPanel buttonPanel=new JPanel(new GridLayout(1, 4));

		// add assignment button
		JButton addSOCAssignment=new JButton(addSelectedAssignment);
		buttonPanel.add(addSOCAssignment);
		// move selection up
		JButton moveAssignmentUp=new JButton(increaseSelection);
		buttonPanel.add(moveAssignmentUp);
		// move selection down
		JButton moveAssignmentDown=new JButton(decreaseSelection);
		buttonPanel.add(moveAssignmentDown);
		// remove assignment button
		JButton removeSOCAssignment=new JButton(removeSelectedAssignment);
		buttonPanel.add(removeSOCAssignment);

		// load the icons on the button.  FontAwesome is an open-source font distributed with SOCassign.
		// if there is a problem, use the icons that I drew.  They are not as pretty.
		try {
			try {
				fontAwesome = Font.createFont(Font.TRUETYPE_FONT, SOCAssign.class.getResourceAsStream("fonts/fontawesome-webfont.ttf"));				
				fontAwesome = fontAwesome.deriveFont(20f);
				addSOCAssignment.setFont(fontAwesome); addSOCAssignment.setForeground(Color.BLUE);
				removeSOCAssignment.setFont(fontAwesome); removeSOCAssignment.setForeground(Color.BLUE);
				moveAssignmentUp.setFont(fontAwesome); moveAssignmentUp.setForeground(Color.BLUE);
				moveAssignmentDown.setFont(fontAwesome); moveAssignmentDown.setForeground(Color.BLUE);
				addSOCAssignment.setText("\uf055");
				removeSOCAssignment.setText("\uf056");
				moveAssignmentUp.setText("\uf0aa");
				moveAssignmentDown.setText("\uf0ab");
			} catch (Exception e) {
				addSOCAssignment.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/add-blue.png"))));
				removeSOCAssignment.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/remove-blue.png"))));
				moveAssignmentUp.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/up-blue.png"))));
				moveAssignmentDown.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/down-blue.png"))));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}			

		inputPanel.add(buttonPanel,BorderLayout.CENTER);
		inputPanel.add(new JScrollPane(assigmentList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.SOUTH);

		// and the single Job Description Panel.
		centerPanel.add(inputPanel,BorderLayout.NORTH);
		centerPanel.add(new JScrollPane(singleJobDescriptionTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.CENTER);
		centerPanel.add(new JScrollPane(jobDescriptionInfoList,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.SOUTH);
		mainPanel.add(centerPanel,BorderLayout.CENTER);

		// add the Coding System panel on the right
		//codingSystemPanel.addTreeSelectionListener(codingSystemTreeListener);
		codingSystemPanel.addMouseListenerToJTree(codingSystemMouseAdapter);
		mainPanel.add(codingSystemPanel, BorderLayout.EAST);

		createMenus();

		//mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.SHIFT_DOWN_MASK), "FirstJobDescription");

		// if you are not in the text box,  The "<" key selected the previous row job description ,SHIFT-"<" the first.
		// The ">" key selects the next job description and shift-">" the last.
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,0), "PreviousJobDescription");
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD,0), "NextJobDescription");		
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,KeyEvent.SHIFT_DOWN_MASK), "FirstJobDescription");
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD,KeyEvent.SHIFT_DOWN_MASK), "LastJobDescription");
		mainPanel.getActionMap().put("LastJobDescription", lastJobDescription);
		mainPanel.getActionMap().put("NextJobDescription", nextJobDescription);
		mainPanel.getActionMap().put("PreviousJ/gettetobDescription", previousJobDescription);
		mainPanel.getActionMap().put("FirstJobDescription", firstJobDescription);

		resultsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F,0), "ToggleFlag");
		resultsTable.getActionMap().put("ToggleFlag", toggleFlagAction);

		applicationFrame.setContentPane(mainPanel);
		applicationFrame.pack();
		applicationFrame.setVisible(true);
	}

	private static JMenu fileMenu=new JMenu("File");
	private static void createMenus(){
		JMenuBar menuBar=new JMenuBar();

		// fileMenu is a field because it needs to be updated when a user selects a database.
		// create File > load
		JMenuItem loadMI=new JMenuItem(loadAction);
		fileMenu.add(loadMI);

		// create File > load previous coding
		JMenuItem loadDBMI=new JMenuItem(loadDBAction);loadDBMI.setActionCommand("");
		fileMenu.add(loadDBMI);

		fileMenu.add(new JSeparator());
		// create File > LAST 3 Working Files...
		if (lastWorkingFileList.size()>0){
			for (File file:lastWorkingFileList.asRollingStack()){
				JMenuItem menuItem=new JMenuItem(loadDBAction);menuItem.setText(file.getName());menuItem.setActionCommand(file.getAbsolutePath());
				fileMenu.add(menuItem);
			}
		}
		fileMenu.add(new JSeparator());

		fileMenu.add(exportAction);

		// create File > Quit
		JMenuItem quitMI=new JMenuItem(quitAction);quitMI.setText("Quit");
		fileMenu.add(quitMI);
		menuBar.add(fileMenu);

		// create System
		JMenu systemMenu=new JMenu("CodingSystem");
		//create System > SOC2010 ...
		ButtonGroup codingSystemButtonGroup=new ButtonGroup();
		for (AssignmentCodingSystem system:AssignmentCodingSystem.values()){
			JRadioButtonMenuItem item=new JRadioButtonMenuItem(selectCodingSystemAction);
			codingSystemButtonGroup.add(item);
			if (system==AssignmentCodingSystem.SOC2010) {
				item.setSelected(true);
			}
			item.setText(system.toString());
			systemMenu.add(item);
		}
		menuBar.add(systemMenu);		
		selectCodingSystemAction.actionPerformed(new ActionEvent(systemMenu, 0, "SOC2010"));

		applicationFrame.setJMenuBar(menuBar);
	}


	private static void setAppProperties(AppProperties appProperties) {
		SOCAssign.appProperties = appProperties;
	}

	private static void fillLastWorkingFileList(){
		lastWorkingFileList.clear();
		for (int i=0;i<lastWorkingFileList.capacity();i++){
			String fileName=appProperties.getProperty("last.file."+i, "");
			File file=new File(fileName);
			if (file.exists()){
				lastWorkingFileList.add(file);
			} else {
				appProperties.remove(fileName);
			}
		}
	}

	private static void updateLastWorkingFileList(File f){		
		// only update the file menu if the file is not on the list...
		if ( lastWorkingFileList.add(f) ) {
			updateFileMenu();

			List<String> props=new ArrayList<String>();
			for (File file:lastWorkingFileList){
				props.add(file.getAbsolutePath());
			}
			appProperties.setListOfProperties("last.file", props);
		}
	}

	private static void updateFileMenu(){

		for (int i=fileMenu.getMenuComponentCount()-4;i>=3;i-- ){
			fileMenu.remove(i);
		}

		for (int i=0;i<lastWorkingFileList.size();i++){
			File file=lastWorkingFileList.get(i);
			JMenuItem menuItem=new JMenuItem(loadDBAction);menuItem.setText(file.getName());menuItem.setActionCommand(file.getAbsolutePath());
			fileMenu.insert(menuItem, 3);
		}
		fileMenu.invalidate();
	}

	private static boolean validResultSelected(){
		return resultsTable.getSelectedRow()>=0;
	}

	private static JFileChooser jfc=new JFileChooser(System.getProperty("user.home"));	
	private static AbstractAction loadAction=new AbstractAction("Load SOCcer Results") {


		@Override
		public void actionPerformed(ActionEvent event) {
			jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
			jfc.setFileFilter(csvFF);
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
					updateLastWorkingFileList(new File(fileName));

					applicationFrame.setTitle(title+" ("+fileName+")");
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(applicationFrame, "Error trying to Open File "+jfc.getSelectedFile().getAbsolutePath(), "SOCassign Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private static FileFilter dbFF=new FileNameExtensionFilter("Working Files (.db)","db");
	private static FileFilter csvFF=new FileNameExtensionFilter("SOCcer Results Files (.csv)","csv");
	private static FileFilter annFF=new FileNameExtensionFilter("Annotation Results Files (.csv)","csv");

	private static AbstractAction loadDBAction=new AbstractAction("Load Previous Work") {

		public File getFile(){
			jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
			int res=jfc.showOpenDialog(applicationFrame);
			if (res==JFileChooser.APPROVE_OPTION){
				appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
				return jfc.getSelectedFile();
			}
			return null;
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			jfc.setFileFilter(dbFF);
			// if the user selected a file from the menu ... load the file
			// else get the file from a JFileChooser...
			File dbFile= (actionEvent.getActionCommand().length() == 0) ? getFile() : new File(actionEvent.getActionCommand());
			if (dbFile == null ) return;

			// if somehow the file does not exist delete it from the lastWorkingFileList...
			if (!dbFile.exists()) {
				lastWorkingFileList.remove(dbFile);
				appProperties.remove(dbFile.getAbsolutePath());
				updateFileMenu();
				return;
			}

			// load the db...
			try {
				testModel.loadPreviousWork(dbFile);
				updateLastWorkingFileList(dbFile);
				resultsTable.invalidate();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(applicationFrame, "Error trying to Open database: (Did you select results instead of a working file?) "+dbFile.getAbsolutePath(), "SOCassign Error", JOptionPane.ERROR_MESSAGE);
			}
			applicationFrame.setTitle(title+" ("+dbFile.getAbsolutePath()+")");
			boolean systemSpecified=testModel.isCodingSystemSpecifiedInResults();
			if (systemSpecified){
				selectCodingSystemAction.setEnabled(false);
				codingSystemPanel.updateCodingSystem(testModel.getCodingSystem());
			}else{
				selectCodingSystemAction.setEnabled(true);
			}

		}
	};
	private static AbstractAction exportAction=new AbstractAction("Export Annotation to CSV") {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (resultsTable.getRowCount()==0) return;
			jfc.setCurrentDirectory(new File(appProperties.getProperty("last.directory", System.getProperty("user.home"))));
			jfc.setFileFilter(annFF);
			int res=jfc.showSaveDialog(applicationFrame);
			if (res==JFileChooser.APPROVE_OPTION){
				try {
					testModel.exportAssignments(jfc.getSelectedFile());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(applicationFrame, "Warning could not write out the annotation: "+e.getMessage());
					e.printStackTrace();
				}
				appProperties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
			}

		}
	};
	private static AbstractAction quitAction=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			testModel.onExit();
			System.exit(0);
		}
	};

	private static AbstractAction selectCodingSystemAction =new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent event) {
			AssignmentCodingSystem codingSystem=AssignmentCodingSystem.valueOf(event.getActionCommand());
			if (testModel.getCodingSystem()!=codingSystem){
				testModel.setCodingSystem(codingSystem);
				codingSystemPanel.updateCodingSystem(codingSystem);
			}
		}
	};

	private static AbstractAction firstJobDescription=new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validResultSelected()) return;			
			resultsTable.setRowSelectionInterval(0, 0);			
			resultsTable.scrollRectToVisible( resultsTable.getCellRect(0, 0, true) );
		}
	};
	private static AbstractAction nextJobDescription=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			logger.finer("NJD called!!!");
			if (!validResultSelected()) return;

			int nextRow=(resultsTable.getSelectedRow()+1)%resultsTable.getRowCount();
			resultsTable.setRowSelectionInterval(nextRow, nextRow);

			resultsTable.scrollRectToVisible( resultsTable.getCellRect(nextRow, 0, true) );
		}
	};
	private static AbstractAction previousJobDescription=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validResultSelected()) return;

			int nextRow=(resultsTable.getSelectedRow()+resultsTable.getRowCount()-1)%resultsTable.getRowCount();
			resultsTable.setRowSelectionInterval(nextRow, nextRow);

			resultsTable.scrollRectToVisible( resultsTable.getCellRect(nextRow, 0, true) );
		}
	};
	private static AbstractAction lastJobDescription=new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validResultSelected()) return;
			int row=resultsTable.getRowCount()-1;
			resultsTable.setRowSelectionInterval(row, row);			
			resultsTable.scrollRectToVisible( resultsTable.getCellRect(row, 0, true) );
		}
	};
	private static AbstractAction addSelectedAssignment=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validResultSelected()) return;

			String txt=assignmentTF.getText();
			if (testModel.getCodingSystem().matches(txt)){
				testModel.addSelection(assignmentTF.getText());
			}else{
				JOptionPane.showMessageDialog(applicationFrame, "Assignment is not formatted appropriately "+txt, "SOCassign Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
	private static AbstractAction removeSelectedAssignment = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!validResultSelected() || assigmentList.getSelectedIndex()<0) return;
			testModel.removeElementAt(assigmentList.getSelectedIndex());
		}
	};

	private static AbstractAction increaseSelection = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!validResultSelected()) return;

			int selectedIndex=assigmentList.getSelectedIndex();
			if (selectedIndex>0){
				testModel.increaseSelection(selectedIndex);
				assigmentList.setSelectedIndex(selectedIndex-1);
			}
		}
	};

	private static AbstractAction decreaseSelection = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!validResultSelected()) return;

			int selectedIndex=assigmentList.getSelectedIndex();
			if (selectedIndex<0) return;

			testModel.decreaseSelection(selectedIndex);
			if (selectedIndex<assigmentList.getModel().getSize()-1){
				assigmentList.setSelectedIndex(selectedIndex+1);
			}
		}
	};

	private static AbstractAction toggleFlagAction=new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int row= resultsTable.getSelectedRow();
			if (row>=0){
				int selectedRow=resultsTable.convertRowIndexToModel( row );
				boolean flagValue=(Boolean)resultsTable.getValueAt(selectedRow, 0);
				int rowID=(Integer)resultsTable.getValueAt(selectedRow, 1);
				System.out.println("FLAG TOGGLER: (row) "+selectedRow+" (rowID) "+rowID+" (current value) "+flagValue);
				testModel.updateFlag(selectedRow, flagValue?FlagType.NOT_FLAGGED:FlagType.FLAGGED);
				testModel.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
			}else{
				System.out.println("bad row selected..."+row);
			}
		}
	};
	private static ListSelectionListener resultsTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent event) {
			if (!validResultSelected()) return;

			assignmentTF.setText("");
			assigmentList.clearSelection();
			codingSystemPanel.clearSelection();

			int selectRow=resultsTable.getSelectedRow();
			selectRow=resultsTable.convertRowIndexToModel(selectRow);
			testModel.setSelectedResult(selectRow);
		}
	};

	/**
	 * Use a MouseListener instead of a TreeSelectionListener to handle double clicks..
	 */
	private static MouseAdapter codingSystemMouseAdapter = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {

			if (!validResultSelected() || codingSystemPanel.getSelectedPathCount()<=1) return;

			OccupationCode code=codingSystemPanel.getLastSelectedPathComponent();
			if (code.isLeaf() && e.getClickCount()>1){
				logger.finer("Setting the Assigned SOC...");
				testModel.addSelection(code.getName());
			}
			assignmentTF.setText(code.getName());
		};
	};
	static MouseAdapter selectAnotherSoccerResultListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent event) {

			int row=singleJobDescriptionTable.rowAtPoint(event.getPoint());
			row=singleJobDescriptionTable.convertRowIndexToModel(row);
			OccupationCode code= testModel.getOccupationCodeForTop10Row(row);
			codingSystemPanel.selectOccupation(code);			
			if (event.getClickCount()>=2){
				logger.finer("selected row: "+code.getName());
				testModel.addSelection(code.getName());
			}
		}
	};



	/*
	private static TreeSelectionListener codingSystemTreeListener =new TreeSelectionListener() {
	
		@Override
		public void valueChanged(TreeSelectionEvent event) {
			if (!validResultSelected()) return;
			if (event.getPath().getPathCount()==1) return;
	
			OccupationCode code=codingSystemPanel.getLastSelectedPathComponent();
			if (code==null) return;
	
			if (code.isLeaf()){
				logger.finer("Setting the Assigned SOC...");
				testModel.addSelection(code.getName());
			}
			assignmentTF.setText(code.getName());
		}
	};


	private static JPopupMenu popup=new JPopupMenu();

	private static KeyListener myKeyListener=new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			if ( (e.getKeyChar() >= KeyEvent.VK_0) && (e.getKeyChar()<=KeyEvent.VK_9) ){
				System.out.println(assignmentTF.getText()+e.getKeyChar()+" "+assignmentTF.getX());
				popup.setLocation(assignmentTF.getLocation());
			}else{
			}
		}
	};
*/
	private static ListSelectionListener assignmentListSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent event) {
			int row=event.getFirstIndex();
			Object element=assigmentList.getModel().getElementAt(row);
			// the element is "" return;
			if (element instanceof String){
				return;
			}
			OccupationCode code=(OccupationCode)assigmentList.getModel().getElementAt(row);
			codingSystemPanel.selectOccupation(code);
		}
	};

	public SOCAssign() {}

	public static void main(String[] args) {

		setAppProperties(AppProperties.getDefaultAppProperties("SOCassign"));
		fillLastWorkingFileList();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 *  Closes the database connection when the window is closed.
	 */
	public static WindowListener windowListener=new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			testModel.onExit();
		}
	};

	private static Color PALE_GREEN=new Color(152, 251, 152);
	private static DecimalFormat fmt1=new DecimalFormat("0.0000");
	private static DecimalFormat fmt2=new DecimalFormat("0.000E0");

	private static TableCellRenderer selectedResultRenderer = new DefaultTableCellRenderer(){
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column<3){
				setHorizontalAlignment(JLabel.CENTER);

				if (column==2){
					double val=Double.parseDouble(getText());
					if (val<1e-4){
						setText(fmt2.format(val));
					}else{
						setText(fmt1.format(val));
					}
				}

			}
			return this;
		};

	};

	private static TableCellRenderer flagRenderer = new DefaultTableCellRenderer(){
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (fontAwesome!=null){
				setFont(fontAwesome);
				setForeground(Color.RED);
				setText(((Boolean)value)?"\uf024":"");
			}
			if (!isSelected){
				if ( testModel.isRowAssigned(resultsTable.convertRowIndexToModel(row))) {
					setBackground(PALE_GREEN);
				} else {
					setBackground(Color.WHITE);
				}
			}
			return this;
		}
	};

	private static TableCellRenderer resultsRenderer = new DefaultTableCellRenderer(){

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column==2||column==4){
				setToolTipText(value.toString());
			}else if (column==5){
				AssignmentCodingSystem codingSystem=testModel.getCodingSystem();
				OccupationCode code=codingSystem.getOccupationalCode(value.toString());
				if (code!=null){
					setToolTipText(code.getTitle()+" - "+code.getDescription());
				}else{
					setToolTipText(null);
				}
			} else{
				setToolTipText(null);
			}

			if (!isSelected){
				if ( testModel.isRowAssigned(resultsTable.convertRowIndexToModel(row))) {
					setBackground(PALE_GREEN);
				} else {
					setBackground(Color.WHITE);
				}
			}
			return this;
		};

	};
}


