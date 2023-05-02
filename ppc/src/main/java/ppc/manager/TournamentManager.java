package ppc.manager;

import static ppc.annotation.ManagerPriority.LOW;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.TournamentCreateEvent;
import ppc.tournament.Tournament;

/**
 * <p>
 * This manager creates, loads and edits tournaments to generate results.
 * </p>
 * 
 * @author Adrien Jakubiak
 *
 */
@ppc.annotation.Manager(priority = LOW)
public final class TournamentManager implements Manager, Listener {

	private static final TournamentManager instance = new TournamentManager();

	private File tournamentDirectory;
	private File tournamentDataDirectory;
	private Map<String, Tournament> tournamentList;

	private LogsManager logs = LogsManager.getInstance();

	private TournamentManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising TournamentManager...");

		EventManager.getInstance().registerListener(this);

		tournamentList = new HashMap<>();
		tournamentDirectory = FileManager.getInstance().getTournamentDirectory();
		tournamentDataDirectory = FileManager.getInstance().getTournamentDataDirectory();

		// Reading files in tournament and tournamet data directory
		if (!tournamentDirectory.isDirectory()) {
			logs.writeFatalErrorMessage("Tournament directory is not a directory! Trying to fix error");

			if (!tournamentDirectory.delete()) {
				logs.writeFatalErrorMessage("Impossible to delete it... Need help...");
				System.exit(1);
			}

			if (!tournamentDirectory.mkdir()) {
				logs.writeFatalErrorMessage("Impossible to recreate it... Need help...");
				System.exit(1);
			} else
				logs.writeInformationMessage("Fatal error fixed!");
		}

		if (!tournamentDataDirectory.isDirectory()) {
			logs.writeFatalErrorMessage("Tournament data directory is not a directory! Trying to fix error");

			if (!tournamentDataDirectory.delete()) {
				logs.writeFatalErrorMessage("Impossible to delete it... Need help...");
				System.exit(1);
			}

			if (!tournamentDataDirectory.mkdir()) {
				logs.writeFatalErrorMessage("Impossible to recreate it... Need help...");
				System.exit(1);
			} else
				logs.writeInformationMessage("Fatal error fixed!");
		}

		File[] tournamentFiles = tournamentDirectory.listFiles(pathname -> pathname.getName().endsWith(".trn"));
		logs.writeInformationMessage(String.format("Found %d tournament files", tournamentFiles.length));
		for (File tournamentFile : tournamentFiles) {
			String fileName = tournamentFile.getName();
			fileName = fileName.substring(0, fileName.length() - 4);

			// Checks if the tournament file has data associated to it
			File tournamentData = new File(tournamentDataDirectory.getAbsolutePath() + "/" + fileName);

			if (!tournamentData.exists()) {
				logs.writeFatalErrorMessage(String
						.format("Tournament data does not exist for tournament %s... Trying to fix it...", fileName));

				if (!tournamentData.mkdir()) {
					logs.writeFatalErrorMessage(String.format("Impossible to create it... Need help..."));
					System.exit(-1);
				} else {
					logs.writeInformationMessage("Fatal error fixed!");
				}
			}

			if (!tournamentData.isDirectory()) {
				logs.writeFatalErrorMessage(String.format(
						"Tournament data folder is not a folder for tournament %s... Trying to fix it...", fileName));

				if (!tournamentData.delete()) {
					logs.writeFatalErrorMessage(String.format("Impossible to delete it... Need help..."));
					System.exit(-1);
				}

				if (!tournamentData.mkdir()) {
					logs.writeFatalErrorMessage(String.format("Impossible to create it... Need help..."));
					System.exit(-1);
				} else {
					logs.writeInformationMessage("Fatal error fixed!");
				}
			}

			Tournament tournament = new Tournament(fileName, tournamentData);
			tournamentList.put(fileName, tournament);

			logs.writeInformationMessage(String.format("Tournament %s initialised", fileName));
		}

		logs.writeInformationMessage("TournamentManager initialised!");
	}

	@EventHandler
	public void onCreateTournament(TournamentCreateEvent event) {
		Tournament tournament = tournamentList.get(event.getName());

		if (tournament != null) {
			System.err.println(String.format("Tournament %s already exists!", event.getName()));
			return;
		}

		tournament = new Tournament(event.getName(), tournamentDataDirectory);
		tournament.createTournament(event.getMaxSearchingTime(), event.getStudentsMetThreshold(), event.getClassesMetThreshold());
		tournamentList.put(event.getName(), tournament);
		// TODO create file, folder
	}

	public static TournamentManager getInstance() {
		return instance;
	}

}
