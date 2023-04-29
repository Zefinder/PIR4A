package ppc.frame.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;

public class TournamentSolver {

	private static final int NUMBER_MATCHES = 6;
	private static int nbStudents;
	private static int nbClasses;
	private static int[] studentClasses;
	private static Integer ghost = -1;
	private static int maxClassesMet = 0;
	private static boolean paul = true;
	Map<Integer, Integer[]> classmates;

	public TournamentSolver(Integer[][] listClasses) {
		Loader.loadNativeLibraries();

		nbClasses = listClasses.length;

		for (Integer[] classs : listClasses)
			nbStudents += classs.length;
		if (nbStudents % 2 == 1) {
			System.out.println("adding ghost player");
			ghost = nbStudents++;
			nbClasses++;
			listClasses = Arrays.copyOf(listClasses, listClasses.length + 1);
			listClasses[listClasses.length - 1] = new Integer[] { ghost };
		}

		studentClasses = new int[nbStudents];
		classmates = getClassmates(newIdClasses(listClasses));
	}

	private double solve() {
		CpModel model = new CpModel();

		IntVar[][] opponents = new IntVar[nbStudents][NUMBER_MATCHES];
		IntVar[][] opponentsClasses = new IntVar[opponents.length][opponents[0].length];
		Literal[][] sameClassesMet = new BoolVar[opponents.length][NUMBER_MATCHES - 1];

		// Debug
		System.out.print("camarades des joueurs : ");
		for (Map.Entry<Integer, Integer[]> entry : classmates.entrySet()) {
			System.out.print(entry.getKey() + ": [");
			for (Integer id : entry.getValue()) {
				System.out.print(id + " ");
			}
			System.out.print("] ");
		}
		System.out.println();

		for (int student = 0; student < opponents.length; student++) {
			for (int game = 0; game < opponents[0].length; game++) {
				if (student < nbStudents / 2)
					opponents[student][game] = model.newIntVar(nbStudents / 2, nbStudents - 1,
							"student" + student + "_game" + game);
				else
					opponents[student][game] = model.newIntVar(0, nbStudents / 2 - 1,
							"student" + student + "_game" + game);

				// associating the opponent to their class
				opponentsClasses[student][game] = model.newIntVar(0, nbClasses - 1, "class_" + game);
				model.addElement(opponents[student][game], studentClasses, opponentsClasses[student][game]);

				for (int classmate : classmates.get(student)) {
					model.addDifferent(opponents[student][game], classmate);
				}
			}

			model.addAllDifferent(opponents[student]);
		}

		for (int i = 0; i < opponents[0].length; i++) {
			IntVar[] column = new IntVar[opponents.length];
			for (int j = 0; j < opponents.length; j++) {
				column[j] = opponents[j][i];
			}
			model.addInverse(column, column);
			model.addAllDifferent(column);
		}

		// Checking if the class met is different than the previous one
		for (int student = 0; student < opponents.length; student++) {
			for (int curr = 0; curr < NUMBER_MATCHES; curr++) {
				List<Literal> sameClasses = new ArrayList<>();
				// For each opponent, checking if their class has already been met
				for (int prev = 0; prev < curr; prev++) {
					BoolVar alreadyMet = model.newBoolVar("Met_" + curr + "_" + prev);
					sameClasses.add(alreadyMet);

					// checking that the classes are different
					model.addDifferent(opponentsClasses[student][prev], opponentsClasses[student][curr])
							.onlyEnforceIf(alreadyMet);
				}

				// The first index is always 1
				if (curr != 0) {
					BoolVar hasMet = model.newBoolVar("hasMet_" + curr);
					sameClassesMet[student][curr - 1] = hasMet;
					model.addBoolAnd(sameClasses).onlyEnforceIf(hasMet);

				}
			}
		}
		// Maximising the number of classes met for everyone but ghost player
		LinearExprBuilder objectiveFunction = LinearExpr.newBuilder();
		int firstStudent = 0;
		if (ghost != -1) {
			firstStudent = 1;
		}
		for (int student = firstStudent; student < nbStudents; student++) {
			Literal[] hasMetClasses = sameClassesMet[student];
			objectiveFunction.addSum(hasMetClasses);
		}
		model.maximize(objectiveFunction);

		CpSolver solver = new CpSolver();

		SolutionPrinter sp = new SolutionPrinter(opponents, sameClassesMet);
		solver.getParameters().setEnumerateAllSolutions(true);
		solver.getParameters().setMaxTimeInSeconds(60);

		solver.solve(model, sp);

		System.out.println("Timed out after " + solver.wallTime());
		return solver.wallTime();
	}

