package ppc.event;

import java.util.ArrayList;
import java.util.List;

import ppc.annotation.Event;
import ppc.manager.TournamentManager;

/**
 * Event called when a tournament needs to be created. This event must be
 * registered during initialization.
 * 
 * @see ppc.event.Event
 * @see ppc.annotation.Event
 * @see TournamentManager
 * 
 * @author Adrien Jakubiak
 *
 */
@Event
public class TournamentCreateEvent extends ppc.event.Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String name;
	private int matchesNumber;
	private int groupNumber;
	private int maxSearchingTime;
	private float studentsMetThreshold;
	private float classesMetThreshold;

	/**
	 * Empty constructor for the event, this constructor should not be used!
	 */
	public TournamentCreateEvent() {
	}

	/**
	 * Creates a new {@link TournamentCreateEvent}. This event will create a
	 * tournament with some specific options.
	 * 
	 * @param name                 the name of the tournament
	 * @param matchesNumber        the number of rounds for each students
	 * @param groupNumber          the number of groups per class
	 * @param maxSearchingTime     maximum time spent by search for a group
	 * @param studentsMetThreshold threshold to stop the search when a certain
	 *                             percentage of different students met is found
	 * @param classesMetThreshold  threshold to stop the search when a certain
	 *                             percentage of different classes met is found
	 */
	public TournamentCreateEvent(String name, int matchesNumber, int groupNumber, int maxSearchingTime,
			float studentsMetThreshold, float classesMetThreshold) {
		this.name = name;
		this.matchesNumber = matchesNumber;
		this.groupNumber = groupNumber;
		this.maxSearchingTime = maxSearchingTime;
		this.studentsMetThreshold = studentsMetThreshold;
		this.classesMetThreshold = classesMetThreshold;
	}

	public String getName() {
		return name;
	}
	
	public int getMatchesNumber() {
		return matchesNumber;
	}
	
	public int getGroupsNumber() {
		return groupNumber;
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
