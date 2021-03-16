package fop.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * Stellt die Grundeinheit einer Ansicht des Fensters dar.
 *
 */
@SuppressWarnings("serial")
public class View extends JPanel {
	
	private MainFrame window;
	
	public View(MainFrame window) {
		this.window = window;
	}
	
	protected MainFrame getWindow() {
		return window;
	}
	
	/**
	 * Ändert die Größe des Fensters bei Wechsel der Ansicht.
	 * @param window das Fenster
	 * @param oldView die vorherige Ansicht
	 */
	public void setWindowSize(JFrame window, View oldView) {}
	
	/**
	 * Wird aufgerufen, damit die sich Ansicht an die neue Fenstergröße anpassen kann.
	 */
	public void onResize() {}
	
}
