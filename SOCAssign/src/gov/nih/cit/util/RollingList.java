package gov.nih.cit.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <P>A Fixed List, when you add more items than the list can hold, the oldest item
 * falls off the end of the queue.</P>
 *
 * <P>You can get the items in a reverse order by using the asRollingStack() class.  The
 * RollingStack class inserts the items at the being of the list instead of the end.</P>
 *
 * <P>if you add A then B then C</P>
 * <pre>
 * List :  (0)A, (1)B, (2)C
 * Stack:  (0)C, (1)B, (2)A
 * </pre>
 * <P>You can go back and forth with the asRollingList() method of RollingStack and
 * asRollingStackMethod of RollingList</P>
 *
 * @author Daniel Russ, Ph.D.
 *
 */
public class RollingList<E> extends AbstractList<E> implements List<E> {

	private int position = 0;
	private E[] buffer;
	private RollingStack stack = new RollingStack();

	@SuppressWarnings("unchecked")
	public RollingList(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + size);
		}
		buffer = (E[])new Object[size];
	}

	@Override
	public boolean add(E object) {
		if (contains(object)) return false;

		buffer[position%buffer.length] = object;
		position++;
		return true;
	}

	@Override
	public E get(int index) {
		rangeCheck(index);

		if (position < buffer.length) {
			return buffer[index];
		}

		return buffer[(index + position)%buffer.length];
	}

	@Override
	public int size() {
		return Math.min(position, buffer.length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends E> objects) {

		E[] objArray = (E[])objects.toArray();
		if (objArray.length == 0) return false;

		if (objArray.length >= buffer.length) {
			// fill the buffer with the last part of the objects...
			// note: set the position to the buffer length not 0, otherwise we think the buffer is empty!
			System.arraycopy(objArray, objArray.length - buffer.length, buffer, 0, buffer.length);
			position = buffer.length;
		} else {
			int spaceRemaining = buffer.length - (position%buffer.length);
			int x = Math.min(spaceRemaining, objArray.length);
			// fill the last x items in the buffer.
			System.arraycopy(objArray, 0, buffer, position%buffer.length, x);

			// then fill the remaining in the front of the buffer
			int remaining = objArray.length - x;
			if (remaining > 0) {
				System.arraycopy(objArray, x, buffer, 0, remaining);
				position = remaining + buffer.length;
			} else {
				position = position + x;
			}

		}
		return true;
	}

	@Override
	public String toString() {
		if (buffer.length == 0 || buffer[0] == null) return "[]";

		int from = Math.max(position - buffer.length,0);

		StringBuilder b = new StringBuilder("[" + buffer[from%buffer.length]);
		for (int i = (from + 1);i < position;i++) {
			b.append(", " + buffer[i%buffer.length].toString());
		}

		b.append(']');
		return b.toString();
//		return b.toString() + Arrays.toString(buffer);
	}

	public int capacity() {
		return buffer.length;
	}

	@Override
	public void clear() {
		position = 0;
		Arrays.fill(buffer, null);
	}

	public RollingStack asRollingStack() {
		return stack;
	}

	private void rangeCheck(int indx) {
		if (indx >= size() || indx < 0) {
			throw new IndexOutOfBoundsException("Index: " + indx + ", Size: " + size());
		}
	}

	public class RollingStack extends AbstractList<E> {
		private RollingStack() {}

		@Override
		public boolean add(E object) {
			return RollingList.this.add(object);
		};

		@Override
		public E get(int index) {
			return buffer[ (position - index - 1) % buffer.length ];
		}

		@Override
		public int size() {
			return RollingList.this.size();
		};
		@Override
		public boolean addAll(Collection<? extends E> c) {
			return RollingList.this.addAll(c);
		};

		@Override
		public String toString() {
			if (buffer.length == 0 || buffer[0] == null) return "[]";

			//int from = 0;int to = 0;
			int from = (position - 1);
			int to = Math.max(position - buffer.length,0);
			//System.out.println("from: " + from + " to: " + to + " position: " + position);

			StringBuilder b = new StringBuilder("[" + buffer[from%buffer.length]);
			for (int i = from - 1; i >= to; i--) {
				b.append(", " + buffer[i%buffer.length].toString());
			}

			b.append(']');
			//return b.toString() + Arrays.toString(buffer);
			return b.toString();
		}

		public int capacity() {
			return RollingList.this.capacity();
		}

		@Override
		public void clear() {
			RollingList.this.clear();
		}

		public RollingList<E> asRollingList() {
			return RollingList.this;
		}
	};
}
