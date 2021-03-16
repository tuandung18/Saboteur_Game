package fop.view.game;

import static fop.io.CardImageReader.ASPECT_RATIO;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import fop.controller.GameController;
import fop.io.CardImageReader;
import fop.model.cards.Card;
import javax.swing.JPanel;

/**
 * 
 * Stellt den Ablagestapel und den Nachziehstapel dar.
 *
 */
@SuppressWarnings("serial")
public class PilePanel extends JPanel implements MouseListener, MouseMotionListener {
	
	// draw deck variations
	private static final int EVERY_NTH_DRAW_DECK_CARD = 2; // speed optimization
	private static final double EFFECT_3D_X = 1 / 250.0;
	private static final double EFFECT_3D_Y = EFFECT_3D_X / ASPECT_RATIO * 0.7;
	
	// discard pile variations
	private static final int MAX_DRAW_PILE_CARDS = 25; // speed optimization
	private static final double MAX_ROT = Math.PI * 0.3;
	private static final double MAX_TRANS = 0.2;
	
	// max amount of cards of the draw deck
	private int maxCards = 0;
	
	// mouse hovering
	private boolean mouseHoveringDrawDeck;
	private boolean mouseHoveringDiscardPile;
	
	// discard pile values
	private Rectangle drawDeckPosition;
	private Ellipse2D discardPilePosition;
	private List<Double> rotationList = new ArrayList<>();
	private List<Double> translationListX = new ArrayList<>();
	private List<Double> translationListY = new ArrayList<>();
	
	public PilePanel() {
		setBackground(new Color(222, 222, 222));
		addMouseListener(this);
		addMouseMotionListener(this);
		GameController.addPropertyChangeListener(GameController.NEXT_PLAYER, evt -> repaint());
		GameController.addPropertyChangeListener(GameController.SELECT_CARD, evt -> repaint());
	}
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		Graphics2D g = (Graphics2D) g0;
		
		// set rendering hints
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// update max cards
		maxCards = Math.max(maxCards, GameController.getDrawDeckSize());
		
		// calculate card dimension
		/*
		 * Nachziehstapel bekommt obere Hälfte:
		 * getHeight() / 4 >= .. + [cardHeight + cardHeight * EFFECT_3D_Y * (maxCards - 1)] / 2
		 * . . mit .. als Abstand durch cardHeight * p
		 * . . <=> cardWidth <= h / (2 * AR * (EFFECT_3D_Y * (maxCards - 1) + 2 * p + 1))
		 * 
		 * Ablagestapel bekommt untere Hälfte:
		 * getHeight() / 4 >= [sqrt(cardHeight^2 + cardWidth^2) + MAX_TRANS * cardHeight] / 2 + ..
		 * . . mit .. als Abstand durch cardHeight * p
		 * . . <=> cardWidth <= h / (2 * (sqrt(AR^2 + 1) + AR * (MAX_TRANS + 2 * p)))
		 * 
		 * Nachziehstapel Breite:
		 * getWidth() >= .. + [cardWidth + cardWidth * EFFECT_3D_X * (maxCards - 1)] + ..
		 * . . mit .. als Abstand durch cardWidth * p
		 * . . <=> cardWidth <= getWidth() / (EFFECT_3D_X * (maxCards - 1) + 2 * p + 1)
		 * 
		 * Ablagestapel Breite:
		 * getWidth() >= .. + [sqrt(cardHeight^2 + cardWidth^2) + MAX_TRANS * cardWidth] + ..
		 * . . mit .. als Abstand durch cardWidth * p
		 * . . <=> cardWidth <= getWidth() / (sqrt(AR^2 + 1) + MAX_TRANS + 2 * p)
		 */
		
		double p = 0.05;
		int width1 = (int) (getHeight() / (2 * ASPECT_RATIO * (EFFECT_3D_Y * (maxCards - 1) + 2 * p + 1)));
		int width2 = (int) (getHeight() / (2 * (Math.sqrt(ASPECT_RATIO * ASPECT_RATIO + 1) + ASPECT_RATIO * (MAX_TRANS + 2 * p))));
		int width3 = (int) (getWidth() / (EFFECT_3D_X * (maxCards - 1) + 2 * p + 1));
		int width4 = (int) (getWidth() / (Math.sqrt(ASPECT_RATIO * ASPECT_RATIO + 1) + MAX_TRANS + 2 * p));
		int cardWidth = Math.min(Math.min(width1, width2), Math.min(width3, width4));
		
		int cardHeight = (int) (cardWidth * ASPECT_RATIO);
		if (cardWidth == 0 || cardHeight == 0) return;
		
		
		// == draw deck == //
		
		BufferedImage cardBack = CardImageReader.readImage("back");
		double effect3Dx = cardWidth * EFFECT_3D_X;
		double effect3Dy = cardHeight * EFFECT_3D_Y;
		int x0 = (int) ((getWidth() - cardWidth - effect3Dx * (maxCards - 1)) / 2);
		int y0 = (int) (getHeight() / 4 - (cardHeight - effect3Dy * (maxCards - 1)) / 2);
		int drawDeckSize = GameController.getDrawDeckSize();
		drawDeckPosition = new Rectangle(x0, (int) (y0 - effect3Dy * drawDeckSize),
				(int) (cardWidth + effect3Dx * drawDeckSize), (int) (cardHeight + effect3Dy * drawDeckSize));
		for (int i = 0; i < drawDeckSize; i += EVERY_NTH_DRAW_DECK_CARD)
			g.drawImage(cardBack, x0 + (int) (effect3Dx * i), y0 - (int) (effect3Dy * i), cardWidth, cardHeight, null);
		
		
		// == discard pile == //
		
