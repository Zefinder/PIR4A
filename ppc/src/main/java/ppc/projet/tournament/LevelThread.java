package ppc.projet.tournament;

public class LevelThread implements Runnable {
	
	private String[][] classes;
	private int level;
	private boolean softConstraint;
	private int timeout;
	private volatile Solution solution;
	
	public LevelThread(String[][] classes, int level, boolean softConstraint, int timeout) {
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
		Tournament tournament = new Tournament(classes, level, softConstraint);
		this.solution = tournament.solve(timeout);
	}
}
