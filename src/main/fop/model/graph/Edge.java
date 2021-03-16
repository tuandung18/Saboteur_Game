package fop.model.graph;

/**
 * 
 * Stellt eine Kante des Graphen {@link Graph} als ein Paar von Knoten dar.
 *
 * @param <V> Typ der Knoten
 */
public final class Edge<V> {
	
	private final V x;
	private final V y;
	
	public Edge(V x, V y) {
		this.x = x;
		this.y = y;
	}
	
	public static <V> Edge<V> of(V x, V y) {
		return new Edge<>(x, y);
	}
	
	public V x() {
		return x;
	}
	
	public V y() {
		return y;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", x, y);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (x == null ? 0 : x.hashCode());
		result = prime * result + (y == null ? 0 : y.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Edge<?> other = (Edge<?>) obj;
		if (x == null) {
			if (other.x != null) return false;
		} else if (!x.equals(other.x)) return false;
		if (y == null) {
			if (other.y != null) return false;
		} else if (!y.equals(other.y)) return false;
		return true;
	}
	
}
