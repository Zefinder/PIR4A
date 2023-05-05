package ppc.manager;

import static ppc.annotation.ManagerPriority.LOW;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.TournamentCreateEvent;
import ppc.event.TournamentCreationStatusEvent;
import ppc.event.TournamentCreationStatusEvent.TournamentCreationStatus;
import ppc.event.TournamentOpenEvent;
import ppc.event.TournamentOpeningStatusEvent;
import ppc.event.TournamentOpeningStatusEvent.TournamentOpeningStatus;
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
		// TODO Auto-generated constructor stub
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

			Tournament tournament = new Tournament(fileName, tournamentData);
			tournamentList.put(fileName, tournament);

			logs.writeInformationMessage(String.format("Tournament %s initialised", fileName));
		}

		logs.writeInformationMessage("TournamentManager initialised!");
	}

	@EventHandler
	public void onCreateTournament(TournamentCreateEvent event) {
		Tournament tournament = tournamentList.get(event.getName());
		TournamentCreationStatusEvent createdEvent;

		if (tournament != null) {
			createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.FILE_EXIST);
		} else {
			if (event.getMatchesNumber() < 0) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.NEGATIVE_MATCHES);
			} else if (event.getGroupsNumber() < 0) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.NEGATIVE_GROUPS);
			} else if (event.getMaxSearchingTime() < 0) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.NEGATIVE_TIME);
			} else if (event.getStudentsMetThreshold() < 0f) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.NEGATIVE_STUDENT_THRESHOLD);
			} else if (event.getStudentsMetThreshold() > 1f) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.STUDENT_THRESHOLD_TOO_BIG);
			} else if (event.getClassesMetThreshold() < 0f) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.NEGATIVE_CLASSES_THRESHOLD);
			} else if (event.getClassesMetThreshold() > 1f) {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.CLASSES_THRESHOLD_TOO_BIG);
			} else {
				createdEvent = new TournamentCreationStatusEvent(TournamentCreationStatus.CREATED);
				tournament = new Tournament(event.getName(),
						FileManager.getInstance().getTournamentData(event.getName()));

				tournament.createTournament(event.getMatchesNumber(), event.getGroupsNumber(),
						event.getMaxSearchingTime(), event.getStudentsMetThreshold(), event.getClassesMetThreshold());

				if (FileManager.getInstance().createTournamentFile(event.getName()))
					tournamentList.put(event.getName(), tournament);
			}
		}

		EventManager.getInstance().callEvent(createdEvent);
	}

	@EventHandler
	public void onOpenTournament(TournamentOpenEvent event) {
		Tournament tournament = tournamentList.get(event.getTournamentName());
		TournamentOpeningStatusEvent openingEvent;

		if (tournament == null) {
			openingEvent = new TournamentOpeningStatusEvent(TournamentOpeningStatus.ERROR);
		} else {
			tournament.openTournament();
			openingEvent = new TournamentOpeningStatusEvent(TournamentOpeningStatus.OPENED);
		}
		
		EventManager.getInstance().callEvent(openingEvent);
	}

	public static TournamentManager getInstance() {
		return instance;
	}

}
