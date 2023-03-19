package ppc.exercises;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class Sudoku {

	private static int[][] sudoku = { { 0, 0, 8, 5, 6, 1, 4, 0, 0 }, { 0, 0, 0, 3, 2, 4, 0, 0, 0 },
			{ 3, 0, 4, 0, 0, 0, 5, 0, 6 }, { 0, 9, 0, 0, 3, 0, 0, 2, 0 }, { 0, 0, 0, 8, 0, 7, 0, 0, 0 },
			{ 0, 1, 0, 0, 0, 0, 0, 8, 0 }, { 6, 0, 0, 0, 0, 0, 0, 0, 7 }, { 1, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 2, 9, 3, 0, 0, 0 } };

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		// Création du modèle
		CpModel model = new CpModel();
		
		// Création des variables
		IntVar[][] cases = new IntVar[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int val = sudoku[i][j];
				if (val == 0)
					cases[i][j] = model.newIntVar(1, 9, "x" + i + "_" + j);
				else
					cases[i][j] = model.newConstant(val);
			}
		}

		// Création des contraintes
		// Pas la même valeur sur une ligne
		for (int i = 0; i < 9; i++)
			model.addAllDifferent(cases[i]);

		// Pas la même valeur sur une colonne
		for (int i = 0; i < 9; i++) {
			IntVar[] column = new IntVar[9];
			for (int j = 0; j < sudoku[0].length; j++) {
				column[j] = cases[j][i];
			}
			model.addAllDifferent(column);
		}

		// Pas la même valeur sur un carré
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				IntVar[] square = new IntVar[9];
				square[0] = cases[i * 3][j * 3];
				square[1] = cases[i * 3][j * 3 + 1];
				square[2] = cases[i * 3][j * 3 + 2];
				square[3] = cases[i * 3 + 1][j * 3];
				square[4] = cases[i * 3 + 1][j * 3 + 1];
				square[5] = cases[i * 3 + 1][j * 3 + 2];
				square[6] = cases[i * 3 + 2][j * 3];
				square[7] = cases[i * 3 + 2][j * 3 + 1];
				square[8] = cases[i * 3 + 2][j * 3 + 2];

				model.addAllDifferent(square);
			}
		}
		
		System.out.println(model.modelStats());
				
		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);
		if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					System.out.print(solver.value(cases[i][j]) + " ");
					if (j == 2 || j == 5)
						System.out.print("| ");
				}
				System.out.println();
				if (i == 2 || i == 5)
					System.out.println("_____________________");
			}
		}
	}
	
}
