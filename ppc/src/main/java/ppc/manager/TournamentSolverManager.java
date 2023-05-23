package ppc.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private Map<Integer[], Solution> precalculatedSolutions;
	private LogsManager logs = LogsManager.getInstance();

	public TournamentSolverManager() {
	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising TournamentSolverManager...");
		EventManager.getInstance().registerListener(this);

		this.classesByLevel = new HashMap<>();
		this.loadPreData();
	}

	private void loadPreData() {
		this.precalculatedSolutions = new HashMap<>();
		// TODO Ouvrir le fichier et mettre dans la map
		File file = new File("input.txt"); // Replace "input.txt" with your file path
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while (reader.readLine() != null) { // Ignore the first line

				// Parse the second line into a sorted array of integers
				String[] studentsPerClass = reader.readLine().split(" ");
				Integer[] configuration = new Integer[studentsPerClass.length];
				int totalStudents = 0;
				for (int i = 0; i < studentsPerClass.length; i++) {
					int nbStudents = Integer.parseInt(studentsPerClass[i]);
					configuration[i] = nbStudents;
					totalStudents += nbStudents;
				}
				Arrays.sort(configuration, Collections.reverseOrder());

				int ghost = -1;
				if (totalStudents % 2 != 0)
					ghost = 0;

				// Parse the third line into a matrix of integers
				String[] opponentsLine = reader.readLine().split(";");
				int numRows = opponentsLine.length;
				int numCols = opponentsLine[0].split(" ").length;
				Integer[][] opponentsMatrix = new Integer[numRows][numCols];
				for (int i = 0; i < numRows; i++) {
					String[] rowValues = opponentsLine[i].split(" ");
					for (int j = 0; j < numCols; j++) {
						opponentsMatrix[i][j] = Integer.parseInt(rowValues[j]);
					}
				}

				// Parse soft constraint
				String fourthLine = reader.readLine();
				boolean softConstraint = Boolean.parseBoolean(fourthLine.split(" ")[1]);

				// Parse the fifth line into double and integers
				String[] stats = reader.readLine().split(" ");
				double runtime = Double.parseDouble(stats[0]);
				int nbStudentsMet = Integer.parseInt(stats[1]);
				int nbClassesMet = Integer.parseInt(stats[2]);

				// TODO : we shouldn't have null null null... or the pdf generator will be
				// angry...
				precalculatedSolutions.put(configuration, new Solution(opponentsMatrix, null, null, null, ghost,
						softConstraint, runtime, nbStudentsMet, nbClassesMet));
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Integer[] getClassesConfiguration(String[][][] levelClasses) {
		Integer[] configuration = new Integer[levelClasses.length];
		for (int classNb = 0; classNb < levelClasses.length; classNb++)
			configuration[classNb] = levelClasses[classNb].length;
		Arrays.sort(configuration, Collections.reverseOrder());
		return configuration;
	}

	@EventHandler
	public void onSolverCalled(TournamentSolveEvent event) {

		int nbClasses = classesByLevel.values().iterator().next().size();
		List<Thread> threads = new ArrayList<>();
		List<LevelThread> lvlThreads = new ArrayList<>();
		List<Solution> solutions = new ArrayList<>();
		int lastLevelWithGhost = -1;

		// so that the levels are in increasing order
		SortedSet<Integer> keys = new TreeSet<>(classesByLevel.keySet());
		int lvl = 0;
		for (Integer key : keys) {
			String[][][] lvlClasses = new String[nbClasses][][];
			int classNb = 0;
			for (String[][] currentClass : classesByLevel.get(key).values())
				lvlClasses[classNb++] = currentClass;

			// Checking if solution already has been computed
			Integer[] configuration = getClassesConfiguration(lvlClasses);
			Solution precalculatedSolution = precalculatedSolutions.get(configuration);
			if (precalculatedSolution != null && (precalculatedSolution.isSoftConstraint() == event.isSoftConstraint())
					&& precalculatedSolution.getMaxStudentsMet() >= event.getStudentThreshold()
					&& precalculatedSolution.getMaxClassesMet() >= event.getClassThreshold()) {
				solutions.add(precalculatedSolution);
				if (precalculatedSolution.getGhost() != -1)
					lastLevelWithGhost = lvl;
				// Else we launch the solver
			} else {
				LevelThread lvlThread = new LevelThread(lvlClasses, event.isSoftConstraint(), event.getClassThreshold(),
						event.getStudentThreshold(), event.getTimeout(), lvl, event.isVerbose());
				Thread thread = new Thread(lvlThread);
				threads.add(thread);
				lvlThreads.add(lvlThread);
				thread.start();
			}
			lvl++;
		}

		// waiting for all threads to be done
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// retrieving solutions from threads
		for (LevelThread lvlThread : lvlThreads) {
			Solution currentSolution = lvlThread.getSolution();
			solutions.add(currentSolution);
			if (currentSolution.getGhost() != -1 && lvlThread.getLevel() > lastLevelWithGhost)
				lastLevelWithGhost = lvlThread.getLevel();
		}

		// generating the PDF files
		String[] classNames = classesByLevel.values().iterator().next().keySet().toArray(new String[0]);
		PdfGenerator pdfGen = new PdfGenerator(solutions, classNames, nbClasses, event.getFirstTable(),
				lastLevelWithGhost);
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
