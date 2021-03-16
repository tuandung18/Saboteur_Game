package fop.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * 
 * Wird genutzt, um Icons aus den Ressourcen zu laden.<br>
 * <br>
 * Alle Bilder liegen im Ordner {@link #PATH}.<br>
 * Mittels {@link #readIcon(String)} kann das passende Bild anhand des Namens abgerufen werden.
 *
 */
public final class IconReader {
	
	/** Der Pfad zu den Bildern */
	private static final String PATH = "/icon";
	
	private IconReader() {}
	
	/** Speichert Bilder */
	private static final Map<String, BufferedImage> safe = new HashMap<>();
	
	/**
	 * Liefert das Bild mit dem übergebenen Namen aus dem Ordner {@value #PATH}.
	 * @param name der Name des Bildes
	 * @return das Bild
	 */
	public static BufferedImage readIcon(String name) {
		if (safe.containsKey(name)) return safe.get(name);
		
		// try to load from resources
		BufferedImage img;
		try (InputStream is = IconReader.class.getResourceAsStream(String.format("%s/%s.png", PATH, name))) {
			img = ImageIO.read(is);
		} catch (IOException | IllegalArgumentException e) {
			// nothing found throw warning
			throw new IllegalArgumentException(String.format("No image for the given icon was found: %s", name));
		}
		
		// save and return image
		safe.put(name, img);
		return img;
	}
	
}
