package fop.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fop.model.cards.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Stellt eine statische Methode zum Einlesen von {@link ActionCard}s aus einer XML-Datei bereit.<br>
 * @see #readFromResource(String)
 * @see #readFromFile(URI)
 *
 */
public final class ActionCardReader {
	
	private ActionCardReader() {}
	
	/**
	 * Liest alle Aktionskarten aus der übergebenen Datei ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param resourceName der Name der zu lesenden Ressource
	 * @return eine Liste mit allen Aktionskarten, die in der Datei beschrieben wurden
	 */
	public static List<ActionCard> readFromResource(String resourceName) {
		try (InputStream is = ActionCardReader.class.getResourceAsStream(resourceName)) {
			return read(is);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Liest alle Aktionskarten aus der übergebenen Datei ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param uri der URI der zu lesenden Datei
	 * @return eine Liste mit allen Aktionskarten, die in der Datei beschrieben wurden
	 */
	public static List<ActionCard> readFromFile(URI uri) {
		File file = new File(uri);
		if (!file.exists()) return List.of();
		if (file.length() == 0) return List.of();
		try (InputStream is = new FileInputStream(file)) {
			return read(is);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * Liest alle Aktionskarten aus dem übergebenen Stream ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param is zu lesender Stream
	 * @return eine Liste mit allen Aktionskarten, die in der Datei beschrieben wurden
	 */
	private static List<ActionCard> read(InputStream is) {
		// parse document
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		// create action cards
		List<ActionCard> actionCards = new LinkedList<>();
		Element root = doc.getDocumentElement();
		NodeList cards = root.getElementsByTagName("card");
		
		// iterate over every card
		for (int i = 0; i < cards.getLength(); i++) {
			Element card = (Element) cards.item(i);
			
			// parse type, name and count
			String type = card.getAttribute("type");
			if (type.equals("")) throw new IllegalArgumentException("The card does not specify a 'type'.");
			String name = card.getAttribute("name");
			if (name.equals("")) throw new IllegalArgumentException("The card does not specify a 'name'.");
			int count = card.hasAttribute("count") ? Integer.parseInt(card.getAttribute("count")) : 1;
			
			// parse broken tool
			if (type.equals("broken_tool")) {
				NodeList tools = card.getElementsByTagName("tool");
				if (tools.getLength() != 1)
					throw new IllegalArgumentException("A card with type 'broken_tool' must specify exactly one 'tool'.");
				Element tool = (Element) tools.item(0);
				ToolType toolType = ToolType.valueOf(tool.getAttribute("type"));
				for (int k = 0; k < count; k++)
					actionCards.add(new BrokenToolCard(String.format("%s_%d", name, k + 1), toolType));
			}
			
			// parse fixed tool
			else if (type.equals("fixed_tool")) {
				Set<ToolType> toolTypes = new HashSet<>();
				NodeList tools = card.getElementsByTagName("tool");
				int toolCount = tools.getLength();
				for (int j = 0; j < toolCount; j++) {
					Element tool = (Element) tools.item(j);
					ToolType toolType = ToolType.valueOf(tool.getAttribute("type"));
					toolTypes.add(toolType);
				}
				for (int k = 0; k < count; k++)
					actionCards.add(new FixedToolCard(String.format("%s_%d", name, k + 1), toolTypes.toArray(ToolType[]::new)));
			}
			
			// parse rockfall
			else if (type.equals("rockfall"))
				for (int k = 0; k < count; k++)
					actionCards.add(new RockfallCard(String.format("%s_%d", name, k + 1)));
			else if (type.equals("map"))
				for (int k = 0; k < count; k++)
					actionCards.add(new MapCard(String.format("%s_%d", name, k + 1)));
			else throw new IllegalArgumentException(String.format("Unknown action card type: '%s'", type));
		}
		
		// return all path cards
		return actionCards;
	}
	
}
