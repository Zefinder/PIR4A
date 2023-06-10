package ppc.event.mainpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;
import ppc.event.EventStatus;
import ppc.event.RegisteredListener;
import ppc.event.StatusEvent;

@Event
public class SettingsChangeStatusEvent extends StatusEvent {
	
	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public SettingsChangeStatusEvent() {
	}

	public SettingsChangeStatusEvent(EventStatus status) {
		super(status);
	}

	public SettingsChangeStatusEvent(EventStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
