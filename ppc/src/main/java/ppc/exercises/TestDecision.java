package ppc.exercises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.DoubleLinearExpr;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.Literal;

public class TestDecision {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		CpModel model = new CpModel();

		/*
		 * On considère que les élèves disponibles sont des élèves qui ne sont pas dans
		 * la même classe que l'élève pour qui on organise le match
		 */
		int[] classes = { 0, 0, 1, 2, 2, 2, 2, 2 };
		int studentNumber = classes.length;
		int maxClass = Arrays.stream(classes).max().getAsInt();
		int matchNumber = 6;

		IntVar[] opponent = new IntVar[matchNumber];
		IntVar[] opponentClasses = new IntVar[matchNumber];
		Literal[] sameClassesMet = new BoolVar[matchNumber - 1];

		for (int i = 0; i < matchNumber; i++) {
			// On initialise les opposants...
			opponent[i] = model.newIntVar(0, studentNumber - 1, "opponent_" + i);

			// ... et on les lie à leur classe !
			opponentClasses[i] = model.newIntVar(0, maxClass, "class_" + i);
			model.addElement(opponent[i], classes, opponentClasses[i]);
		}

		// Booléens !
		for (int i = 0; i < matchNumber; i++) {
			List<Literal> sameClasses = new ArrayList<>();
			// Pour chaque opposant placé, on vérifie s'il y a un précédent qui a la même
			// classe
			for (int prec = 0; prec < i; prec++) {
				BoolVar alreadyMet = model.newBoolVar("Met_" + i + "_" + prec);
				sameClasses.add(alreadyMet);

				// En réalité, on vérifie que les classes soient différentes. Si elles ne le
				// sont pas, alreadyMet = false et le AND final sera à false
				model.addDifferent(opponentClasses[prec], opponentClasses[i]).onlyEnforceIf(alreadyMet);
			}

			// Le premier indice est toujours à 1 !
			if (i != 0) {
				BoolVar hasMet = model.newBoolVar("hasMet_" + i);
				sameClassesMet[i - 1] = hasMet;
				model.addBoolAnd(sameClasses).onlyEnforceIf(hasMet);

			}
		}

		// On ne rencontre pas 2 fois la même personne
		model.addAllDifferent(opponent);

		// On maximise le nombre de classes différentes
		model.maximize(new DoubleLinearExpr(sameClassesMet, 0));

		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);

		if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
			System.out.print("Student: \t[");
			for (int i = 0; i < matchNumber; i++) {
				if (i != matchNumber - 1)
					System.out.print(
							String.format("%d (%d),\t", solver.value(opponent[i]), solver.value(opponentClasses[i])));
				else
					System.out.println(
							String.format("%d (%d)]", solver.value(opponent[i]), solver.value(opponentClasses[i])));
			}

			System.out.print("Classes met: \t[");
			for (int i = 0; i < matchNumber; i++) {
				if (i == 0)
					System.out.print(String.format("true,\t"));
				else if (i != matchNumber - 1)
					System.out.print(String.format("%b,\t", solver.value(sameClassesMet[i - 1])));
				else
					System.out.println(String.format("%b]", solver.value(sameClassesMet[i - 1])));
			}
		} else
			System.out.println("No solution!");
	}

}