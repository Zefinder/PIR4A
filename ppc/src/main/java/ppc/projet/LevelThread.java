package ppc.projet;

public class LevelThread implements Runnable {
	
	private Integer[][] classes;
	private boolean softConstraint;
	private int timeout;
	
	public LevelThread(Integer[][] classes, boolean softConstraint, int timeout) {
		this.classes = classes;
		this.softConstraint = softConstraint;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		Tournament tournament = new Tournament(classes, softConstraint);
		tournament.solve(timeout);
	}
}
