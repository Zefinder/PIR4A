package ppc.projet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class TournoiV1 {

//	private static final int NUMBER_STUDENTS = 48;
	private static final int NUMBER_MATCHES = 6;

	public TournoiV1() {
		Loader.loadNativeLibraries();
	}

	private double solve(int numStudents, int numClasses) {
		if ((numStudents & 1) == 1) {
			System.out.println("No solution !");
			return -1;
		}

		CpModel model = new CpModel();

		IntVar[][] students = new IntVar[numStudents][NUMBER_MATCHES];

		Map<Integer, Integer[]> classes = initClasses(numStudents, numClasses);

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

			model.addInverse(column, column);
			model.addAllDifferent(column);
		}

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
//			for (int i = 0; i < students.length; i++) {
//				System.out.print(String.format("Student %d (class %d): \t[", i + 1, i / numClasses));
//				for (int j = 0; j < students[0].length; j++) {
//					if (j == students[0].length - 1)
//						System.out.print(solver.value(students[i][j]) + 1);
//					else
//						System.out.print((solver.value(students[i][j]) + 1) + "\t");
//
//				}
//				System.out.println("]");
//			}

			System.out.println("Problem for " + numStudents + " students done !");

			System.out.println("Time spent: " + solver.wallTime());
			return solver.wallTime();
		} else {
			System.out.println("No solution !");
			return -1;
		}

	}

	/**
	 * Init of students' classes from a CSV file.
	 * 
	 * @return the {@link HashMap} linking students to its class
	 */
	private Map<Integer, Integer[]> initClasses(int numStudents, int numClasses) {
		Map<Integer, Integer[]> mapClasses = new HashMap<>();
		int nbStudentsPerClass = numStudents / numClasses;

		Integer[][] classes = new Integer[numClasses][nbStudentsPerClass];
		for (int i = 0; i < numStudents; i++) {
			classes[i / nbStudentsPerClass][i % nbStudentsPerClass] = i;
		}

		for (int i = 0; i < numClasses; i++) {
			for (int j = i * nbStudentsPerClass; j < (i + 1) * nbStudentsPerClass; j++)
				mapClasses.put(j, classes[i]);
		}

//		System.out.println(Arrays.deepToString(array));
		return mapClasses;
	}

	public static void benchmark(int lowerBound, int upperBound, int classNumber) {
		TournoiV1 tournoi = new TournoiV1();

		String res = "[";
		for (int i = lowerBound; i <= upperBound; i += classNumber) {
			double time = tournoi.solve(i, classNumber);
			if (i == upperBound)
				res += String.format(Locale.US, "%.3f]", time);
			else
				res += String.format(Locale.US, "%.3f, ", time);
		}

		System.out.println(res);
	}

	public static void main(String[] args) {
		benchmark(6, 150, 6);
	}

}
