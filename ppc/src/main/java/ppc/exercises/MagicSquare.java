package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;

public class MagicSquare {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		int squareSize = 4;
		int sup = 16;

		CpModel model = new CpModel();

		// Variables
		IntVar[][] vars = new IntVar[squareSize][squareSize];
		for (int i = 0; i < squareSize; i++) {
			for (int j = 0; j < squareSize; j++) {
				vars[i][j] = model.newIntVar(1, sup, "x" + i + "_" + "j");
			}
		}

		// Contraintes
		LinearArgument firstLine = LinearExpr.sum(vars[0]);

		IntVar[][] lines = new IntVar[squareSize][squareSize];
		lines[0] = vars[0];
		// Toutes les contraintes vont se référer à la première ligne
		for (int i = 1; i < squareSize; i++) {
			lines[i] = vars[i];
			model.addEquality(firstLine, LinearExpr.sum(vars[i]));
		}

		// Colonnes
		IntVar[][] columns = new IntVar[squareSize][squareSize];
		for (int i = 0; i < squareSize; i++) {
			IntVar[] column = new IntVar[squareSize];
			for (int j = 0; j < squareSize; j++) {
				column[j] = vars[j][i];
			}

			columns[i] = column;
			model.addEquality(firstLine, LinearExpr.sum(column));
		}

		// Diagoales
		IntVar[] diag = new IntVar[squareSize];
		IntVar[] antidiag = new IntVar[squareSize];

		for (int i = 0; i < squareSize; i++) {
			diag[i] = vars[i][i];
			antidiag[i] = vars[i][squareSize - i - 1];
		}

		model.addEquality(firstLine, LinearExpr.sum(diag));
		model.addEquality(firstLine, LinearExpr.sum(antidiag));

		// Toutes les valeurs doivent être différentes
		IntVar[] allVars = new IntVar[squareSize * squareSize];

		for (int i = 0; i < squareSize; i++) {
			for (int j = 0; j < squareSize; j++) {
				allVars[j + squareSize * i] = vars[i][j];
			}
		}

		model.addAllDifferent(allVars);

		// Résolution
		CpSolver solver = new CpSolver();
		solver.getParameters().setEnumerateAllSolutions(true);
		
		SolutionPrinter sp = new SolutionPrinter(squareSize, vars, lines, columns, diag, antidiag);
		solver.solve(model, sp);

	}

	private static class SolutionPrinter extends CpSolverSolutionCallback {

		private int solutionCount;
		private int squareSize;
		private IntVar[][] vars;
		private IntVar[][] lines;
		private IntVar[][] columns;
		private IntVar[] diag;
		private IntVar[] antidiag;

		public SolutionPrinter(int squareSize, IntVar[][] vars, IntVar[][] lines, IntVar[][] columns, IntVar[] diag,
				IntVar[] antidiag) {
			this.squareSize = squareSize;
			this.vars = vars;
			this.lines = lines;
			this.columns = columns;
			this.diag = diag;
			this.antidiag = antidiag;
			solutionCount = 0;
		}

		@Override
		public void onSolutionCallback() {
			System.out.println("Solution " + ++solutionCount + " :");
//			for (int i = 0; i < squareSize; i++) {
//				System.out.print("\t");
//				for (int j = 0; j < squareSize; j++) {
//					System.out.print(value(vars[i][j]) + "\t");
//				}
//
//				System.out.println("|" + value(LinearExpr.sum(lines[i])));
//			}
//
//			System.out.print("__\t");
//			for (int i = 0; i < squareSize; i++) {
//				System.out.print("__\t");
//			}
//			System.out.print(" __");
//
//			System.out.println();
//			System.out.print(value(LinearExpr.sum(antidiag)) + "|\t");
//
//			for (int i = 0; i < squareSize; i++) {
//				System.out.print(value(LinearExpr.sum(columns[i])) + "\t");
//			}
//
//			System.out.println("|" + value(LinearExpr.sum(diag)));

		}

	}

}
