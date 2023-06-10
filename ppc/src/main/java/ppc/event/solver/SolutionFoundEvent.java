package ppc.event.solver;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class SolutionFoundEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private int level;
	private int studentsMet;
	private int maxStudentsMet;
	private int classesMet;
	private int maxClassesMet;

	public SolutionFoundEvent() {
	}

	public SolutionFoundEvent(int level, int studentsMet, int maxStudentsMet, int classesMet, int maxClassesMet) {
		this.level = level;
		this.studentsMet = studentsMet;
		this.maxStudentsMet = maxStudentsMet;
		this.classesMet = classesMet;
		this.maxClassesMet = maxClassesMet;
	}

	public int getLevel() {
		return level;
	}

	public int getStudentsMet() {
		return studentsMet;
	}

	public int getMaxStudentsMet() {
		return maxStudentsMet;
	}

	public int getClassesMet() {
		return classesMet;
	}

	public int getMaxClassesMet() {
		return maxClassesMet;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}