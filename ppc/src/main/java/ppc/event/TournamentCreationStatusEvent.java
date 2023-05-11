package ppc.event;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;

@Event
public class TournamentCreationStatusEvent extends StatusEvent {
	
	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	
	public TournamentCreationStatusEvent() {
	}

	public TournamentCreationStatusEvent(String tournamentName, EventStatus status) {
		super(status);
		this.tournamentName = tournamentName;
	}

	public TournamentCreationStatusEvent(String tournamentName, EventStatus status, String errorMessage) {
		super(status, errorMessage);
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
