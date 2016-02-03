package gov.nih.cit.socassign.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SelectedResultRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1445099096023357790L;
	private static final DecimalFormat FMT1 = new DecimalFormat("0.0000");
	private static final DecimalFormat FMT2 = new DecimalFormat("0.000E0");

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (column < 3) {
			setHorizontalAlignment(JLabel.CENTER);
			if (column == 2) {
				double val = Double.parseDouble(getText());
				if (val < 1e-4) {
					setText(FMT2.format(val));
				} else {
					setText(FMT1.format(val));
				}
			}
		}
		return this;
	};
}
