package ppc.event.openpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;
import ppc.event.EventStatus;
import ppc.event.RegisteredListener;
import ppc.event.StatusEvent;

@Event
public class TournamentClassCopyStatusEvent extends StatusEvent {
	
	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private File toCopy;

	public TournamentClassCopyStatusEvent() {
	}

	public TournamentClassCopyStatusEvent(File toCopy, EventStatus status) {
		super(status);
		this.toCopy = toCopy;
	}

	public TournamentClassCopyStatusEvent(File toCopy, EventStatus status, String errorMessage) {
		super(status, errorMessage);
		this.toCopy = toCopy;
	}
	
	public File getFileToCopy() {
		return toCopy;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
