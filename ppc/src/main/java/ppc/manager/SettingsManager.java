package ppc.manager;

import static ppc.annotation.ManagerPriority.MEDIUM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.SettingsChangeEvent;
import ppc.event.SettingsChangeStatusEvent;

/**
 * <p>
 * This manager saves and loads all settings used by the app. These settings
 * include location of some directories, customization of the frame and default
 * values when creating a tournament.
 * </p>
 * 
 * @see Manager
 * @see ppc.annotation.Manager
 * 
 * @author Adrien Jakubiak
 *
 */
@ppc.annotation.Manager(priority = MEDIUM)
public final class SettingsManager implements Manager, Listener {

	private static final SettingsManager instance = new SettingsManager();

	private static final String RESULTS_PATH_PROPERTY = "resultsPath";
	private static final String CREATE_FOLDER_COPY_RESULTS = "createWhenCopy";
	private static final String PROGRESSBAR_COLOR_PROPERTY = "progressBarColor";
	private static final String MATCHES_NUMBER = "matchesNumber";
	private static final String GROUPS_NUMBER = "groupNumber";
	private static final String MAX_TIME_PROPERTY = "maxTime";
	private static final String MAX_STUDENTS_MET_TH_PROPERTY = "maxStudentsMetThreshold";
	private static final String MAX_CLASSES_MET_TH_PROPERTY = "maxClassesMetThreshold";

	private File settingsFile;
	private Properties props;
	private LogsManager logs = LogsManager.getInstance();

