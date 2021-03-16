package fop.view.game;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import fop.controller.GameController;
import fop.model.Player;
import javax.swing.JPanel;

/**
 * 
 * Zeit Informationen aller Spieler inklusive deren Handkarten.
 *
 */
@SuppressWarnings("serial")
public class PlayersPanel extends JPanel {
	
	private final List<PlayerPanel> playerPanels = new LinkedList<>();
	
	public PlayersPanel() {
		setBackground(new Color(222, 222, 222));
		setLayout(null);
		for (Player player : GameController.getPlayers()) {
			PlayerPanel playerPanel = new PlayerPanel(player);
			playerPanels.add(playerPanel);
			add(playerPanel);
		}
		GameController.addPropertyChangeListener(GameController.NEXT_PLAYER, evt -> repaint());
	}
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		Graphics2D g = (Graphics2D) g0;
		
		// set rendering hints
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setStroke(new BasicStroke(2));
		
		// 2 is width of border
		int playerHeight = getHeight() - 4;
		List<Integer> widths = playerPanels.stream().map(pp -> pp.weightedWidth()).collect(Collectors.toList());
		
		// update player panel positions
		int x = 2, y = 2;
		for (int i = 0; i < playerPanels.size(); i++) {
			// get width of player panel
			int playerWidth = widths.get(i);
			if (playerWidth == 0) // set maximum width
				// total width - border - n * separation line - total other players width
				playerWidth = getWidth() - 2 - (widths.size() - 1) * 8 - widths.stream().mapToInt(e -> e).sum();
			
			// position player panel
			playerPanels.get(i).setBounds(x, y, playerWidth, playerHeight);
			
			// draw separation line
			if (i != playerPanels.size() - 1) {
				x += playerWidth;
				x += 4;
				g.setColor(getForeground());
				g.drawLine(x, 8, x, getHeight() - 9);
				x += 4;
			}
		}
	}
	
}
