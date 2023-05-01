package ppc.event;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;

@Event
public class TournamentOpenEvent extends ppc.event.Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String name;
	private int maxSearchingTime;
	private float studentsMetThreshold;
	private float classesMetThreshold;
	
	public TournamentOpenEvent() {
	}
	
	public TournamentOpenEvent(String name, int maxSearchingTime, float studentsMetThreshold,
			float classesMetThreshold) {
		this.name = name;
		this.maxSearchingTime = maxSearchingTime;
		this.studentsMetThreshold = studentsMetThreshold;
		this.classesMetThreshold = classesMetThreshold;
	}

	public String getName() {
		return name;
	}

	public int getMaxSearchingTime() {
		return maxSearchingTime;
	}

	public float getStudentsMetThreshold() {
		return studentsMetThreshold;
	}

	public float getClassesMetThreshold() {
		return classesMetThreshold;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
