package ppc.manager;

import ppc.annotation.EventHandler;
import ppc.annotation.ManagerPriority;
import ppc.event.Listener;
import ppc.event.TournamentAddLevelGroupEvent;
import ppc.event.TournamentSolveEvent;

// No need for an higher priority
@ppc.annotation.Manager(priority = ManagerPriority.LOW)
public class TournamentSolverManager implements Manager, Listener {
	
	// Good luck my lil sun

	private static final TournamentSolverManager instance = new TournamentSolverManager();
	
	public TournamentSolverManager() {
		// Nothing here in general
	}

	@Override
	public void initManager() {
		// Inits the manager !
		// Here you put everything you need for your manager (lists creation, etc...)
		// Don't forget to eat !
		
	}
	
	@EventHandler
	public void onSolverCalled(TournamentSolveEvent event) {
		// If you need something in event, add it to the class TournamentSolveEvent class!
			
	}
	
	@EventHandler
	public void onLevelGroupAddedToSolver(TournamentAddLevelGroupEvent event) { // Maybe change the method's name...
		// If you need something in event, add it to the class TournamentAddLevelGroupEvent class!
		
	}
	
	public static TournamentSolverManager getInstance() {
		return instance;
	}

}
