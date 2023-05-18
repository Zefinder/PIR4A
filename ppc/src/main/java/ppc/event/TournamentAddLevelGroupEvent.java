package ppc.event;

import java.util.ArrayList;
import java.util.List;

public class TournamentAddLevelGroupEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	// Variables you need here

	public TournamentAddLevelGroupEvent() {
		// KEEP IT LIKE THIS, CREATE ANOTHER CONSTRUCTOR !
	}

	// Getters here !

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
