package gov.nih.cit.consensus;

import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class OccupationCodeRenderer extends DefaultTableCellRenderer {

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
			} else if (value instanceof String){
				String svalue=(String)value;
				if (svalue.length()==0){
					setText("");
				} else{
//					int slen=svalue.indexOf(' ');
//					if (slen==-1) slen=svalue.length();
//
//					setText(svalue.substring(0,slen));
					setText(svalue);
				}
			}
		}
		return this;
	}
}
