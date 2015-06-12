package gov.nih.cit.socassign;

import gov.nih.cit.util.IPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 * Attempting to Filter the combobox based on what the user is typing.  Not currently working
 * 
 * @author Daniel Russ
 *
 * @param <T>  the class of the object in the ComboBox.
 */
public class FilteringComboBoxModel<T> extends AbstractListModel implements MutableComboBoxModel {

	ArrayList<T> completeItemList,filteredItems;
	T selectedItem=null;
	IPredicate<T> predicate;

	public FilteringComboBoxModel(Collection<? extends T> collection){
		this.completeItemList=new ArrayList<T>(collection);
		filteredItems=completeItemList;
	}

	public FilteringComboBoxModel(T[] items) {	
		this(Arrays.asList(items));
	}

	public void setPredicate(IPredicate<T> predicate) {
		this.predicate = predicate;
	}

	public boolean validPrefix(){
		if (predicate==null) throw new NullPointerException("predicate not defined before filtering");

		for (T item:filteredItems){
			if (item==null) continue;
			if (predicate.apply(item)) return true;
		}
		return false;
	}
	public void filter(){		
		if (predicate==null) throw new NullPointerException("predicate not defined before filtering");

		int oldSize=filteredItems.size();		
		ArrayList<T> items=new ArrayList<T>();
		if (filteredItems.size()==0) filteredItems=completeItemList;
		for (T item:filteredItems){
			if (predicate.apply(item)) items.add(item);
		}
		filteredItems=items;
		fireContentsChanged(this, 0, oldSize);
	}
	public void resetItems(){
		int oldSize=filteredItems.size();
		this.filteredItems=completeItemList;
		fireContentsChanged(this, 0, oldSize);
	}

	// ============ ComboBox Model ==========
	@Override
	public Object getSelectedItem() {		
		//		if (selectedItem==null) return "";
		//       System.out.println("FilteringComboBoxModel getSelected: Selected "+selectedItem);
		return selectedItem;
	}

	@Override
	public void setSelectedItem(Object anItem) {

		System.out.println("FilteringComboBoxModel setSelected: Selected "+anItem);
		if (anItem==null || !filteredItems.contains(anItem)){
			selectedItem=null;
			return;
		}


		@SuppressWarnings("unchecked")
		T item=(T)anItem;
		selectedItem=item;		

	}

	// ============ List Model (Not in AbstractListModel) ==========
	@Override
	public int getSize() {
		if (filteredItems==null) return 0;
		return filteredItems.size();
	}

	@Override
	public Object getElementAt(int index) {
		if (filteredItems.get(index)==null) return "";
		return filteredItems.get(index).toString();
	}

	// ============ Mutable ComboBox Model ==========
	@Override
	public void addElement(Object obj) {}
	@Override
	public void removeElement(Object obj) {}
	@Override
	public void removeElementAt(int index) {}
	@Override
	public void insertElementAt(Object obj, int index) {}
}
