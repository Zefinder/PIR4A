package ppc.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentCopyEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();
	private String tournamentName;
	private File destinationDirectory;

	public TournamentCopyEvent() {
	}

	public TournamentCopyEvent(String tournamentName, File destinationDirectory) {
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
