package ppc.event.openpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentAddClassEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private String tournamentName;
	private File tournamentFile;
	private int groupsNumber;
	
	public TournamentAddClassEvent() {
	}
	
	public TournamentAddClassEvent(String tournamentName, File tournamentFile, int groupsNumber) {
		this.tournamentName = tournamentName;
		this.tournamentFile = tournamentFile;
		this.groupsNumber = groupsNumber;
	}
	
	public String getTournamentName() {
		return tournamentName;
	}
	
	public File getTournamentFile() {
		return tournamentFile;
	}
	
	public int getGroupsNumber() {
		return groupsNumber;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
