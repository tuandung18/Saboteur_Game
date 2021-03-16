package fop.view.menu;

import java.awt.*;
import java.awt.event.WindowEvent;

import fop.io.IconReader;
import fop.view.MainFrame;
import fop.view.View;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * Die Ansicht für das Hauptmenü.
 *
 */
@SuppressWarnings("serial")
public final class MainMenu extends MenuView {
	
	public MainMenu(MainFrame window) {
		super(window, "Saboteur");
	}
	
	@Override
	protected void addContent(JPanel contentPanel) {
		contentPanel.setLayout(new GridBagLayout());
		
		// left image //
		GridBagConstraints leftImageConstraints = new GridBagConstraints();
		leftImageConstraints.weightx = 1.5;
		leftImageConstraints.weighty = 1.0;
		leftImageConstraints.fill = GridBagConstraints.BOTH;
		leftImageConstraints.insets = new Insets(2, 0, 2, 2);
		leftImageConstraints.gridx = 1;
		leftImageConstraints.gridy = 0;
		contentPanel.add(new ImageLabel(false), leftImageConstraints);
		
		// button panel //
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
		buttonPanelConstraints.weightx = 1.0;
		buttonPanelConstraints.weighty = 1.0;
		buttonPanelConstraints.fill = GridBagConstraints.BOTH;
		buttonPanelConstraints.insets = new Insets(2, 2, 2, 2);
		buttonPanelConstraints.gridx = 2;
		buttonPanelConstraints.gridy = 0;
		JPanel buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, buttonPanelConstraints);
		buttonPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JButton startButton = createButton("Start");
		startButton.addActionListener(evt -> getWindow().setView(new NewGameView(getWindow())));
		buttonPanel.add(startButton, gbc);
		gbc.gridy++;
		
		JButton highscoreButton = createButton("Punkte");
		// TODO Aufgabe 4.3.1
		// highscoreButton.addActionListener(evt -> getWindow().setView(new HighscoreView(getWindow())));
		buttonPanel.add(highscoreButton, gbc);
		gbc.gridy++;
		
		JButton infoButton = createButton("Info");
		infoButton.addActionListener(evt -> getWindow().setView(new AboutView(getWindow())));
		buttonPanel.add(infoButton, gbc);
		gbc.gridy++;
		
		JButton exitButton = createButton("Beenden");
		exitButton.addActionListener(evt -> getWindow().dispatchEvent(new WindowEvent(getWindow(), WindowEvent.WINDOW_CLOSING)));
		buttonPanel.add(exitButton, gbc);
		
		// right image //
		GridBagConstraints rightImageConstraints = new GridBagConstraints();
		rightImageConstraints.weightx = 1.5;
		rightImageConstraints.weighty = 1.0;
		rightImageConstraints.fill = GridBagConstraints.BOTH;
		rightImageConstraints.insets = new Insets(2, 2, 2, 0);
		rightImageConstraints.gridx = 3;
		rightImageConstraints.gridy = 0;
		contentPanel.add(new ImageLabel(true), rightImageConstraints);
	}
	
	@Override
	public void setWindowSize(JFrame window, View oldView) {
		if (oldView instanceof MenuView) return;
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double scale = dimension.width > 2000 ? 0.3 : 0.5;
		dimension.width *= scale;
		dimension.height *= scale;
		window.setPreferredSize(dimension);
		window.setSize(dimension);
		window.setLocationRelativeTo(null);
	}
	
	private static class ImageLabel extends JLabel {
		
		private boolean mirrored = false;
		
		public ImageLabel(boolean mirrored) {
			this.mirrored = mirrored;
		}
		
		@Override
		public void paint(Graphics g0) {
			super.paint(g0);
			Graphics2D g = (Graphics2D) g0;
			
			// set rendering hints
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			// calculate size
			int size = (int) (Math.min(getWidth(), getHeight()) * 0.7);
			
			// draw images
			Image img = IconReader.readIcon("pickaxe");
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			if (mirrored)
				g.drawImage(img, x + size, y, -size, size, null);
			else g.drawImage(img, x, y, size, size, null);
		}
	}
	
}
