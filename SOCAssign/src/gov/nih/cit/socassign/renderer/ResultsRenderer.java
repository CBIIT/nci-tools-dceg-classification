package gov.nih.cit.socassign.renderer;

import gov.nih.cit.socassign.AssignmentCodingSystem;
import gov.nih.cit.socassign.SOCAssignGlobals;
import gov.nih.cit.socassign.SOCAssignModel;
import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ResultsRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1163812319370252084L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		SOCAssignModel testModel = SOCAssignModel.getInstance();
		if (column == 2||column == 4) {
			setToolTipText(value.toString());
		} else if (column == 5) {
			AssignmentCodingSystem codingSystem = testModel.getCodingSystem();
			OccupationCode code = codingSystem.getOccupationalCode(value.toString());
			if (code != null) {
				setToolTipText(code.getTitle() + " - " + code.getDescription());
			} else {
				setToolTipText(null);
			}
		} else {
			setToolTipText(null);
		}
		if (!isSelected) {
			if (testModel.isRowAssigned(SOCAssignGlobals.getResultsTable().convertRowIndexToModel(row))) {
				setBackground(SOCAssignGlobals.PALE_GREEN);
			} else {
				setBackground(Color.WHITE);
			}
		}
		return this;
	}
}
