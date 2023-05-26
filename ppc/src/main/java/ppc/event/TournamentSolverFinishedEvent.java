package ppc.event;

import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentSolverFinishedEvent extends SolutionFoundEvent {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	public TournamentSolverFinishedEvent() {
	}
	
	public TournamentSolverFinishedEvent(int level, int studentsMet, int maxStudentsMet, int classesMet, int maxClassesMet) {
		super(level, studentsMet, maxStudentsMet, classesMet, maxClassesMet);
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
