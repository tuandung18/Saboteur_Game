package fop.model.board;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import fop.model.cards.CardAnchor;
import fop.model.cards.GoalCard;
import fop.model.cards.PathCard;
import fop.model.graph.Edge;
import fop.model.graph.Graph;

/**
 * 
 * Stellt das Wegelabyrinth als Liste von Karten und als Graph dar.
 *
 */
public class Gameboard {
	
	protected final Map<Position, PathCard> board = new HashMap<>();
	protected final Graph<BoardAnchor> graph = new Graph<>();
	
	/**
	 * Erstellt ein leeres Wegelabyrinth und platziert Start- sowie Zielkarten.
	 */
	public Gameboard() {
		clear();
	}
	
	/**
	 * Zum Debuggen kann hiermit der Graph ausgegeben werden.<br>
	 * Auf {@code http://webgraphviz.com/} kann der Code dargestellt werden.
	 */
	public void printGraph() {
		graph.toDotCode().forEach(System.out::println);
	}
	
	/**
	 * Leert das Wegelabyrinth.
	 */
	public void clear() {
		board.clear();
		graph.clear();
	}
	
	// add, remove //
	
	/**
	 * Setzt eine neue Wegekarte in das Wegelabyrinth.<br>
	 * Verbindet dabei alle Kanten des Graphen zu benachbarten Karten,
	 * sofern diese einen Knoten an der benachbarten Stelle besitzen.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @param card die zu platzierende Wegekarte
	 */
	public void placeCard(int x, int y, PathCard card) {
		// TODO Aufgabe 4.1.4
		Position pos = new Position(x,y);
		Set<CardAnchor> cardAnchors = card.getGraph().vertices();
		// put the new card into the board
		board.put(pos,card);
		//put all nodes from the card's graph to the board's graph
		for(CardAnchor cardAnchor1 : cardAnchors) {
			Position nextPos = cardAnchor1.getAdjacentPosition(pos);
			BoardAnchor boardAnchor1 = BoardAnchor.of(pos, cardAnchor1);
			graph.addVertex(boardAnchor1);

			//put all edges from the card's graph to the board's graph
			for (CardAnchor cardAnchor2 : card.getGraph().getAdjacentVertices(cardAnchor1)) {
				BoardAnchor boardAnchor2;
				//check if there is an edge between cardAnchor 1 and 2
				if (card.getGraph().hasEdge(cardAnchor1,cardAnchor2)) {
					boardAnchor2 = BoardAnchor.of(pos, cardAnchor2);
					graph.addEdge(boardAnchor1, boardAnchor2);
				} else {
				}
			}
			//check if there is no card next to the processing cardAnchor 1
			if(isPositionEmpty(nextPos.x(), nextPos.y()))
				continue;
			//connect edges from nodes within the card with nodes from board's graph around them
			for(CardAnchor cardAnchor3 : board.get(nextPos).getGraph().vertices()){
				if(cardAnchor3.equals(cardAnchor1.getOppositeAnchor())){
					BoardAnchor boardAnchor3 = BoardAnchor.of(nextPos, cardAnchor3);
					graph.addEdge(boardAnchor1,boardAnchor3);
					break;
				}
			}
		}
		// check for goal cards
		checkGoalCards();
	}
	
	/**
	 * Prüft, ob eine Zielkarte erreichbar ist und dreht diese gegebenenfalls um.
	 */
	private void checkGoalCards() {
		for (Entry<Position, PathCard> goal : board.entrySet().stream().filter(e -> e.getValue().isGoalCard()).collect(Collectors.toList())) {
			int x = goal.getKey().x();
			int y = goal.getKey().y();
			if (existsPathFromStartCard(x, y)) {
				GoalCard goalCard = (GoalCard) goal.getValue();
				if (goalCard.isCovered()) {
					// turn card
					goalCard.showFront();
					// generate graph to match all neighbor cards
					goalCard.generateGraph(card -> doesCardMatchItsNeighbors(x, y, card));
					// connect graph of card
					placeCard(x, y, goalCard);
				}
			}
		}
		
	}
	
