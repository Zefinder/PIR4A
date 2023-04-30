package ppc.projet;

public class LevelThread implements Runnable {
	
	private Integer[][] classes;
	private int level;
	private boolean softConstraint;
	private int timeout;
	
	public LevelThread(Integer[][] classes, int level, boolean softConstraint, int timeout) {
		this.classes = classes;
		this.level = level;
		this.softConstraint = softConstraint;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		Tournament tournament = new Tournament(classes, level, softConstraint);
		tournament.solve(timeout);
	}
}
