package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class InverseConstraint {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		CpModel model = new CpModel();

		IntVar[] vars = new IntVar[6];
		for (int i = 0; i < 6; i++) {
			vars[i] = model.newIntVar(0, 5, "x" + i);
			model.addDifferent(vars[i], i);
		}

		model.addInverse(vars, vars);

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
			for (int i = 0; i < 6; i++)
				System.out.print(solver.value(vars[i]) + "\t");

			System.out.println();
		} else
			System.out.println("No solution !");
	}

}
