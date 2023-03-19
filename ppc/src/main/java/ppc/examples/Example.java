package ppc.examples;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;

public class Example {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        
        CpModel model = new CpModel();
        
        int numVals = 3;
        
        IntVar x = model.newIntVar(0, numVals - 1, "x");
        IntVar y = model.newIntVar(0, numVals, "y");
        IntVar z = model.newIntVar(0, numVals + 1, "z");
        
        model.addDifferent(x, y);
        model.addDifferent(y, z);
        model.addGreaterOrEqual(x, z);

        CpSolver solver = new CpSolver();
        SolutionPrinter sp = new SolutionPrinter(x, y, z);
        solver.getParameters().setEnumerateAllSolutions(true);
        
        solver.solve(model, sp);

        System.out.println("Statistiques");
        System.out.printf("\tconflits: %d%n", solver.numConflicts());
        System.out.printf("\tbranches : %d%n", solver.numBranches());
        System.out.printf("\ttemps écoulé : %f s%n", solver.wallTime());
    }

    private static class SolutionPrinter extends CpSolverSolutionCallback {
        private int solutionCount;
        private IntVar x, y, z;
        
        public SolutionPrinter(IntVar x, IntVar y, IntVar z) {
            solutionCount = 0;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public void onSolutionCallback() {
            System.out.println("Solution " + ++solutionCount);
            System.out.println("x = " + value(x));
            System.out.println("y = " + value(y));
            System.out.println("z = " + value(z));
        }
    }
}