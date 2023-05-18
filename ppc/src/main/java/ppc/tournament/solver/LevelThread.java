package ppc.tournament.solver;

public class LevelThread implements Runnable {
	
	private String[][][] classes;
	private int level;
	private boolean softConstraint;
	private int timeout;
	private int firstTable = 1;
	private volatile Solution solution;
	
	public LevelThread(String[][][] classes, int level, boolean softConstraint, int timeout, int firstTable) {
		this.classes = classes;
		this.level = level;
		this.softConstraint = softConstraint;
		this.timeout = timeout;
		this.firstTable = firstTable;
	}
	
	public LevelThread(String[][][] classes, int level, boolean softConstraint, int timeout) {
		this.classes = classes;
		this.level = level;
		this.softConstraint = softConstraint;
		this.timeout = timeout;
	}
	
	public Solution getSolution() {
		return this.solution;
	}
	
	@Override
	public void run() {
		SolverTournament tournament = new SolverTournament(classes, level, softConstraint, firstTable);
		this.solution = tournament.solve(timeout);
	}
}
