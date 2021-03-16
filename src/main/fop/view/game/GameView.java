package fop.view.game;

import java.awt.*;

import fop.controller.GameController;
import fop.view.MainFrame;
import fop.view.View;
import fop.view.menu.MainMenu;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class GameView extends View {
	
	public GameView(MainFrame window) {
		super(window);
		setLayout(new GridBagLayout());
		
		// Gameboard
		JPanel gameboardPanel = new GameboardPanel();
		gameboardPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints gameboardConstraints = new GridBagConstraints();
		gameboardConstraints.weightx = 22.0;
		gameboardConstraints.weighty = 14.0;
		gameboardConstraints.fill = GridBagConstraints.BOTH;
		gameboardConstraints.insets = new Insets(2, 2, 2, 2);
		gameboardConstraints.gridx = 0;
		gameboardConstraints.gridy = 0;
		add(gameboardPanel, gameboardConstraints);
		
		// Piles
		JPanel pilePanel = new PilePanel();
		pilePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints pileConstraints = new GridBagConstraints();
		pileConstraints.weightx = 4.0;
		pileConstraints.fill = GridBagConstraints.BOTH;
		pileConstraints.insets = new Insets(2, 0, 2, 2);
		pileConstraints.gridx = 1;
		pileConstraints.gridy = 0;
		add(pilePanel, pileConstraints);
		
		// Players
		JPanel playersPanel = new PlayersPanel();
		playersPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints playersConstraints = new GridBagConstraints();
		playersConstraints.gridwidth = GameController.getPlayers().length;
		playersConstraints.weighty = 4.5;
		playersConstraints.fill = GridBagConstraints.BOTH;
		playersConstraints.insets = new Insets(0, 2, 2, 2);
		playersConstraints.gridx = 0;
		playersConstraints.gridy = 1;
		add(playersPanel, playersConstraints);
		
		// Dialog Handler
		@SuppressWarnings("unused")
		DialogHandler dialogHandler = new DialogHandler(this);
		
		// Game Over //
		GameController.addPropertyChangeListener(GameController.GAME_OVER, evt -> {
			getWindow().setView(new MainMenu(getWindow()));
		});
	}
	
	@Override
	public void setWindowSize(JFrame window, View oldView) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double scale = dimension.width > 2000 ? 0.65 : 0.9;
		dimension.width *= scale;
		dimension.height *= scale;
		window.setPreferredSize(dimension);
		window.setSize(dimension);
		window.setLocationRelativeTo(null);
	}
	
}
