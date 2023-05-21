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
import java.util.Arrays;
import java.util.List;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentAddClassEvent;
import ppc.event.TournamentAddClassStatusEvent;
import ppc.event.TournamentClassCopyEvent;
import ppc.event.TournamentClassCopyStatusEvent;
import ppc.event.TournamentResultsCopyEvent;
import ppc.event.TournamentResultsCopyStatusEvent;
import ppc.event.TournamentDeleteClassEvent;
import ppc.event.TournamentDeleteClassStatusEvent;
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
			try {
				verifyFile(settingsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

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

	/**
	 * Returns the settings file for the {@link SettingsManager}.
	 * 
	 * @return the settings file
	 */
	File getSettingsFile() {
		return settingsFile;
	}

	/**
	 * Returns the results' directory path for the {@link SettingsManager}.
	 * 
	 * @return the results' directory path
	 */
	String getResDirectoryPath() {
		return resDirectory.getAbsolutePath();
	}

	/**
	 * Changes the results' directory during {@link SettingsManager}'s init.
	 * 
	 * @param resDirectory the new directory
	 */
	void changeResDirectory(File resDirectory) {
		this.resDirectory = resDirectory;
	}

	/**
	 * Changes the results' directory and copy all files in it into the new
	 * directory.
	 * 
	 * @param resDirectory the new directory
	 */
	void changeCopyResDirectory(File resDirectory) {
		if (!this.resDirectory.equals(resDirectory)) {
			copyDirectory(this.resDirectory, resDirectory);
			this.resDirectory = resDirectory;
		}
	}

	private void copyResultsFile(File tournamentResultsFolder, File destinationFolder) throws IOException {
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

	/**
	 * Return all tournament files present in the tournament directory.
	 * 
	 * @return the list of tournament files
	 */
	File[] getTournamentFiles() {
		return tournamentDirectory.listFiles(pathname -> pathname.getName().endsWith(".trn"));
	}

	/**
	 * Return the tournament data directory for the specified tournament.
	 * 
	 * @param tournamentName the name of the tournament
	 * @return the tournament data folder
	 */
	File getTournamentDataFolder(String tournamentName) {
		return new File(tournamentDataDirectory.getAbsolutePath() + "/" + tournamentName);
	}

	/**
	 * Get CSV files in the data folder of the specified tournament.
	 * 
	 * @param tournamentName the name of the tournament
	 * @return the list of CSV files
	 */
	public File[] getTournamentData(String tournamentName) {
		return new File(tournamentDataDirectory.getAbsolutePath() + "/" + tournamentName).listFiles();
	}

	/**
	 * Creates a tournament file and a data folder for the specified tournament
	 * name.
	 * 
	 * @param tournamentName the name of the tournament
	 * @return the newly-created tournament file
	 * @throws IOException if an error occurs
	 */
	File createTournamentFile(String tournamentName) throws IOException {
		File tournamentFile = new File(tournamentDirectory.getAbsolutePath() + "/" + tournamentName + ".trn");
		tournamentFile.createNewFile();

		File tournamentDataFile = new File(tournamentDataDirectory.getAbsolutePath() + "/" + tournamentName);
		tournamentDataFile.mkdir();

		return tournamentFile;
	}

	/**
	 * Creates a temporary file a returns it to use it. The file can be delete using
	 * the {@link #deleteTemporaryFile(String)} method. The temporary file
	 * automatically deletes at the end of the execution.
	 * 
	 * @return a temporary File
	 * @throws IOException if the temporary file could not be created
	 */
	public File createTemporaryFile() throws IOException {
		File tmpFile = File.createTempFile("eem", ".tmp", tmpDirectory);
		tmpFile.deleteOnExit();

		return tmpFile;
	}

	/**
	 * Deletes a temporary file. Does nothing if the file name doesn't correspond to
	 * an actual temporary file.
	 * 
	 * @param fileName the name of the temporary file to delete
	 */
	public void deleteTemporaryFile(String fileName) {
		File tmpFile = new File(tmpDirectory.getAbsolutePath() + "/" + fileName);
		if (tmpFile.exists())
			tmpFile.delete();
		else
			logs.writeWarningMessage("The temporary file does not exist and can't be deleted...");
	}

	/**
	 * Lists all tournament for which results have been generated.
	 * 
	 * @return a list of tournament
	 */
	public File[] getResultFiles() {
		return resDirectory.listFiles(file -> file.isDirectory());
	}

	/**
	 * Listener called when a result has been chosen and needs to be copied
	 * somewhere.
	 * 
	 * @param event the called event
	 */
	@EventHandler
	public void onResultsCopyRequested(TournamentResultsCopyEvent event) {
		File tournamentResultsFolder = new File(resDirectory + "/" + event.getTournamentName());
		File destinationFolder = event.getDestinationFolder();
		TournamentResultsCopyStatusEvent statusEvent;

		if (!verifyDirectory(tournamentResultsFolder)) {
			tournamentResultsFolder.delete();
			System.err.println("Results do not exist for this tournament...");
			statusEvent = new TournamentResultsCopyStatusEvent(EventStatus.ERROR,
					"Les résultats n'existent pas pour ce tournoi...");

		} else if (!verifyDirectory(destinationFolder)) {
			destinationFolder.delete();
			System.err.println("Destination do not exist...");
			statusEvent = new TournamentResultsCopyStatusEvent(EventStatus.ERROR, "La destination n'existe pas...");
			return;

		} else
			try {
				copyResultsFile(tournamentResultsFolder, destinationFolder);
				statusEvent = new TournamentResultsCopyStatusEvent(EventStatus.SUCCESS);
			} catch (IOException e) {
				e.printStackTrace();
				statusEvent = new TournamentResultsCopyStatusEvent(EventStatus.ERROR,
						"Erreur lors de la copie des fichiers :\n" + e.getMessage());
			}

		EventManager.getInstance().callEvent(statusEvent);
	}

	@EventHandler
	public void onCopyClassRequested(TournamentClassCopyEvent event) {
		File toCopy = event.getFileToCopy();
		TournamentClassCopyStatusEvent statusEvent;

		if (!toCopy.exists()) {
			System.err.println("Class file to copy does not exist!");
			statusEvent = new TournamentClassCopyStatusEvent(toCopy, EventStatus.ERROR,
					"Le fichier temporaire n'existe pas !");
		} else if (!toCopy.isFile()) {
			System.err.println("Class file to copy is not a file!");
			statusEvent = new TournamentClassCopyStatusEvent(toCopy, EventStatus.ERROR,
					"Le fichier temporaire n'est pas un fichier !");
		} else {
			File destinationFile = new File(tournamentDataDirectory.getAbsolutePath() + "/" + event.getTournamentName()
					+ "/" + "class" + event.getClassNumber() + ".csv");

			try {
				Files.copy(toCopy.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				statusEvent = new TournamentClassCopyStatusEvent(toCopy, EventStatus.SUCCESS);
			} catch (IOException e) {
				e.printStackTrace();
				statusEvent = new TournamentClassCopyStatusEvent(toCopy, EventStatus.ERROR,
						"Erreur lors de la copie du fichier de classe !\n" + e.getMessage());
			}
		}

		EventManager.getInstance().callEvent(statusEvent);
	}

	/**
	 * Listener called when a class file has been chosen when adding a class for an
	 * opened tournament.
	 * 
	 * @param event the called event
	 */
	@EventHandler
	public void onClassFileSelected(TournamentAddClassEvent event) {
		File toCopy = event.getTournamentFile();
		TournamentAddClassStatusEvent statusEvent;

		if (!toCopy.exists()) {
			System.err.println("Tournament file does not exist!");
			statusEvent = new TournamentAddClassStatusEvent(null, EventStatus.ERROR,
					"Le fichier de tournoi n'existe pas !");
		} else if (!toCopy.isFile()) {
			System.err.println("Tournament file is not a file!");
			statusEvent = new TournamentAddClassStatusEvent(null, EventStatus.ERROR,
					"Le fichier de tournoi n'est pas un fichier !");
		} else {
			File[] tournamentDataFiles = getTournamentData(event.getTournamentName());
			Arrays.sort(tournamentDataFiles);
			int classNumber = 0;
			for (File tournamentDataFile : tournamentDataFiles) {
				try {
					int classFile = Integer.parseInt(tournamentDataFile.getName().substring(5, 6));
					if (classNumber == classFile)
						classNumber++;
					else
						break;

				} catch (NumberFormatException e) {
					logs.writeWarningMessage(String.format("File %s isn't a regular tournament file, ignored...",
							tournamentDataFile.getName()));
				}
			}

			System.out.println(classNumber);

			File destinationFile = new File(tournamentDataDirectory.getAbsolutePath() + "/" + event.getTournamentName()
					+ "/class" + classNumber + ".csv");

			try {
				Files.copy(toCopy.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				statusEvent = new TournamentAddClassStatusEvent(destinationFile, EventStatus.SUCCESS);
			} catch (IOException e) {
				e.printStackTrace();
				statusEvent = new TournamentAddClassStatusEvent(null, EventStatus.ERROR,
						"Erreur lors de la copie du fichier de classe !\n" + e.getMessage());
			}
		}

		EventManager.getInstance().callEvent(statusEvent);
	}

	/**
	 * Listener called when a class file has been chosen to be deleted for an opened
	 * tournament.
	 * 
	 * @param event the called event
	 */
	@EventHandler
	public void onDeleteClass(TournamentDeleteClassEvent event) {
		File toDelete = new File(tournamentDataDirectory.getAbsolutePath() + "/" + event.getTournamentName() + "/class"
				+ event.getClassIndex() + ".csv");
		TournamentDeleteClassStatusEvent statusEvent;

		if (!toDelete.exists()) {
			System.err.println("Class file does not exist!");
			statusEvent = new TournamentDeleteClassStatusEvent(event.getListIndex(), EventStatus.ERROR,
					"Le fichier de classe n'existe pas !");
		} else if (!toDelete.isFile()) {
			System.err.println("Class file is not a file!");
			statusEvent = new TournamentDeleteClassStatusEvent(event.getListIndex(), EventStatus.ERROR,
					"Le fichier de classe n'est pas un fichier !");
		} else {
			toDelete.delete();
			statusEvent = new TournamentDeleteClassStatusEvent(event.getListIndex(), EventStatus.SUCCESS);
		}

		EventManager.getInstance().callEvent(statusEvent);
	}

	/**
	 * Writes logs into a file in the logs directory.
	 * 
	 * @throws IOException if an error occurs
	 */
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

	private void verifyFile(File file) throws IOException {
		if (!file.exists()) {
			logs.writeWarningMessage(file.getName() + " file not found! Creating file...");
			if (!file.createNewFile()) {
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

			if (!file.createNewFile()) {
				logs.writeFatalErrorMessage(String.format("Impossible to create it... Need help..."));
				System.exit(-1);
			} else {
				logs.writeInformationMessage("Fatal error fixed!");
			}
		}
	}

	private void copyDirectory(File originFolder, File destinationFolder) {
		File[] folders = originFolder.listFiles(file -> file.isDirectory());

		for (File folder : folders) {
			// Create folder
			File destination = new File(destinationFolder.getAbsolutePath() + "/" + folder.getName());
			destination.mkdir();

			for (File file : folder.listFiles()) {
				File newFile = new File(destination.getAbsolutePath() + "/" + file.getName());
				try {
					Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static FileManager getInstance() {
		return instance;
	}

}
