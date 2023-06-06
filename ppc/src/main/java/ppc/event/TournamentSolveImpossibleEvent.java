package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolveImpossibleEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String message;

	public TournamentSolveImpossibleEvent() {
	}

	public TournamentSolveImpossibleEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
