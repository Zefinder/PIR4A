package ppc.event;

public abstract class StatusEvent extends Event {
	
	private EventStatus status;
	private String errorMessage;

	public StatusEvent() {
	}

	public StatusEvent(EventStatus status) {
		this.status = status;
	}

	public StatusEvent(EventStatus status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public EventStatus getStatus() {
		return status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
