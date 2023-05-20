package ppc.tournament.solver;

/**
 * <p>
 * Class to launch the solver for a specific level of the tournament and store
 * the solution.
 * </p>
 * 
 * @author Sarah Mousset
 *
 */
public class LevelThread implements Runnable {

	private String[][][] classes;
	private boolean softConstraint;
	private int classThreshold;
	private int studentThreshold;
	private int timeout;
	private int level;
	private volatile Solution solution;

	private boolean verbose;

	public LevelThread(String[][][] classes, boolean softConstraint, int classThreshold, int studentThreshold,
			int timeout, int level, boolean verbose) {
		this.classes = classes;
		this.softConstraint = softConstraint;
		this.classThreshold = classThreshold;
		this.studentThreshold = studentThreshold;
		this.timeout = timeout;
		this.level = level;
		this.verbose = verbose;
	}

	public Solution getSolution() {
		return this.solution;
	}
	
	public int getLevel() {
		return level;
	}

	@Override
	public void run() {
		TournamentSolver tournament = new TournamentSolver(classes, softConstraint, classThreshold, studentThreshold,
				verbose);
		this.solution = tournament.solve(timeout);
	}
}
