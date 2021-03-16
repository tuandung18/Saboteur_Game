package fop.view.game;

import static fop.io.CardImageReader.ASPECT_RATIO;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import fop.controller.GameController;
import fop.io.CardImageReader;
import fop.model.Player;
import fop.model.cards.Card;
import fop.model.cards.GoalCard;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * Stellt Methoden zum Anzeigen verschiedener Dialoge bereit.
 *
 */
public final class DialogHandler {
	
	/** Kann genutzt werden, um unerwünschte Dialoge während der Entwicklung auszublenden. */
	private static final boolean DISABLE_DIALOGS = false;
	
	private final Component parent;
	
	@SuppressWarnings("unchecked")
	public DialogHandler(Component parent) {
		this.parent = parent;
		GameController.addPropertyChangeListener(GameController.INFORM_NEXT_PLAYER, evt -> showNextPlayerDialog((Player) evt.getNewValue()));
		GameController.addPropertyChangeListener(GameController.ACTIVE_PLAYER_NO_HAND_CARDS, evt -> showActivePlayerNoHandCardsDialog());
		GameController.addPropertyChangeListener(GameController.DRAW_CARD, evt -> showDrawCardDialog((Card) evt.getNewValue()));
		GameController.addPropertyChangeListener(GameController.LOOK_AT_GOAL_CARD, evt -> showGoalCardDialog((GoalCard) evt.getNewValue()));
		GameController.addPropertyChangeListener(GameController.GAME_OVER, evt -> showGameOverDialog((List<Player>) evt.getNewValue()));
	}
	
