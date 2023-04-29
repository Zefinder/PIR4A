package ppc.frame.manager;

import static ppc.frame.annotation.ManagerPriority.CRITICAL;

import java.util.ArrayList;
import java.util.List;

import ppc.frame.listener.TournamentListener;

@ppc.frame.annotation.Manager(priority = CRITICAL)
public final class ListenerManager implements Manager {

	private static final ListenerManager instance = new ListenerManager();

	private List<TournamentListener> tournamentListenerList;

	private ListenerManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initManager() {
		tournamentListenerList = new ArrayList<>();
	}

	public void fireOpenTournament(String name, int maxSearchingTime, float studentsMetThreshold,
			float classesMetThreshold) {
		tournamentListenerList.forEach(
				listener -> listener.openTournament(name, maxSearchingTime, studentsMetThreshold, classesMetThreshold));
	}

	public static ListenerManager getInstance() {
		return instance;
	}

}
