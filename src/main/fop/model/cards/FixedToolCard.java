package fop.model.cards;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * Stellt eine Aktionskarte mit einem oder mehreren reparierten Werkzeugen dar.<br>
 * Die Typen werden durch den {@link ToolType} bestimmt.
 *
 */
public final class FixedToolCard extends ActionCard {
	
	private final Set<ToolType> toolTypes;
	
	/**
	 * Erstellt eine neue Karte mit einem oder mehreren reparierten Werkzeugen der übergebenen Typen.<br>
	 * Dabei werden Duplikate entfernt.
	 * @param name der Name der Karte zum Laden des korrekten Bildes
	 * @param toolTypes die Typen der Werkzeuge
	 */
	public FixedToolCard(String name, ToolType... toolTypes) {
		super(name);
		this.toolTypes = new HashSet<>(Arrays.asList(toolTypes));
	}
	
	public FixedToolCard(ToolType... toolTypes) {
		this(null, toolTypes);
	}
	
	/**
	 * {@inheritDoc}
	 * @see fop.model.cards.ActionCard#isFixedTool()
	 */
	@Override
	public boolean isFixedTool() {
		return true;
	}
	
	/**
	 * Liefert alle Typen, die das Werkzeug reparieren kann.
	 * @return alle Typen des Werkzeugs
	 */
	public Set<ToolType> getToolTypes() {
		return new HashSet<>(toolTypes);
	}
	
	/**
	 * Gibt {@code true} zurück, wenn diese Karte den übergebenen Typen reparieren kann.
	 * @param toolType der zu testende Typ
	 * @return {@code true} wenn der Typ repariert werden kann; sonst {@code false}
	 */
	public boolean canFix(ToolType toolType) {
		return toolTypes.contains(toolType);
	}
	
	@Override
	public String toString() {
		return String.format("FixedToolCard (%s)", toolTypes.stream().map(ToolType::name).collect(Collectors.joining(", ")));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (toolTypes == null ? 0 : toolTypes.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		FixedToolCard other = (FixedToolCard) obj;
		if (toolTypes == null) {
			if (other.toolTypes != null) return false;
		} else if (!toolTypes.equals(other.toolTypes)) return false;
		return true;
	}
	
}
