package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolveEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	// Variables you need here
	
	public TournamentSolveEvent() {
		// KEEP IT LIKE THIS, CREATE ANOTHER CONSTRUCTOR !
	}

	// Getters here !
	
	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
