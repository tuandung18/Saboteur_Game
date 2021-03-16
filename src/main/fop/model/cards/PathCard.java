package fop.model.cards;

import java.util.List;

import fop.model.graph.Edge;
import fop.model.graph.Graph;

/**
 * 
 * Stellt eine Wegekarte dar.
 * @see #graph
 *
 */
public class PathCard extends Card {
	
	/**
	 * Der Graph der Wegekarte.<br>
	 * Ein Knoten im Graphen zeigt an, dass die Karte an dieser Stelle eine Verbindung nach außen hat.<br>
	 * Eine Kante im Graphen zeigt an, dass zwischen den beiden Knoten eine Verbindung besteht.
	 */
	protected Graph<CardAnchor> graph = new Graph<>();
	
	/** Gibt an, ob die Karte gedreht wurde oder nicht. */
	private boolean rotated = false;
	
	/**
	 * Erstellt eine Wegekarte mit dem übergebenen Graphen.
	 * @param name der Name der Karte
	 * @param graph der Graph der Karte
	 * @see #graph
	 */
	public PathCard(String name, Graph<CardAnchor> graph) {
		super(name);
		this.graph = graph;
	}
	
	/**
	 * Erstellt eine Wegekarte aus einer Liste von Kanten.<br>
	 * Dabei ist die äußere Liste eine Aufzählung von inneren Listen
	 * und die innere Liste beinhaltet mehrere untereinander verbundene Knoten.
	 * @param name der Name der Karte
	 * @param edges eine Liste von Kanten
	 */
	public PathCard(String name, List<List<CardAnchor>> edges) {
		super(name);
		for (List<CardAnchor> edgeList : edges)
			for (int i = 0; i < edgeList.size() - 1; i++)
				for (int j = i + 1; j < edgeList.size(); j++)
					graph.addEdge(edgeList.get(i), edgeList.get(j));
	}
	
	/**
	 * {@inheritDoc}
	 * @see fop.model.cards.Card#isPathCard()
	 */
	@Override
	public boolean isPathCard() {
		return true;
	}
	
	/**
	 * Dreht den Graphen und ändert den Wert von {@link #rotated}.
	 * @see #graph
	 */
	public void rotate() {
		// switch rotated state
		rotated = !rotated;
		
		// create new rotated graph
		Graph<CardAnchor> rotatedGraph = new Graph<>();
		// add vertices
		for (CardAnchor anchor : graph.vertices())
			rotatedGraph.addVertex(anchor.getOppositeAnchor());
		// add edges
		for (Edge<CardAnchor> edges : graph.edges())
			rotatedGraph.addEdge(edges.x().getOppositeAnchor(), edges.y().getOppositeAnchor());
		
		// update graph
		graph = rotatedGraph;
	}
	
	/**
	 * Liefert den Graphen, der die möglichen Wege der Karte beschreibt.
	 * @return den Graphen der Karte
	 */
	public Graph<CardAnchor> getGraph() {
		return graph;
	}
	
	/**
	 * Gibt an, ob die Karte auf dem Kopf steht oder nicht.
	 * @return {@code true} wenn die Karte gedreht ist; sonst {@code false}
	 */
	public boolean isRotated() {
		return rotated;
	}
	
	@Override
	public String toString() {
		return String.format("PathCard (%s, %s)", name, graph.toString());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (graph == null ? 0 : graph.hashCode());
		result = prime * result + (rotated ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		PathCard other = (PathCard) obj;
		if (graph == null) {
			if (other.graph != null) return false;
		} else if (!graph.equals(other.graph)) return false;
		if (rotated != other.rotated) return false;
		return true;
	}
	
}
