package gov.nih.cit.socassign;

/**
 * A scored object wraps another object and holds a score.
 *
 * @author Daniel Russ
 *
 * @param <E>  the class of the wrapped object.
 */
public class Scored<E> {

	private final E object;
	private final double score;

	public Scored(E object,double score) {
		this.object = object;
		this.score = score;
	}

	public double getScore() {
		return score;
	}
	public E getScoredObject() {
		return object;
	}

	/* =======  Eclipse Generated Code ======= */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scored<?> other = (Scored<?>) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		return true;
	}

}
