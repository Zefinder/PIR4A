package ppc.event.mainpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

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
