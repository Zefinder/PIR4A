package ppc.tournament.solver;

public class LevelThread implements Runnable {
	
	private String[][][] classes;
	private boolean softConstraint;
	private int timeout;
	private int firstTable = 1;
	private volatile Solution solution;
	
	public LevelThread(String[][][] classes, boolean softConstraint, int timeout, int firstTable) {
		this.classes = classes;
		this.softConstraint = softConstraint;
		this.timeout = timeout;
		this.firstTable = firstTable;
	}
	
	public Solution getSolution() {
		return this.solution;
	}
	
	@Override
	public void run() {
		TournamentSolver tournament = new TournamentSolver(classes, softConstraint, firstTable);
		this.solution = tournament.solve(timeout);
	}
}
