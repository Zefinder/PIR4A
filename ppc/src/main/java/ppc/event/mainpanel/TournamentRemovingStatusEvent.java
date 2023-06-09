package ppc.event.mainpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;
import ppc.event.EventStatus;
import ppc.event.RegisteredListener;
import ppc.event.StatusEvent;

@Event
public class TournamentRemovingStatusEvent extends StatusEvent {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	
	public TournamentRemovingStatusEvent() {
	}

	public TournamentRemovingStatusEvent(String tournamentName, EventStatus status) {
		super(status);
		this.tournamentName = tournamentName;
	}

	public TournamentRemovingStatusEvent(String tournamentName, EventStatus status, String errorMessage) {
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
