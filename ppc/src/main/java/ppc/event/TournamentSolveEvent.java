package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolveEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	private boolean softConstraint;
	private float classThreshold;
	private float studentThreshold;
	private int timeout;
	private int firstTable;
	private boolean verbose;

	public TournamentSolveEvent() {
	}

	public TournamentSolveEvent(String tournamentName, boolean softConstraint, float classThreshold, float studentThreshold, int timeout,
			int firstTable, boolean verbose) {
		this.tournamentName = tournamentName;
		this.softConstraint = softConstraint;
		this.classThreshold = classThreshold;
		this.studentThreshold = studentThreshold;
		this.timeout = timeout;
		this.firstTable = firstTable;
		this.verbose = verbose;
	}

	public String getTournamentName() {
		return tournamentName;
	}
	
	public boolean isSoftConstraint() {
		return softConstraint;
	}

	public float getClassThreshold() {
		return classThreshold;
	}

	public float getStudentThreshold() {
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