	private SettingsManager() {

	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising SettingsManager...");

		EventManager.getInstance().registerListener(this);

		// Getting settings file
		settingsFile = FileManager.getInstance().getSettingsFile();

		try {
			// Reading file as Property File
			BufferedReader reader = new BufferedReader(new FileReader(settingsFile));

			props = new Properties();
			props.load(reader);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Checking if new file (empty)
		if (props.isEmpty()) {
			logs.writeWarningMessage("Empty settings file... Creating one with default parameters!");
			resetProperties();
			writeProperties();
		} else {
			boolean toReset;
			if (props.size() != 8)
				toReset = true;
			else
				toReset = verifyProperties(props.getProperty(RESULTS_PATH_PROPERTY),
						props.getProperty(CREATE_FOLDER_COPY_RESULTS), props.getProperty(MATCHES_NUMBER),
						props.getProperty(GROUPS_NUMBER), props.getProperty(PROGRESSBAR_COLOR_PROPERTY),
						props.getProperty(MAX_TIME_PROPERTY), props.getProperty(MAX_STUDENTS_MET_TH_PROPERTY),
						props.getProperty(MAX_CLASSES_MET_TH_PROPERTY));

			// Reset if needed
			if (toReset) {
				resetProperties();
				writeProperties();
				logs.writeInformationMessage("Settings got reset!");
			} else {
				FileManager.getInstance().changeResDirectory(new File(props.getProperty(RESULTS_PATH_PROPERTY)));

			}
		}

		logs.writeInformationMessage("SettingsManager initilised!");
	}

	private void resetProperties() {
		props.clear();
		props.setProperty(RESULTS_PATH_PROPERTY, FileManager.getInstance().getResDirectoryPath());
		props.setProperty(CREATE_FOLDER_COPY_RESULTS, "1");
		props.setProperty(MATCHES_NUMBER, "6");
		props.setProperty(GROUPS_NUMBER, "3");
		props.setProperty(PROGRESSBAR_COLOR_PROPERTY, "default");
		props.setProperty(MAX_TIME_PROPERTY, "1800");
		props.setProperty(MAX_STUDENTS_MET_TH_PROPERTY, "1f");
		props.setProperty(MAX_CLASSES_MET_TH_PROPERTY, "1f");
	}

	private void writeProperties() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			props.store(writer, "Settings file");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onSettingsUpdated(SettingsChangeEvent event) {
		SettingsChangeStatusEvent statusEvent;
		if (verifyProperties(event.getResultsPath(), event.getCreateString(), event.getMatchesNumberString(),
				event.getGroupsNumberString(), event.getBarColor(), event.getMaxTimeString(),
				event.getMaxStudentsMetString(), event.getMaxClassesMetString())) {
			LogsManager.getInstance().writeFatalErrorMessage("Error when modifying settings!");
			statusEvent = new SettingsChangeStatusEvent(EventStatus.ERROR,
					"Erreur dans les champs pour modifier les paramètres !");
		} else {
			props.setProperty(RESULTS_PATH_PROPERTY, event.getResultsPath());
			props.setProperty(CREATE_FOLDER_COPY_RESULTS, event.getCreateString());
			props.setProperty(MATCHES_NUMBER, event.getMatchesNumberString());
			props.setProperty(GROUPS_NUMBER, event.getGroupsNumberString());
			props.setProperty(PROGRESSBAR_COLOR_PROPERTY, event.getBarColor());
			props.setProperty(MAX_TIME_PROPERTY, event.getMaxTimeString());
			props.setProperty(MAX_STUDENTS_MET_TH_PROPERTY, event.getMaxStudentsMetString());
			props.setProperty(MAX_CLASSES_MET_TH_PROPERTY, event.getMaxClassesMetString());
			FileManager.getInstance().changeCopyResDirectory(new File(props.getProperty(RESULTS_PATH_PROPERTY)));
			statusEvent = new SettingsChangeStatusEvent(EventStatus.SUCCESS);
			writeProperties();
		}

		EventManager.getInstance().callEvent(statusEvent);
	}

	public boolean verifyProperties(String resultsPath, String createString, String matchesNumberString,
			String groupsNumberString, String barColor, String maxTimeString, String maxStudentsMetString,
			String maxClassesMetString) {
		boolean toReset = false;
		// Checking if file not corrupted
		if (props.size() == 8) {
			// Checking results path
			if (resultsPath == null) {
				logs.writeErrorMessage("Error when reading property " + RESULTS_PATH_PROPERTY);
				toReset = true;
			} else {
				File resDirectory = new File(resultsPath);
				if (!resDirectory.exists() || !resDirectory.isDirectory()) {
					logs.writeErrorMessage("Error with results path...");
					logs.writeErrorMessage("Attempting to reset it...");
					resDirectory = new File(FileManager.getInstance().getResDirectoryPath());
					if (!resDirectory.exists() || !resDirectory.isDirectory()) {
						System.err.println("Failed! Reseting settings...");
						toReset = true;
					} else {
						props.setProperty(RESULTS_PATH_PROPERTY, FileManager.getInstance().getResDirectoryPath());
						logs.writeInformationMessage("Reset! Results directory: " + resDirectory.getAbsolutePath());
						writeProperties();
					}
				} else {
					logs.writeInformationMessage("Results directory: " + resultsPath);
				}
			}

			// Checking create folder when copying results
			if (createString == null) {
				logs.writeErrorMessage("Error when reading property " + CREATE_FOLDER_COPY_RESULTS);
				toReset = true;
			} else {
				int createValue = Integer.valueOf(createString);
				if (createValue != 0 && createValue != 1) {
					logs.writeErrorMessage("Wrong boolean value for creating folder when copy...");
					toReset = true;
				} else
					logs.writeInformationMessage("Creating folder when copy: " + createString);
			}

			// Checking matches number
			if (matchesNumberString == null) {
				logs.writeErrorMessage("Error when reading property " + MATCHES_NUMBER);
				toReset = true;
			} else {
				int matchesNumber = Integer.valueOf(matchesNumberString);
				if (matchesNumber <= 0) {
					logs.writeErrorMessage("Negative number of rounds detected...");
					toReset = true;
				} else
					logs.writeInformationMessage("Number of rounds: " + matchesNumberString);
			}

			// Checking matches number
			if (groupsNumberString == null) {
				logs.writeErrorMessage("Error when reading property " + GROUPS_NUMBER);
				toReset = true;
			} else {
				int groupsNumber = Integer.valueOf(groupsNumberString);
				if (groupsNumber <= 0) {
					logs.writeErrorMessage("Negative number of groups detected...");
					toReset = true;
				} else
					logs.writeInformationMessage("Number of groups: " + groupsNumberString);
			}

			// Checking progress bar color
			if (barColor == null) {
				logs.writeErrorMessage("Error when reading property " + PROGRESSBAR_COLOR_PROPERTY);
				toReset = true;
			} else {
				switch (barColor.toLowerCase()) {
				case "défaut":
				case "default":
					logs.writeInformationMessage("Default progress bar color set");
					break;

				case "vert":
				case "green":
					logs.writeInformationMessage("Green progress bar color set");
					break;

				case "violet":
					logs.writeInformationMessage("Violet progress bar color set");
					break;

				default:
					logs.writeErrorMessage("Error with progress bar color...");
					toReset = true;
					break;
				}
			}

			// Checking max time
			if (maxTimeString == null) {
				logs.writeErrorMessage("Error when reading property " + MAX_TIME_PROPERTY);
				toReset = true;
			} else {
				int maxTime = Integer.valueOf(maxTimeString);
				if (maxTime <= 0) {
					logs.writeErrorMessage("Negative max time detected...");
					toReset = true;
				} else
					logs.writeInformationMessage("Maximum default searching time: " + maxTimeString);
			}

			// Checking students threshold
			if (maxStudentsMetString == null) {
				logs.writeErrorMessage("Error when reading property " + MAX_STUDENTS_MET_TH_PROPERTY);
				toReset = true;
			} else {
				float maxStudentsMet = Float.valueOf(maxStudentsMetString);
				if (maxStudentsMet <= 0.) {
					logs.writeErrorMessage("Negative threshold detected for students...");
					toReset = true;
				} else if (maxStudentsMet > 1.) {
					logs.writeErrorMessage("Threshold over 100% detected for students...");
					toReset = true;
				} else
					logs.writeInformationMessage("Maximum students met threshold: " + maxStudentsMetString);
			}

			// Checking classes threshold
			if (maxClassesMetString == null) {
				logs.writeErrorMessage("Error when reading property " + MAX_CLASSES_MET_TH_PROPERTY);
				toReset = true;
			} else {
				float maxClassesMet = Float.valueOf(maxClassesMetString);
				if (maxClassesMet <= 0.) {
					logs.writeErrorMessage("Negative threshold detected for classes...");
					toReset = true;
				} else if (maxClassesMet > 1.) {
					logs.writeErrorMessage("Threshold over 100% detected for classes...");
					toReset = true;
				} else
					logs.writeInformationMessage("Maximum classes met threshold: " + maxClassesMetString);
			}
		} else {
			System.err.println("Wrong number of properties, file corrupted!");
			toReset = true;
		}

		return toReset;
	}

	public boolean createFolderWhenCopy() {
		return props.get(CREATE_FOLDER_COPY_RESULTS).equals("1");
	}

	public String getResultsPath() {
		return props.getProperty(RESULTS_PATH_PROPERTY);
	}

	public int getMatchesNumber() {
		return Integer.valueOf(props.getProperty(MATCHES_NUMBER));
	}

	public int getGroupsNumber() {
		return Integer.valueOf(props.getProperty(GROUPS_NUMBER));
	}

	public int getMaxTime() {
		return Integer.valueOf(props.getProperty(MAX_TIME_PROPERTY));
	}

	public float getStudentsMetThreshold() {
		return Float.valueOf(props.getProperty(MAX_STUDENTS_MET_TH_PROPERTY));
	}

	public float getClassesMetThreshold() {
		return Float.valueOf(props.getProperty(MAX_CLASSES_MET_TH_PROPERTY));
	}

	public String getProgressBarColor() {
		return props.getProperty(PROGRESSBAR_COLOR_PROPERTY);
	}

	public static SettingsManager getInstance() {
		return instance;
	}

}
