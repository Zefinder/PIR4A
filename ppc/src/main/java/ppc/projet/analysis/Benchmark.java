package ppc.projet.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ppc.projet.Tournament;

public class Benchmark {

	private BlockingQueue<Elt<Tournament, Integer>> queue = new LinkedBlockingQueue<>();
	private final int timeout = 20;
	private int test = 1;
	
	public Benchmark() {
		this.generateProblems();
	}
	
	private class Elt<A,B> {
	    private A a;
	    private B b;
	    public Elt(A a, B b){
	        this.a = a;
	        this.b = b;
	    }
	    public A getA(){ return a; }
	    public B getB(){ return b; }
	}

	private void generateProblems() {
		Integer[][] initProblem3 = {{0, 1, 2, 3, 4, 5}, {6, 7, 8, 9, 10, 11}, {12, 13, 14, 15, 16, 17}};
		Integer[][] initProblem4 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}};
		Integer[][] initProblem5 = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 10, 11}, {12, 13, 14, 15}};
		Integer[][] initProblem6  = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 10, 11}, {12, 13}, {14, 15}};
		Integer[][] initProblem7 = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {8, 9}, {10, 11}, {12, 13}};
		Integer[][][] initProblems = new Integer[][][] {initProblem3, initProblem4, initProblem5, initProblem6, initProblem7};
		
		for (Integer[][] initProblem : initProblems) {
			queue.add(new Elt<Tournament, Integer>(new Tournament(initProblem, 0, false), test));
			queue.add(new Elt<Tournament, Integer>(new Tournament(initProblem, 0, true), test++));
			
			Integer[][] prevProblem = initProblem;
			for (int randomFactor = 1; randomFactor <= 4; randomFactor++) {
				for (int pbNb = 0; pbNb < 5; pbNb++) {
					RandomProblem problem;
					do
						problem = new RandomProblem(randomFactor, prevProblem);
					while (!new Tournament(problem.getClasses(), 0, false).isSolvable());
					
					queue.add(new Elt<Tournament, Integer>(new Tournament(problem.getClasses(), 0, false), test));
					queue.add(new Elt<Tournament, Integer>(new Tournament(problem.getClasses(), 0, true), test++));
					prevProblem = problem.getClasses();
				}
			}
		}
	}
	
	private void writeStatsFile(String fileName, Integer[][] classes, String params, String stats) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(classes.length + "\n");
	    for (Integer[] c : classes)
	    	writer.write(c.length + " ");
	    writer.write("\n" + params + "\n" + stats);
	    writer.close();
	}

	private class LaunchSolverBenchmark implements Runnable {
		@Override
		public void run() {
			try {
				while (!queue.isEmpty()) {
					Elt<Tournament, Integer> problem = queue.take();
					Tournament tournament = problem.getA();
					double walltime = tournament.solve(timeout);
					
					String testNb = (problem.getB() < 10) ? "0" + problem.getB() : "" + problem.getB();
					boolean soft = tournament.isAllowMeetingSameStudent();
					String fileName = "benchmark_" + tournament.getInitClasses().length + "_" + testNb + "_" + soft + ".txt";
					
					String params = timeout + " " + soft + " " + tournament.getMaxStudentsMet() + " " + tournament.getMaxClassesMet();
					String stats = tournament.getStats();
					if (stats.isEmpty())
						stats = walltime + " -1 -1";
					
					writeStatsFile(fileName, tournament.getInitClasses(), params, stats);
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		int threadNb = 8;
		Benchmark benchmark = new Benchmark();
		for (int i = 0; i < threadNb; i++)
			(new Thread(benchmark.new LaunchSolverBenchmark())).start();
	}
}
