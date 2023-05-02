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

	public void createTournament(int maxSearchingTime, float studentsMetThreshold, float classesMetThreshold) {
		this.maxSearchingTime = maxSearchingTime;
		this.studentsMetThreshold = studentsMetThreshold;
		this.classesMetThreshold = classesMetThreshold;

		System.out.println(String.format(
				"Tournament created with default options [maxSearchingTime=%d,studentsMetThreshold=%f,classesMetThreshold=%f]",
				maxSearchingTime, studentsMetThreshold, classesMetThreshold));
	}

}
