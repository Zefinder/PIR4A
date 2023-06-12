package ppc.projet.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ppc.projet.Tournament;

public class Benchmark {

	private BlockingQueue<Tournament> queue = new LinkedBlockingQueue<>();
	private Set<String> generatedProblems = new HashSet<>();
	private BufferedWriter writer;
	private static int nbProblemsSolved = 1;
	private final int timeout = 900;

	public Benchmark() {
		Integer[][] initProblem3 = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12, 13, 14, 15, 16, 17 } };
		Integer[][] initProblem4 = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
		Integer[][] initProblem5 = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 }, { 12, 13, 14, 15 } };
		Integer[][] initProblem6 = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 }, { 12, 13 }, { 14, 15 } };
		Integer[][] initProblem7 = { { 0, 1 }, { 2, 3 }, { 4, 5 }, { 6, 7 }, { 8, 9 }, { 10, 11 }, { 12, 13 } };

		this.generateProblems(initProblem3, 1, 3, 17);
		this.generateProblems(initProblem4, 1, 3, 17);
		this.generateProblems(initProblem5, 1, 3, 17);
		this.generateProblems(initProblem6, 1, 3, 17);
		this.generateProblems(initProblem7, 1, 3, 17);

	}

	private synchronized int incrementNbProblemsSolved() {
		return nbProblemsSolved++;
	}

	private void generateProblems(Integer[][] initialProblem, int minRandomFactor, int maxRandomFactor,
			int numberPerFactor) {
		generatedProblems = new HashSet<>();
		generatedProblems.add(Arrays.deepToString(initialProblem));

		int problemNumber = 1;
		
		queue.add(new Tournament(initialProblem, 0, false, problemNumber++));

		Integer[][] prevProblem = initialProblem;
		for (int randomFactor = minRandomFactor; randomFactor <= maxRandomFactor; randomFactor++) {
			for (int pbNb = 0; pbNb < numberPerFactor; pbNb++) {
				boolean sameProblem = false;
				RandomProblem problem;
				do {
					problem = new RandomProblem(randomFactor, prevProblem);
					System.out.println("AA" + Arrays.deepToString(problem.getClasses()));
					if (generatedProblems.contains(Arrays.deepToString(problem.getClasses()))) {
						sameProblem = true;
						prevProblem = problem.getClasses();
					}
				} while (!new Tournament(problem.getClasses(), 0, false, 0).isSolvable() && !sameProblem);

				int res = new Tournament(problem.getClasses(), 0, false, 0).isProblemSolvable();
				queue.add(new Tournament(problem.getClasses(), 0, res == 0, problemNumber++));
				prevProblem = problem.getClasses();
				System.out.println("BB" + Arrays.deepToString(prevProblem));
				generatedProblems.add(Arrays.deepToString(prevProblem));
			}
		}
	}

	private void initWriter(String fileName) throws IOException {
		writer = new BufferedWriter(new FileWriter(fileName, false));
	}

	private void closeWriter() throws IOException {
		writer.flush();
		writer.close();
	}

	private void writeStatsFile(Integer[][] classes, int[][] matches, String params, String stats) throws IOException {

		writer.write(classes.length + "\n");
		for (Integer[] c : classes)
			writer.write(c.length + " ");

		writer.write("\n");
		if (matches == null) {
			writer.write("null");
		} else
			for (int student = 0; student < matches.length; student++) {
				for (int game = 0; game < matches[student].length; game++) {
					if (game == matches[student].length - 1) {
						if (student == matches.length - 1)
							writer.write(String.valueOf(matches[student][game]));
						else
							writer.write(matches[student][game] + ";");
					} else {
						writer.write(matches[student][game] + " ");
					}
				}
			}

		writer.write("\n" + params + "\n" + stats + "\n");
		writer.flush();
	}

	private class LaunchSolverBenchmark implements Runnable {
		@Override
		public void run() {
			try {
				while (!queue.isEmpty()) {
					Tournament tournament = queue.take();
					double walltime = tournament.solve(timeout);

					boolean soft = tournament.isAllowMeetingSameStudent();

					String params = timeout + " " + soft + " " + tournament.getMaxStudentsMet() + " "
							+ tournament.getMaxClassesMet();
					String stats = tournament.getStats();
					if (stats.isEmpty())
						stats = walltime + " -1 -1";

					int[][] matches = tournament.getRepartionSolution();

					writeStatsFile(tournament.getInitClasses(), matches, params, stats);
					System.out.println("Number of problems solved: " + incrementNbProblemsSolved());
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		int threadNb = 8;
		Benchmark benchmark = new Benchmark();

		benchmark.initWriter("./solverData.txt");

		Thread[] threads = new Thread[threadNb];
		
		for (int i = 0; i < threadNb; i++) {
			threads[i] = new Thread(benchmark.new LaunchSolverBenchmark());
			threads[i].start();
		}
		
		for (int i = 0; i < threadNb; i++) {
			threads[i].join();
		}
		
		benchmark.closeWriter();
	}
}
