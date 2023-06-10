package ppc.event.openpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentDeleteClassEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	private int classIndex;
	private int listIndex;

	public TournamentDeleteClassEvent() {
	}

	public TournamentDeleteClassEvent(String tournamentName, int classIndex, int listIndex) {
		this.tournamentName = tournamentName;
		this.classIndex = classIndex;
		this.listIndex = listIndex;
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public int getClassIndex() {
		return classIndex;
	}
	
	public int getListIndex() {
		return listIndex;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
