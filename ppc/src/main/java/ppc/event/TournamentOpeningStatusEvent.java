package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentOpeningStatusEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public enum TournamentOpeningStatus {
		OPENED, ERROR;
	}

	private TournamentOpeningStatus status;

	public TournamentOpeningStatusEvent() {
	}
	
	public TournamentOpeningStatusEvent(TournamentOpeningStatus status) {
		this.status = status;
	}

	public TournamentOpeningStatus getStatus() {
		return status;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
