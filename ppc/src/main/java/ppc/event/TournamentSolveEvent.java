package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolveEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private boolean softConstraint;
	private int classThreshold;
	private int studentThreshold;
	private int timeout;
	private int firstTable;
	private boolean verbose;

	public TournamentSolveEvent() {
	}

	public TournamentSolveEvent(boolean softConstraint, int classThreshold, int studentThreshold, int timeout,
			int firstTable, boolean verbose) {
		this.softConstraint = softConstraint;
		this.classThreshold = classThreshold;
		this.studentThreshold = studentThreshold;
		this.timeout = timeout;
		this.firstTable = firstTable;
		this.verbose = verbose;
	}

	public boolean isSoftConstraint() {
		return softConstraint;
	}

	public int getClassThreshold() {
		return classThreshold;
	}

	public int getStudentThreshold() {
		return studentThreshold;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getFirstTable() {
		return firstTable;
	}
	
	public boolean isVerbose() {
		return verbose;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
