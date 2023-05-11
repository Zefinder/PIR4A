package ppc.manager;

import static ppc.annotation.ManagerPriority.LOW;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentCreateEvent;
import ppc.event.TournamentCreationStatusEvent;
import ppc.event.TournamentOpenEvent;
import ppc.event.TournamentOpeningStatusEvent;
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

	private Map<String, Tournament> tournamentList;
	private LogsManager logs = LogsManager.getInstance();

	private TournamentManager() {
	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising TournamentManager...");

		EventManager.getInstance().registerListener(this);

		tournamentList = new HashMap<>();

		File[] tournamentFiles = FileManager.getInstance().getTournamentFiles();
		logs.writeInformationMessage(String.format("Found %d tournament files", tournamentFiles.length));
		for (File tournamentFile : tournamentFiles) {
			String fileName = tournamentFile.getName();
			fileName = fileName.substring(0, fileName.length() - 4);

			// Checks if the tournament file has data associated to it
			File tournamentData = FileManager.getInstance().getTournamentData(fileName);

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

			try {
				Tournament tournament = new Tournament(tournamentFile);
				logs.writeInformationMessage(String.format("Tournament %s initialised", fileName));
				tournamentList.put(fileName, tournament);
			} catch (IOException e) {
				System.err.println("Error when loading tournament file, ignored!");
			}

		}

		logs.writeInformationMessage("TournamentManager initialised!");
	}

	@EventHandler
	public void onCreateTournament(TournamentCreateEvent event) {
		Tournament tournament = tournamentList.get(event.getName());
		TournamentCreationStatusEvent createdEvent;

		if (tournament != null) {
			LogsManager.getInstance().writeErrorMessage("Tournament's name already exists!");
			createdEvent = new TournamentCreationStatusEvent(event.getName(), EventStatus.ERROR,
					"Ce nom de tournoi existe déjà !");
		} else {
			String message = Tournament.verifyTournamentProperties(event.getName(), event.getMatchesNumber(),
					event.getGroupsNumber(), event.getMaxSearchingTime(), event.getStudentsMetThreshold(),
					event.getClassesMetThreshold());

			if (!message.equals("")) {
				createdEvent = new TournamentCreationStatusEvent(event.getName(), EventStatus.ERROR, message);

			} else {
				createdEvent = new TournamentCreationStatusEvent(event.getName(), EventStatus.SUCCESS);
				tournament = new Tournament(event.getName(), event.getMatchesNumber(), event.getGroupsNumber(),
						event.getMaxSearchingTime(), event.getStudentsMetThreshold(), event.getClassesMetThreshold(),
						FileManager.getInstance().getTournamentData(event.getName()));

				try {
					File tournamentFile = FileManager.getInstance().createTournamentFile(event.getName());
					tournamentList.put(event.getName(), tournament);
					initTournamentFile(tournament, tournamentFile);

					createdEvent = new TournamentCreationStatusEvent(event.getName(), EventStatus.SUCCESS);

				} catch (IOException e) {
					e.printStackTrace();
					createdEvent = new TournamentCreationStatusEvent(event.getName(), EventStatus.ERROR,
							String.format("Le fichier %s.trn n'a pas pu être créé...", event.getName()));
				}
			}
		}

		EventManager.getInstance().callEvent(createdEvent);
	}

	private void initTournamentFile(Tournament tournament, File tournamentFile) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(tournamentFile, false));
		Properties properties = tournament.getTournamentProperties();

		properties.store(writer, "Tournament File");
		writer.close();
	}

	@EventHandler
	public void onOpenTournament(TournamentOpenEvent event) {
		Tournament tournament = tournamentList.get(event.getTournamentName());
		TournamentOpeningStatusEvent openingEvent;

		if (tournament == null) {
			System.err.println("Impossible to open tournament (doesn't exist)...");
			openingEvent = new TournamentOpeningStatusEvent(EventStatus.ERROR, "Le tournoi n'existe pas!");
		} else {
			tournament.openTournament();
			openingEvent = new TournamentOpeningStatusEvent(EventStatus.SUCCESS);
		}

		EventManager.getInstance().callEvent(openingEvent);
	}
	
	public String[] getTournaments() {
		return tournamentList.keySet().toArray(String[]::new);
	}

	public static TournamentManager getInstance() {
		return instance;
	}

}