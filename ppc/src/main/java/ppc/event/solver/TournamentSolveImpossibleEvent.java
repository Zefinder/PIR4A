package ppc.event.solver;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentSolveImpossibleEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String message;
	private int level;

	public TournamentSolveImpossibleEvent() {
	}

	public TournamentSolveImpossibleEvent(String message, int level) {
		this.message = message;
		this.level = level;
	}

	public String getMessage() {
		return message;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
