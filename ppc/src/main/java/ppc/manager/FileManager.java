package ppc.manager;

import static ppc.annotation.ManagerPriority.HIGH;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentCopyEvent;
import ppc.event.TournamentCopyStatusEvent;
import ppc.manager.LogsManager.Message;

/**
 * <p>
 * This manager check data files, create them if they aren't created and gives
 * their paths to other managers. It also generates the logs file.
 * </p>
 * 
 * 
 * @see Manager
 * @see ppc.annotation.Manager
 * 
 * @author Adrien Jakubiak
 *
 */
@ppc.annotation.Manager(priority = HIGH)
public final class FileManager implements Manager, Listener {

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
		EventManager.getInstance().registerListener(this);

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

	public void copyResultsFile(File tournamentResultsFolder, File destinationFolder) throws IOException {
		// TODO A faire
		File[] files = tournamentResultsFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".pdf"));
		if (SettingsManager.getInstance().createFolderWhenCopy()) {
			destinationFolder = new File(destinationFolder.getAbsolutePath() + "/" + tournamentResultsFolder.getName());
			destinationFolder.mkdir();
		}

		for (File toCopy : files) {
			System.out
					.println("Copying file " + toCopy.getAbsolutePath() + " in " + destinationFolder.getAbsolutePath());
			Files.copy(toCopy.toPath(), new File(destinationFolder.getAbsolutePath() + "/" + toCopy.getName()).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void copyResultsDirectory(File destinationFolder) {
		// TODO A faire
	}

	public File[] getTournamentFiles() {
		return tournamentDirectory.listFiles(pathname -> pathname.getName().endsWith(".trn"));
	}

	public File getTournamentData(String tournamentName) {
		return new File(tournamentDataDirectory.getAbsolutePath() + "/" + tournamentName);
	}

	public boolean createTournamentFile(String tournamentName) {
		// TODO A faire
		return true;
	}

	public File[] getResultFiles() {
		return resDirectory.listFiles(file -> file.isDirectory());
	}

	@EventHandler
	public void onCopyRequested(TournamentCopyEvent event) {
		File tournamentResultsFolder = new File(resDirectory + "/" + event.getTournamentName());
		File destinationFolder = event.getDestinationFolder();
		TournamentCopyStatusEvent statusEvent;

		if (!verifyDirectory(tournamentResultsFolder)) {
			tournamentResultsFolder.delete();
			System.err.println("Results do not exist for this tournament...");
			statusEvent = new TournamentCopyStatusEvent(EventStatus.ERROR,
					"Les résultats n'existent pas pour ce tournoi...");

		} else if (!verifyDirectory(destinationFolder)) {
			destinationFolder.delete();
			System.err.println("Destination do not exist...");
			statusEvent = new TournamentCopyStatusEvent(EventStatus.ERROR, "La destination n'existe pas...");
			return;

		} else
			try {
				copyResultsFile(tournamentResultsFolder, destinationFolder);
				statusEvent = new TournamentCopyStatusEvent(EventStatus.SUCCESS);
			} catch (IOException e) {
				e.printStackTrace();
				statusEvent = new TournamentCopyStatusEvent(EventStatus.ERROR,
						"Erreur lors de la copie des fichiers :\n" + e.getMessage());
			}

		EventManager.getInstance().callEvent(statusEvent);
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

	private boolean verifyDirectory(File folder) {
		boolean result = true;

		if (!folder.exists()) {
			logs.writeWarningMessage(folder.getName() + " directory not found! Creating folder...");
			if (!folder.mkdir()) {
				logs.writeFatalErrorMessage("Impossible to create it... Need help...");
				System.exit(-1);
			}
			result = false;
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

			result = false;
		}

		return result;
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
