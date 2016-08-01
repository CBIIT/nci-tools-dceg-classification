package gov.nih.cit.consensus;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class OccupationCodeRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -4083608985765921227L;

	public OccupationCodeRenderer() {}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (value==null) {
			setText("");
		}else {
			if (value instanceof OccupationCode){
				setText(((OccupationCode) value).getName());
				this.setToolTipText( ((OccupationCode) value).getTitle() );
			} else if (value instanceof String){
				String svalue=(String)value;
				if (svalue.length()==0){
					setText("");
				} else{
					setText(svalue);
					// currently column 2 is a string (the SIC)...
					if (column==2){	
						this.setToolTipText(AssignmentCodingSystem.SIC1987.lookup(value.toString()));
					}else{
						this.setToolTipText(value.toString());
					}
				}
			}
		}

		return this;
	}
}
