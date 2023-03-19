package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class NQueens {

	public static void main(String[] args) {
		// Load des libs de OR-Tools
		Loader.loadNativeLibraries();

		// Création du modèle
		CpModel model = new CpModel();

		// Création des variables
		int boardSize = 8;
		IntVar[] queens = new IntVar[boardSize];
		for (int i = 0; i < boardSize; i++) {
			// Une reine par colonne !
			queens[i] = model.newIntVar(0, boardSize - 1, "x" + i);
		}

		// Création des contraintes (déjà pas sur la même colonne)
		// Pas sur la même ligne
		model.addAllDifferent(queens);

		LinearExpr[] diag = new LinearExpr[boardSize];
		LinearExpr[] antidiag = new LinearExpr[boardSize];
		for (int i = 0; i < boardSize; ++i) {
			// Pas sur la même diagonale
			diag[i] = LinearExpr.newBuilder().add(queens[i]).add(-i).build();

			// Pas sur la même anti-diagonale
			antidiag[i] = LinearExpr.newBuilder().add(queens[i]).add(i).build();
		}

		model.addAllDifferent(diag);
		model.addAllDifferent(antidiag);

		// Création du solveur
		CpSolver solver = new CpSolver();
		SolutionPrinter cb = new SolutionPrinter(queens);

		// Chercher toutes les solutions
		solver.getParameters().setEnumerateAllSolutions(true);

		// Recherche de solutions
		solver.solve(model, cb);

		// Statistiques
		System.out.println("Statistiques");
		System.out.printf("\tconflits: %d%n", solver.numConflicts());
		System.out.printf("\tbranches : %d%n", solver.numBranches());
		System.out.printf("\ttemps écoulé : %f s%n", solver.wallTime());

	}

	// Classe qui s'occupe d'afficher les solutions !
	private static class SolutionPrinter extends CpSolverSolutionCallback {
		private int solutionCount, boardSize;
		private IntVar[] queens;

		public SolutionPrinter(IntVar[] queensIn) {
			solutionCount = 0;
			queens = queensIn;
			boardSize = queens.length;
		}

		@Override
		public void onSolutionCallback() {
			System.out.println("Solution " + ++solutionCount);
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {

					if (value(queens[j]) == i)
						System.out.print("Q");
					else
						System.out.print("_");

					if (j != boardSize - 1)
						System.out.print(" ");
				}

				System.out.println();
			}

			System.out.println();
		}

	}

}
