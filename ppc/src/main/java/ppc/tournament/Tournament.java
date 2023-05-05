package ppc.tournament;

import java.io.File;

import ppc.event.TournamentCreateEvent;
import ppc.manager.TournamentManager;

/**
 * This class represents a tournament and will contain all data of a tournament
 * once opened. When instantiated, it will only contain the tournament's name
 * and the location of the folder where CSV files are.
 * 
 * @see TournamentManager
 * @see TournamentCreateEvent
 * 
 * @author Adrien Jakubiak
 *
 */
public class Tournament {

	private String name;
	private File dataFolder;

	private int matchesNumber;
	private int groupsNumber;
	private int maxSearchingTime;
	private float studentsMetThreshold;
	private float classesMetThreshold;

	public Tournament(String name, File dataFolder) {
		this.name = name;
		this.dataFolder = dataFolder;
	}

	public String getName() {
		return name;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public void createTournament(int matchesNumber, int groupsNumber, int maxSearchingTime, float studentsMetThreshold,
			float classesMetThreshold) {
		this.matchesNumber = matchesNumber;
		this.groupsNumber = groupsNumber;
		this.maxSearchingTime = maxSearchingTime;
		this.studentsMetThreshold = studentsMetThreshold;
		this.classesMetThreshold = classesMetThreshold;

		System.out.println(String.format(
				"Tournament created with options [name=%s,matchesNumber=%d,groupsNumber=%d,maxSearchingTime=%d,studentsMetThreshold=%f,classesMetThreshold=%f]",
				name, matchesNumber, groupsNumber, maxSearchingTime, studentsMetThreshold, classesMetThreshold));
	}

	public void openTournament() {
		System.out.println("Loading tournament...");
	}
}
