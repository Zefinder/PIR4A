package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentCreationStatusEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public enum TournamentCreationStatus {
		CREATED, FILE_EXIST, NEGATIVE_TIME, NEGATIVE_STUDENT_THRESHOLD, STUDENT_THRESHOLD_TOO_BIG,
		NEGATIVE_CLASSES_THRESHOLD, CLASSES_THRESHOLD_TOO_BIG;
	}

	private TournamentCreationStatus status;

	public TournamentCreationStatusEvent() {
	}

	public TournamentCreationStatusEvent(TournamentCreationStatus status) {
		this.status = status;
	}
	
	public TournamentCreationStatus getStatus() {
		return status;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
