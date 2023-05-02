package ppc.manager;

/**
 * <p>
 * Interface that declares a manager. To fully register a manager, use the
 * {@link ppc.annotation.Manager} annotation.
 * </p>
 * 
 * <p>
 * Managers must be implemented as a singleton and have a getInstance() method
 * that returns the unique instance of the manager.
 * </p>
 * 
 * @see ppc.annotation.Manager
 * 
 * @author Adrien Jakubiak
 *
 */
public interface Manager {

	/**
	 * Inits the manager
	 */
	public void initManager();

}
