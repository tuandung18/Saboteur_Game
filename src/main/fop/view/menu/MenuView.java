package fop.view.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import fop.io.FontReader;
import fop.view.MainFrame;
import fop.view.View;
import javax.swing.*;

/**
 * 
 * Stellt die Grundeinheit für Ansichten der Menüführung dar.<br>
 * Die abstrakte Methode {@link #addContent(JPanel)} fügt der Ansicht die einzelnen Elemente hinzu.<br>
 * Mit {@link #createButton(String)} kann ein einheitlicher Button erstellt werden.
 *
 */
@SuppressWarnings("serial")
public abstract class MenuView extends View {
	
	private final Component rigidBefore, rigidAfter;
	private final JLabel titleLabel;
	
	private final List<JButton> resizeButtons;
	
	public MenuView(MainFrame window, String titleText) {
		super(window);
		resizeButtons = new LinkedList<>();
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		
		// title
		add(rigidBefore = Box.createRigidArea(new Dimension(0, 1)));
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
		titleLabel = new JLabel(titleText);
		titleLabel.setFont(FontReader.readMenuFont());
		titlePanel.add(titleLabel);
		add(titlePanel);
		add(rigidAfter = Box.createRigidArea(new Dimension(0, 1)));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		add(mainPanel);
		
		// main content
		addContent(mainPanel);
	}
	
	/**
	 * Fügt dem übergebenen Panel den Inhalt der Ansicht hinzu.
	 * @param contentPanel das Panel, zu dem der Inhalt hinzugefügt werden soll
	 */
	protected abstract void addContent(JPanel contentPanel);
	
	/**
	 * Erstelle einen einheitlichen Button für die Menüführung.
	 * @param text der Text des Buttons
	 * @return den Button
	 */
	protected JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setFont(FontReader.readMenuFont());
		button.setBorder(BorderFactory.createEtchedBorder());
		button.setBackground(new Color(121, 85, 72));
		button.setForeground(Color.WHITE);
		button.setFocusable(false);
		resizeButtons.add(button);
		return button;
	}
	
	/**
	 * Aktualisiert die Dimensionen und Schriftgrößen aller Elemente
	 * inklusive der Buttons in {@link #resizeButtons}.
	 */
	@Override
	public void onResize() {
		super.onResize();
		int size = Math.min(getWidth(), getHeight());
		
		Dimension before = new Dimension(0, size / 20);
		rigidBefore.setMinimumSize(before);
		rigidBefore.setPreferredSize(before);
		rigidBefore.setMaximumSize(before);
		
		titleLabel.setFont(titleLabel.getFont().deriveFont(size / 8f));
		
		Dimension after = new Dimension(0, size / 40);
		rigidAfter.setMinimumSize(after);
		rigidAfter.setPreferredSize(after);
		rigidAfter.setMaximumSize(after);
		
		for (JButton button : resizeButtons)
			button.setFont(button.getFont().deriveFont(size / 15f));
	}
	
}
