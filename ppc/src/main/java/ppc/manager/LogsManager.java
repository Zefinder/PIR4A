package ppc.manager;

import static ppc.annotation.ManagerPriority.MONITOR;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This manager registers all messages that needs to be printed to the console.
 * It is the most important manager to initialize since it show all messages and
 * store them.
 * </p>
 * 
 * <p>
 * Standard output and error printstreams are replaced by instances of this
 * class (resp. INFO and FATAL messages) only when strings are printed.
 * </p>
 * 
 * <p>
 * Logs are formatted this way [dd/mm/yyyy hh:mm:ss] [MessageNature]: message
 * </p>
 * 
 * @see Manager
 * @see ppc.annotation.Manager
 * @see Message
 * 
 * @author Adrien Jakubiak
 *
 */
@ppc.annotation.Manager(priority = MONITOR)
public final class LogsManager implements Manager {

	private static final LogsManager instance = new LogsManager();
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("'['dd/MM/yyyy HH:mm:ss']'");

	private enum MessageNature {
		INFO, WARN, ERROR, FATAL;
	}

	private static class ConsoleRedirection extends PrintStream {

		MessageNature nature;

		public ConsoleRedirection(OutputStream out, MessageNature nature) {
			super(out);
			this.nature = nature;
		}

		@Override
		public void println(String x) {
			Message message = instance.new Message(nature, x);
			instance.messagesList.add(message);
			super.println(message.toString());
		}
	}

	/**
	 * Class representing a message. A message stores a String, a date and a nature.
	 * 
	 * @author Adrien Jakubiak
	 */
	public class Message {
		private LocalDateTime date;
		private MessageNature nature;
		private String message;

		/**
		 * Create a message
		 * 
		 * @param nature  the nature of the message
		 * @param message the message
		 */
		public Message(MessageNature nature, String message) {
			this.date = LocalDateTime.now();
			this.nature = nature;
			this.message = message;
		}

		@Override
		public String toString() {
			return String.format("%s [%s]: %s", date.format(dateFormat), nature.name(), message);
		}

	}

	private List<Message> messagesList;
	private ConsoleRedirection consoleInfo;
//	private ConsoleRedirection consoleWarn;
//	private ConsoleRedirection consoleErr;
	private ConsoleRedirection consoleFatal;

	private LogsManager() {
	}

	@Override
	public void initManager() {
		messagesList = new ArrayList<>();

		consoleInfo = new ConsoleRedirection(System.out, MessageNature.INFO);
//		consoleWarn = new ConsoleRedirection(System.out, MessageNature.WARN);
//		consoleErr = new ConsoleRedirection(System.out, MessageNature.ERROR);
		consoleFatal = new ConsoleRedirection(System.out, MessageNature.FATAL);

		// Replacing system out and err
		System.setOut(consoleInfo);
		System.setErr(consoleFatal);

		writeInformationMessage("LogsManager initialised!");
	}

	/**
	 * Registers a message as INFO and display it in the console
	 * 
	 * @param messageString the message to print
	 */
	public void writeInformationMessage(String messageString) {
		writeMessage(MessageNature.INFO, messageString);
	}

	/**
	 * Registers a message as WARN and display it in the console
	 * 
	 * @param messageString the message to print
	 */
	public void writeWarningMessage(String messageString) {
		writeMessage(MessageNature.WARN, messageString);
	}

	/**
	 * Registers a message as ERROR and display it in the console
	 * 
	 * @param messageString the message to print
	 */
	public void writeErrorMessage(String messageString) {
		writeMessage(MessageNature.ERROR, messageString);
	}

	/**
	 * Registers a message as FATAL and display it in the console
	 * 
	 * @param messageString the message to print
	 */
	public void writeFatalErrorMessage(String messageString) {
		writeMessage(MessageNature.FATAL, messageString);
	}

	public List<Message> getMessages() {
		return messagesList;
	}

	private void writeMessage(MessageNature nature, String messageString) {
		Message message = new Message(nature, messageString);
		System.out.println(message);
		messagesList.add(message);
	}

	public static LogsManager getInstance() {
		return instance;
	}
}
