package gov.nih.cit.socassign;

import gov.nih.cit.socassign.action.AddAutocompleteAssignmentAction;
import gov.nih.cit.socassign.action.AddSelectedAssignmentAction;
import gov.nih.cit.socassign.action.DecreaseAutocompleteIndexAction;
import gov.nih.cit.socassign.action.DecreaseSelectionAction;
import gov.nih.cit.socassign.action.EmptyAction;
import gov.nih.cit.socassign.action.EnterAutocompleteFieldAction;
import gov.nih.cit.socassign.action.ExportAnnotationAction;
import gov.nih.cit.socassign.action.FirstJobDescriptionAction;
import gov.nih.cit.socassign.action.IncreaseSelectionAction;
import gov.nih.cit.socassign.action.LastJobDescriptionAction;
import gov.nih.cit.socassign.action.LoadPreviousWorkAction;
import gov.nih.cit.socassign.action.LoadSoccerResultsAction;
import gov.nih.cit.socassign.action.NextJobDescriptionAction;
import gov.nih.cit.socassign.action.PreviousJobDescriptionAction;
import gov.nih.cit.socassign.action.QuitAction;
import gov.nih.cit.socassign.action.RemoveSelectedAssignmentAction;
import gov.nih.cit.socassign.action.SelectCodingSystemAction;
import gov.nih.cit.socassign.action.ToggleFlagAction;
import gov.nih.cit.socassign.action.VisibilityConditionalAction;
import gov.nih.cit.socassign.adapter.AutocompleteDoubleClickAdapter;
import gov.nih.cit.socassign.adapter.CloseEventAdapter;
import gov.nih.cit.socassign.adapter.CodingSystemAdapter;
import gov.nih.cit.socassign.adapter.SelectAnotherSoccerResultAdapter;
import gov.nih.cit.socassign.codingsystem.OccupationCode;
import gov.nih.cit.socassign.listener.AssignmentSelectionListener;
import gov.nih.cit.socassign.listener.AssignmentTextFieldFocusListener;
import gov.nih.cit.socassign.listener.AssignmentTextFieldListener;
import gov.nih.cit.socassign.listener.AutocompleteBlurListener;
import gov.nih.cit.socassign.listener.ResultsTableSelectionListener;
import gov.nih.cit.socassign.renderer.FlagRenderer;
import gov.nih.cit.socassign.renderer.ResultsRenderer;
import gov.nih.cit.socassign.renderer.SelectedResultRenderer;
import gov.nih.cit.util.AppProperties;
import gov.nih.cit.util.RollingList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

/**
 * SOCAssign is the main class.
 *
 * @author Daniel Russ
 *
 */
