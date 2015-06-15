package gov.nih.cit.consensus;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.CodingSystemPanel;
import gov.nih.cit.socassign.codingsysten.OccupationCode;

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
		fileMenu.add(loadWorkingDatabaseAction);
		fileMenu.add(new JSeparator());
		fileMenu.add(exportAction);
		fileMenu.add(new JSeparator());
		fileMenu.add(quitAction);
		
		menuBar.add(fileMenu);

		return menuBar;
	}

	private static AbstractAction loadAssignmentAction=new AbstractAction("Load Assignments") {

		JFileChooser jfc=new JFileChooser("/tmp");

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

		JFileChooser jfc=new JFileChooser("/tmp");
		
		@Override
		public void actionPerformed(ActionEvent e) {
			jfc.setFileFilter(dbFF);
			int res=jfc.showOpenDialog(jf);
			if (res==JFileChooser.APPROVE_OPTION){
				File selFile = jfc.getSelectedFile();
				try {
					consensusTableModel.loadFromDatabase(selFile);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	};

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
	//	private static FilteringComboBoxModel<String> comboBoxModel;
	//	private static JComboBox comboBox;
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
				
				Java2sAutoComboBox comboBox=new Java2sAutoComboBox(listOfValidCodes);
//				comboBox.setEditable(true);
								
				//reviewerTable.setDefaultEditor(OccupationCode.class, cellEditor);
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

/*
 * 
	public static StringBuilder prefixSB=new StringBuilder();
	public static MyPredicate predicate=new MyPredicate(prefixSB);

	static KeyListener l=new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {


			switch (e.getKeyCode()) {
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
			case KeyEvent.VK_MINUS:
				prefixSB.append(e.getKeyChar());
				System.out.println(prefixSB);
				if (!validPrefix()){
					// remove last the char...
					prefixSB.setLength(prefixSB.length()-1);
					e.consume();
				}

				break;
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				if (prefixSB.length()>0){
					prefixSB.setLength(prefixSB.length()-1);
				}
				break;
			case KeyEvent.VK_ESCAPE:
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_LEFT:
				break;
			case KeyEvent.VK_ENTER:
				break;
			default:
				System.out.println("1: NO!!! default: "+e.paramString());
				e.consume();
				break;
			}

		}

		private boolean validPrefix(){
			for (String code:listOfValidCodes){
				if (predicate.apply(code)) return true;
			}
			return false;
		}

		public void keyTyped(KeyEvent event) {
			//event.consume();
		};
	};
	public static class MyPredicate implements IPredicate<String>,ActionListener{
		StringBuilder prefix;

		public MyPredicate(StringBuilder prefix) {
			this.prefix=prefix;
		}


		@Override
		public boolean apply(String code) {
			if (code==null) return true;
			System.out.println("does "+ code+" start with: "+prefix+" "+code.startsWith(prefix.toString()));
			if (prefix.length()==0) return true;

			return code.startsWith(prefix.toString());
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			prefix.setLength(0);;
		}
	}
*/
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

	static JFileChooser jfc=new JFileChooser("/tmp");
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

/*
class TextTableEditor extends DefaultCellEditor{
	public TextTableEditor(KeyListener listener) {
		super(new JTextField());
		setClickCountToStart(1);

		getComponent().addKeyListener(listener);
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value==null) value="";
		SOCconsensus.prefixSB.replace(0, SOCconsensus.prefixSB.length(), value.toString());

		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}


class JCBTableEditor extends AbstractCellEditor implements TableCellEditor,PopupMenuListener,ActionListener{
	private JComboBox comboBox;
	protected static final String EDIT = "edit";
	String selection;

	public JCBTableEditor(JComboBox combobox) {
		this.comboBox=combobox;
		combobox.addKeyListener(SOCconsensus.l);

		comboBox.addPopupMenuListener(this);
		combobox.addActionListener(this);
	}

	@Override
	public Object getCellEditorValue() {
		System.out.println("get value... "+comboBox.getSelectedItem());
		return comboBox.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		System.out.println("getTableCellEditorComponent (A): ("+row+","+column+") >"+value+"<" + "  "+comboBox.getSelectedItem());
		comboBox.getModel().setSelectedItem(value);
		if (value==null){
			((JTextField)comboBox.getEditor().getEditorComponent()).setText("");	
		}else{
			((JTextField)comboBox.getEditor().getEditorComponent()).setText(value.toString());	
		}

		System.out.println("getTableCellEditorComponent (B): ("+row+","+column+") >"+value+"<" + "  "+comboBox.getSelectedItem());
		System.out.println("----");

		return comboBox;
	}


	// ============   Action listener ===============  
	public void actionPerformed(ActionEvent event) {
		System.out.println("Firing that we are done!!!");
		fireEditingStopped();
		System.out.println(event.getActionCommand());
	};
	// ============   popup listener ===============  
	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		System.out.println("PM canceled "+e.toString());

	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		System.out.println("PM invisible "+e.toString());
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		System.out.println("PM Visible");

	}

}
 */