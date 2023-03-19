package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class Opti2 {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		CpModel model = new CpModel();

		IntVar[] vars = new IntVar[3];
		for (int i = 0; i < 3; i++) {
			vars[i] = model.newIntVar(i + 1, i + 3, "x" + (i + 1));
		}

		model.addGreaterThan(vars[1], vars[0]);

		model.minimize(LinearExpr.weightedSum(vars, new long[] { 1, -2, 3 }));

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
			for (int i = 0; i < 3; i++) {
				System.out.println("x" + (i + 1) + " = " + solver.value(vars[i]));
			}
		}
	}
}