public class SOCAssign {
	private static final AbstractAction loadAction = new LoadSoccerResultsAction();
	private static final AbstractAction loadDBAction = new LoadPreviousWorkAction();
	private static final AbstractAction exportAction = new ExportAnnotationAction();
	private static final AbstractAction quitAction = new QuitAction();
	private static final AbstractAction selectCodingSystemAction = new SelectCodingSystemAction();
	private static final AbstractAction firstJobDescription = new FirstJobDescriptionAction();
	private static final AbstractAction nextJobDescription = new NextJobDescriptionAction();
	private static final AbstractAction decreaseAutocompleteIndexAction = new DecreaseAutocompleteIndexAction();
	private static final AbstractAction enterAutocompleteFieldAction = new EnterAutocompleteFieldAction();
	private static final AbstractAction addAutocompleteAssignmentAction = new AddAutocompleteAssignmentAction();
	private static final AbstractAction previousJobDescription = new PreviousJobDescriptionAction();
	private static final AbstractAction lastJobDescription = new LastJobDescriptionAction();
	private static final AbstractAction addSelectedAssignment = new AddSelectedAssignmentAction();
	private static final AbstractAction removeSelectedAssignment = new RemoveSelectedAssignmentAction();
	private static final AbstractAction increaseSelection = new IncreaseSelectionAction();
	private static final AbstractAction decreaseSelection = new DecreaseSelectionAction();
	private static final AbstractAction toggleFlagAction = new ToggleFlagAction();
	private static final AbstractAction emptyAction = new EmptyAction();
	private static final MouseAdapter autocompleteDoubleClickAdapter = new AutocompleteDoubleClickAdapter();
	private static final MouseAdapter codingSystemMouseAdapter = new CodingSystemAdapter(); // Use a MouseListener instead of a TreeSelectionListener to handle double clicks.
	private static final MouseAdapter selectAnotherSoccerResultListener = new SelectAnotherSoccerResultAdapter();
	private static final DocumentListener assignmentTextFieldListener = new AssignmentTextFieldListener();
	private static final FocusListener autocompleteBlurListener = new AutocompleteBlurListener();
	private static final FocusListener assignmentTextFieldFocusListener = new AssignmentTextFieldFocusListener();
	private static final ListSelectionListener resultsTableSelectionListener = new ResultsTableSelectionListener();
	private static final ListSelectionListener assignmentListSelectionListener = new AssignmentSelectionListener();
	private static final WindowListener windowListener = new CloseEventAdapter(); //Closes the database connection when the window is closed.
	private static final TableCellRenderer selectedResultRenderer = new SelectedResultRenderer();
	private static final TableCellRenderer flagRenderer = new FlagRenderer();
	private static final TableCellRenderer resultsRenderer = new ResultsRenderer();

	/** holds most of the data required by the gui components */
	private static SOCAssignModel testModel = SOCAssignModel.getInstance();
	/** A text field where coders can type in a code */
	private static JTextField assignmentTF = new JTextField(8);
	private static JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
	private static JTextArea commentTA=new JTextArea(testModel.getCommentDocument(),"",10,60); 
	
	/** Autocomplete fields */
	private static DefaultListModel<String> autocompleteList = new DefaultListModel<String>();
	private static JList<String> autocompleteField = new JList<String>(autocompleteList);
	private static JScrollPane autocompleteScroll = SOCAssignGlobals.initializeAutocompleteScroll(new JScrollPane(autocompleteField));
	/** A list that holds the last 3 files used */
	private static RollingList<File> lastWorkingFileList = SOCAssignGlobals.initializeLastWorkingFileList(new RollingList<File>(3));
	/** Stores information (the last files used) in a properties file so it will be remembered next time the program starts*/
	private static AppProperties appProperties;
	private static Font fontAwesome;

