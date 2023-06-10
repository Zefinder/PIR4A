package ppc.event.mainpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class TournamentResultsCopyEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	private String tournamentName;
	private File destinationDirectory;

	public TournamentResultsCopyEvent() {
	}

	public TournamentResultsCopyEvent(String tournamentName, File destinationDirectory) {
		this.tournamentName = tournamentName;
		this.destinationDirectory = destinationDirectory;
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public File getDestinationFolder() {
		return destinationDirectory;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
