package fop.view.game;

import static fop.io.CardImageReader.ASPECT_RATIO;
import static fop.io.CardImageReader.readImage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import fop.controller.GameController;
import fop.model.board.Position;
import fop.model.cards.Card;
import fop.model.cards.GoalCard;
import fop.model.cards.PathCard;
import fop.model.cards.StartCard;
import javax.swing.JPanel;

/**
 * 
 * Stellt das Wegelabyrinth dar.
 *
 */
@SuppressWarnings("serial")
public class GameboardPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	// spacing
	private int margin = 5;
	private int paddingX, paddingY;
	
	// min and max model coordinates
	private int minX, maxX, minY, maxY;
	
	// mouse position in model coordinates
	private int mouseX, mouseY;
	
	// card screen dimensions
	private int cardWidth, cardHeight;
	
	// mappings from model to screen coordinates
	private int sx(int mx) {
		return (mx - minX) * cardWidth + margin + paddingX;
	}
	
	private int sy(int my) {
		return (my - minY) * cardHeight + margin + paddingY;
	}
	
	// mappings from screen to model coordinates
	private int mx(int sx) {
		int mx = (sx - margin - paddingX) / cardWidth + minX;
		if (mx < minX || mx > maxX) return Integer.MAX_VALUE;
		return mx;
	}
	
	private int my(int sy) {
		int my = (sy - margin - paddingY) / cardHeight + minY;
		if (my < minY || my > maxY) return Integer.MAX_VALUE;
		return my;
	}
	
	// speed optimizations
	private Set<Position> validPositions = new HashSet<>();
	
	public GameboardPanel() {
		setBackground(new Color(252, 245, 222));
		addMouseListener(this);
		addMouseMotionListener(this);
		GameController.addPropertyChangeListener(GameController.SELECT_CARD, evt -> {
			updateValidPositions();
			repaint();
		});
	}
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		Graphics2D g = (Graphics2D) g0;
		
		// set rendering hints
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// calculate min and max values
		Set<Position> cardPositions = GameController.getCardPositions();
		minX = cardPositions.stream().mapToInt(Position::x).min().orElse(0) - 1;
		maxX = cardPositions.stream().mapToInt(Position::x).max().orElse(0) + 1;
		minY = cardPositions.stream().mapToInt(Position::y).min().orElse(0) - 1;
		maxY = cardPositions.stream().mapToInt(Position::y).max().orElse(0) + 1;
		
		// calculate card dimension
		cardWidth = (int) Math.min((getWidth() - 2 * margin) / (maxX - minX + 1), (getHeight() - 2 * margin) / (maxY - minY + 1) / ASPECT_RATIO);
		cardHeight = (int) (cardWidth * ASPECT_RATIO);
		if (cardWidth == 0 || cardHeight == 0) return;
		int arcSize = cardHeight / 10;
		
		// increase min and max values to use available space
		int incX = (getWidth() - 2 * margin - (maxX - minX + 1) * cardWidth) / cardWidth;
		minX -= incX / 2;
		maxX += incX - incX / 2;
		int incY = (getHeight() - 2 * margin - (maxY - minY + 1) * cardHeight) / cardHeight;
		minY -= incY / 2;
		maxY += incY - incY / 2;
		
		// set padding to center board
		paddingX = (getWidth() - 2 * margin - (maxX - minX + 1) * cardWidth) / 2;
		paddingY = (getHeight() - 2 * margin - (maxY - minY + 1) * cardHeight) / 2;
		
		
		// == draw bounds == //
		g.setColor(new Color(246, 225, 157));
		for (int x = minX; x <= maxX + 1; x++)
			g.drawLine(sx(x), sy(minY), sx(x), sy(maxY + 1));
		for (int y = minY; y <= maxY + 1; y++)
			g.drawLine(sx(minX), sy(y), sx(maxX + 1), sy(y));
		
		
		// == draw cards == //
		
		for (Position pos : cardPositions)
			drawCard(g, GameController.getCardAt(pos), pos, 1f);
		
		
		// == draw outlines and shadows == //
		
		if (GameController.getSelectedCard() == null) return;
		
		int strokeWidth = Math.max(cardWidth / 20, 2);
		float[] strokeDash = {Math.max(cardWidth / 10f, 5f), Math.max(cardWidth / 15f, 3f)};
		float strokeOffset = strokeDash[0] + strokeDash[1] * 1.5f;
		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, strokeDash, strokeOffset));
		g.setColor(Color.RED);
		
		// path card -> highlight suitable spots
		boolean hasBrokenTool = GameController.getActivePlayer() == null || GameController.getActivePlayer().hasBrokenTool();
		if (GameController.getSelectedCard().isPathCard() && !hasBrokenTool) {
			PathCard selectedCard = (PathCard) GameController.getSelectedCard();
			for (int x = minX; x <= maxX; x++)
				for (int y = minY; y <= maxY; y++) {
					Position pos = Position.of(x, y);
					if (!validPositions.contains(pos)) continue;
					g.drawRoundRect(sx(x), sy(y), cardWidth, cardHeight, arcSize, arcSize);
					if (mouseX == x && mouseY == y)
						drawCard(g, selectedCard, pos, 0.3f);
				}
		}
		
		// rockfall -> highlight all cards except start and end
		if (GameController.getSelectedCard().isRockfall()) for (Position pos : cardPositions) {
			PathCard card = GameController.getCardAt(pos);
			if (card instanceof StartCard || card instanceof GoalCard) continue;
			g.drawRoundRect(sx(pos.x()), sy(pos.y()), cardWidth, cardHeight, arcSize, arcSize);
			if (mouseX == pos.x() && mouseY == pos.y())
				drawCard(g, GameController.getSelectedCard(), pos, 0.7f);
		}
		
		// map -> highlight not turned goal cards
		if (GameController.getSelectedCard().isMap()) for (Position pos : cardPositions) {
			PathCard card = GameController.getCardAt(pos);
			if (!card.isGoalCard() || !((GoalCard) card).isCovered()) continue;
			g.drawRoundRect(sx(pos.x()), sy(pos.y()), cardWidth, cardHeight, arcSize, arcSize);
			if (mouseX == pos.x() && mouseY == pos.y())
				drawCard(g, GameController.getSelectedCard(), pos, 0.6f);
		}
		
	}
	
	private void drawCard(Graphics2D g, Card card, Position pos, float opacity) {
		BufferedImage img = readImage(card);
		if (opacity != 1f) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(img, sx(pos.x()), sy(pos.y()), cardWidth, cardHeight, null);
		if (opacity != 1f) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (GameController.getActivePlayer() != null && GameController.getActivePlayer().isComputer()) return;
		if (GameController.getSelectedCard() == null) return;
		
		switch (e.getButton()) {
			// left click
			case MouseEvent.BUTTON1:
				// place path card
				boolean hasBrokenTool = GameController.getActivePlayer() == null || GameController.getActivePlayer().hasBrokenTool();
				if (GameController.getSelectedCard().isPathCard() && !hasBrokenTool) {
					PathCard selectedCard = (PathCard) GameController.getSelectedCard();
					if (GameController.canCardBePlacedAt(mouseX, mouseY, selectedCard))
						GameController.placeSelectedCardAt(mouseX, mouseY);
				}
				
				// destroy with rockfall
				else if (GameController.getSelectedCard().isRockfall()) {
					PathCard card = GameController.getCardAt(Position.of(mouseX, mouseY));
					if (card != null && !card.isStartCard() && !card.isGoalCard())
						GameController.destroyCardWithSelectedCardAt(mouseX, mouseY);
				}
				
				// look at goal card with map
				else if (GameController.getSelectedCard().isMap()) {
					PathCard card = GameController.getCardAt(Position.of(mouseX, mouseY));
					if (card != null && card.isGoalCard())
						GameController.lookAtGoalCardWithSelectedCard((GoalCard) card);
				}
				
				break;
			
			// right click
			case MouseEvent.BUTTON3:
				// rotate path card
				if (GameController.getSelectedCard().isPathCard()) {
					((PathCard) GameController.getSelectedCard()).rotate();
					GameController.selectCard(GameController.getSelectedCard());
				}
				break;
			
			default:
				break;
		}
		
		repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// computer player
		if (GameController.getActivePlayer() != null && GameController.getActivePlayer().isComputer()) {
			setToolTipText(null);
			return;
		}
		// update mouse position and repaint if changed
		int oldX = mouseX;
		int oldY = mouseY;
		mouseX = mx(e.getPoint().x);
		mouseY = my(e.getPoint().y);
		setToolTipText(generateToolTipText());
		if (oldX != mouseX || oldY != mouseY) repaint();
	}
	
	private String generateToolTipText() {
		Card mouseCard = GameController.getCardAt(Position.of(mouseX, mouseY));
		Card selectedCard = GameController.getSelectedCard();
		if (selectedCard != null) {
			if (selectedCard.isPathCard())
				if (GameController.getActivePlayer() != null && GameController.getActivePlayer().hasBrokenTool())
					return "Du bist gesperrt und kannst keine Wegekarte legen.";
			
			if (selectedCard.isMap())
				if (mouseCard != null && mouseCard.isGoalCard()) return "Hier könnte der Goldschatz sein.";
		}
		
		if (mouseCard != null) {
			if (mouseCard.isStartCard()) return "<html>Die Startkarte.<br>Hier beginnt der Weg ins Wegelabyrinth.</html>";
			if (mouseCard.isGoalCard()) {
				if (!((GoalCard) mouseCard).isCovered()) {
					if (((GoalCard) mouseCard).getType() == GoalCard.Type.Gold)
						return "<html>Die Zielkarte mit dem Goldschatz.<br>Das Ziel wurde erreicht.</html>";
					return "<html>Eine Zielkarte.<br>Hier ist der Goldschatz nicht.</html>";
				}
				return "<html>Eine Zielkarte.<br>Hier könnte der Goldschatz sein.</html>";
			}
		}
		
		return null;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseX = Integer.MAX_VALUE;
		mouseY = Integer.MAX_VALUE;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
	
	private void updateValidPositions() {
		// clear positions of last card
		validPositions.clear();
		
		// skip if selection is not a path card
		if (GameController.getSelectedCard() == null || !GameController.getSelectedCard().isPathCard()) return;
		
		// collect all valid positions
		PathCard selectedCard = (PathCard) GameController.getSelectedCard();
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				if (GameController.canCardBePlacedAt(x, y, selectedCard))
					validPositions.add(Position.of(x, y));
	}
	
}
