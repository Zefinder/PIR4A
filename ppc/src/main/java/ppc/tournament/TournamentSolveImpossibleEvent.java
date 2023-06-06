package ppc.tournament;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentSolveImpossibleEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private int level;

	public TournamentSolveImpossibleEvent() {
	}

	public TournamentSolveImpossibleEvent(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
