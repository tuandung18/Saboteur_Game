package fop.model.cards;

/**
 * 
 * Stellt die Aktionskarte Steinschlag dar.
 *
 */
public final class RockfallCard extends ActionCard {
	
	public RockfallCard(String name) {
		super(name);
	}
	
	public RockfallCard() {
		this(null);
	}
	
	@Override
	public boolean isRockfall() {
		return true;
	}
	
	@Override
	public String toString() {
		return "RockfallCard";
	}
	
}
