package fop.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fop.model.cards.Card;
import fop.model.cards.CardAnchor;
import fop.model.cards.PathCard;
import fop.model.graph.Edge;
import fop.model.graph.Graph;
import javax.imageio.ImageIO;

/**
 * 
 * Wird genutzt, um Bilder von Karten aus den Ressourcen zu laden.<br>
 * <br>
 * Alle Bilder liegen im Ordner {@link #PATH}.<br>
 * Mittels {@link #readImage(Card)} kann das zu einer Karte passende Bild abgerufen werden.<br>
 * Mittels {@link #readImage(String)} kann das zu einer Karte passende Bild anhand des Kartennamens abgerufen werden.
 *
 */
public final class CardImageReader {
	
	/** Der Pfad zu den Bildern */
	private static final String PATH = "/image";
	
	/** Das Seitenverhältnis einer Karte, Höhe durch Breite. */
	public static final double ASPECT_RATIO;
	static {
		try (Scanner scan = new Scanner(CardImageReader.class.getResourceAsStream(String.format("%s/size.data", PATH)), "UTF-8")) {
			double x = scan.nextDouble();
			double y = scan.nextDouble();
			ASPECT_RATIO = y / x;
		}
	}
	
	private CardImageReader() {}
	
	/** Speichert Bilder von Karten */
	private static final Map<Card, BufferedImage> cardSafe = new HashMap<>();
	/** Speichert Bilder von Namen von Karten */
	private static final Map<String, BufferedImage> nameSafe = new HashMap<>();
	
	/**
	 * Liefert ein zur übergebenen Wegekarte passendes Bild.
	 * @param card die Karte
	 * @return das Bild
	 */
	public static BufferedImage readImage(Card card) {
		if (cardSafe.containsKey(card)) return cardSafe.get(card);
		
		// try to load from resources
		BufferedImage img = loadImage(card);
		
		// if path card draw it
		if (card.isPathCard() && img == null) {
			System.err.printf("No image for card '%s' was found. An image was created.%n", card.getName());
			img = createImage(((PathCard) card).getGraph());
		}
		
		// nothing found draw card with name
		if (img == null) {
			System.err.printf("No image for card '%s' was found. An image was created.%n", card.getName());
			img = createImage(card.toString());
		}
		// nothing found throw warning
		//if (img == null)
		//	throw new IllegalArgumentException(String.format("No image for the given card was found: %s", card));
		
		// save and return image
		cardSafe.put(card, img);
		return img;
	}
	
	/**
	 * Liefert das Bild mit dem übergebenen Namen aus dem Ordner {@value #PATH}.
	 * @param name der Name des Bildes
	 * @return das Bild
	 */
	public static BufferedImage readImage(String name) {
		if (nameSafe.containsKey(name)) return nameSafe.get(name);
		
		// try to load from resources
		BufferedImage img;
		try (InputStream is = CardImageReader.class.getResourceAsStream(String.format("%s/%s.png", PATH, name))) {
			img = ImageIO.read(is);
		} catch (IOException | IllegalArgumentException e) {
			// nothing found draw card with name
			System.err.printf("No image for card '%s' was found. An image was created.%n", name);
			img = createImage(name);
			// nothing found throw warning
			//throw new IllegalArgumentException(String.format("No image for the given card was found: %s", name));
		}
		
		// save and return image
		nameSafe.put(name, img);
		return img;
	}
	
