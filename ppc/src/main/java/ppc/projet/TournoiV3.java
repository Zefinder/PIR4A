package ppc.projet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class TournoiV3 {

	private static final int NUMBER_MATCHES = 6;
	private Integer[] ids;
	private Integer ghost = -1;

	public TournoiV3() {
		Loader.loadNativeLibraries();
	}

	private double solve(Integer[][] listClasses) {
		CpModel model = new CpModel();

		int nbStudents = 0;
		for (Integer[] classs : listClasses)
			nbStudents += classs.length;

		if (nbStudents % 2 == 1) {
			System.out.println("adding ghost");
			ghost = nbStudents++;
			listClasses = Arrays.copyOf(listClasses, listClasses.length + 1);
			listClasses[listClasses.length - 1] = new Integer[]{ghost};
		}

		IntVar[][] opponents = new IntVar[nbStudents / 2][NUMBER_MATCHES];

		listClasses = newIdClasses(listClasses, nbStudents);
		Map<Integer, Integer[]> classmates = initClasses(listClasses, nbStudents);

		// Debug
		System.out.println("nouvelles classes : ");
		for (Integer[] classs : listClasses) {
			System.out.print("[ ");
			for (Integer id : classs) {
				System.out.print(id + " ");
			}
			System.out.println("]");
		}

		System.out.print("camarades des n/2 premiers joueurs : ");
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
				opponents[student][game] = model.newIntVar(nbStudents / 2, nbStudents - 1,
						"student" + student + "_game" + game);
				
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
			model.addAllDifferent(column);
		}

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
			for (int i = 0; i < opponents.length; i++) {
				System.out.print(String.format("Student %d (class %d): \t[", i + 1, getClass(listClasses, i) + 1));
				for (int j = 0; j < opponents[0].length; j++) {
					if (j == opponents[0].length - 1)
						System.out.print(solver.value(opponents[i][j]) + 1);
					else
						System.out.print((solver.value(opponents[i][j]) + 1) + "\t");

				}
				System.out.println("]");
			}
			if (ghost != -1)
				System.out.println("Need a ghost player; id " + ghost);

			System.out.println("Problem for " + nbStudents + " students done!");
		} else {
			System.out.println("No solution!");
		}

		System.out.println("Time spent: " + solver.wallTime());

		return solver.wallTime();
	}

	private Integer[][] newIdClasses(Integer[][] classes, int nbStudents) {
		// initialising ids
		this.ids = new Integer[nbStudents];
		Arrays.fill(ids, -1);

		// copying the classes to a list of lists
		List<List<Integer>> listClasses = new ArrayList<>();
		for (Integer[] currentClass : classes) {
			listClasses.add(new LinkedList<Integer>(Arrays.asList(currentClass)));
		}

		// The first numStudents/2 will start with the white pawns
		// We are going to choose a student to start with the white pawns
		// To choose them, we take a student from the class that has the most
		// non assigned students
		int id = 0;
		while (id < nbStudents / 2) {
			List<Integer> biggestClass = listClasses.get(0);
			for (int classNb = 1; classNb < listClasses.size(); classNb++) {
				if (listClasses.get(classNb).size() > biggestClass.size()) {
					biggestClass = listClasses.get(classNb);
				}
			}
			Integer initId = biggestClass.get(0);
			biggestClass.remove(biggestClass.get(0));
			this.ids[initId] = id++;
		}
		// The last numStudents/2 will start with the black pawns
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == -1) {
				ids[i] = id++;
			}
		}

		// updating the classes matrix
		for (int classNb = 0; classNb < classes.length; classNb++) {
			for (int student = 0; student < classes[classNb].length; student++) {
				int oldId = classes[classNb][student];
				classes[classNb][student] = ids[oldId];
			}
		}
		return classes;
	}

	/**
	 * Init of students' classes from a CSV file. Chooses which students will start
	 * with white/black pawns
	 * 
	 * @return the {@link HashMap} (idStudent -> classmates)
	 */
	private Map<Integer, Integer[]> initClasses(Integer[][] classes, int nbStudents) {
		Map<Integer, Integer[]> classMates = new HashMap<>();

		// creating the map
		for (Integer[] classs : classes) {
			for (int student : classs) {
				if (student < nbStudents / 2)
					classMates.put(student, classs);
			}
		}
		return classMates;
	}

	private int getClass(Integer[][] classes, int student) {
		int classs = -1;
		for (int c = 0; c < classes.length; c++) {
			for (int s : classes[c]) {
				if (s == student) {
					classs = c;
					break;
				}
			}
		}
		return classs;
	}

	public static void main(String[] args) {
		TournoiV3 tournoi = new TournoiV3();
		
		// works
		Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12, 13, 14, 15, 16, 17 } };
		
		// doesn't work with the ghost player somehow
//		Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12, 13, 14, 15, 16 } };
		
		// niveau 1 rencontre 2019, works :
//		Integer[][] classes = { {0, 1, 2, 3}, {4, 5, 6}, {7, 8, 9, 10, 11, 12, 13}, {14, 15, 16, 17, 18, 19, 20, 21}, {22, 23, 24, 25}};

		tournoi.solve(classes);
	}

}
