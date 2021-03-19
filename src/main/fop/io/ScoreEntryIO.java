package fop.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import fop.model.ScoreEntry;

/**
 * 
 * Wird genutzt, um {@link ScoreEntry} Objekte zu schreiben und zu lesen.<br>
 * <br>
 * Es handelt sich um die Datei {@value #PATH}.<br>
 * Mit {@link #loadScoreEntries()} werden die Elemente gelesen.<br>
 * Mit {@link #writeScoreEntries(List)} werden die Elemente geschrieben.
 *
 */
public final class ScoreEntryIO {
	
	/** Der Pfad zur ScoreEntry Datei */
	private static String PATH = "highscores.txt";
	
	private ScoreEntryIO() {}
	
	/**
	 * Liest eine Liste von {@link ScoreEntry} Objekten aus der Datei {@value #PATH}.<br>
	 * Die Liste enthält die Elemente in der Reihenfolge, in der sie in der Datei vorkommen.<br>
	 * Ungültige Einträge werden nicht zurückgegeben.
	 * @return die ScoreEntry Objekte
	 */
	public static List<ScoreEntry> loadScoreEntries() {
		// TODO Aufgabe 4.2.2
		List <ScoreEntry> loadedScoreEntries  = new ArrayList<>();
		FileReader fileReader;
		try {
			fileReader = new FileReader(PATH);
		} catch (FileNotFoundException exp) {
			return loadedScoreEntries;
		}
		try{
			BufferedReader reader = new BufferedReader(fileReader);
			String toBeReadLine= reader.readLine();
			while (toBeReadLine!=null){
				if(ScoreEntry.read(toBeReadLine) == null){
					loadedScoreEntries.clear();
					return loadedScoreEntries;
				}

				else {
					loadedScoreEntries.add(ScoreEntry.read(toBeReadLine));
					toBeReadLine=reader.readLine();
				}
			}
			reader.close();
		}
		catch (IOException exp){
			loadedScoreEntries.clear();
			return loadedScoreEntries;
		}
		return loadedScoreEntries;
	}
	
	/**
	 * Schreibt eine Liste von {@link ScoreEntry} Objekten in die Datei {@value #PATH}.<br>
	 * Die Elemente werden in der Reihenfolge in die Datei geschrieben, in der sie in der Liste vorkommen.
	 * @param scoreEntries die zu schreibenden ScoreEntry Objekte
	 */
	public static void writeScoreEntries(List<ScoreEntry> scoreEntries) {
		// TODO Aufgabe 4.2.2
		PrintWriter printed;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(PATH);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printed = new PrintWriter(fileOutputStream);
		for(ScoreEntry scoreEntry: scoreEntries){
			scoreEntry.write(printed);
		}
		printed.close();
	}
	
	/**
	 * Schreibt das übergebene {@link ScoreEntry} Objekt an der korrekten Stelle in die Datei {@value #PATH}.<br>
	 * Die Elemente sollen absteigend sortiert sein. Wenn das übergebene Element dieselbe Punktzahl wie ein
	 * Element der Datei hat, soll das übergebene Element danach eingefügt werden.
	 * @param scoreEntry das ScoreEntry Objekt, das hinzugefügt werden soll
	 */
	public static void addScoreEntry(ScoreEntry scoreEntry) {
		// TODO Aufgabe 4.2.3
		// a copy of the  high scores list
		List<ScoreEntry> listOfHighScores = loadScoreEntries();
		// if list is empty just add the new scoreEntry to the list and write it
		if(listOfHighScores.isEmpty()){
			listOfHighScores.add(scoreEntry);
			writeScoreEntries(listOfHighScores);
		}
		// create a list to save the sorted high scores which is greater
		// or equal than the to-be-added score
		List<ScoreEntry> sortedList = new ArrayList<>();
		// next index in list of high scores, used to add scores smaller than the to-be-added score
		int nextIndex = 0;
		for(ScoreEntry highScore : listOfHighScores){
			if(highScore.compareTo(scoreEntry)>=0){
				sortedList.add(highScore);
				nextIndex++;

			}
			else break;
		}

		// add the new high score to the sorted list
		sortedList.add(scoreEntry);
		// continue to add from the next index from list of high scores
		for(int i = nextIndex; i<listOfHighScores.size();i++){
			sortedList.add(listOfHighScores.get(i));
		}

		writeScoreEntries(sortedList);
	}


}
