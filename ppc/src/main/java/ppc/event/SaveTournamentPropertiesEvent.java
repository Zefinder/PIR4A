package ppc.event;

import java.util.ArrayList;
import java.util.List;

public class SaveTournamentPropertiesEvent extends TournamentCreateEvent {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private int tableOffset;
	
	public SaveTournamentPropertiesEvent() {
	}
	
	public SaveTournamentPropertiesEvent(String name, int matchesNumber, int groupNumber, int maxSearchingTime, int tableOffset,
			float studentsMetThreshold, float classesMetThreshold) {
		super(name, matchesNumber, groupNumber, maxSearchingTime, studentsMetThreshold, classesMetThreshold);
		this.tableOffset = tableOffset;
	}

	public int getTableOffset() {
		return tableOffset;
	}
	
	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
