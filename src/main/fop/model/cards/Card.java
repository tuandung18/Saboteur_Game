package fop.model.cards;

/**
 * 
 * Stellt eine beliebige Spielkarte dar.<br>
 * Dazu gehören Wegekarten ({@link PathCard} und Aktionskarten ({@link ActionCard}).
 *
 */
public abstract class Card {
	
	/** Der Name der Karte, der benötigt wird, um das korrekte Bild zu laden. */
	protected String name;
	
	/**
	 * Erstellt eine Spielkarte.
	 * @param name der Name der Karte
	 * @see #name
	 */
	protected Card(String name) {
		this.name = name;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte eine Aktionskarte mit einem zerbrochenen Werkzeug ist.
	 * @return {@code true} wenn es sich um ein zerbrochenes Werkzeug handelt; sonst {@code false}
	 */
	public boolean isBrokenTool() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte eine Aktionskarte mit einem oder mehreren reparierten Werkzeugen ist.
	 * @return {@code true} wenn es sich um reparierte Werkzeuge handelt; sonst {@code false}
	 */
	public boolean isFixedTool() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte die Karten Aktionskarte ist.
	 * @return {@code true} wenn es sich um die Karte handelt; sonst {@code false}
	 */
	public boolean isMap() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte die Steinschlag Aktionskarte ist.
	 * @return {@code true} wenn es sich um den Steinschlag handelt; sonst {@code false}
	 */
	public boolean isRockfall() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte eine Wegekarte ist.
	 * @return {@code true} wenn es sich um eine Wegekarte handelt; sonst {@code false}
	 */
	public boolean isPathCard() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte die Startkarte ist.
	 * @return {@code true} wenn es sich um die Startkarte handelt; sonst {@code false}
	 */
	public boolean isStartCard() {
		return false;
	}
	
	/**
	 * Gibt {@code true} zurück, wenn die Karte eine Zielkarte ist.
	 * @return {@code true} wenn es sich um eine Zielkarte handelt; sonst {@code false}
	 */
	public boolean isGoalCard() {
		return false;
	}
	
	/**
	 * Liefert den Namen der Karte, der zum Laden des korrekten Bildes benötigt wird.
	 * @return der Name der Karte
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Card other = (Card) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}
	
}
