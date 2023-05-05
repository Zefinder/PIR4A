package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentOpenEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	
	public TournamentOpenEvent() {
	}
	
	public TournamentOpenEvent(String tournamentName) {
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
