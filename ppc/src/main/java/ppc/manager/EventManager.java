package ppc.manager;

import static ppc.annotation.ManagerPriority.CRITICAL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import ppc.annotation.EventHandler;
import ppc.event.Event;
import ppc.event.Listener;
import ppc.event.RegisteredListener;

@ppc.annotation.Manager(priority = CRITICAL)
public final class EventManager implements Manager {

	private static final EventManager instance = new EventManager();

	private Map<Class<? extends Event>, List<RegisteredListener>> eventMap;

	private EventManager() {
	}

	@Override
	public void initManager() {
		System.out.println("Initialising EventManager");
		eventMap = new HashMap<>();

		// Searching for events and registering them
		Reflections reflections = new Reflections("ppc");
		Set<Class<?>> types = reflections.getTypesAnnotatedWith(ppc.annotation.Event.class);
		for (Class<?> clazz : types) {
			if (!Event.class.isAssignableFrom(clazz)) {
				System.err.println(
						String.format("Event %s has Event annotation but doesn't extends Event class... Discarded!",
								clazz.getName()));
			} else
				try {
					Event eventClass = Event.class.cast(clazz.getConstructor().newInstance());
					List<RegisteredListener> registeredListeners = eventClass.getHandlers();
					if (registeredListeners == null) {
						System.err.println(
								String.format("List of handlers of event %s is null... Discarded!", clazz.getName()));
						continue;
					}

					eventMap.put(eventClass.getClass(), registeredListeners);
					System.out.println(String.format("Event %s initialised!", clazz.getName()));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
		}

		System.out.println("EventManager initialised");
	}

	/**
	 * Registers event to the EventManager
	 * 
	 * @param event the event to register
	 */
	public void registerEvent(Event event) {
		eventMap.put(event.getClass(), event.getHandlers());
		System.out.println(String.format("Event %s initialised!", event.getClass().getName()));
	}

	/**
	 * Registers the listener to be processed when the corresponding event will be
	 * called
	 * 
	 * @param listener the listener to register
	 */
	public void registerListener(Listener listener) {
		// We get methods from the listener
		Method[] methods = listener.getClass().getDeclaredMethods();

		// For each method we check
		for (Method method : methods) {
			// if there is the EventHandler annotation
			if (!method.isAnnotationPresent(EventHandler.class))
				continue;

			// if there is one argument and is a registered Event
			if (method.getParameterCount() != 1) {
				System.err.println(String.format(
						"Event handler %s does not have the good number of parameters (only one)... Discarded!",
						method.getName()));
				continue;
			}

			Class<?> check;
			if (!Event.class.isAssignableFrom(check = method.getParameterTypes()[0])) {
				System.err.println(String.format("Parameter of handler %s is not a subclass of Event... Discarded!",
						method.getName()));
				continue;
			}

			Class<? extends Event> eventClass = check.asSubclass(Event.class);
			if (!eventMap.containsKey(eventClass)) {
				System.err
						.println(String.format("Event %s has not been registered... Discarded!", eventClass.getName()));
				continue;
			}

			// Adding registeredListener to the list of the event
			RegisteredListener registeredListener = new RegisteredListener(listener, method);
			eventMap.get(eventClass).add(registeredListener);

			System.out.println(String.format("Event handler %s() has been registered!", method.getName()));
		}
	}

	public void callEvent(Event event) {
		Class<?> eventClass = event.getClass();

		// Verify that the event has been registered
		if (!eventMap.containsKey(event.getClass())) {
			System.err.println(String.format("Event %s has not been registered... Discarded!", eventClass.getName()));
			return;
		}
		
		// Call listeners
		List<RegisteredListener> registeredListeners = eventMap.get(eventClass);
		registeredListeners.forEach(listener -> listener.fireChange(event));
	}

	public static EventManager getInstance() {
		return instance;
	}

}