	private static class SolutionPrinter extends CpSolverSolutionCallback {
		private int solutionCount;
		IntVar[][] opponents;
		Literal[][] sameClassesMet;

		public SolutionPrinter(IntVar[][] opponents, Literal[][] sameClassesMet) {
			solutionCount = 0;
			this.opponents = opponents;
			this.sameClassesMet = sameClassesMet;
		}

		@Override
		public void onSolutionCallback() {
			System.out.println("Solution " + ++solutionCount);
			int sumClassesMet = 0;
			for (int i = 0; i < opponents.length; i++) {
				int nbClassesMet = 1;
				System.out.print(String.format("Student %d (class %d): \t[", i, studentClasses[i]));
				for (int j = 0; j < NUMBER_MATCHES; j++) {
					if (j < NUMBER_MATCHES - 1)
						nbClassesMet += booleanValue(sameClassesMet[i][j]) ? 1 : 0;
					long opponent = value(opponents[i][j]);
					if (j == opponents[0].length - 1)
						System.out.print(opponent + " (" + studentClasses[(int) opponent] + ")");
					else
						System.out.print(opponent + " (" + studentClasses[(int) opponent] + ")" + "\t");
				}

				if (i != ghost) {
					sumClassesMet += nbClassesMet;
					System.out.print("]");
					System.out.println("\t   -> " + nbClassesMet + " classes met");
				} else {
					System.out.println("]");
				}
			}
			if (ghost != -1)
				System.out.println("Need a ghost player; id " + ghost);

			System.out.println("Problem for " + nbStudents + " students done!");
			System.out.println("Total classes met: " + sumClassesMet + " (max: " + maxClassesMet + ")");
			if (sumClassesMet == maxClassesMet) {
				System.out.println("optimal solution found!");
				stopSearch();
			}
			System.out.println("Time spent: " + wallTime());
		}
	}

	/**
	 * Init of students' classes from a CSV file. Chooses which students will start
	 * with white/black pawns
	 * 
	 * @param classes
	 * @param nbStudents
	 * @return classes with the new ids
	 */
	private Integer[][] newIdClasses(Integer[][] listClasses) {
		// array that keeps track of which old id (the index)
		// is turned into which new id (the value)
		Integer[] ids = new Integer[nbStudents];
		Arrays.fill(ids, -1);

		int[][] pawnDistribution = new int[listClasses.length][2];
		int whitePawns = 0;
		int blackPawns = 1;

		int id = 0;
		int classes = listClasses.length;
		if (ghost != -1) {
			studentClasses[id] = classes - 1;
			ids[ghost] = id;
			ghost = id++;
			classes--;
			pawnDistribution[classes][0] = 1;
			pawnDistribution[classes][1] = 0;
		}

		if (paul) {
			// sorting the classes from biggest to smallest
			List<Integer[]> tmpList = new ArrayList<>();
			for (int i = 0; i < classes; i++) {
				tmpList.add(listClasses[i]);
			}
			Collections.sort(tmpList, new Comparator<Integer[]>() {
				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					return o2.length - o1.length;
				}
			});
			Integer[][] orderedClasses = tmpList.toArray(new Integer[0][]);
			System.out.println(Arrays.deepToString(orderedClasses));

			boolean unbalanced = false;
			for (int classNb = 0; classNb < orderedClasses.length; classNb++) {
				Integer[] c = orderedClasses[classNb];
				for (int i = 0; i < c.length / 2; i++) {
					int oldId = c[i];
					studentClasses[id] = classNb;
					ids[oldId] = id++;
					pawnDistribution[classNb][0]++;
				}
				if (c.length % 2 != 0) {
					if (unbalanced) {
						studentClasses[id] = classNb;
						int oldId = c[c.length / 2];
						ids[oldId] = id++;
						pawnDistribution[classNb][whitePawns]++;
					}
					unbalanced = !unbalanced;
				}
			}

			for (int classNb = 0; classNb < orderedClasses.length; classNb++) {
				for (int student : orderedClasses[classNb]) {
					if (ids[student] == -1) {
						studentClasses[id] = classNb;
						ids[student] = id++;
						pawnDistribution[classNb][blackPawns]++;
					}
				}
			}
		}

