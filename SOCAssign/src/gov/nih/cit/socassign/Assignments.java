package gov.nih.cit.socassign;

import gov.nih.cit.socassign.codingsystem.OccupationCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A list of Occupation Codes selected by the coder.
 * It is possible to have NO selections, but flag the
 * Job Description for later review.  Only 3 Assignments
 * are stored.
 *
 * @author Daniel Russ
 *
 */
public class Assignments {

	public enum FlagType {NOT_FLAGGED, FLAGGED};

	private final int rowId;
	private List<OccupationCode> codes;
	private FlagType flag;

	public Assignments(int rowId,List<OccupationCode> codes,FlagType flag) {
		this.rowId = rowId;
		this.codes = new ArrayList<OccupationCode > (codes);
		if (this.codes.size() > 3) {
			this.codes = this.codes.subList(0, 3);
		}
		this.flag = flag;
	}

	public Assignments(int rowId, FlagType flag, OccupationCode... codes) {
		this(rowId,Arrays.asList(codes),flag);
	}

	public Assignments(int rowId,List<OccupationCode> codes) {
		this(rowId, codes, FlagType.NOT_FLAGGED);
	}

	public Assignments(int rowId,OccupationCode... codes) {
		this(rowId,Arrays.asList(codes),FlagType.NOT_FLAGGED);
	}

	public void setFlag(FlagType flag) {
		this.flag = flag;
	}

	public FlagType getFlag() {
		return flag;
	}

	public OccupationCode getCode(int indx) {
		if (indx >= 3 || indx >= codes.size()) return null;
		return codes.get(indx);
	}

	public int getRowId() {
		return rowId;
	}

	/* Delegate methods to handle adding/removing from the list */
	public int size() {
		return codes.size();
	}

	public boolean contains(OccupationCode o) {
		return codes.contains(o);
	}

	public OccupationCode remove(int index) {
		return codes.remove(index);
	}

	public boolean add(OccupationCode e) {
		return codes.add(e);
	}

	public OccupationCode set(int index, OccupationCode element) {
		return codes.set(index, element);
	}

	public OccupationCode get(int index) {
		return codes.get(index);
	}

}
