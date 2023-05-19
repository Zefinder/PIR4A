package ppc.projet.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ppc.projet.Tournament;

public class Benchmark {

	private BlockingQueue<Elt<Tournament, Integer>> queue = new LinkedBlockingQueue<>();
	private Set<Integer[][]> generatedProblems = new HashSet<>();
	private static int nbProblemsSolved = 0;
	private final int timeout = 900;

	public Benchmark() {
		Integer[][] initProblem3 = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12, 13, 14, 15, 16, 17 } };
//		Integer[][] initProblem4 = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
//		Integer[][] initProblem5 = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 }, { 12, 13, 14, 15 } };
//		Integer[][] initProblem6 = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 }, { 12, 13 }, { 14, 15 } };
//		Integer[][] initProblem7 = { { 0, 1 }, { 2, 3 }, { 4, 5 }, { 6, 7 }, { 8, 9 }, { 10, 11 }, { 12, 13 } };
		
		this.generateProblems(initProblem3, 1, 5, 20);
	}

	private synchronized int incrementNbProblemsSolved() {
		return nbProblemsSolved++;
	}

	private class Elt<A, B> {
		private A a;
		private B b;

		public Elt(A a, B b) {
			this.a = a;
			this.b = b;
		}

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}
	}

	private void generateProblems(Integer[][] initialProblem, int minRandomFactor, int maxRandomFactor,
			int numberPerFactor) {
		generatedProblems = new HashSet<>();
		generatedProblems.add(initialProblem);

		int test = 1;
		queue.add(new Elt<Tournament, Integer>(new Tournament(initialProblem, 0, false), test));
		queue.add(new Elt<Tournament, Integer>(new Tournament(initialProblem, 0, true), test++));

		Integer[][] prevProblem = initialProblem;
		for (int randomFactor = minRandomFactor; randomFactor <= maxRandomFactor; randomFactor++) {
			for (int pbNb = 0; pbNb < numberPerFactor; pbNb++) {
				boolean sameProblem = false;
				RandomProblem problem;
				do {
					problem = new RandomProblem(randomFactor, prevProblem);
					if (generatedProblems.contains(problem.getClasses())) {
						sameProblem = true;
						prevProblem = problem.getClasses();
					}
				} while (!new Tournament(problem.getClasses(), 0, false).isSolvable() && !sameProblem);

				queue.add(new Elt<Tournament, Integer>(new Tournament(problem.getClasses(), 0, false), test));
				queue.add(new Elt<Tournament, Integer>(new Tournament(problem.getClasses(), 0, true), test++));
				prevProblem = problem.getClasses();
				generatedProblems.add(prevProblem);
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

					String testNb = (problem.getB() < 10) ? "0" : "";
					testNb += (problem.getB() < 100) ? "0" + problem.getB() : problem.getB();
					boolean soft = tournament.isAllowMeetingSameStudent();
					String fileName = "benchmark_" + tournament.getInitClasses().length + "_" + testNb + "_" + soft
							+ ".txt";

					String params = timeout + " " + soft + " " + tournament.getMaxStudentsMet() + " "
							+ tournament.getMaxClassesMet();
					String stats = tournament.getStats();
					if (stats.isEmpty())
						stats = walltime + " -1 -1";

					writeStatsFile(fileName, tournament.getInitClasses(), params, stats);
					System.out.println("Number of problems solved: " + incrementNbProblemsSolved());
				}
			} catch (InterruptedException | IOException e) {
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
