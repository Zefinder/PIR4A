package ppc.event;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;

@Event
public class TournamentCreationStatusEvent extends StatusEvent {
	
	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public TournamentCreationStatusEvent() {
	}

	public TournamentCreationStatusEvent(EventStatus status) {
		super(status);
	}

	public TournamentCreationStatusEvent(EventStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
