package ppc.projet.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ppc.projet.Tournament;

public class Benchmark {

	private BlockingQueue<Elt<Integer[][], Boolean, Integer>> queue = new LinkedBlockingQueue<>();
	private final int timeout = 20;
	private int test = 1;
	
	public Benchmark() {
		this.generateProblems();
	}
	
	private class Elt<A,B,C> {
	    private A a;
	    private B b;
	    private C c;
	    public Elt(A a, B b, C c){
	        this.a = a;
	        this.b = b;
	        this.c = c;
	    }
	    public A getA(){ return a; }
	    public B getB(){ return b; }
	    public C getC(){ return c; }
	}

	private void generateProblems() {
		Integer[][] initProblem3 = {{0, 1, 2, 3, 4, 5}, {6, 7, 8, 9, 10, 11}, {12, 13, 14, 15, 16, 17}};
		Integer[][] initProblem4 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}};
		Integer[][] initProblem5 = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 10, 11}, {12, 13, 14, 15}};
		Integer[][] initProblem6  = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 10, 11}, {12, 13}, {14, 15}};
		Integer[][] initProblem7 = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {8, 9}, {10, 11}, {12, 13}};
		Integer[][][] initProblems = new Integer[][][] {initProblem3, initProblem4, initProblem5, initProblem6, initProblem7};
		
		for (Integer[][] initProblem : initProblems) {
			queue.add(new Elt<Integer[][], Boolean, Integer>(initProblem, false, test));
			queue.add(new Elt<Integer[][], Boolean, Integer>(initProblem, true, test++));
			
			Integer[][] prevProblem = initProblem;
			for (int randomFactor = 1; randomFactor <= 4; randomFactor++) {
				for (int pbNb = 0; pbNb < 5; pbNb++) {
					RandomProblem problem;
					do 
						problem = new RandomProblem(randomFactor, prevProblem);
					while (!new Tournament(problem.getClasses(), 0, false).isSolvable());
					queue.add(new Elt<Integer[][], Boolean, Integer>(problem.getClasses(), false, test));
					queue.add(new Elt<Integer[][], Boolean, Integer>(problem.getClasses(), true, test++));
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
					Elt<Integer[][], Boolean, Integer> problem = queue.take();
					Tournament tournament = new Tournament(problem.getA(), 0, problem.getB());
					double walltime = tournament.solve(timeout);
					
					String fileName = "benchmark_" + problem.getA().length + "_" + problem.getC() + "_" + problem.getB() + ".txt";
					String params = timeout + " " + problem.getB() + " " + tournament.getMaxStudentsMet() + " " + tournament.getMaxClassesMet();
					String stats = tournament.getStats();
					if (stats.isEmpty())
						stats = walltime + " -1 -1";
					writeStatsFile(fileName, problem.getA(), params, stats);
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
