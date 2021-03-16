package fop.model.cards;

import static fop.model.cards.CardAnchor.bottom;
import static fop.model.cards.CardAnchor.left;
import static fop.model.cards.CardAnchor.right;
import static fop.model.cards.CardAnchor.top;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 
 * Stellt eine Ziel-Wegekarte dar.
 *
 */
public final class GoalCard extends PathCard {
	
	/** Der Typ der Zielkarte. */
	private final Type type;
	
	/** Falls {@code true} ist nur die Rückseite der Karte sichtbar. */
	private boolean covered;
	
	public GoalCard(Type type) {
		super("goal", List.of());
		this.type = type;
		covered = true;
	}
	
	/**
	 * {@inheritDoc}
	 * @see fop.model.cards.Card#isGoalCard()
	 */
	@Override
	public boolean isGoalCard() {
		return true;
	}
	
	/**
	 * Gibt den Typ der Zielkarte zurück.
	 * @see Type
	 * @return der Typ der Zielkarte
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Gibt an, ob die Karte auf der Vorderseite oder auf der Rückseite liegt.
	 * @return {@code true} wenn nur die Rückseite sichtbar ist;
	 *         {@code false} wenn die Vorderseite sichtbar ist
	 */
	public boolean isCovered() {
		return covered;
	}
	
	/**
	 * Generiert einen zur Zielkarte und ihren Nachbarn passenden Graphen.
	 * @param doesCardMatchItsNeighbors gibt an, ob die übergebene Karte zu ihren Nachbarn passt
	 */
	public void generateGraph(Predicate<GoalCard> doesCardMatchItsNeighbors) {
		switch (type) {
			case Stone:
				List<CardAnchor[]> possibilities = List.of(
						new CardAnchor[] {left, top},
						new CardAnchor[] {left, bottom},
						new CardAnchor[] {top, bottom},
						new CardAnchor[] {left, right},
						new CardAnchor[] {top},
						new CardAnchor[] {left},
						new CardAnchor[] {left, top, bottom},
						new CardAnchor[] {left, right, bottom},
						new CardAnchor[] {left, top, right, bottom});
				// try all possibilities
				for (CardAnchor[] possibility : possibilities) {
					// normal rotation
					if (isRotated()) rotate();
					setFullyConnectedGraph(possibility);
					name = String.format("goal_%s_%s", type.name().toLowerCase(),
							Arrays.stream(possibility).map(CardAnchor::name).map(String::toLowerCase).sorted().collect(Collectors.joining("_")));
					if (doesCardMatchItsNeighbors.test(this)) break;
					// rotated card
					rotate();
					if (doesCardMatchItsNeighbors.test(this)) break;
				}
				break;
			default:
				setFullyConnectedGraph(CardAnchor.values());
				break;
		}
	}
	
	private void setFullyConnectedGraph(CardAnchor... anchors) {
		graph.clear();
		for (CardAnchor anchor : anchors)
			graph.addVertex(anchor);
		for (int i = 0; i < anchors.length - 1; i++)
			for (int j = i + 1; j < anchors.length; j++)
				graph.addEdge(anchors[i], anchors[j]);
	}
	
	
	/**
	 * Dreht die Karte so, dass die Vorderseite sichtbar wird.
	 */
	public void showFront() {
		covered = false;
		name = String.format("goal_%s", type.name().toLowerCase());
	}
	
	@Override
	public String toString() {
		return String.format("GoalCard (%s, covered=%s)", type, covered);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (covered ? 1231 : 1237);
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		GoalCard other = (GoalCard) obj;
		if (covered != other.covered) return false;
		if (type != other.type) return false;
		return true;
	}
	
	/**
	 * 
	 * Der Typ der Zielkarte.<br>
	 * Entweder ist es die Schatzkarte mit dem Gold ({@link #Gold} oder nur die Karte mit dem Stein ({@link #Stone}).
	 *
	 */
	public enum Type {
		Gold, Stone;
	}
	
}
