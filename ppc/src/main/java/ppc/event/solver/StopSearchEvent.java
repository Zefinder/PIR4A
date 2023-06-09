package ppc.event.solver;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class StopSearchEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private int level;

	public StopSearchEvent() {
	}

	public StopSearchEvent(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}