	/**
	 * "Main" method of the application should be run on the Event Dispatch Thread.
	 */
	public static void createAndShowGUI() {
		JComponent leftPanel = new JScrollPane(SOCAssignGlobals.intializeResultsTable(createLeftPanel()), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		SpringLayout layout = new SpringLayout();
		JComponent centerPanel = createCenterPanel(layout);
		CodingSystemPanel rightPanel = SOCAssignGlobals.intializeCodingSystemPanel(createRightPanel());
		JComponent mainPanel = createMainPanel(leftPanel, centerPanel, rightPanel);
		SOCAssignGlobals.intializeApplicationFrame(createApplicationFrame(mainPanel));
		addAutoCompleteBox(layout,centerPanel);
	}

	private static JFrame createApplicationFrame(JComponent contentPane) {
		// create the application frame ...
		JFrame applicationFrame = new JFrame(SOCAssignGlobals.title);
		applicationFrame.addWindowListener(windowListener);
		applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createMenus(applicationFrame);

		applicationFrame.setContentPane(contentPane);
		applicationFrame.pack();
		applicationFrame.setVisible(true);
		return applicationFrame;
	}

	private static void createMenus(JFrame applicationFrame) {
		JMenu fileMenu = SOCAssignGlobals.initializeFileMenu(new JMenu("File"));
		JMenuBar menuBar = new JMenuBar();

		// fileMenu is a field because it needs to be updated when a user selects a database.
		// create File > load
		fileMenu.add(new JMenuItem(loadAction));

		// create File > load previous coding
		JMenuItem loadDBMI = new JMenuItem(loadDBAction);
		loadDBMI.setActionCommand("");
		fileMenu.add(loadDBMI);

		fileMenu.add(new JSeparator());
		// create File > LAST 3 Working Files...
		if (lastWorkingFileList.size() > 0) {
			for (File file:lastWorkingFileList.asRollingStack()) {
				JMenuItem menuItem = new JMenuItem(loadDBAction);
				menuItem.setText(file.getName());
				menuItem.setActionCommand(file.getAbsolutePath());
				fileMenu.add(menuItem);
			}
		}
		fileMenu.add(new JSeparator());

		fileMenu.add(exportAction);

		// create File > Quit
		JMenuItem quitMI = new JMenuItem(quitAction);
		quitMI.setText("Quit");
		fileMenu.add(quitMI);
		menuBar.add(fileMenu);

		// create System
		JMenu systemMenu = new JMenu("CodingSystem");
		//create System > SOC2010 ...
		ButtonGroup codingSystemButtonGroup = new ButtonGroup();
		for (AssignmentCodingSystem system:AssignmentCodingSystem.values()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(selectCodingSystemAction);
			codingSystemButtonGroup.add(item);
			if (system == AssignmentCodingSystem.SOC2010) {
				item.setSelected(true);
			}
			item.setText(system.toString());
			systemMenu.add(item);
		}
		menuBar.add(systemMenu);
		selectCodingSystemAction.actionPerformed(new ActionEvent(systemMenu, 0, "SOC2010"));

		applicationFrame.setJMenuBar(menuBar);
	}

	private static JPanel createMainPanel(JComponent leftPanel, JComponent centerPanel, JComponent rightPanel) {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(leftPanel,BorderLayout.WEST);
		mainPanel.add(centerPanel,BorderLayout.CENTER);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		// if you are not in the text box, The "<" key selected the previous row job description ,SHIFT-"<" the first.
		// The ">" key selects the next job description and shift-">" the last.
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,0), "PreviousJobDescription");
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD,0), "NextJobDescription");
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,KeyEvent.SHIFT_DOWN_MASK), "FirstJobDescription");
		mainPanel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD,KeyEvent.SHIFT_DOWN_MASK), "LastJobDescription");
		mainPanel.getActionMap().put("LastJobDescription", lastJobDescription);
		mainPanel.getActionMap().put("NextJobDescription", nextJobDescription);
		mainPanel.getActionMap().put("PreviousJobDescription", previousJobDescription);
		mainPanel.getActionMap().put("FirstJobDescription", firstJobDescription);
		return mainPanel;
	}

	private static JTable createLeftPanel() {
		JTable resultsTable = new JTable(testModel.getTableModel());
		// format the results table ...
		resultsTable.setAutoCreateRowSorter(true);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(resultsTableSelectionListener);
		resultsTable.setDefaultRenderer(String.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Double.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Integer.class, resultsRenderer);
		resultsTable.setDefaultRenderer(Boolean.class, flagRenderer);
		resultsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F,0), "ToggleFlag");
		resultsTable.getActionMap().put("ToggleFlag", toggleFlagAction);
		return resultsTable;
	}

	private static JComponent createCenterPanel(SpringLayout layout) {
		// create elements
		JTextField assignmentTF = SOCAssignGlobals.initializeAssignmentTF(createAssignmentTextField());
		JPanel buttonPanel = createButtonPanel();
		JScrollPane tableScroll = new JScrollPane(SOCAssignGlobals.initializeSingleJobDescriptionTable(createSingleJobDescriptionTable()),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane assignmentScroll = new JScrollPane(SOCAssignGlobals.initializeAssignmentList(createAssignmentList()), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane infoScroll = new JScrollPane(new JList<String>(testModel.getSingleJobDescriptionListModel()),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane	commentScroll = new JScrollPane(commentTA,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		// add Titles to the JScrollPanes..
		assignmentScroll.setColumnHeaderView(new JLabel("Assignments"));
		infoScroll.setColumnHeaderView(new JLabel("Job Description"));
		commentScroll.setColumnHeaderView(new JLabel("Comments"));
		

		// consume periods and comma when typing in the comment box and assignmentTextField or the job description will change while typing in comments.
		List<JTextComponent> componentList=Arrays.asList(commentTA,assignmentTF);
		int[] keystrokes={KeyEvent.VK_PERIOD,KeyEvent.VK_COMMA};
		for (JTextComponent component:componentList){
			component.getActionMap().put("consumeEvent", emptyAction);
			InputMap inputMap=component.getInputMap();
			for (int keystroke:keystrokes){
				inputMap.put(KeyStroke.getKeyStroke(keystroke,0), "consumeEvent");
				inputMap.put(KeyStroke.getKeyStroke(keystroke,KeyEvent.SHIFT_DOWN_MASK), "consumeEvent");
			}
		}
		
		
		// create panel
		JComponent centerPanel = new JLayeredPane();
		centerPanel.setLayout(layout);
		
		//add all elements in order
		centerPanel.add(assignmentTF,JLayeredPane.DEFAULT_LAYER);
		centerPanel.add(buttonPanel,JLayeredPane.DEFAULT_LAYER);
		centerPanel.add(assignmentScroll,JLayeredPane.DEFAULT_LAYER);
		centerPanel.add(tableScroll,JLayeredPane.DEFAULT_LAYER);
		centerPanel.add(infoScroll,JLayeredPane.DEFAULT_LAYER);
		centerPanel.add(commentScroll,JLayeredPane.DEFAULT_LAYER);
		
		// setup visual layout
		centerPanel.setPreferredSize(new Dimension(458,721));
		layout.putConstraint(SpringLayout.WEST, assignmentTF, 0, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.EAST, assignmentTF, 0, SpringLayout.EAST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, assignmentTF, 0, SpringLayout.NORTH, centerPanel);
		
		alignSpring(layout,buttonPanel,centerPanel,assignmentTF);
		alignSpring(layout,assignmentScroll,centerPanel,buttonPanel);
		alignSpring(layout,infoScroll,centerPanel,assignmentScroll);
		alignSpring(layout,commentScroll,centerPanel,infoScroll);
		alignSpring(layout,tableScroll,centerPanel,commentScroll);

		
		layout.putConstraint(SpringLayout.SOUTH, tableScroll, 0, SpringLayout.SOUTH, centerPanel);
		return centerPanel;
	}

	private static void alignSpring(SpringLayout layout, JComponent child, JComponent parent, JComponent predecessor) {
		alignSpring(layout,child,parent,predecessor,0);
	}

	private static void alignSpring(SpringLayout layout, JComponent child, JComponent parent, JComponent predecessor, int widthBuffer) {
		layout.putConstraint(SpringLayout.WEST, child, widthBuffer, SpringLayout.WEST, parent);
		layout.putConstraint(SpringLayout.EAST, child, -widthBuffer, SpringLayout.EAST, parent);
		layout.putConstraint(SpringLayout.NORTH, child, 0, SpringLayout.SOUTH, predecessor);
	}

	private static JTextField createAssignmentTextField() {
		// if you hit the up/down arrow in the textfield, it switches the selected soccerResult
		assignmentTF.setEditable(true);
		assignmentTF.setAction(addSelectedAssignment);
		assignmentTF.getDocument().addDocumentListener(assignmentTextFieldListener);
		assignmentTF.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
		assignmentTF.getActionMap().put("DOWN", new VisibilityConditionalAction(autocompleteScroll,enterAutocompleteFieldAction,nextJobDescription));
		assignmentTF.getInputMap().put(KeyStroke.getKeyStroke("UP"), "UP");
		assignmentTF.getActionMap().put("UP", previousJobDescription);
		assignmentTF.addFocusListener(assignmentTextFieldFocusListener);
		return assignmentTF;
	}

	private static JPanel createButtonPanel() {
		// move selection up
		JButton moveAssignmentUp = new JButton(increaseSelection);
		buttonPanel.add(moveAssignmentUp);
		// move selection down
		JButton moveAssignmentDown = new JButton(decreaseSelection);
		buttonPanel.add(moveAssignmentDown);
		// remove assignment button
		JButton removeSOCAssignment = new JButton(removeSelectedAssignment);
		buttonPanel.add(removeSOCAssignment);
		// load the icons on the button. FontAwesome is an open-source font distributed with SOCassign.
		// if there is a problem, use the icons that I drew. They are not as pretty.
		fontAwesome = SOCAssignGlobals.getFontAwesome();
		if (fontAwesome == null) {
			removeSOCAssignment.setFont(fontAwesome);
			removeSOCAssignment.setForeground(Color.BLUE);
			removeSOCAssignment.setText("\uf056");
			moveAssignmentUp.setFont(fontAwesome);
			moveAssignmentUp.setForeground(Color.BLUE);
			moveAssignmentUp.setText("\uf0aa");
			moveAssignmentDown.setFont(fontAwesome);
			moveAssignmentDown.setForeground(Color.BLUE);
			moveAssignmentDown.setText("\uf0ab");
		} else {
			try {
				removeSOCAssignment.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/remove-blue.png"))));
				moveAssignmentUp.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/up-blue.png"))));
				moveAssignmentDown.setIcon(new ImageIcon(ImageIO.read(SOCAssign.class.getResourceAsStream("images/down-blue.png"))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buttonPanel;
	}

	private static JTable createSingleJobDescriptionTable() {
		JTable singleJobDescriptionTable = new JTable(testModel.getTop10Model());
		// and the selected soccer result table...
		singleJobDescriptionTable.setAutoCreateRowSorter(true);
		singleJobDescriptionTable.addMouseListener(selectAnotherSoccerResultListener);
		singleJobDescriptionTable.setDefaultRenderer(Integer.class, selectedResultRenderer);
		singleJobDescriptionTable.setDefaultRenderer(Double.class, selectedResultRenderer);
		return singleJobDescriptionTable;
	}

	private static JList<OccupationCode> createAssignmentList() {
		JList<OccupationCode> assignmentList = new JList<OccupationCode>(testModel.getAssignmentListModel());
		assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		assignmentList.addListSelectionListener(assignmentListSelectionListener);
		assignmentList.setVisibleRowCount(4);
		return assignmentList;
	}

	private static CodingSystemPanel createRightPanel() {
		CodingSystemPanel codingSystemPanel = new CodingSystemPanel();
		codingSystemPanel.addMouseListenerToJTree(codingSystemMouseAdapter);
		return codingSystemPanel;
	}

	private static void addAutoCompleteBox(SpringLayout layout, JComponent centerPanel) {
		autocompleteField.setFont(UIManager.getDefaults().getFont("ScrollPane.font").deriveFont(Font.ITALIC));
		autocompleteField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		autocompleteField.addFocusListener(autocompleteBlurListener);
		autocompleteField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), "UP");
		autocompleteField.getActionMap().put("UP",decreaseAutocompleteIndexAction);
		autocompleteField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "ENTER");
		autocompleteField.getActionMap().put("ENTER",addAutocompleteAssignmentAction);
		autocompleteField.addMouseListener(autocompleteDoubleClickAdapter);
		centerPanel.add(autocompleteScroll,JLayeredPane.POPUP_LAYER);
		alignSpring(layout,autocompleteScroll,centerPanel,assignmentTF,5);
		autocompleteScroll.setVisible(false);
	}

	private static void fillLastWorkingFileList() {
		lastWorkingFileList.clear();
		for (int i = 0;i < lastWorkingFileList.capacity();i++) {
			String fileName = appProperties.getProperty("last.file." + i, "");
			File file = new File(fileName);
			if (file.exists()) {
				lastWorkingFileList.add(file);
			} else {
				appProperties.remove(fileName);
			}
		}
	}

	public static void main(String[] args) {
		appProperties = SOCAssignGlobals.initializeAppProperties(AppProperties.getDefaultAppProperties("SOCassign"));
		fillLastWorkingFileList();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
