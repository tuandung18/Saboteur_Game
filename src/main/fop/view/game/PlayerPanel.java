package fop.view.game;

import static fop.io.CardImageReader.ASPECT_RATIO;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fop.controller.GameController;
import fop.io.CardImageReader;
import fop.io.IconReader;
import fop.model.Player;
import fop.model.cards.*;
import javax.swing.JPanel;

/**
 * 
 * Stellt den Spielbereich und damit u.A. die Handkarten eines Spielers dar.
 *
 */
@SuppressWarnings("serial")
public class PlayerPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	private final Player player;
	
	// spacing
	private int scrollX = 0;
	private int padding = 8;
	
	// screen dimensions of different parts
	private int infoWidth, infoHeight;
	private int cardsWidth, cardsHeight;
	
	// the card the mouse is hovering
	private Card mouseHandCard; // if it is a hand card
	private BrokenToolCard mouseToolTypeCard; // if it is a broken tool card
	private boolean mouseHoveringName; // if it is the player name
	private boolean mouseHoveringRole; // if it is the role card
	
	// card screen dimensions
	private int cardWidth, cardHeight;
	
	// speed optimizations
	private Rectangle rolePosition = new Rectangle();
	private Map<Rectangle, Card> cardPositions = new HashMap<>();
	private Map<Ellipse2D, BrokenToolCard> brokenToolPositions = new HashMap<>();
	private Rectangle nameFrame = new Rectangle();
	
	public PlayerPanel(Player player) {
		this.player = player;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		GameController.addPropertyChangeListener(GameController.NEXT_PLAYER, evt -> {
			if (player != GameController.getActivePlayer()) {
				scrollX = 0;
				mouseHandCard = null;
				mouseToolTypeCard = null;
				mouseHoveringName = false;
			}
			repaint();
		});
		GameController.addPropertyChangeListener(GameController.SELECT_CARD, evt -> repaint());
	}
	
	@Override
	public void paint(Graphics g0) {
		setBackground(getParent().getBackground());
		super.paint(g0);
		Graphics2D g = (Graphics2D) g0;
		
		// set rendering hints
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// define highlight stroke
		int strokeWidth = Math.max(cardWidth / 20, 2);
		float[] strokeDash = {Math.max(cardWidth / 10, 5f), Math.max(cardWidth / 15, 3f)};
		float strokeOffset = strokeDash[0] + strokeDash[1] * 1.5f;
		BasicStroke highlightStroke = new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, strokeDash, strokeOffset);
		
		// update scroll if resized
		scrollX = getWidth() < minimumWidth() ? Math.max(getWidth() - minimumWidth(), Math.min(scrollX, 0)) : 0;
		
		
		//== draw info ==//
		Card selectedCard = GameController.getSelectedCard();
		infoWidth = 0;
		infoHeight = 0;
		padding = getHeight() / 20;
		
		// draw name and score //
		g.setStroke(new BasicStroke(Math.max(getHeight() / 170f, 1f)));
		Font nameFont = g.getFont().deriveFont(Math.max(getHeight() / 13f, 1f)).deriveFont(Font.BOLD);
		Font scoreFont = g.getFont().deriveFont(Math.max(getHeight() / 15f, 1f)).deriveFont(Font.PLAIN);
		String name = player.getName();
		String score = String.format("(%d)", player.getScore());
		int nameWidth = g.getFontMetrics(nameFont).stringWidth(name);
		int nameHeight = g.getFontMetrics(nameFont).getHeight();
		int nameAscent = g.getFontMetrics(nameFont).getAscent();
		int scoreWidth = g.getFontMetrics(scoreFont).stringWidth(score);
		int scoreHeight = g.getFontMetrics(scoreFont).getHeight();
		int scoreAscent = g.getFontMetrics(scoreFont).getAscent();
		nameFrame = new Rectangle(padding, padding, 4 + nameWidth + 4 + scoreWidth + 4, Math.max(nameHeight, scoreHeight));
		g.drawRoundRect(nameFrame.x, nameFrame.y, nameFrame.width, nameFrame.height, 4, 4);
		g.setFont(nameFont);
		g.drawString(name, padding + 4, padding + Math.max(nameAscent, scoreAscent));
		g.setFont(scoreFont);
		g.drawString(score, padding + 4 + nameWidth + 4, padding + Math.max(nameAscent, scoreAscent) - 1);
		
		// highlight if a tool of the player can be broken
		if (selectedCard != null && selectedCard.isBrokenTool() && player != GameController.getActivePlayer()
				&& player.canToolBeBroken((BrokenToolCard) selectedCard)) {
			g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
					new float[] {strokeDash[0] / 1.5f, strokeDash[1] / 1.5f}, strokeWidth));
			g.setColor(Color.RED);
			g.drawRoundRect(nameFrame.x - padding / 4, nameFrame.y - padding / 4, nameFrame.width + padding / 2, nameFrame.height + padding / 2, 4, 4);
			
			// shadow if mouse is hovering the selected card
			if (mouseHoveringName)
				fillShape(g, nameFrame, Color.RED, 0.5f);
		}
		infoWidth += nameFrame.width;
		infoHeight += nameFrame.height;
		
		// draw broken tools //
		brokenToolPositions.clear();
		for (ToolType type : ToolType.values())
			if (player.hasBrokenTool(type)) {
				BrokenToolCard brokenToolCard = player.getBrokenTool(type);
				BufferedImage brokenToolIcon = IconReader.readIcon(String.format("broken_%s", type.name().toLowerCase()));
				Ellipse2D circle = new Ellipse2D.Double(padding + infoWidth + padding, padding, infoHeight, infoHeight);
				g.drawImage(brokenToolIcon, (int) circle.getX(), (int) circle.getY(), (int) circle.getWidth(), (int) circle.getHeight(), null);
				
				// highlight if tool can be fixed by using the selected card
				if (selectedCard != null && selectedCard.isFixedTool()
						&& player.canBrokenToolBeFixed(brokenToolCard, (FixedToolCard) selectedCard)) {
					g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
							new float[] {(float) ((circle.getWidth() + padding / 2) * Math.PI / 16f)}, strokeWidth));
					g.setColor(Color.RED);
					g.drawOval((int) circle.getX() - padding / 4, (int) circle.getY() - padding / 4,
							(int) circle.getWidth() + padding / 2, (int) circle.getHeight() + padding / 2);
					
					// shadow if mouse is hovering the selected card
					if (mouseToolTypeCard == brokenToolCard)
						fillShape(g, circle, new Color(0, 128, 0), 0.5f);
				}
				brokenToolPositions.put(circle, brokenToolCard);
				infoWidth += padding + infoHeight;
			}
		
		
		//== draw cards ==//
		cardsWidth = 0;
		cardsHeight = 0;
		cardHeight = getHeight() - padding * 3 - infoHeight;
		cardWidth = (int) (cardHeight / ASPECT_RATIO);
		int arcSize = cardHeight / 10;
		
		// draw role //
		String roleImageName = player == GameController.getActivePlayer() ? String.format("role_%s", player.getRole().name().toLowerCase()) : "role";
		BufferedImage roleImage = CardImageReader.readImage(roleImageName);
		rolePosition = new Rectangle(scrollX + padding, padding + infoHeight + padding * 3 / 2, cardWidth, cardHeight);
		g.drawImage(roleImage, rolePosition.x, rolePosition.y, rolePosition.width, rolePosition.height, null);
		cardsWidth += cardWidth;
		cardsHeight += cardHeight;
		
		// draw line //
		if (player == GameController.getActivePlayer()) {
			g.setColor(getParent().getBackground().darker());
			g.setStroke(new BasicStroke(1));
			g.drawLine(scrollX + padding + cardsWidth + padding, padding + infoHeight + padding * 3 / 2 + 4,
					scrollX + padding + cardsWidth + padding, padding + infoHeight + padding + cardsHeight - 4);
			cardsWidth += padding;
		}
		
		// draw hand //
		cardPositions.clear();
		if (player == GameController.getActivePlayer()) {
			int x = scrollX + padding + cardsWidth;
			int y = padding + infoHeight + padding * 3 / 2;
			for (Card card : player.getAllHandCards()) {
				x += padding;
				boolean moveUp = card == mouseHandCard || card == selectedCard; // move up if selected or hovered
				Rectangle rect = new Rectangle(x, moveUp ? y - padding : y, cardWidth, cardHeight);
				g.drawImage(CardImageReader.readImage(card), rect.x, rect.y, rect.width, rect.height, null);
				
				// highlight if selected card
				if (card == selectedCard) {
					g.setStroke(highlightStroke);
					g.setColor(Color.RED);
					g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, arcSize, arcSize);
				}
				cardPositions.put(rect, card);
				x += cardWidth;
			}
			cardsWidth += player.getAllHandCards().size() * (padding + cardWidth);
		}
		
		// debug bounds
		//g.setStroke(new BasicStroke(1));
		//g.setColor(Color.BLACK);
		//g.drawRect(padding, padding, infoWidth, infoHeight);
		//g.drawRect(scrollX + padding, padding + infoHeight + padding * 3 / 2, cardsWidth, cardsHeight);
		
		// force size for the first time
		if (getWidth() < 20) setSize(getHeight(), minimumWidth());
	}
	
	private static void fillShape(Graphics2D g, Shape shape, Color color, float opacity) {
		if (opacity != 1f) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.setColor(color);
		g.fill(shape);
		if (opacity != 1f) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (GameController.getActivePlayer() != null && GameController.getActivePlayer().isComputer()) return;
		switch (e.getButton()) {
			// left click
			case MouseEvent.BUTTON1:
				Card selectedCard = GameController.getSelectedCard();
				
				// select hand card
				if (mouseHandCard != null)
					if (mouseHandCard != selectedCard)
						GameController.selectCard(mouseHandCard);
					else GameController.selectCard(null);
				else
					if (player == GameController.getActivePlayer() && mouseToolTypeCard == null && !mouseHoveringName)
						GameController.selectCard(null);
				
				// fix broken tool card
				if (selectedCard != null && selectedCard.isFixedTool() && mouseToolTypeCard != null)
					if (player.canBrokenToolBeFixed(mouseToolTypeCard, (FixedToolCard) selectedCard))
						GameController.fixBrokenToolCardWithSelectedCard(player, mouseToolTypeCard);
				
				// break tool
				if (selectedCard != null && selectedCard.isBrokenTool() && mouseHoveringName)
					if (player != GameController.getActivePlayer() && player.canToolBeBroken((BrokenToolCard) selectedCard))
						GameController.breakToolWithSelectedCard(player);
				
				break;
			
			// right click
			case MouseEvent.BUTTON3:
				// rotate path card
				if (mouseHandCard != null && mouseHandCard.isPathCard()) {
					((PathCard) mouseHandCard).rotate();
					// update selected card if mouse card was the selected card
					if (mouseHandCard == GameController.getSelectedCard())
						GameController.selectCard(mouseHandCard);
				}
				break;
			
			default:
				break;
		}
		
		repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMoved(e.getPoint());
	}
	
	private void mouseMoved(Point p) {
		// computer player
		if (GameController.getActivePlayer() != null && GameController.getActivePlayer().isComputer()) {
			setToolTipText(null);
			return;
		}
		// update mouse card and repaint if changed
		Card oldHandCard = mouseHandCard;
		mouseHandCard = getHandCardAt(p);
		Card oldToolTypeCard = mouseToolTypeCard;
		mouseToolTypeCard = getToolTypeCardAt(p);
		boolean oldNameValue = mouseHoveringName;
		mouseHoveringName = nameFrame.contains(p);
		boolean oldRoleValue = mouseHoveringRole;
		mouseHoveringRole = rolePosition.contains(p);
		if (oldHandCard != mouseHandCard || oldToolTypeCard != mouseToolTypeCard
				|| oldNameValue != mouseHoveringName || oldRoleValue != mouseHoveringRole) {
			setToolTipText(generateToolTipText());
			repaint();
		}
	}
	
	private String generateToolTipText() {
		if (player == GameController.getActivePlayer()) {
			
			if (mouseHandCard != null) {
				if (mouseHandCard.isPathCard()) {
					if (player.hasBrokenTool()) return "Du bist gesperrt und kannst keine Wegekarte legen.";
					return "<html>Eine Wegekarte.<br>Mit einem Rechtsklick kann sie gedreht werden.</html>";
				}
				if (mouseHandCard.isMap()) return "<html>Mit der Schatzkarte kannst du schauen,<br>was sich unter einer Zielkarte verbirgt.</html>";
				if (mouseHandCard.isRockfall()) return "<html>Mit dem Steinschlag kann eine Karte<br>aus dem Wegelabyrinth zerstört werden.</html>";
				if (mouseHandCard.isBrokenTool()) return "<html>Mit dem zerbrochenen Werkzeug kann<br>ein anderer Spieler gesperrt werden.</html>";
				if (mouseHandCard.isFixedTool()) return "<html>Zerbrochene Werkzeuge können<br>hiermit wieder repariert werden.</html>";
			}
			
			if (mouseToolTypeCard != null) {
				Card selectedCard = GameController.getSelectedCard();
				if (selectedCard != null && selectedCard.isFixedTool())
					if (player.canBrokenToolBeFixed(mouseToolTypeCard, (FixedToolCard) selectedCard))
						return "Hiermit kannst du das Werkzeug wieder reparieren.";
				return "Du bist gesperrt und kannst keine Wegekarte legen.";
			}
			
			if (mouseHoveringRole) switch (player.getRole()) {
				case GOLD_MINER:
					return "<html>Du bist Goldsucher.<br>Dein Ziel ist es, schnellstmöglich zum Goldschatz zu gelangen.</html>";
				case SABOTEUR:
					return "<html>Du bist Saboteur.<br>Du willst auf keinen Fall, dass der Goldschatz aufgedeckt wird.</html>";
				default:
					return null;
			}
			
		} else {
			
			if (mouseToolTypeCard != null) {
				Card selectedCard = GameController.getSelectedCard();
				if (selectedCard != null && selectedCard.isFixedTool())
					if (player.canBrokenToolBeFixed(mouseToolTypeCard, (FixedToolCard) selectedCard))
						return String.format("Hiermit kannst du das Werkzeug von %s wieder reparieren.", player.getName());
				return String.format("%s ist gesperrt und kann keine Wegekarte legen.", player.getName());
			}
			
			if (mouseHoveringName) {
				Card selectedCard = GameController.getSelectedCard();
				if (selectedCard != null && selectedCard.isBrokenTool())
					if (player != GameController.getActivePlayer() && player.canToolBeBroken((BrokenToolCard) selectedCard))
						return String.format("Hiermit kannst du das Werkzeug von %s zerstören.", player.getName());
				return null;
			}
			
		}
		return null;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseHandCard = null;
		mouseToolTypeCard = null;
		mouseHoveringName = false;
	}
	
	private Card getHandCardAt(Point point) {
		for (Entry<Rectangle, Card> cardPosition : cardPositions.entrySet())
			if (cardPosition.getKey().contains(point))
				return cardPosition.getValue();
		return null;
	}
	
	private BrokenToolCard getToolTypeCardAt(Point point) {
		for (Entry<Ellipse2D, BrokenToolCard> brokenToolPosition : brokenToolPositions.entrySet())
			if (brokenToolPosition.getKey().contains(point))
				return brokenToolPosition.getValue();
		return null;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getPoint().y < padding + infoHeight + padding) return;
		if (getWidth() < minimumWidth()) {
			int scrollSpeed = 12;
			int units = e.getUnitsToScroll() * scrollSpeed;
			scrollX -= units;
			scrollX = Math.max(getWidth() - minimumWidth(), Math.min(scrollX, 0));
			mouseMoved(new Point(e.getPoint().x + units, e.getPoint().y));
		} else scrollX = 0;
		repaint();
	}
	
	private Point lastDragPoint;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getPoint().y < padding + infoHeight + padding)
			lastDragPoint = null;
		else lastDragPoint = e.getPoint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (lastDragPoint == null) return;
		scrollX += e.getPoint().x - lastDragPoint.x;
		scrollX = Math.max(getWidth() - minimumWidth(), Math.min(scrollX, 0));
		mouseMoved(e.getPoint());
		lastDragPoint = e.getPoint();
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	
	public int weightedWidth() {
		if (player == GameController.getActivePlayer()) return 0;
		return minimumWidth();
	}
	
	private int minimumWidth() {
		return padding + Math.max(infoWidth, cardsWidth) + padding;
	}
	
}
