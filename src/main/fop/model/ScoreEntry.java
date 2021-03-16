package fop.model;

import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * 
 * Speichert einen Highscore-Eintrag, der aus dem Namen des Spielers,
 * einem Zeitstempel und der erzielten Punktzahl besteht.
 *
 */
public class ScoreEntry implements Comparable<ScoreEntry> {
	
	protected String name;
	protected LocalDateTime dateTime;
	protected int score;
	
	/**
	 * Erstellt eine neue ScoreEntry.
	 * @param name der Name des Spielers
	 * @param dateTime Datum und Zeit des Spiels
	 * @param score die erreichte Punktzahl
	 */
	public ScoreEntry(String name, LocalDateTime dateTime, int score) {
		this.name = name;
		this.dateTime = dateTime;
		this.score = score;
	}
	
	// load and save //
	
	/**
	 * Wandelt eine Zeile in ein ScoreEntry Objekt.<br>
	 * Gibt {@code null} zurück, wenn die Zeile nicht in ein
	 * ScoreEntry Objekt umgewandelt werden kann.<br>
	 * Format: {@code name;dateTime;score}
	 * @param line die zu lesende Zeile
	 * @return das neue ScoreEntry Objekt; oder {@code null}
	 */
	public static ScoreEntry read(String line) {
		// TODO Aufgabe 4.2.1
		return null;
	}
	
	/**
	 * Schreibt das ScoreEntry Objekt mit dem übergebenen {@link PrintWriter}.<br>
	 * Format: {@code name;dateTime;score}
	 * @param printWriter der PrintWriter
	 */
	public void write(PrintWriter printWriter) {
		// TODO Aufgabe 4.2.1
	}
	
	// get //
	
	public String getName() {
		return name;
	}
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public int getScore() {
		return score;
	}
	
	// Comparable //
	
	@Override
	public int compareTo(ScoreEntry other) {
		return Integer.compare(score, other.score);
	}
	
	// Object //
	
	@Override
	public String toString() {
		return "ScoreEntry [name=" + name + ", dateTime=" + dateTime + ", score=" + score + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (dateTime == null ? 0 : dateTime.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + score;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ScoreEntry other = (ScoreEntry) obj;
		if (dateTime == null) {
			if (other.dateTime != null) return false;
		} else if (!dateTime.equals(other.dateTime)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (score != other.score) return false;
		return true;
	}
	
}
