package fop.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import fop.model.cards.CardAnchor;
import fop.model.cards.PathCard;
import fop.model.graph.Graph;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Stellt eine statische Methode zum Einlesen von {@link PathCard}s aus einer XML-Datei bereit.<br>
 * @see #readFromResource(String)
 * @see #readFromFile(URI)
 *
 */
public final class PathCardReader {
	
	private PathCardReader() {}
	
	/**
	 * Liest alle Wegekarten aus der übergebenen Datei ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param resourceName der Name der zu lesenden Ressource
	 * @return eine Liste mit allen Wegekarten, die in der Datei beschrieben wurden
	 */
	public static List<PathCard> readFromResource(String resourceName) {
		try (InputStream is = PathCardReader.class.getResourceAsStream(resourceName)) {
			return read(is);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Liest alle Wegekarten aus der übergebenen Datei ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param uri der URI der zu lesenden Datei
	 * @return eine Liste mit allen Wegekarten, die in der Datei beschrieben wurden
	 */
	public static List<PathCard> readFromFile(URI uri) {
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
	 * Liest alle Wegekarten aus dem übergebenen Stream ein.<br>
	 * Wichtig: Tritt beim Einlesen ein Fehler auf, wird eine
	 * {@link RuntimeException} mit entsprechender Fehlermeldung geworfen.
	 * @param is zu lesender Stream
	 * @return eine Liste mit allen Wegekarten, die in der Datei beschrieben wurden
	 */
	private static List<PathCard> read(InputStream is) {
		// parse document
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		// create path cards
		List<PathCard> pathCards = new LinkedList<>();
		Element root = doc.getDocumentElement();
		NodeList cards = root.getElementsByTagName("card");
		int cardCount = cards.getLength();
		
		// iterate over every card
		for (int i = 0; i < cardCount; i++) {
			Element card = (Element) cards.item(i);
			
			// parse name and count
			String name = card.getAttribute("name");
			if (name.equals("")) throw new IllegalArgumentException("The card does not specify a 'name'.");
			int count = card.hasAttribute("count") ? Integer.parseInt(card.getAttribute("count")) : 1;
			
			// parse nodes and edges
			Graph<CardAnchor> graph = new Graph<>();
			NodeList features = card.getChildNodes();
			for (int j = 0; j < features.getLength(); j++) {
				if (features.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
				Element feature = (Element) features.item(j);
				
				// parse node
				if (feature.getTagName().equals("node")) {
					CardAnchor value = CardAnchor.valueOf(feature.getAttribute("value"));
					graph.addVertex(value);
				}
				
				// parse edge
				else if (feature.getTagName().equals("edge")) {
					CardAnchor start = CardAnchor.valueOf(feature.getAttribute("start"));
					CardAnchor end = CardAnchor.valueOf(feature.getAttribute("end"));
					graph.addEdge(start, end);
				}
				
				// unknown feature
				else throw new IllegalArgumentException(String.format("A feature of a card must be 'node' or 'edge', not: '%s'", feature.getTagName()));
			}
			
			// add card to path cards
			for (int k = 0; k < count; k++)
				pathCards.add(new PathCard(String.format("%s_%d", name, k + 1), graph));
		}
		
		// return all path cards
		return pathCards;
	}
	
}
