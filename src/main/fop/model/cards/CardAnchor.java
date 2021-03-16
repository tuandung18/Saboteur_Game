package fop.model.cards;

import fop.model.board.Position;

/**
 * 
 * Eine Enumeration aller möglicher Ankerpunkte einer Wegekarte, an der ein Weg
 * die Karte verlassen und somit eine Verbindung zu anderen Karten herstellen kann.<br>
 * <br>
 * Die Methode {@link #getOppositeAnchor()} liefert den gegenüberliegenden Ankerpunkt zurück.<br>
 * Die Methode {@link #getAdjacentPosition(Position)} liefert für eine übergebene Position die
 * Position der Karte zurück, zu der dieser Ankerpunkt zeigt.<br>
 * <br>
 * <i>Beispiel:</i>
 * Eine Karte an Position {@code (4, 0)}, die eine Ankerpunkt an der rechten Seite ({@link #right}) besitzt,
 * benötigt eine Karte zu ihrer Rechten an Position {@code (5, 0)} mit einem Ankerpunkt an der linken Seite ({@link #left}).<br>
 * Die Relation von {@link #right} zu {@link #left} wird durch die Methode {@link #getOppositeAnchor()} modelliert.<br>
 * Die Relation von {@code (4, 0)} zu {@code (5, 0)} wird durch die Methode {@link #getAdjacentPosition(Position)} modelliert.
 *
 */
public enum CardAnchor {
	
	left, bottom, right, top;
	
	public CardAnchor getOppositeAnchor() {
		switch (this) {
			case left:
				return right;
			case bottom:
				return top;
			case right:
				return left;
			case top:
				return bottom;
			default:
				return null;
		}
	}
	
	public Position getAdjacentPosition(Position pos) {
		switch (this) {
			case left:
				return Position.of(pos.x() - 1, pos.y());
			case bottom:
				return Position.of(pos.x(), pos.y() + 1);
			case right:
				return Position.of(pos.x() + 1, pos.y());
			case top:
				return Position.of(pos.x(), pos.y() - 1);
			default:
				return pos;
		}
	}
	
}
