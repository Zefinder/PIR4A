package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolverFinishedEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public TournamentSolverFinishedEvent() {
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
