package ppc.projet.tournament;

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

public class Tournament {

	public static final int NUMBER_MATCHES = 6;
	private int level;
	private int nbStudents;
	private int nbClasses;
	private int[] studentClasses;
	private Integer ghost = -1;
	private int ghostClass = -1;
	private int maxClassesMet = 0;
	private int maxStudentsMet = 0;
	private boolean paul = true;
	private boolean allowMeetingSameStudent = false;
	Map<Integer, Integer[]> classmates;
	private Integer[][] solution;
	
	private void initAttributes(Integer[][] listClasses) {
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
	
	public Tournament(Integer[][] listClasses, int level, boolean soft) {
		this.level = level;
		allowMeetingSameStudent = soft;
		Loader.loadNativeLibraries();
		initAttributes(listClasses);
	}
	
	public Solution solve(int timeout) {
		CpModel model = new CpModel();

		IntVar[][] opponents = new IntVar[nbStudents][NUMBER_MATCHES];
		IntVar[][] opponentsClasses = new IntVar[opponents.length][opponents[0].length];
		Literal[][] sameClassesMet = new BoolVar[opponents.length][NUMBER_MATCHES - 1];
		Literal[][] sameStudentsMet = new BoolVar[opponents.length][NUMBER_MATCHES - 1];

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
			
			if (!allowMeetingSameStudent)
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
				List<Literal> sameStudents = new ArrayList<>();
				// For each opponent, checking if their class has already been met
				// For each opponent, checking if they have already been met
				for (int prev = 0; prev < curr; prev++) {
					BoolVar alreadyMetClass = model.newBoolVar("Met_" + curr + "_" + prev);
					sameClasses.add(alreadyMetClass);
					BoolVar alreadyMetStudent = model.newBoolVar("StudentMet_" + curr + "_" + prev);
					sameStudents.add(alreadyMetStudent);

					// checking that the classes/students are different
					model.addDifferent(opponentsClasses[student][prev], opponentsClasses[student][curr])
							.onlyEnforceIf(alreadyMetClass);
					model.addDifferent(LinearExpr.constant(ghostClass), opponentsClasses[student][curr])
							.onlyEnforceIf(alreadyMetClass);
					model.addDifferent(opponents[student][prev], opponents[student][curr])
							.onlyEnforceIf(alreadyMetStudent);
				}

				// The first index is always 1
				if (curr != 0) {
					BoolVar hasMetClass = model.newBoolVar("hasMet_" + curr);
					sameClassesMet[student][curr - 1] = hasMetClass;
					model.addBoolAnd(sameClasses).onlyEnforceIf(hasMetClass);
					
					BoolVar hasMetStudent = model.newBoolVar("hasMetStudent_" + curr);
					sameStudentsMet[student][curr - 1] = hasMetStudent;
					model.addBoolAnd(sameStudents).onlyEnforceIf(hasMetStudent);

				}
			}
		}
		// Maximising the number of classes met for everyone but ghost player
		// Also minimising the number of times the same student is met if allowed
		LinearExprBuilder objectiveFunction = LinearExpr.newBuilder();
		int firstStudent = 0;
		if (ghost != -1) {
			firstStudent = 1;
		}
		for (int student = firstStudent; student < nbStudents; student++) {
			if (allowMeetingSameStudent)
				objectiveFunction.addWeightedSum(sameStudentsMet[student], new int[]{5, 5, 5, 5, 5});
			objectiveFunction.addSum(sameClassesMet[student]);
		}
		model.maximize(objectiveFunction);

		CpSolver solver = new CpSolver();

		SolutionCallback sp = new SolutionCallback(opponents);
		solver.getParameters().setEnumerateAllSolutions(true);
		solver.getParameters().setMaxTimeInSeconds(timeout);

		solver.solve(model, sp);
		
		System.out.println("Final solution:");
		System.out.println(this.toString());
		System.out.println("Timed out after " + solver.wallTime());
		return new Solution(solution, studentClasses, ghost);
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
			ids[ghost] = id;
			ghost = id++;
			ghostClass = classes - 1;
			studentClasses[ghost] = ghostClass;
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
				if (opponentClass != classNb && opponentClass != ghostClass) {
					maxClassesMet += (pawnDistribution[opponentClass][blackPawns] == 0) ? 0
							: pawnDistribution[classNb][whitePawns];
					maxClassesMet += (pawnDistribution[opponentClass][whitePawns] == 0) ? 0
							: pawnDistribution[classNb][blackPawns];
				}
			}
		}
		maxStudentsMet = (ghost == -1) ? nbStudents * NUMBER_MATCHES : (nbStudents - 1) * NUMBER_MATCHES;

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
	
	
	@Override
	public String toString() {
		String solutionString = new String();
		for (int i = 0; i < solution.length; i++) {
			List<Integer> classesMet = new ArrayList<>();
			solutionString += String.format("Student %d (class %d): \t[", i, studentClasses[i]);
			
			for (int j = 0; j < NUMBER_MATCHES; j++) {
				int opponent = solution[i][j];
				if (opponent != ghost) {
					classesMet.add(studentClasses[opponent]);
				}
				if (j == solution[0].length - 1)
					solutionString += opponent + " (" + studentClasses[opponent] + ")";
				else
					solutionString += opponent + " (" + studentClasses[opponent] + ")" + "\t";
			}

			if (i != ghost) {
				solutionString += "]\t  -> ";
				if (allowMeetingSameStudent) {
					solutionString += "\t" + Arrays.stream(solution[i]).distinct().count() + " students met";
				}
				solutionString += "\t" + classesMet.stream().distinct().count() + " classes met\n";
			} else {
				solutionString += "] \t -> \tghost player\n";
			}
		}
		if (ghost != -1)
			solutionString += "Need a ghost player; id " + ghost + "\n";
		return solutionString;
	}
	
	
	private class SolutionCallback extends CpSolverSolutionCallback {
		private int solutionCount;
		IntVar[][] opponents;

		public SolutionCallback(IntVar[][] opponents) {
			solutionCount = 0;
			this.opponents = opponents;
		}

		@Override
		public void onSolutionCallback() {
			System.out.println("Solution " + ++solutionCount + " of level " + level);
			int sumClassesMet = 0;
			int sumStudentsMet = 0;
			
			Integer[][] solutionMatches = new Integer[opponents.length][opponents[0].length];
			
			for (int i = 0; i < opponents.length; i++) {
				List<Integer> classesMet = new ArrayList<>();
				List<Integer> studentsMet = new ArrayList<>();
				for (int j = 0; j < NUMBER_MATCHES; j++) {
					int opponent = (int) value(opponents[i][j]);
					if (opponent != ghost) {
						classesMet.add(studentClasses[opponent]);
					}
					studentsMet.add(opponent);
					solutionMatches[i][j] = opponent;
				}
				
				if (i != ghost) {
					sumClassesMet += classesMet.stream().distinct().count();
					sumStudentsMet += studentsMet.stream().distinct().count();
				}
			}
			
			solution = solutionMatches;
			
			System.out.print("Total classes met: " + sumClassesMet + " (max: " + maxClassesMet + ")\t");
			System.out.println("Total students met: " + sumStudentsMet + " (maxmax: " + maxStudentsMet + ")");
			if (sumClassesMet == maxClassesMet && sumStudentsMet == maxStudentsMet) {
				System.out.println("optimal solution found!");
				stopSearch();
			}
			System.out.println("Time spent: " + wallTime());
		}
	}

}
