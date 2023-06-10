package ppc.event.openpanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentEstimateEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private int level;
	private int groupsNumber;
	private Map<String, String[][]> classes;

	public TournamentEstimateEvent() {
	}

	public TournamentEstimateEvent(int level, int groupsNumber, Map<String, String[][]> classes) {
		this.level = level;
		this.groupsNumber = groupsNumber;
		this.classes = classes;
	}

	public int getLevel() {
		return level;
	}
	
	public int getGroupsNumber() {
		return groupsNumber;
	}
	
	public Map<String, String[][]> getClasses() {
		return classes;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
