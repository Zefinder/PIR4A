package ppc.event.mainpanel;

import java.util.ArrayList;
import java.util.List;

import ppc.event.Event;
import ppc.event.RegisteredListener;

@ppc.annotation.Event
public class SettingsChangeEvent extends Event {

	private static final List<RegisteredListener> HANDLERS = new ArrayList<>();

	private String resultsPath;
	private String createString;
	private String matchesNumberString;
	private String groupsNumberString;
	private String barColor;
	private String maxTimeString;
	private String maxStudentsMetString;
	private String maxClassesMetString;

	public SettingsChangeEvent() {
	}

	public SettingsChangeEvent(String resultsPath, String createString, String matchesNumberString,
			String groupsNumberString, String barColor, String maxTimeString, String maxStudentsMetString,
			String maxClassesMetString) {
		this.resultsPath = resultsPath;
		this.createString = createString;
		this.matchesNumberString = matchesNumberString;
		this.groupsNumberString = groupsNumberString;
		this.barColor = barColor;
		this.maxTimeString = maxTimeString;
		this.maxStudentsMetString = maxStudentsMetString;
		this.maxClassesMetString = maxClassesMetString;
	}

	public String getResultsPath() {
		return resultsPath;
	}

	public String getCreateString() {
		return createString;
	}

	public String getMatchesNumberString() {
		return matchesNumberString;
	}

	public String getGroupsNumberString() {
		return groupsNumberString;
	}

	public String getBarColor() {
		return barColor;
	}

	public String getMaxTimeString() {
		return maxTimeString;
	}

	public String getMaxStudentsMetString() {
		return maxStudentsMetString;
	}

	public String getMaxClassesMetString() {
		return maxClassesMetString;
	}

	@Override
	public List<RegisteredListener> getHandlers() {
		return HANDLERS;
	}

}
