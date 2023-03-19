package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import com.google.ortools.sat.LinearExpr;

public class Opti1 {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		CpModel model = new CpModel();

		IntVar[] vars = new IntVar[5];
		for (int i = 0; i < 5; i++) {
			vars[i] = model.newIntVar(0, 10, "x" + (i + 1));
		}
		
		model.addAllDifferent(vars);
		model.addLessThan(vars[0], LinearExpr.weightedSum(vars, new long[] { 0, -3, 2, 0, 0 }));
		model.addGreaterOrEqual(LinearExpr.weightedSum(vars, new long[] { 0, 0, 0, 1, 1 }),
				LinearExpr.newBuilder().addWeightedSum(vars, new int[] { 0, 0, 1, 0, 0 }).add(4));

		model.maximize(LinearExpr.weightedSum(vars, new long[] { 1, 1, 1, 1, -2 }));

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
			for (int i = 0; i < 5; i++) {
				System.out.println("x" + (i + 1) + " = " + solver.value(vars[i]));
			}
		}
	}

}
