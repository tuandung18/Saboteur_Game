package fop.model;

import java.util.ArrayList;
import java.util.List;

import fop.model.cards.*;

/**
 * 
 * Stellt einen Spieler dar.
 *
 */
public class Player {
	
	/** Der Name des Spielers. */
	protected final String name;
	
	/** Die Punktzahl des Spielers. */
	protected int score;
	
	/** Die Rolle des Spielers. */
	protected Role role;
	
	/** Die Handkarten des Spielers */
	protected List<Card> handCards;
	
	/** Die Aktionskarten, die vor dem Spieler liegen. */
	protected List<ActionCard> actionCards;
	
	/**
	 * Erstellt einen neuen Spieler mit dem übergebenen Namen und der übergebenen Rolle.
	 * @param name der Name des Spielers
	 * @see Player.Role
	 */
	public Player(String name) {
		this.name = name;
		score = 0;
		handCards = new ArrayList<>();
		actionCards = new ArrayList<>();
	}
	
	public void scorePoints(int points) {
		score += points;
	}
	
	public void assignRole(Role role) {
		this.role = role;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public Role getRole() {
		return role;
	}
	
	/**
	 * Gibt an, ob es sich um einen Computergegner oder einen normalen Spieler handelt.
	 * @return {@code true} wenn der Spieler ein Computergegner ist; sonst {@code false}
	 */
	public boolean isComputer() {
		return false;
	}
	
	
	////////////////
	// HAND CARDS //
	////////////////
	
	/**
	 * Liefert alle Karten, die der Spieler zurzeit auf der Hand hat.
	 * @return die Handkarten des Spielers
	 */
	public List<Card> getAllHandCards() {
		return handCards;
	}
	
	/**
	 * Nimmt die übergebene Karte auf die Hand.
	 * @param card die neue Handkarte
	 */
	public void drawCard(Card card) {
		handCards.add(card);
	}
	
	/**
	 * Spielt die übergebene Karte aus.
	 * @param card die auszuspielende Karte
	 * @throws IllegalArgumentException wenn die Karte nicht in der Hand des Spielers ist
	 */
	public void playCard(Card card) throws IllegalArgumentException {
		if (!handCards.remove(card)) throw new IllegalArgumentException("The player cannot play the given card.");
	}
	
	
	//////////////////
	// ACTION CARDS //
	//////////////////
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn der Spieler ein zerbrochenes Werkzeug vor sich liegen hat.
	 * @return {@code true} wenn der Spieler ein zerbrochenes Werkzeug hat; sonst {@code false}
	 */
	public boolean hasBrokenTool() {
		return actionCards.stream().anyMatch(ActionCard::isBrokenTool);
	}
	
	/**
	 * Gibt genau dann {@code true} zurück, wenn der Spieler ein zerbrochenes Werkzeug des übergebenen Typs vor sich liegen hat.
	 * @param type der zu prüfende Typ des Werkzeugs
	 * @return {@code true} wenn der Spieler ein zerbrochenes Werkzeug des Typs hat; sonst {@code false}
	 */
	public boolean hasBrokenTool(ToolType type) {
		return getBrokenTool(type) != null;
	}
	
	/**
	 * Gibt die Karte mit dem zerbrochenen Werkzeug des übergebenen Typs zurück, die vor dem Spieler liegt.
	 * @param type der Typ des Werkzeugs
	 * @return die Karte mit dem zerbrochenen Werkzeug; oder {@code null} falls der Spieler keine entsprechende Karte vor sich liegen hat
	 */
	public BrokenToolCard getBrokenTool(ToolType type) {
		return (BrokenToolCard) actionCards.stream().filter(ac -> ac.isBrokenTool() && ((BrokenToolCard) ac).getToolType() == type).findFirst().orElse(null);
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die übergebene Karte mit einem zerbrochenen Werkzeug vor dem Spieler platziert werden kann.
	 * @param brokenToolCard die zu testende Karte
	 * @return {@code true} wenn die Karte vor den Spieler gelegt werden kann; sonst {@code false}
	 */
	public boolean canToolBeBroken(BrokenToolCard brokenToolCard) {
		return actionCards.stream().filter(ActionCard::isBrokenTool).noneMatch(card -> ((BrokenToolCard) card).getToolType() == brokenToolCard.getToolType());
	}
	
	/**
	 * Legt die übergebene Karte vor diesen Spieler ab.
	 * @param brokenToolCard die Karte mit einem zerbrochenen Werkzeug, die vor den Spieler platziert werden soll
	 * @throws IllegalArgumentException wenn die Karte nicht gespielt werden kann
	 */
	public void breakTool(BrokenToolCard brokenToolCard) {
		if (!canToolBeBroken(brokenToolCard)) throw new IllegalArgumentException("The given broken tool card cannot be placed in front of the player.");
		actionCards.add(brokenToolCard);
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die übergebene Karte mit einem reparierten Werkzeug eine Karte mit zerbrochenem Werkzeug reparieren kann.
	 * @param brokenToolCard die Karte mit dem zerbrochenen Werkzeug, die vor diesem Spieler liegen muss
	 * @param fixedToolCard die Karte mit dem reparierten Werkzeug
	 * @return {@code true} wenn das Werkzeug repariert werden kann und die Karte mit dem zerbrochenen Werkzeug vor diesem Spieler liegt
	 */
	public boolean canBrokenToolBeFixed(BrokenToolCard brokenToolCard, FixedToolCard fixedToolCard) {
		if (!actionCards.contains(brokenToolCard)) return false;
		return fixedToolCard.canFix(brokenToolCard.getToolType());
	}
	
	/**
	 * Repariert eine Karte mit einem zerbrochenen Werkzeug, die vor diesem Spieler liegt.
	 * @param brokenToolCard die Karte mit dem zerbrochenen Werkzeug, die vor diesem Spieler liegen muss
	 * @param fixedToolCard die Karte mit dem reparierten Werkzeug
	 * @throws IllegalArgumentException wenn die Karte nicht gespielt werden kann
	 */
	public void fixBrokenTool(BrokenToolCard brokenToolCard, FixedToolCard fixedToolCard) {
		if (!canBrokenToolBeFixed(brokenToolCard, fixedToolCard))
			throw new IllegalArgumentException("The broken tool card cannot be fixed by the given fixed tool card.");
		actionCards.remove(brokenToolCard);
	}
	
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	public String toString() {
		return "Player [name=" + name + ", score=" + score + ", role=" + role + ", handCards=" + handCards + ", actionCards=" + actionCards + "]";
	}
	
	
	//////////
	// ROLE //
	//////////
	
	/**
	 * Alle möglichen Rollen für Spieler.
	 */
	public enum Role {
		GOLD_MINER, SABOTEUR;
	}
	
}
