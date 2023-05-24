package ppc.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ppc.annotation.Event
public class TournamentAddLevelGroupEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private Map<String, String[][]> classes;
	private int level;

	public TournamentAddLevelGroupEvent() {}
	
	public TournamentAddLevelGroupEvent(Map<String, String[][]> classes, int level) {
		this.classes = classes;
		this.level = level;
	}

	public Map<String, String[][]> getClasses() {
		return classes;
	}
	
	public int getLevel() {
		return level;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
