package ppc.manager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.itextpdf.text.DocumentException;

import ppc.annotation.EventHandler;
import ppc.annotation.ManagerPriority;
import ppc.event.Listener;
import ppc.event.TournamentAddLevelGroupEvent;
import ppc.event.TournamentSolveEvent;
import ppc.tournament.output.PdfGenerator;
import ppc.tournament.solver.LevelThread;
import ppc.tournament.solver.Solution;
import ppc.tournament.solver.TournamentSolver;

/**
 * <p>
 * This manager adds level groups to the tournament, launches the solver, and
 * generates the PDF output files.
 * </p>
 * 
 * @see Manager
 * @see ppc.annotation.Manager
 * @see TournamentSolver
 * 
 * @author Sarah Mousset
 *
 */
@ppc.annotation.Manager(priority = ManagerPriority.LOW)
public class TournamentSolverManager implements Manager, Listener {

	private static final TournamentSolverManager instance = new TournamentSolverManager();

	private Map<Integer, Map<String, String[][]>> classesByLevel;
	private LogsManager logs = LogsManager.getInstance();

	public TournamentSolverManager() {
	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising TournamentSolverManager...");
		EventManager.getInstance().registerListener(this);

		this.classesByLevel = new HashMap<>();
	}

	@EventHandler
	public void onSolverCalled(TournamentSolveEvent event) {

		int nbClasses = classesByLevel.values().iterator().next().size();
		List<Thread> threads = new ArrayList<>();
		List<LevelThread> lvlThreads = new ArrayList<>();

		// so that the levels are in increasing order
		SortedSet<Integer> keys = new TreeSet<>(classesByLevel.keySet());
		for (Integer key : keys) {
			String[][][] lvlClasses = new String[nbClasses][][];
			int classNb = 0;
			for (String[][] classes : classesByLevel.get(key).values())
				lvlClasses[classNb++] = classes;

			LevelThread lvlThread = new LevelThread(lvlClasses, event.isSoftConstraint(), event.getClassThreshold(),
					event.getStudentThreshold(), event.getTimeout(), event.getFirstTable(), event.isVerbose());
			Thread thread = new Thread(lvlThread);
			threads.add(thread);
			lvlThreads.add(lvlThread);
			thread.start();
		}

		// waiting for all threads to be done
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// retrieving solutions
		int lastLevelWithGhost = -1;
		List<Solution> solutions = new ArrayList<>();
		for (int lvl = 0; lvl < lvlThreads.size(); lvl++) {
			Solution currentSolution = lvlThreads.get(lvl).getSolution();
			solutions.add(currentSolution);
			if (currentSolution.getGhost() != -1)
				lastLevelWithGhost = lvl;
		}

		// generating the PDF files
		String[] classNames = classesByLevel.values().iterator().next().keySet().toArray(new String[0]);
		PdfGenerator pdfGen = new PdfGenerator(solutions, classNames, nbClasses, lastLevelWithGhost);
		try {
			logs.writeInformationMessage("Creating pdf ListeMatches... ");
			pdfGen.createPdfListeMatches();
			logs.writeInformationMessage("Creating pdf ListeClasses... ");
			pdfGen.createPdfListeClasses();
			logs.writeInformationMessage("Creating pdf ListeNiveaux... ");
			pdfGen.createPdfListeNiveaux();
			logs.writeInformationMessage("Creating pdf FicheProf... ");
			pdfGen.createPdfProfs();
			logs.writeInformationMessage("Creating pdf FichesEleves... ");
			pdfGen.createPdfEleves();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onLevelGroupAdded(TournamentAddLevelGroupEvent event) {
		this.classesByLevel.put(event.getLevel(), event.getClasses());
	}

	public static TournamentSolverManager getInstance() {
		return instance;
	}

}
