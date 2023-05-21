package ppc.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ppc.annotation.Event
public class TournamentClassCopyEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String tournamentName;
	private File toCopy;
	private int classNumber;

	public TournamentClassCopyEvent() {
	}

	public TournamentClassCopyEvent(String tournamentName, File toCopy, int classNumber) {
		this.tournamentName = tournamentName;
		this.toCopy = toCopy;
		this.classNumber = classNumber;
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public File getFileToCopy() {
		return toCopy;
	}

	public int getClassNumber() {
		return classNumber;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