	/**
	 * Entfernt die Wegekarte an der übergebenen Position.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @return die Karte, die an der Position lag
	 */
	public PathCard removeCard(int x, int y) {
		// TODO Aufgabe 4.1.5
		Position pos = new Position(x,y);
		PathCard toRemoveCard = board.get(pos);
		board.remove(pos);
		Arrays.stream(CardAnchor.values()).map(cardAnchor -> BoardAnchor.of(x, y, cardAnchor)).forEach(boardAnchor -> {
			if (graph.hasVertex(boardAnchor))
				graph.removeVertex(boardAnchor);
			else {
			}
		});
		return toRemoveCard;
	}
	
	
	// can //
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn die übergebene Karte an der übergebene Position platziert werden kann.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @param card die zu testende Karte
	 * @return {@code true}, wenn die Karte dort platziert werden kann; sonst {@code false}
	 */
	public boolean canCardBePlacedAt(int x, int y, PathCard card) {
		return isPositionEmpty(x, y) && existsPathFromStartCard(x, y) && doesCardMatchItsNeighbors(x, y, card);
	}
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn auf der übergebenen Position keine Karte liegt.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @return {@code true}, wenn der Platz frei ist; sonst {@code false}
	 */
	private boolean isPositionEmpty(int x, int y) {
		// TODO Aufgabe 4.1.6
		Position pos = new Position(x, y);
		return !board.containsKey(pos);
	}
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn die übergebene Position von einer Startkarte aus erreicht werden kann.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @return {@code true}, wenn die Position erreichbar ist; sonst {@code false}
	 */
	private boolean existsPathFromStartCard(int x, int y) {
		// TODO Aufgabe 4.1.7
		try {

			for (Entry<Position, PathCard> startCard : board.entrySet().stream().filter(e -> e.getValue().isStartCard()).collect(Collectors.toList())) {
				for (CardAnchor cardAnchorInPos : CardAnchor.values()) {
					Position pos = cardAnchorInPos.getAdjacentPosition(new Position(x, y));
					if (isPositionEmpty(pos.x(), pos.y()))
						continue;
					CardAnchor oppositeAnchor = cardAnchorInPos.getOppositeAnchor();
					BoardAnchor boardAnchorInPos = BoardAnchor.of(pos, oppositeAnchor);
					Set<CardAnchor> startCardAnchors = startCard.getValue().getGraph().vertices();
					for (CardAnchor cardAnchorFromStartCard : startCardAnchors) {
						Position startCardPos = startCard.getKey();
						BoardAnchor boardAnchorFromStartBoard = BoardAnchor.of(startCardPos, cardAnchorFromStartCard);
						if (graph.hasPath(boardAnchorFromStartBoard, boardAnchorInPos))
							return true;
					}
				}
			}

		}
		catch (IllegalStateException exp){
			exp.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn die übergebene Karte an der übergebene Position zu ihren Nachbarn passt.
	 * @param x x-Position im Wegelabyrinth
	 * @param y y-Position im Wegelabyrinth
	 * @param card die zu testende Karte
	 * @return {@code true}, wenn die Karte dort zu ihren Nachbarn passt; sonst {@code false}
	 */
	private boolean doesCardMatchItsNeighbors(int x, int y, PathCard card) {
		// TODO Aufgabe 4.1.8
		Position pos = new Position(x,y);
		for (CardAnchor ca : CardAnchor.values()) {
			Position nextpos = ca.getAdjacentPosition(pos);
			// skip if no card or goal card is in next position
			if (!isPositionEmpty(nextpos.x(), nextpos.y()) && !board.get(nextpos).isGoalCard()) {
				if ((card.getGraph().hasVertex(ca) || !graph.hasVertex(BoardAnchor.of(nextpos, ca.getOppositeAnchor()))) && (!card.getGraph().hasVertex(ca) || graph.hasVertex(BoardAnchor.of(nextpos, ca.getOppositeAnchor()))))
					return false;
			}

		}

		return true;
	}
	/**
	 * Gibt genau dann {@code true} zurück, wenn eine aufgedeckte Goldkarte im Wegelabyrinth liegt.
	 * @return {@code true} wenn eine Goldkarte aufgedeckt ist; sonst {@code false}
	 */
	public boolean isGoldCardVisible() {
		return board.values().stream().anyMatch(c -> c.isGoalCard() && ((GoalCard) c).getType() == GoalCard.Type.Gold && !((GoalCard) c).isCovered());
	}
	/**
	 * Gibt genau dann {@code true} zurück, wenn eine aufgedeckte Steinkarte im Wegelabyrinth liegt.
	 * @return {@code true} wenn eine Steinkarte aufgedeckt ist; sonst {@code false}
	 */
	public boolean isStoneCardVisible(){
		return board.values().stream().anyMatch(c -> c.isGoalCard() && ((GoalCard) c).getType() == GoalCard.Type.Stone && !((GoalCard) c).isCovered());
	}
	
	// get //
	
	public Map<Position, PathCard> getBoard() {
		return board;
	}
	
	public int getNumberOfAdjacentCards(int x, int y) {
		Set<Position> neighborPositions = Set.of(Position.of(x - 1, y), Position.of(x + 1, y), Position.of(x, y - 1), Position.of(x, y + 1));
		return (int) board.keySet().stream().filter(pos -> neighborPositions.contains(pos)).count();
	}
	
}
