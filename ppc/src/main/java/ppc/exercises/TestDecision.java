package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;

public class TestDecision {
	
	public static void main(String[] args) {
		Loader.loadNativeLibraries();
		
		CpModel model = new CpModel();
		model.addDecisionStrategy(null, null, null);
		
		CpSolver solver = new CpSolver();

	}
	
}
//	public static void main(String[] args) {
//		Loader.loadNativeLibraries();
//
//		int size = 10;
//		int[][] classes = { { 0, 1, 2 }, { 3, 4 }, { 5, 6, 7 }, { 8, 9 } };
//
//		IntVar[] vars = new IntVar[size];
//
//		Solver solver = new Solver("CpSimple");
//
//		for (int i = 0; i < size; i++) {
//			vars[i] = solver.makeIntVar(0, size - 1, "x" + i);
//		}
//
//		solver.addConstraint(solver.makeAllDifferent(vars));
//
//		DecisionBuilder db = new NewValueDecision(vars);
//		solver.newSearch(db);
//		solver.nextSolution();
//
//		for (int i = 0; i < size; i++) {
//			System.out.print(vars[i].toString() + "\n");
//		}
//
//		System.out.println();
//
//		solver.endSearch();
//
//	}
//
//}
//
//class NewValueDecision extends JavaDecisionBuilder {
//	private IntVar[] vars;
//
//	public NewValueDecision(IntVar[] vars) {
//		this.vars = vars;
//	}
//
//	@Override
//	public Decision next(Solver solver) throws FailException {
//		for (IntVar var : vars) {
//			if (!var.bound())
//				return solver.makeAssignVariableValue(var, var.min());
//		}
//
//		return null;
//	}
//
//}