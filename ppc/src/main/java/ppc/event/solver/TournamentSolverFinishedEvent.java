package ppc.event.solver;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentSolverFinishedEvent extends Event {
	
	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	
	public TournamentSolverFinishedEvent() {
	}
	
	public TournamentSolverFinishedEvent(String tournamentName) {
		this.tournamentName = tournamentName;
	}
	
	public String getTournamentName() {
		return tournamentName;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
