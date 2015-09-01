package gov.nih.cit.consensus;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.CodingSystemPanel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;
import gov.nih.cit.util.AppProperties;
import gov.nih.cit.util.RollingList;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.java2s.swingx.Java2sAutoComboBox;

public class SOCconsensus{

	private static CodingSystemPanel codingSystemPanel=new CodingSystemPanel();
	private static ConsensusTableModel consensusTableModel=new ConsensusTableModel();
	private static JTable reviewerTable;
	private static AssignmentCodingSystem codingSystem=null;
	private static JLabel typeLabel=new JLabel();
	private static JFrame jf;
	private static JMenu fileMenu;
	private static ConsensusExporter consensusExporter = new ConsensusExporter();
	private static AppProperties properties=AppProperties.getDefaultAppProperties("SOCconsensus");
	/** A list that holds the last 3 files used */
	private static RollingList<File> lastWorkingFileList=new RollingList<File>(3);
	
	
	private static void createAndShowGUI() {
		codingSystemPanel.updateCodingSystem(AssignmentCodingSystem.SOC2010);
		OccupationCodeRenderer cellRenderer=new OccupationCodeRenderer();


		consensusTableModel.addTableModelListener(tableModelListener);
		reviewerTable=new JTable(consensusTableModel);
		reviewerTable.setRowHeight(20);
		consensusTableModel.addPropertyChangeListener(codingSystemChangeListener);
		reviewerTable.setDefaultRenderer(OccupationCode.class, cellRenderer);
		reviewerTable.setDefaultRenderer(String.class, cellRenderer);
		reviewerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		reviewerTable.addMouseListener(mouseListener);
		tableModelListener.tableChanged(null);
		
		jf=new JFrame("Consensus");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setJMenuBar(buildMenuBar());


		JPanel contentPanel=new JPanel(new BorderLayout());
		contentPanel.add(codingSystemPanel,BorderLayout.EAST);

		JPanel reviewPanel=new JPanel(new BorderLayout());
		reviewPanel.add(new JScrollPane(reviewerTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.CENTER);
		reviewPanel.add(typeLabel,BorderLayout.SOUTH);
		contentPanel.add(reviewPanel,BorderLayout.CENTER);

		jf.addWindowListener(windowListener);
		jf.getContentPane().add(contentPanel);
		jf.pack();
		jf.setVisible(true);
	}

	private static JMenuBar buildMenuBar(){
		JMenuBar menuBar=new JMenuBar();

		fileMenu=new JMenu("File");
		fileMenu.add(loadAssignmentAction);
		JMenuItem loadDBMI=new JMenuItem(loadWorkingDatabaseAction);loadDBMI.setActionCommand("");
		fileMenu.add(loadDBMI);
		fileMenu.add(new JSeparator());
		
		List<File> files=properties.getListOfFiles("last.file");
		lastWorkingFileList.addAll(files);
		for (int i=0;i<lastWorkingFileList.size();i++){
			File file=lastWorkingFileList.get(i);
			JMenuItem menuItem=new JMenuItem(loadWorkingDatabaseAction);menuItem.setText(file.getName());menuItem.setActionCommand(file.getAbsolutePath());
			fileMenu.add(menuItem);
		}
		
		fileMenu.add(new JSeparator());
		fileMenu.add(exportAction);
		fileMenu.add(quitAction);
		
		menuBar.add(fileMenu);

		return menuBar;
	}
	
	public static void addDBFileToLastFileList(File dbFile){
		lastWorkingFileList.add(dbFile);
		properties.setListOfFiles("last.file", lastWorkingFileList);
		updateFileMenu();
	}

	private static AbstractAction loadAssignmentAction=new AbstractAction("Load Assignments") {

		JFileChooser jfc=new JFileChooser(new File(properties.getProperty("last.directory", System.getProperty("user.home"))));

		@Override
		public void actionPerformed(ActionEvent arg0) {
			jfc.setMultiSelectionEnabled(true);
			
			int retVal=jfc.showOpenDialog(codingSystemPanel);
			if (retVal==JFileChooser.APPROVE_OPTION){
				File[] files=jfc.getSelectedFiles();
				for (File file:files){
					try {
						String name=JOptionPane.showInputDialog(codingSystemPanel, "Reviewer for "+file.getAbsolutePath(), "Coder-"+(consensusTableModel.getReviewerNames().length+1));
						consensusTableModel.addReviewerAssignment(name,file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	};
	
	private static AbstractAction loadWorkingDatabaseAction=new AbstractAction("Load Working File") {

		/**
		 * gets a file from a JFileChooser
		 * 
		 * @return selected file
		 */
		public File getFile(){			
			int res=jfc.showOpenDialog(null);
			if (res==JFileChooser.APPROVE_OPTION){
				properties.setProperty("last.directory", jfc.getCurrentDirectory().getAbsolutePath());
				return jfc.getSelectedFile();
			}
			return null;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			jfc.setFileFilter(dbFF);

			// if the user selected a file from the file menu, load it.
			// else get it from the JFileChooser...
			File dbFile= (event.getActionCommand().length() == 0) ? getFile() : new File(event.getActionCommand());
			// if the user canceled the JFileChoose return...
			if (dbFile == null ) return;
			
			// if somehow the file does not exist delete it from the lastWorkingFileList...
			if (!dbFile.exists()) {
				lastWorkingFileList.remove(dbFile);
				properties.setListOfFiles("last.file", lastWorkingFileList);
				updateFileMenu();
				return;
			}
			
			try {
				consensusTableModel.loadFromDatabase(dbFile);
				addDBFileToLastFileList(dbFile);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error trying to Open database: (Did you select results instead of a working file?) "+dbFile.getAbsolutePath(), "SOCconsensus Error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	};
	private static void updateFileMenu(){

		for (int i=fileMenu.getMenuComponentCount()-4;i>=3;i-- ){
			fileMenu.remove(i);
		}

		for (int i=0;i<lastWorkingFileList.size();i++){
			File file=lastWorkingFileList.get(i);
			JMenuItem menuItem=new JMenuItem(loadWorkingDatabaseAction);menuItem.setText(file.getName());menuItem.setActionCommand(file.getAbsolutePath());
			fileMenu.insert(menuItem, 3);
		}
		fileMenu.invalidate();
	}
	private static AbstractAction exportAction=new AbstractAction("Export Annotation to CSV") {
	
		@Override
		public void actionPerformed(ActionEvent event) {
			jfc.setFileFilter(annFF);
			int res=jfc.showSaveDialog(jf);
			if (res==JFileChooser.APPROVE_OPTION){
				List<DataRow> rowsToExport = consensusTableModel.getConsensusDataRows();
				try {
					consensusExporter.export(rowsToExport, jfc.getSelectedFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private static AbstractAction quitAction=new AbstractAction("Quit") {

		@Override
		public void actionPerformed(ActionEvent e) {
			consensusTableModel.onExit();
			System.exit(0);
		}
	};
	
	private static List<String> listOfValidCodes;

	private static PropertyChangeListener codingSystemChangeListener=new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName()==ConsensusTableModel.CODING_SYSTEM_CHANGED_KEY){
				System.out.println("-- setting the coding system...");
				codingSystem=(AssignmentCodingSystem)event.getNewValue();
				codingSystemPanel.updateCodingSystem(codingSystem);
				
				listOfValidCodes=codingSystem.getListOfCodesAsStrings();
				listOfValidCodes.add(0, "");
				listOfValidCodes.remove(codingSystem.toString());

				// at some point we want to make this a list of OccupationCodes, not Strings...
				Java2sAutoComboBox<String> comboBox=new Java2sAutoComboBox<String>(listOfValidCodes);
								
				reviewerTable.setDefaultEditor(String.class, new DefaultCellEditor(comboBox));
				reviewerTable.invalidate();
			}
		}
	};


	public SOCconsensus() {}
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}


	private static TableModelListener tableModelListener=new TableModelListener() {
		
		@Override
		public void tableChanged(TableModelEvent e) {
			reviewerTable.getColumnModel().getColumn(0).setPreferredWidth(50);
			for (int i=1;i<reviewerTable.getColumnCount();i++){
				if (reviewerTable.getColumnName(i).startsWith("Coder")){
					reviewerTable.getColumnModel().getColumn(i).setPreferredWidth(75);	
				}else{
					reviewerTable.getColumnModel().getColumn(i).setPreferredWidth(100);	
				}
				
			}
			
		}
		
	};


	private static MouseListener mouseListener = new MouseAdapter() {

		public void mouseClicked(MouseEvent event) {

			if (event.getClickCount()>1){
				int row=reviewerTable.rowAtPoint(event.getPoint());
				int col=reviewerTable.columnAtPoint(event.getPoint());				
				int columnCount=reviewerTable.getColumnCount();

				Object v=reviewerTable.getValueAt(row, col);
				if (v==null) return;

				if (v instanceof OccupationCode){
					OccupationCode selectedValue=(OccupationCode)v;

					String con1=(String)reviewerTable.getValueAt(row, columnCount-2);

					int addToColumn= (con1==null || con1.trim().length()==0 )?columnCount-2:columnCount-1;
					reviewerTable.setValueAt(selectedValue.getName(), row, addToColumn);
					
					codingSystemPanel.setSelection(selectedValue);
				}
			}
		};
	}; 

	static JFileChooser jfc=new JFileChooser(properties.getProperty("last.directory", System.getProperty("user.home")));
	static FileFilter annFF=new FileNameExtensionFilter("Annotation Results Files (.csv)","csv");
	static FileFilter dbFF=new FileNameExtensionFilter("Working SQLite Files (.db)","db");
	
	/**
	 *  Closes the database connection when the window is closed.
	 */
	public static WindowListener windowListener=new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("window closing...");
			consensusTableModel.onExit();
		}
	};
}
