package ppc.event;

import java.util.ArrayList;
import java.util.List;

public class TournamentEstimateEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	public TournamentEstimateEvent() {
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
