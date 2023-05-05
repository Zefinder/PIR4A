package ppc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import ppc.annotation.ManagerPriority;
import ppc.frame.MainFrame;
import ppc.manager.Manager;

public class Launch {

	private static void init() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {

		List<Class<?>> monitorPrioManager = new ArrayList<>();
		List<Class<?>> criticalPrioManager = new ArrayList<>();
		List<Class<?>> highPrioManager = new ArrayList<>();
		List<Class<?>> mediumPrioManager = new ArrayList<>();
		List<Class<?>> lowPrioManager = new ArrayList<>();

		Reflections reflections = new Reflections("ppc");
		Set<Class<?>> types = reflections.getTypesAnnotatedWith(ppc.annotation.Manager.class);
		monitorPrioManager.addAll(types.stream().filter(
				manager -> manager.getAnnotation(ppc.annotation.Manager.class).priority() == ManagerPriority.MONITOR)
				.toList());

		criticalPrioManager.addAll(types.stream().filter(
				manager -> manager.getAnnotation(ppc.annotation.Manager.class).priority() == ManagerPriority.CRITICAL)
				.toList());

		highPrioManager.addAll(types.stream().filter(
				manager -> manager.getAnnotation(ppc.annotation.Manager.class).priority() == ManagerPriority.HIGH)
				.toList());

		mediumPrioManager.addAll(types.stream().filter(
				manager -> manager.getAnnotation(ppc.annotation.Manager.class).priority() == ManagerPriority.MEDIUM)
				.toList());

		lowPrioManager.addAll(types.stream().filter(
				manager -> manager.getAnnotation(ppc.annotation.Manager.class).priority() == ManagerPriority.LOW)
				.toList());

		for (Class<?> monitorPrioClass : monitorPrioManager) {
			Manager manager = (Manager) monitorPrioClass.getMethod("getInstance").invoke(null);
			manager.initManager();
		}

		for (Class<?> criticalPrioClass : criticalPrioManager) {
			Manager manager = (Manager) criticalPrioClass.getMethod("getInstance").invoke(null);
			manager.initManager();
		}

		for (Class<?> highPrioClass : highPrioManager) {
			Manager manager = (Manager) highPrioClass.getMethod("getInstance").invoke(null);
			manager.initManager();
		}

		for (Class<?> mediumPrioClass : mediumPrioManager) {
			Manager manager = (Manager) mediumPrioClass.getMethod("getInstance").invoke(null);
			manager.initManager();
		}

		for (Class<?> lowPrioClass : lowPrioManager) {
			Manager manager = (Manager) lowPrioClass.getMethod("getInstance").invoke(null);
			manager.initManager();
		}

	}

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		init();

		MainFrame mf = new MainFrame();
		mf.initFrame();
	}

}
