package fop.model;

import java.util.concurrent.TimeUnit;

import fop.controller.GameController;
import fop.model.cards.Card;
import javax.swing.SwingWorker;

/***
 * 
 * Stellt einen Computerspieler dar.
 *
 */
public class ComputerPlayer extends Player {
	
	public ComputerPlayer(String name) {
		super(name);
		GameController.addPropertyChangeListener(GameController.NEXT_PLAYER, evt -> {
			// skip if it is not the players turn
			if (GameController.getActivePlayer() != this) return;
			
			// do action in background worker
			new SwingWorker<Object, Void>() {
				
				@Override
				protected Object doInBackground() throws Exception {
					sleep(800);
					doAction();
					sleep(800);
					return null;
				}
			}.execute();
		});
	}
	
	@Override
	public boolean isComputer() {
		return true;
	}
	
	/**
	 * Pausiert das Programm, damit die Änderungen auf der Benutzeroberfläche sichtbar werden.
	 * @param timeMillis zu wartende Zeit in Millisekunden
	 */
	protected void sleep(int timeMillis) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeMillis);
		} catch (InterruptedException ignored) {}
	}
	
	protected void selectCard(Card card) {
		GameController.selectCard(card);
		sleep(800);
	}
	
	/**
	 * Führt einen Zug des Computerspielers aus.<br>
	 * Benutzt {@link #selectCard(Card)}, um eine Karte auszuwählen.<br>
	 * Benutzt Methoden in {@link GameController}, um Aktionen auszuführen.
	 */
	protected void doAction() {
		// TODO Aufgabe 4.3.3
		// Sie dürfen diese Methode vollständig umschreiben und den vorhandenen Code entfernen.
		
		// erhalte zufällige Handkarte
		Card card = handCards.get((int) (Math.random() * handCards.size()));
		
		// wähle Karte aus
		selectCard(card);
		
		// werfe Karte ab
		GameController.discardSelectedCard();
	}
	
}
