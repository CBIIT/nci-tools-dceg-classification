package gov.nih.cit.socassign.renderer;

import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FlagRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 2527747990111479929L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		Font fontAwesome = SOCAssignGlobals.getFontAwesome();
		if (fontAwesome != null) {
			setFont(fontAwesome);
			setForeground(Color.RED);
			setText(((Boolean)value)?"\uf024":"");
		}
		if (!isSelected) {
			if (SOCAssignModel.getInstance().isRowAssigned(SOCAssignGlobals.getResultsTable().convertRowIndexToModel(row))) {
				setBackground(SOCAssignGlobals.PALE_GREEN);
			} else {
				setBackground(Color.WHITE);
			}
		}
		return this;
	}
}
