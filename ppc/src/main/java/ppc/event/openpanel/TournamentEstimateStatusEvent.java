package ppc.event.openpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentEstimateStatusEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private int level;
	private int groupsNumber;
	private int code;
	
	public TournamentEstimateStatusEvent() {
	}
	
	public TournamentEstimateStatusEvent(int level, int groupsNumber, int code) {
		this.level = level;
		this.groupsNumber = groupsNumber;
		this.code = code;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getGroupsNumber() {
		return groupsNumber;
	}
	
	public int getCode() {
		return code;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
