package ppc.event.mainpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;
import ppc.event.EventStatus;
import ppc.event.RegisteredListener;
import ppc.event.StatusEvent;

@Event
public class TournamentResultsCopyStatusEvent extends StatusEvent {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public TournamentResultsCopyStatusEvent() {
	}

	public TournamentResultsCopyStatusEvent(EventStatus status) {
		super(status);
	}

	public TournamentResultsCopyStatusEvent(EventStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
