package ppc.annotation;

import ppc.manager.Manager;

/**
 * Defines the priority of initialization of a manager. It goes from LOW (the
 * lowest) to monitor (the highest). Be sure when putting priority that your
 * manager doesn't depend on a lower-priority manager (for example, do not use
 * the log system while putting a monitor priority to your manager).
 * 
 * @see Manager
 * @see ppc.annotation.Manager
 * 
 * @author Adrien Jakubiak
 *
 */
public enum ManagerPriority {
	LOW, MEDIUM, HIGH, CRITICAL, MONITOR;
}
