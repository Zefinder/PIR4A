package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolveEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private boolean softConstraint;
	private int timeout;
	private int firstTable;
	
	public TournamentSolveEvent() {}
	
	public TournamentSolveEvent(boolean softConstraint, int timeout, int firstTable) {
		this.softConstraint = softConstraint;
		this.timeout = timeout;
		this.firstTable = firstTable;
	}

	public boolean isSoftConstraint() {
		return softConstraint;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public int getFirstTable() {
		return firstTable;
	}
	
	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
