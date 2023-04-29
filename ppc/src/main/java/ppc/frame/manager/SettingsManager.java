package ppc.frame.manager;

import static ppc.frame.annotation.ManagerPriority.MEDIUM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@ppc.frame.annotation.Manager(priority = MEDIUM)
public final class SettingsManager implements Manager {

	private static final SettingsManager instance = new SettingsManager();

	private static final String RESULTS_PATH_PROPERTY = "resultsPath";
	private static final String PROGRESSBAR_COLOR_PROPERTY = "progressBarColor";
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
			boolean toReset = false;
			// Checking if file not corrupted
			if (props.size() == 5) {
				// Checking results path
				String resultsPath = props.getProperty(RESULTS_PATH_PROPERTY);
				if (resultsPath == null) {
					logs.writeErrorMessage(
							"Error when reading property " + RESULTS_PATH_PROPERTY + "... Reseting settings...");
					toReset = true;
				} else {
					File resDirectory = new File(resultsPath);
					if (!resDirectory.exists() || !resDirectory.isDirectory()) {
						logs.writeErrorMessage("Error with results path... Reseting settings...");
						toReset = true;
					} else {
						FileManager.getInstance().changeResDirectory(resDirectory);
						logs.writeInformationMessage("Results directory: " + resultsPath);
					}
				}

				// Checking progress bar color
				String barColor = props.getProperty(PROGRESSBAR_COLOR_PROPERTY);
				if (barColor == null) {
					logs.writeErrorMessage(
							"Error when reading property " + PROGRESSBAR_COLOR_PROPERTY + "... Reseting settings...");
					toReset = true;
				} else {
					switch (barColor) {
					case "default":
						logs.writeInformationMessage("Default progress bar color set");
						break;

					case "green":
						logs.writeInformationMessage("Green progress bar color set");
						break;

					case "violet":
						logs.writeInformationMessage("Violet progress bar color set");
						break;
					default:
						logs.writeErrorMessage("Error with progress bar color... Reseting settings...");
						toReset = true;
						break;
					}
				}

				// Checking max time
				String maxTimeString = props.getProperty(MAX_TIME_PROPERTY);
				if (maxTimeString == null) {
					logs.writeErrorMessage(
							"Error when reading property " + MAX_TIME_PROPERTY + "... Reseting settings...");
					toReset = true;
				} else {
					int maxTime = Integer.valueOf(maxTimeString);
					if (maxTime <= 0) {
						logs.writeErrorMessage("Negative max time detected... Reseting settings...");
						toReset = true;
					} else
						logs.writeInformationMessage("Maximum default searching time: " + maxTimeString);
				}

				// Checking students threshold
				String maxStudentsMetString = props.getProperty(MAX_STUDENTS_MET_TH_PROPERTY);
				if (maxStudentsMetString == null) {
					logs.writeErrorMessage(
							"Error when reading property " + MAX_STUDENTS_MET_TH_PROPERTY + "... Reseting settings...");
					toReset = true;
				} else {
					float maxStudentsMet = Float.valueOf(maxStudentsMetString);
					if (maxStudentsMet <= 0.) {
						logs.writeErrorMessage("Negative threshold detected for students... Reseting settings...");
						toReset = true;
					} else if (maxStudentsMet > 1.) {
						logs.writeErrorMessage("Threshold over 100% detected for students... Reseting settings...");
						toReset = true;
					} else
						logs.writeInformationMessage("Maximum students met threshold: " + maxStudentsMetString);
				}

				// Checking classes threshold
				String maxClassesMetString = props.getProperty(MAX_CLASSES_MET_TH_PROPERTY);
				if (maxClassesMetString == null) {
					logs.writeErrorMessage(
							"Error when reading property " + MAX_CLASSES_MET_TH_PROPERTY + "... Reseting settings...");
					toReset = true;
				} else {
					float maxClassesMet = Float.valueOf(maxClassesMetString);
					if (maxClassesMet <= 0.) {
						logs.writeErrorMessage("Negative threshold detected for classes... Reseting settings...");
						toReset = true;
					} else if (maxClassesMet > 1.) {
						logs.writeErrorMessage("Threshold over 100% detected for classes... Reseting settings...");
						toReset = true;
					} else
						logs.writeInformationMessage("Maximum classes met threshold: " + maxClassesMetString);
				}
			} else {
				System.err.println("Wrong number of properties, file corrupted! Reseting settings...");
				toReset = true;
			}

			// Reset if needed
			if (toReset) {
				resetProperties();
				writeProperties();
				logs.writeInformationMessage("Settings got reset!");
			}
		}
		
		logs.writeInformationMessage("SettingsManager initilised!");
	}

	private void resetProperties() {
		props.setProperty(RESULTS_PATH_PROPERTY, FileManager.getInstance().getResDirectoryPath());
		props.setProperty(PROGRESSBAR_COLOR_PROPERTY, "default");
		props.setProperty(MAX_TIME_PROPERTY, "1800");
		props.setProperty(MAX_STUDENTS_MET_TH_PROPERTY, "1.");
		props.setProperty(MAX_CLASSES_MET_TH_PROPERTY, "1.");
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

	public int getMaxTime() {
		return Integer.valueOf(props.getProperty(MAX_TIME_PROPERTY));
	}

	public static SettingsManager getInstance() {
		return instance;
	}

}
