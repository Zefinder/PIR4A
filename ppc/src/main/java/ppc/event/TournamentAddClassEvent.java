package ppc.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentAddClassEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	
	private String tournamentName;
	private File tournamentFile;
	
	public TournamentAddClassEvent() {
	}
	
	public TournamentAddClassEvent(String tournamentName, File tournamentFile, int classNumber) {
		this.tournamentName = tournamentName;
		this.tournamentFile = tournamentFile;
	}
	
	public String getTournamentName() {
		return tournamentName;
	}
	
	public File getTournamentFile() {
		return tournamentFile;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
