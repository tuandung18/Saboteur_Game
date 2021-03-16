package fop.model.cards;

/**
 * 
 * Stellt eine Aktionskarte mit einem zerbrochenen Werkzeug dar.<br>
 * Der Typ wird durch den {@link ToolType} bestimmt.
 *
 */
public final class BrokenToolCard extends ActionCard {
	
	private final ToolType toolType;
	
	/**
	 * Erstellt eine neue Karten mit einem zerbrochenen Werkzeug des übergebenen Typs.
	 * @param name der Name der Karte zum Laden des korrekten Bildes
	 * @param toolType der Typ des Werkzeugs
	 */
	public BrokenToolCard(String name, ToolType toolType) {
		super(name);
		this.toolType = toolType;
	}
	
	public BrokenToolCard(ToolType toolType) {
		this(null, toolType);
	}
	
	/**
	 * {@inheritDoc}
	 * @see fop.model.cards.ActionCard#isBrokenTool()
	 */
	@Override
	public boolean isBrokenTool() {
		return true;
	}
	
	/**
	 * Liefert den Typen des zerbrochenen Werkzeugs.
	 * @return der Typ des Werkzeugs
	 * @see #toolType
	 */
	public ToolType getToolType() {
		return toolType;
	}
	
	@Override
	public String toString() {
		return String.format("BrokenToolCard (%s)", toolType.name());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (toolType == null ? 0 : toolType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		BrokenToolCard other = (BrokenToolCard) obj;
		if (toolType != other.toolType) return false;
		return true;
	}
	
}