		else {
			// copying the classes to a list of lists
			List<List<Integer>> studentsToBeAssigned = new ArrayList<>();
			for (int i = 0; i < classes; i++) {
				Integer[] currentClass = listClasses[i];
				studentsToBeAssigned.add(new LinkedList<Integer>(Arrays.asList(currentClass)));
			}

			// The first numStudents/2 will start with the white pawns
			// We are going to choose a student to start with the white pawns
			// To choose them, we take a student from the class that has the most
			// non assigned students
			while (id < nbStudents / 2) {
				int biggestClassId = 0;
				List<Integer> biggestClass = studentsToBeAssigned.get(biggestClassId);
				for (int classNb = 1; classNb < studentsToBeAssigned.size(); classNb++) {
					if (studentsToBeAssigned.get(classNb).size() > biggestClass.size()) {
						biggestClass = studentsToBeAssigned.get(classNb);
						biggestClassId = classNb;
					}
				}
				Integer initId = biggestClass.get(0);
				biggestClass.remove(biggestClass.get(0));
				studentClasses[id] = biggestClassId;
				ids[initId] = id++;
				pawnDistribution[biggestClassId][whitePawns]++;
			}

			// The last numStudents/2 will start with the black pawns
			for (int classNb = 0; classNb < studentsToBeAssigned.size(); classNb++) {
				List<Integer> c = studentsToBeAssigned.get(classNb);
				for (Integer s : c) {
					studentClasses[id] = classNb;
					ids[s] = id++;
					pawnDistribution[classNb][blackPawns]++;
				}
			}
		}

		// updating the classes matrix
		for (int classNb = 0; classNb < listClasses.length; classNb++) {
			for (int student = 0; student < listClasses[classNb].length; student++) {
				int oldId = listClasses[classNb][student];
				listClasses[classNb][student] = ids[oldId];
			}
		}

		// Debug
		System.out.println(Arrays.deepToString(pawnDistribution));

		// computing maxClassesMet
		for (int classNb = 0; classNb < classes; classNb++) {
			for (int opponentClass = 0; opponentClass < pawnDistribution.length; opponentClass++) {
				if (opponentClass != classNb) {
					maxClassesMet += (pawnDistribution[opponentClass][blackPawns] == 0) ? 0
							: pawnDistribution[classNb][whitePawns];
					maxClassesMet += (pawnDistribution[opponentClass][whitePawns] == 0) ? 0
							: pawnDistribution[classNb][blackPawns];
				}
			}
		}

		// Debug
		System.out.println("classes : ");
		for (Integer[] classs : listClasses) {
			System.out.print("[ ");
			for (Integer s : classs)
				System.out.print(s + " ");
			System.out.println("]");
		}

		// debug, printing studentClasses
		System.out.print("studentClasses: [");
		for (int i = 0; i < studentClasses.length; i++)
			System.out.print(studentClasses[i] + " ");
		System.out.println("]");

		return listClasses;
	}

	/**
	 * associates each student to its classmates (including themselves) for the
	 * first half of students
	 * 
	 * @return the {@link HashMap} (idStudent -> classmates)
	 */
	private Map<Integer, Integer[]> getClassmates(Integer[][] listClasses) {
		Map<Integer, Integer[]> classmates = new HashMap<>();

		// creating the map
		for (Integer[] classs : listClasses) {
			for (int student : classs) {
				if (student < nbStudents)
					classmates.put(student, classs);
			}
		}
		return classmates;
	}

	public static void main(String[] args) {
		// works
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16, 17 } };

		// to test maximisation + better maxClassesMet for paul
		// Integer[][] classes = { { 0, 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9, 10, 11 }, {
		// 12, 13, 14, 15, 16 },
		// { 17, 18, 19, 20 }, { 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 } };

		// works with the ghost player!
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16 } };

		// tests paul vs adrien: fonctionne pas pour adrien !
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16 }, { 17, 18, 19, 20} };

		// works :
		// Integer[][] classes = { {0, 1, 2, 3}, {4, 5, 6}, {7, 8, 9, 10, 11, 12, 13},
		// {14, 15, 16, 17, 18, 19, 20, 21}, {22, 23, 24, 25}};

		// niveau 2 rencontre 2019
		// Integer[][] classes = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {12, 13, 14,
		// 15, 16, 17, 18, 19, 20, 21}, {22, 23, 24, 25, 26, 27}};

		// niveau 3 rencontre 2019
		// Integer[][] classes = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, {11, 12, 13, 14,
		// 15, 16, 17, 18, 19, 20, 21, 22}, {23, 24, 25, 26, 27, 28, 29}};
		
		// tournoi MJ
		Integer[][] classes = { { 0, 1, 2, 3, 4, 5, 6, 7 }, { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 },
				{ 20, 21, 22, 23, 24, 25, 26, 27, 28 }, { 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41 },
				{ 42, 43, 44, 45, 46, 47, 48 }, { 49, 50, 51, 52, 53, 54 } };

		TournamentSolver tournoi = new TournamentSolver(classes);
		tournoi.solve();
	}

}