		List<Card> discardPile = GameController.getDiscardPile();
		
		// remove unused rotations and translations
		while (rotationList.size() > discardPile.size())
			rotationList.remove(rotationList.size() - 1);
		while (translationListX.size() > discardPile.size())
			translationListX.remove(translationListX.size() - 1);
		while (translationListY.size() > discardPile.size())
			translationListY.remove(translationListY.size() - 1);
		
		// generate new rotations and translations
		while (rotationList.size() < discardPile.size()) {
			double rotation;
			do
				rotation = (Math.random() - 0.5) * MAX_ROT;
			while (!rotationList.isEmpty() && Math.abs(rotationList.get(rotationList.size() - 1) - rotation) < MAX_ROT / 4);
			rotationList.add(rotation);
		}
		while (translationListX.size() < discardPile.size())
			translationListX.add((Math.random() - 0.5) * MAX_TRANS);
		while (translationListY.size() < discardPile.size())
			translationListY.add((Math.random() - 0.5) * MAX_TRANS);
		
		// draw discard pile
		int discardPileX = getWidth() / 2;
		int discardPileY = getHeight() * 3 / 4;
		int diameter = Math.min(cardWidth, cardHeight);
		discardPilePosition = new Ellipse2D.Double(discardPileX - diameter / 2, discardPileY - diameter / 2, diameter, diameter);
		x0 = discardPileX - cardWidth / 2;
		y0 = discardPileY - cardHeight / 2;
		for (int i = Math.max(0, discardPile.size() - MAX_DRAW_PILE_CARDS); i < discardPile.size(); i++) {
			Card card = discardPile.get(i);
			if (card == null) continue;
			BufferedImage cardImage = CardImageReader.readImage(card);
			AffineTransform af = new AffineTransform();
			af.translate(translationListX.get(i) * cardWidth, translationListY.get(i) * cardHeight);
			af.translate(x0, y0);
			af.scale((double) cardWidth / cardImage.getWidth(), (double) cardHeight / cardImage.getHeight());
			af.rotate(rotationList.get(i), cardImage.getWidth() / 2, cardImage.getHeight() / 2);
			g.drawImage(cardImage, af, null);
		}
		
		// highlight discard pile
		if (GameController.getSelectedCard() != null) {
			int strokeWidth = Math.max(cardWidth / 20, 2);
			float[] strokeDash = {Math.max(cardWidth / 10f, 5f), Math.max(cardWidth / 15f, 3f)};
			float strokeOffset = strokeDash[0] + strokeDash[1] * 1.5f;
			g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, strokeDash, strokeOffset));
			g.setColor(Color.RED);
			g.draw(discardPilePosition);
			// shadow if mouse is hovering the circle
			if (mouseHoveringDiscardPile) fillShape(g, discardPilePosition, Color.RED, 0.5f);
		}
		
		// debug bounds
		//int width = (int) (cardWidth + cardWidth * EFFECT_3D_X * (maxCards - 1));
		//int height = (int) (cardHeight + cardHeight * EFFECT_3D_Y * (maxCards - 1));
		//g.drawRect((getWidth() - width) / 2, getHeight() / 4 - height / 2, width, height);
		//g.draw(drawDeckPosition);
		//width = (int) (Math.sqrt(cardHeight * cardHeight + cardWidth * cardWidth) + MAX_TRANS * cardWidth);
		//height = (int) (Math.sqrt(cardHeight * cardHeight + cardWidth * cardWidth) + MAX_TRANS * cardHeight);
		//g.drawRect((getWidth() - width) / 2, getHeight() * 3 / 4 - height / 2, width, height);
		//g.fillOval(discardPileX - 5, discardPileY - 5, 9, 9);
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
				if (GameController.getSelectedCard() != null && mouseHoveringDiscardPile)
					GameController.discardSelectedCard();
				break;
			default:
				break;
		}
		
		repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (GameController.getActivePlayer() != null && GameController.getActivePlayer().isComputer()) {
			setToolTipText(null);
			return;
		}
		boolean oldDrawDeckValue = mouseHoveringDrawDeck;
		mouseHoveringDrawDeck = drawDeckPosition.contains(e.getPoint());
		boolean oldDiscardPileValue = mouseHoveringDiscardPile;
		mouseHoveringDiscardPile = discardPilePosition.contains(e.getPoint());
		if (oldDrawDeckValue != mouseHoveringDrawDeck || oldDiscardPileValue != mouseHoveringDiscardPile) {
			setToolTipText(generateToolTipText());
			repaint();
		}
	}
	
	private String generateToolTipText() {
		if (mouseHoveringDrawDeck) {
			if (GameController.getDrawDeckSize() == 0) return "<html>Der Nachziehstapel ist leer.<br>Er wird nicht wieder aufgefüllt.</html>";
			return "<html>Der Nachziehstapel.<br>Am Ende jedes Zuges wird eine Karte nachgezogen.</html>";
		}
		
		if (mouseHoveringDiscardPile) {
			if (GameController.getSelectedCard() != null) return "Die ausgewählte Karte kann hier abgeworfen werden.";
			return "<html>Der Ablagestapel.<br>Hier landen alle Karten, die abgeworfen wurden.</html>";
		}
		
		return null;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseHoveringDiscardPile = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
}
