package fop.model.board;

import fop.model.cards.CardAnchor;

/**
 * 
 * Stellt die Knoten des Wegelabyrinth Graphen dar.<br>
 * Sie bestehen aus einer {@link Position} mit {@code x} und {@code y} Koordinaten
 * sowie einem {@link CardAnchor}.
 *
 */
public final class BoardAnchor {
	
	private final Position pos;
	private final CardAnchor anchor;
	
	protected BoardAnchor(int x, int y, CardAnchor anchor) {
		pos = Position.of(x, y);
		this.anchor = anchor;
	}
	
	public static BoardAnchor of(int x, int y, CardAnchor anchor) {
		return new BoardAnchor(x, y, anchor);
	}
	
	public static BoardAnchor of(Position pos, CardAnchor anchor) {
		return new BoardAnchor(pos.x(), pos.y(), anchor);
	}
	
	public int x() {
		return pos.x();
	}
	
	public int y() {
		return pos.y();
	}
	
	public CardAnchor anchor() {
		return anchor;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", pos.x(), pos.y(), anchor);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (anchor == null ? 0 : anchor.hashCode());
		result = prime * result + (pos == null ? 0 : pos.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BoardAnchor other = (BoardAnchor) obj;
		if (anchor != other.anchor) return false;
		if (pos == null) {
			if (other.pos != null) return false;
		} else if (!pos.equals(other.pos)) return false;
		return true;
	}
	
}
