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
		try{
			BufferedReader reader = new BufferedReader(new FileReader(PATH));
			String toBeReadLine= reader.readLine();
			while (toBeReadLine!=null){
				if(ScoreEntry.read(toBeReadLine) == null)
					return loadedScoreEntries;
				else {
					loadedScoreEntries.add(ScoreEntry.read(toBeReadLine));
					toBeReadLine=reader.readLine();
				}
			}
			reader.close();
		}
		catch (IOException e){
			return new ArrayList<ScoreEntry>();
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
		try {
			printed = new PrintWriter(new FileOutputStream(PATH));
			for(ScoreEntry scoreEntry: scoreEntries){
				scoreEntry.write(printed);
			}
			printed.close();
		}
		catch (FileNotFoundException exp){
			exp.printStackTrace();
		}
	}
	
	/**
	 * Schreibt das übergebene {@link ScoreEntry} Objekt an der korrekten Stelle in die Datei {@value #PATH}.<br>
	 * Die Elemente sollen absteigend sortiert sein. Wenn das übergebene Element dieselbe Punktzahl wie ein
	 * Element der Datei hat, soll das übergebene Element danach eingefügt werden.
	 * @param scoreEntry das ScoreEntry Objekt, das hinzugefügt werden soll
	 */
	public static void addScoreEntry(ScoreEntry scoreEntry) {
		// TODO Aufgabe 4.2.3
		List<ScoreEntry> listOfHighScores = loadScoreEntries();
		if(listOfHighScores.isEmpty()){
			listOfHighScores.add(scoreEntry);
			writeScoreEntries(listOfHighScores);
		}

		List<ScoreEntry> sortedList = new ArrayList<>();
		int index = 0;
		for(ScoreEntry highScore : listOfHighScores){
			if(highScore.compareTo(scoreEntry)>=0){
				sortedList.add(highScore);
				index++;
			}
			else break;
		}


		sortedList.add(scoreEntry);
		for(int i = index; i<listOfHighScores.size();i++){
			sortedList.add(listOfHighScores.get(i));
		}

		writeScoreEntries(sortedList);
	}


}
