package ppc.tournament;

import java.io.File;

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

	public void openTournament(int maxSearchingTime, float studentsMetThreshold, float classesMetThreshold) {
		this.maxSearchingTime = maxSearchingTime;
		this.studentsMetThreshold = studentsMetThreshold;
		this.classesMetThreshold = classesMetThreshold;

		// TODO Open CSV files in dataFolder, load data and send them to the Frame !
		System.out.println(String.format(
				"Tournament opened with default options [maxSearchingTime=%d,studentsMetThreshold=%f,classesMetThreshold=%f]",
				maxSearchingTime, studentsMetThreshold, classesMetThreshold));
	}

}
