package gov.nih.cit.socassign.renderer;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class FlagRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 2527747990111479929L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		SOCAssignModel model=SOCAssignModel.getInstance();
		int modelRow=SOCAssignGlobals.getResultsTable().convertRowIndexToModel(row);
		
		Font fontAwesome = SOCAssignGlobals.getFontAwesome();
		if (fontAwesome != null) {
			setFont(fontAwesome.deriveFont((float)getFont().getSize()));
			setForeground(Color.RED);
			String txt=( ((Boolean)value)?" \uf024 ":"" )+(model.isRowCommented(modelRow)?" \uf0e5":"");
			setText(txt);
		}
		if (!isSelected) {
			setBackground( model.isRowAssigned(modelRow)? SOCAssignGlobals.PALE_GREEN:Color.WHITE );
		}
		setHorizontalTextPosition(CENTER);
		return this;
	}
}
