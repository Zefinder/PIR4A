package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentRemoveEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	
	public TournamentRemoveEvent() {
	}

	public TournamentRemoveEvent(String tournamentName) {
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
