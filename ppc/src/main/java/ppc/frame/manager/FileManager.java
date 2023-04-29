package ppc.frame.manager;

import static ppc.frame.annotation.ManagerPriority.HIGH;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ppc.frame.manager.LogsManager.Message;

@ppc.frame.annotation.Manager(priority = HIGH)
public final class FileManager implements Manager {

	private static final FileManager instance = new FileManager();

	private static final String APP_FOLDER = "/EchecEtMatch";
	private static final String RES_DIRECTORY_PATH = "/res";
	private static final String TOURNAMENTS_DIRECTORY_PATH = "/tournament";
	private static final String TOURNAMENTS_DATA_DIRECTORY_PATH = TOURNAMENTS_DIRECTORY_PATH + "/data";
	private static final String SETTINGS_FILE_PATH = "/settings.cfg";
	private static final String LOGS_DIRECTORY_PATH = "/logs";
	private static final String TMP_DIRECTORY = "/tmp";

	private File dataDirectory;
	private File logsDirectory;
	private File tmpDirectory;
	private File resDirectory;
	private File tournamentDirectory;
	private File tournamentDataDirectory;
	private File settingsFile;

	private LogsManager logs = LogsManager.getInstance();

	private FileManager() {

	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising FileManager...");
		String osName = System.getProperty("os.name");

		if (osName.contains("Windows")) {
			logs.writeInformationMessage("Windows OS detected, searching for data directory in AppData... ");
			dataDirectory = new File(System.getenv("APPDATA") + APP_FOLDER);
		} else {
			logs.writeInformationMessage(
					"Non Windows OS detected, searching for data directory in current directory... ");
			dataDirectory = new File("." + APP_FOLDER + SETTINGS_FILE_PATH);
		}

		logsDirectory = new File(dataDirectory.getAbsolutePath() + LOGS_DIRECTORY_PATH);
		tmpDirectory = new File(dataDirectory.getAbsolutePath() + TMP_DIRECTORY);
		resDirectory = new File(dataDirectory.getAbsolutePath() + RES_DIRECTORY_PATH);
		tournamentDirectory = new File(dataDirectory.getAbsolutePath() + TOURNAMENTS_DIRECTORY_PATH);
		tournamentDataDirectory = new File(dataDirectory.getAbsolutePath() + TOURNAMENTS_DATA_DIRECTORY_PATH);
		settingsFile = new File(dataDirectory.getAbsolutePath() + SETTINGS_FILE_PATH);

		if (dataDirectory.exists()) {
			logs.writeInformationMessage("Found!");

			// Verify that everything is good
			verifyDirectory(logsDirectory);
			verifyDirectory(tmpDirectory);
			verifyDirectory(resDirectory);
			verifyDirectory(tournamentDirectory);
			verifyDirectory(tournamentDataDirectory);
			verifyFile(settingsFile);

		} else {
			logs.writeWarningMessage("Not found! Creating data folders");

			logs.writeInformationMessage("Creating data folder...");
			dataDirectory.mkdir();

			logs.writeInformationMessage("Creating logs directory...");
			logsDirectory.mkdir();

			logs.writeInformationMessage("Creating results directory...");
			resDirectory.mkdir();

			logs.writeInformationMessage("Creating tournament directory...");
			tournamentDirectory.mkdir();

			logs.writeInformationMessage("Creating tournament data directory...");
			tournamentDataDirectory.mkdir();

			logs.writeInformationMessage("Creating settings file...");
			try {
				settingsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		logs.writeInformationMessage("FileManager initialised!");
	}

	public File getSettingsFile() {
		return settingsFile;
	}

	public String getResDirectoryPath() {
		return resDirectory.getAbsolutePath();
	}

	public void changeResDirectory(File resDirectory) {
		this.resDirectory = resDirectory;
	}

	public File getTournamentDirectory() {
		return tournamentDirectory;
	}

	public File getTournamentDataDirectory() {
		return tournamentDataDirectory;
	}

	public void writeLogs() throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy'_'HHmmss");

		List<Message> messages = LogsManager.getInstance().getMessages();
		File log = new File(logsDirectory + "/logs_" + LocalDateTime.now().format(formatter) + ".log");
		log.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
		for (Message message : messages) {
			writer.write(message.toString() + "\n");
		}

		writer.close();
	}

	private void verifyDirectory(File folder) {
		if (!folder.exists()) {
			logs.writeWarningMessage(folder.getName() + " directory not found! Creating folder...");
			if (!folder.mkdir()) {
				logs.writeFatalErrorMessage("Impossible to create it... Need help...");
				System.exit(-1);
			}
		}

		if (!folder.isDirectory()) {
			logs.writeFatalErrorMessage(folder.getName() + " directory is not a directory... Trying to fix it...");

			if (!folder.delete()) {
				logs.writeFatalErrorMessage(String.format("Impossible to delete it... Need help..."));
				System.exit(-1);
			}

			if (!folder.mkdir()) {
				logs.writeFatalErrorMessage(String.format("Impossible to create it... Need help..."));
				System.exit(-1);
			} else {
				logs.writeInformationMessage("Fatal error fixed!");
			}
		}
	}

	private void verifyFile(File file) {
		if (!file.exists()) {
			logs.writeWarningMessage(file.getName() + " file not found! Creating file...");
			if (!file.mkdir()) {
				logs.writeFatalErrorMessage("Impossible to create it... Need help...");
				System.exit(-1);
			}
		}

		if (!file.isFile()) {
			logs.writeFatalErrorMessage(file.getName() + " file is not a file... Trying to fix it...");

			if (!file.delete()) {
				logs.writeFatalErrorMessage(String.format("Impossible to delete it... Need help..."));
				System.exit(-1);
			}

			if (!file.mkdir()) {
				logs.writeFatalErrorMessage(String.format("Impossible to create it... Need help..."));
				System.exit(-1);
			} else {
				logs.writeInformationMessage("Fatal error fixed!");
			}
		}
	}

	public static FileManager getInstance() {
		return instance;
	}

}
