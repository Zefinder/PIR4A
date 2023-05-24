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
	private float classThreshold;
	private float studentThreshold;
	private int timeout;
	private int level;
	private volatile Solution solution;

	private boolean verbose;

	public LevelThread(String[][][] classes, boolean softConstraint, float classThreshold, float studentThreshold,
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
				level, verbose);
		this.solution = tournament.solve(timeout);
	}
}
