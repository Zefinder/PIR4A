package ppc.projet;

import java.util.HashMap;
import java.util.Map;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class TournoiV2 {

//	private static final int NUMBER_STUDENTS = 48;
	private static final int NUMBER_MATCHES = 6;

	public TournoiV2() {
		Loader.loadNativeLibraries();
	}

	private double solve(Integer[][] listClasses) {
		CpModel model = new CpModel();

		int numStudents = 0;
		for (Integer[] classs : listClasses)
			numStudents += classs.length;

		IntVar[][] students = new IntVar[numStudents][NUMBER_MATCHES];

		Map<Integer, Integer[]> classes = initClasses(listClasses);

		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[0].length; j++) {
				students[i][j] = model.newIntVar(0, students.length - 1, "student" + i + "_" + j);

				Integer[] studentsInClass = classes.get(i);
				for (int komrad : studentsInClass) {
					model.addDifferent(students[i][j], komrad);
				}
			}

			model.addAllDifferent(students[i]);
		}

		for (int i = 0; i < students[0].length; i++) {
			IntVar[] column = new IntVar[students.length];
			for (int j = 0; j < students.length; j++) {
				column[j] = students[j][i];
			}

			model.addAllDifferent(column);
			model.addInverse(column, column);
		}

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
			for (int i = 0; i < students.length; i++) {
				System.out.print(String.format("Student %d (class %d): \t[", i + 1, getClass(listClasses, i)));
				for (int j = 0; j < students[0].length; j++) {
					if (j == students[0].length - 1)
						System.out.print(solver.value(students[i][j]) + 1);
					else
						System.out.print((solver.value(students[i][j]) + 1) + "\t");

				}
				System.out.println("]");
			}

			System.out.println("Problem for " + numStudents + " students done !");
		} else {
			System.out.println("No solution !");
		}

		System.out.println("Time spent: " + solver.wallTime());

		return solver.wallTime();
	}

	/**
	 * Init of students' classes from a CSV file.
	 * 
	 * @return the {@link HashMap} (idStudent -> classmates)
	 */
	private Map<Integer, Integer[]> initClasses(Integer[][] classes) {
		Map<Integer, Integer[]> mapClasses = new HashMap<>();
//
//		for (int i = 0; i < numClasses; i++) {
//			for (int j = 1 + (i * nbStudentsPerClass); j <= (i + 1) * nbStudentsPerClass; j++)
//				mapClasses.put(j, classes[i]);
//		}

		for (Integer[] classs : classes) {
			for (int student : classs) {
				mapClasses.put(student, classs);
			}
		}

//		System.out.println(Arrays.deepToString(array));
		return mapClasses;
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

	public static void benchmark(int initValue, int lowerBound, int upperBound) {
//		TournoiV2 tournoi = new TournoiV2();
//
//		String res = "[";
//		int borneSup = 150;
//		int classNumber = 6;
//		for (int i = 126; i <= borneSup; i += 6) {
//			double time = tournoi.solve(i, classNumber);
//			if (i == borneSup)
//				res += String.format(Locale.US, "%.3f]", time);
//			else
//				res += String.format(Locale.US, "%.3f, ", time);
//		}
//
//		System.out.println(res);
	}

	public static void main(String[] args) {
		TournoiV2 tournoi = new TournoiV2();

		Integer[][] classes = { { 0, 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9, 10, 11 } };
//		Integer[][] classes = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 } };

		tournoi.solve(classes);
	}

}
