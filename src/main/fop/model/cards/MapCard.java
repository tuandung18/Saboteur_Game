package fop.model.cards;

/**
 * 
 * Stellt die Aktionskarte Karte dar.
 *
 */
public final class MapCard extends ActionCard {
	
	public MapCard(String name) {
		super(name);
	}
	
	public MapCard() {
		this(null);
	}
	
	@Override
	public boolean isMap() {
		return true;
	}
	
	@Override
	public String toString() {
		return "MapCard";
	}
	
}