	/**
	 * Versucht ein Bild aus den Ressourcen zu laden.
	 * @param card die zu ladende Karte
	 * @return das Bild; oder {@code null}
	 */
	private static BufferedImage loadImage(Card card) {
		// collect possible file names
		String name = card.getName();
		Matcher m = Pattern.compile("([a-zA-Z]+(_[a-zA-Z]+)*)(_\\d+)?").matcher(name);
		m.find();
		String base = m.group(1);
		String number = m.group(3);
		List<String> files = new LinkedList<>();
		files.add(name);
		if (number != null) files.add(base);
		files.add(base + "_1");
		
		// try possible images
		for (String file : files) {
			InputStream is = CardImageReader.class.getResourceAsStream(String.format("%s/%s.png", PATH, file));
			if (is != null) try (is) {
				// load image
				BufferedImage img = ImageIO.read(is);
				// rotate image if path card is rotated
				if (card.isPathCard() && ((PathCard) card).isRotated()) {
					AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI, img.getWidth() / 2, img.getHeight() / 2);
					img = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR).filter(img, null);
				}
				return img;
			} catch (IOException e) {
				continue;
			}
		}
		
		// no image found
		return null;
	}
	
	/**
	 * Erstellt ein Bild, das dem übergebenen Graphen entspricht.
	 * @param graph der Graph der Wegekarte
	 * @return das Bild
	 */
	private static BufferedImage createImage(Graph<CardAnchor> graph) {
		int w = 100;
		int h = (int) (w * ASPECT_RATIO);
		
		// @formatter:off
		// edge x-coordinate
		Function<CardAnchor, Integer> ex = anchor -> {
			switch (anchor) {
				case bottom: return w / 2;
				case left: return 0;
				case right: return w + 1;
				case top: return w / 2;
				default: return -1;
			}
		};
		// edge y-coordinate
		Function<CardAnchor, Integer> ey = anchor -> {
			switch (anchor) {
				case bottom: return h + 1;
				case left: return h / 2;
				case right: return h / 2;
				case top: return 0;
				default: return -1;
			}
		};
		// dead end x-coordinate
		Function<CardAnchor, Integer> dx = anchor -> {
			switch (anchor) {
				case bottom: return w / 2;
				case left: return w / 3;
				case right: return 2 * w / 3;
				case top: return w / 2;
				default: return -1;
			}
		};
		// dead end y-coordinate
		Function<CardAnchor, Integer> dy = anchor -> {
			switch (anchor) {
				case bottom: return 3 * h / 4;
				case left: return h / 2;
				case right: return h / 2;
				case top: return h / 4;
				default: return -1;
			}
		};
		// @formatter:on
		
		// create image
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// draw background
		try (InputStream is = CardImageReader.class.getResourceAsStream(PATH + "/background.png")) {
			BufferedImage background = ImageIO.read(is);
			g.drawImage(background, 0, 0, w, h, null);
		} catch (IOException e) {
			g.setColor(new Color(133, 160, 171));
			g.fillRect(0, 0, w, h);
		}
		
		// set path color
		g.setStroke(new BasicStroke(w / 5));
		g.setColor(new Color(251, 250, 237));
		
		// draw dead end vertices
		for (CardAnchor vertex : graph.vertices())
			if (graph.getAdjacentVertices(vertex).isEmpty())
				g.drawLine(ex.apply(vertex), ey.apply(vertex), dx.apply(vertex), dy.apply(vertex));
		
		// draw edges
		for (Edge<CardAnchor> edge : graph.edges())
			g.drawLine(ex.apply(edge.x()), ey.apply(edge.x()), ex.apply(edge.y()), ey.apply(edge.y()));
		
		return img;
	}
	
	/**
	 * Erstellt ein Bild mit dem übergebenen String als Text.
	 * @param caption der anzuzeigende Text
	 * @return das Bild
	 */
	private static BufferedImage createImage(String caption) {
		int w = 100;
		int h = (int) (w * ASPECT_RATIO);
		
		// create image
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// draw background
		g.setColor(new Color(220, 250, 180));
		g.fillRect(0, 0, w, h);
		
		// draw text
		g.setColor(Color.BLACK);
		int y = h / 3;
		int start = 0;
		while (start < caption.length()) {
			int end = start + 1;
			while (end < caption.length() && g.getFontMetrics().stringWidth(caption.substring(start, end)) < 0.95 * w)
				end++;
			if (end != caption.length()) end--;
			int x = (w - g.getFontMetrics().stringWidth(caption.substring(start, end))) / 2;
			g.drawString(caption.substring(start, end), x, y);
			y += g.getFontMetrics().getHeight();
			start = end;
		}
		
		return img;
	}
	
}
