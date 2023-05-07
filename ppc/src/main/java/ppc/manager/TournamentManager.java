package ppc.manager;

import static ppc.annotation.ManagerPriority.LOW;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
			LogsManager.getInstance().writeErrorMessage("Tournament's name already exists!");
			createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR, "Ce nom de tournoi existe déjà !");
		} else {
			if (event.getMatchesNumber() < 0) {
				LogsManager.getInstance().writeErrorMessage("Tournament's matches' number cannot be negative!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le nombre de parties ne peut pas être négatif !");
			} else if (event.getGroupsNumber() < 0) {
				LogsManager.getInstance().writeErrorMessage("Tournament's groups' number cannot be negative!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le nombre de groupes ne peut pas être négatif !");
			} else if (event.getMaxSearchingTime() < 0) {
				LogsManager.getInstance().writeErrorMessage("Tournament's max time search cannot be negative!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le temps maximal de recherche ne peut pas être négatif !");
			} else if (event.getStudentsMetThreshold() < 0f) {
				LogsManager.getInstance().writeErrorMessage("Tournament's students threshold cannot be negative!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le seuil d'étudiants ne peut pas être négatif !");
			} else if (event.getStudentsMetThreshold() > 1f) {
				LogsManager.getInstance().writeErrorMessage("Tournament's students threshold cannot be over 1!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le seuil d'étudiants ne peut pas être supérieur à 100% !");
			} else if (event.getClassesMetThreshold() < 0f) {
				LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold cannot be negative!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le seuil de classes ne peut pas être négatif !");
			} else if (event.getClassesMetThreshold() > 1f) {
				LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold can not be over 1!");
				createdEvent = new TournamentCreationStatusEvent(EventStatus.ERROR,
						"Le seuil de classes ne peut pas être supérieur à 100% !");
			} else {
				createdEvent = new TournamentCreationStatusEvent(EventStatus.SUCCESS);
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
			System.err.println("Impossible to open tournament (doesn't exist)...");
			openingEvent = new TournamentOpeningStatusEvent(EventStatus.ERROR, "Le tournoi n'existe pas!");
		} else {
			tournament.openTournament();
			openingEvent = new TournamentOpeningStatusEvent(EventStatus.SUCCESS);
		}

		EventManager.getInstance().callEvent(openingEvent);
	}

	public static TournamentManager getInstance() {
		return instance;
	}

}
