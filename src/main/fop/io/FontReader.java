package fop.io;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Wird genutzt, um Schriftarten aus den Ressourcen zu laden.<br>
 * <br>
 * Alle Schriftarten liegen im Ordner {@link #PATH}.<br>
 * Mittels {@link #readFont(String)} kann die Schriftart anhand des Namens abgerufen werden.
 *
 */
public final class FontReader {
	
	/** Der Pfad zu den Schriftarten */
	private static final String PATH = "/font";
	
	/** Der Name der Standardschriftart für das Menü */
	private static final String MENU_FONT = "CATFranken-Deutsch";
	
	private FontReader() {}
	
	/**
	 * Liefert die Schriftart mit dem übergebenen Namen aus dem Ordner {@value #PATH}.
	 * @param name der Name der Schriftart
	 * @return die Schriftart
	 */
	public static Font readFont(String name) {
		try (InputStream is = FontReader.class.getResourceAsStream(String.format("%s/%s.ttf", PATH, name))) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont(12f);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			return font;
		} catch (FontFormatException | IOException e) {
			// nothing found throw warning
			throw new IllegalArgumentException(String.format("No font with the given name was found: %s", name));
		}
	}
	
	/**
	 * Liefert die Standardschriftart für das Menü.
	 * @return die Schriftart
	 */
	public static Font readMenuFont() {
		return readFont(MENU_FONT);
	}
	
}