	/**
	 * Informiert den nächsten aktiven Spieler, dass er an der Reihe ist.
	 * @param player der nächste aktive Spieler
	 */
	private final void showNextPlayerDialog(Player player) {
		if (DISABLE_DIALOGS) return;
		String[] messages = new String[] {
				"%s ist am Zug!", "%s ist an der Reihe!", "%s, du bist dran!", "Jetzt ist %s an der Reihe!"
		};
		String msg = String.format(messages[(int) (Math.random() * messages.length)], player.getName());
		JOptionPane.showMessageDialog(parent, msg, "Nächster Spieler", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	/**
	 * Informiert den aktiven Spieler darüber, dass ihm keine Handkarten mehr zur Verfügung stehen.
	 */
	private final void showActivePlayerNoHandCardsDialog() {
		if (!GameController.getActivePlayer().isComputer())
			JOptionPane.showMessageDialog(parent, "<html>Du hast keine Handkarten mehr.<br>Dein Zug ist hiermit beendet.</html>",
					"Keine Handkarten mehr", JOptionPane.INFORMATION_MESSAGE, null);
		GameController.doNothing();
	}
	
	/**
	 * Zeigt die neu gezogene Karte.
	 * @param card die neu gezogene Karte
	 */
	private final void showDrawCardDialog(Card card) {
		if (DISABLE_DIALOGS) return;
		if (card == null) return;
		if (GameController.getActivePlayer().isComputer()) return;
		Image img = CardImageReader.readImage(card);
		JOptionPane.showMessageDialog(parent, new JLabel(new ImageIcon(scaleCardImage(img))), "Karte nachgezogen", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	/**
	 * Zeigt die übergebene Zielkarte an.
	 * @param goalCard die zu zeigende Zielkarte
	 */
	private final void showGoalCardDialog(GoalCard goalCard) {
		if (GameController.getActivePlayer().isComputer()) return;
		Image img = CardImageReader.readImage(String.format("goal_%s", goalCard.getType().name().toLowerCase()));
		JOptionPane.showMessageDialog(parent, new JLabel(new ImageIcon(scaleCardImage(img))), "Zielkarte", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	/**
	 * Zeigt den Game Over Dialog mit den Gewinnern an.<br>
	 * Zeigt die Übersicht über die Punkte am Ende des Spiels an.
	 * @param winners die Gewinner des Spiels
	 */
	private final void showGameOverDialog(List<Player> winners) {
		Font font = new JLabel().getFont();
		font = font.deriveFont(font.getSize() * 1.4f);
		font = font.deriveFont(Font.BOLD);
		
		// WINNERS //
		JPanel winnerDialog = new JPanel();
		winnerDialog.setLayout(new BoxLayout(winnerDialog, BoxLayout.PAGE_AXIS));
		
		JPanel headerWinnerPanel = new JPanel();
		headerWinnerPanel.setLayout(new BoxLayout(headerWinnerPanel, BoxLayout.LINE_AXIS));
		JLabel winnerLabel = new JLabel(winners.isEmpty() ? "KEINE GEWINNER" : "GEWINNER");
		winnerLabel.setFont(font);
		headerWinnerPanel.add(winnerLabel);
		winnerDialog.add(headerWinnerPanel);
		
		if (!winners.isEmpty()) {
			JPanel winnerPanel = new JPanel();
			winnerPanel.setBackground(Color.WHITE);
			winnerPanel.setLayout(new BoxLayout(winnerPanel, BoxLayout.LINE_AXIS));
			winnerPanel.setBorder(BorderFactory.createEtchedBorder());
			winnerPanel.add(Box.createRigidArea(new Dimension(6, 0)));
			for (Player player : winners) {
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				
				JLabel nameLabel = new JLabel(player.getName());
				Font nameFont = nameLabel.getFont();
				nameFont = nameFont.deriveFont(nameFont.getSize() * 1.2f);
				nameFont = nameFont.deriveFont(Font.BOLD);
				nameLabel.setFont(nameFont);
				panel.add(nameLabel);
				
				Image img = CardImageReader.readImage(String.format("role_%s", player.getRole().name().toLowerCase()));
				panel.add(new JLabel(new ImageIcon(scaleCardImage(img))));
				
				panel.add(Box.createRigidArea(new Dimension(0, 4)));
				
				winnerPanel.add(panel);
				panel.setBackground(panel.getParent().getBackground());
				winnerPanel.add(Box.createRigidArea(new Dimension(8, 0)));
			}
			winnerPanel.remove(winnerPanel.getComponentCount() - 1);
			winnerPanel.add(Box.createRigidArea(new Dimension(6, 0)));
			winnerDialog.add(winnerPanel);
		}
		
		JOptionPane.showMessageDialog(parent, winnerDialog, "Spielende", JOptionPane.PLAIN_MESSAGE, null);
		
		// SCORES //
		JPanel rankingDialog = new JPanel();
		rankingDialog.setLayout(new BoxLayout(rankingDialog, BoxLayout.PAGE_AXIS));
		
		JPanel headerRankingPanel = new JPanel();
		headerRankingPanel.setLayout(new BoxLayout(headerRankingPanel, BoxLayout.LINE_AXIS));
		JLabel rankingLabel = new JLabel("RANGLISTE");
		rankingLabel.setFont(font);
		headerRankingPanel.add(rankingLabel);
		rankingDialog.add(headerRankingPanel);
		
		DefaultTableModel tableModel = new DefaultTableModel() {
			
			private static final long serialVersionUID = -3009115080240513677L;
			private List<String> columnNames = List.of("Spieler", "Punkte");
			
			@Override
			public String getColumnName(int columnIndex) {
				return columnNames.get(columnIndex);
			}
			
			@Override
			public int getColumnCount() {
				return columnNames.size();
			}
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
		};
		JTable rankings = new JTable(tableModel);
		rankings.setColumnSelectionAllowed(false);
		rankings.setRowSelectionAllowed(false);
		rankings.getTableHeader().setReorderingAllowed(false);
		for (Player player : Arrays.stream(GameController.getPlayers())
				.sorted(Comparator.comparingInt(Player::getScore).reversed()).collect(Collectors.toList()))
			tableModel.addRow(new Object[] {player.getName(), player.getScore()});
		
		rankingDialog.add(new JScrollPane(rankings));
		
		JOptionPane.showMessageDialog(parent, rankingDialog, "Spielende", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	private static final Image scaleCardImage(Image img) {
		float factor = 0.7f;
		int width = new JOptionPane().getPreferredSize().width;
		if (width == 0) width = 100;
		int height = (int) (width * ASPECT_RATIO);
		return img.getScaledInstance((int) (width * factor), (int) (height * factor), Image.SCALE_SMOOTH);
	}
	
}
