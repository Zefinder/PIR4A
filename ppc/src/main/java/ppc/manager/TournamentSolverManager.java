package ppc.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.mainpanel.TournamentOpeningStatusEvent;
import ppc.event.openpanel.TournamentEstimateEvent;
import ppc.event.openpanel.TournamentEstimateStatusEvent;
import ppc.event.solver.FinalSolutionFoundEvent;
import ppc.event.solver.TournamentAddLevelGroupEvent;
import ppc.event.solver.TournamentSolveEvent;
import ppc.event.solver.TournamentSolveImpossibleEvent;
import ppc.event.solver.TournamentSolverFinishedEvent;
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
	private Map<String, Solution> precalculatedSolutions;
	private LogsManager logs = LogsManager.getInstance();

	public TournamentSolverManager() {
	}

	@Override
	public void initManager() {
		logs.writeInformationMessage("Initialising TournamentSolverManager...");
		EventManager.getInstance().registerListener(this);

		this.classesByLevel = new HashMap<>();
		this.loadPreData();
		logs.writeInformationMessage("TournamentSolverManager initialised!");
	}

	private void loadPreData() {
		this.precalculatedSolutions = new HashMap<>();
		InputStream in = getClass().getClassLoader().getResourceAsStream("solverData.txt");

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

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

				StudentListsClass listClasses = createStudentClasses(configuration);

				int ghost = -1;
				if (totalStudents % 2 != 0)
					ghost = 0;

				// Parse the third line into a matrix of integers
				Integer[][] opponentsMatrix;
				String line = reader.readLine();
				if (line.equals("null"))
					opponentsMatrix = null;

				else {
					String[] opponentsLine = line.split(";");
					int numRows = opponentsLine.length;
					int numCols = opponentsLine[0].split(" ").length;
					opponentsMatrix = new Integer[numRows][numCols];
					for (int i = 0; i < numRows; i++) {
						String[] rowValues = opponentsLine[i].split(" ");
						for (int j = 0; j < numCols; j++) {
							opponentsMatrix[i][j] = Integer.parseInt(rowValues[j]);
						}
					}
				}

				// Parse soft constraint
				String[] fourthLine = reader.readLine().split(" ");
				boolean softConstraint = Boolean.parseBoolean(fourthLine[1]);
				int maxStudentsMet = Integer.parseInt(fourthLine[2]);
				int maxClassesMet = Integer.parseInt(fourthLine[3]);

				// Parse the fifth line into double and integers
				String[] stats = reader.readLine().split(" ");
				double runtime = Double.parseDouble(stats[0]);
				int nbStudentsMet = Integer.parseInt(stats[1]);
				int nbClassesMet = Integer.parseInt(stats[2]);

				precalculatedSolutions.put(Arrays.toString(configuration),
						new Solution(opponentsMatrix, listClasses.getListStudents(), listClasses.getListClassesId(),
								null, ghost, softConstraint, runtime, nbStudentsMet, maxStudentsMet, nbClassesMet,
								maxClassesMet));
			}

			reader.close();
		} catch (IOException e) {
			System.err.println("Error when reading file, cancel...");
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
	public void onEstimationRequested(TournamentEstimateEvent event) {
		Map<String, String[][]> evaluate = event.getClasses();

		String[][][] lvlClasses = new String[evaluate.size()][][];
		int classNb = 0;
		int studentsNumber = 0;
		int[] classes = new int[evaluate.size()];
		for (String[][] currentClass : evaluate.values()) {
			studentsNumber += currentClass.length;
			classes[classNb] = currentClass.length;
			lvlClasses[classNb++] = currentClass;
		}

		int feasibility = -1;
		if (studentsNumber == 0)
			feasibility = 1;
		else {
			// Check if feasible and in list
			Solution precalculatedSolution = precalculatedSolutions.get(Arrays.toString(classes));
			TournamentSolver tournament;
			if (precalculatedSolution != null) {
				if (precalculatedSolution.getMatches() == null)
					if (precalculatedSolution.isSoftConstraint())
						feasibility = -1;
					else {
						tournament = new TournamentSolver(lvlClasses);
						feasibility = tournament.isProblemSolvable();
					}
				else {
					if (precalculatedSolution.isSoftConstraint())
						feasibility = 0;
					else
						feasibility = 1;
				}

			} else {
				// Check if feasible and not in list
				tournament = new TournamentSolver(lvlClasses);
				feasibility = tournament.isProblemSolvable();
			}
		}

		System.out.println("Level " + event.getLevel() + ", feasibility: " + feasibility);
		TournamentEstimateStatusEvent statusEvent = new TournamentEstimateStatusEvent(event.getLevel(),
				event.getGroupsNumber(), feasibility);
		EventManager.getInstance().callEvent(statusEvent);
	}

	@EventHandler
	public void onSolverCalled(TournamentSolveEvent event) {
		int nbClasses = classesByLevel.values().iterator().next().size();
		List<Thread> threads = new ArrayList<>();
		List<LevelThread> lvlThreads = new ArrayList<>();
		List<Solution> solutions = new ArrayList<>();
		int lastLevelWithGhost = -1;

		if (nbClasses == 0) {
			logs.writeErrorMessage("No class were added so impossible to create a tournament!");
			EventManager.getInstance()
					.callEvent(new TournamentSolveImpossibleEvent("Il n'y a pas de classes pour faire un tournoi !"));
			return;
		}

		if (nbClasses == 1) {
			logs.writeErrorMessage("Impossible to create a tournament with only one class!");
			EventManager.getInstance().callEvent(new TournamentSolveImpossibleEvent(
					"Il n'est pas possible de faire un tournoi avec seulement une classe !"));
			return;
		}

		// so that the levels are in increasing order
		SortedSet<Integer> keys = new TreeSet<>(classesByLevel.keySet());
		int lvl = 0;
		for (Integer key : keys) {
			String[][][] lvlClasses = new String[nbClasses][][];
			int classNb = 0;
			int studentsNumber = 0;
			for (String[][] currentClass : classesByLevel.get(key).values()) {
				studentsNumber += currentClass.length;
				lvlClasses[classNb++] = currentClass;
			}

			System.out.println("Number of students for this group: " + studentsNumber);
			if (studentsNumber == 0)
				continue;

			// Checking if solution already has been computed and has good computation
			Integer[] configuration = getClassesConfiguration(lvlClasses);
			Solution precalculatedSolution = precalculatedSolutions.get(Arrays.toString(configuration));
			if (precalculatedSolution != null
					&& ((float) precalculatedSolution.getStudentsMet()
							/ precalculatedSolution.getMaxStudentsMet()) >= event.getStudentThreshold()
					&& ((float) precalculatedSolution.getClassesMet()
							/ precalculatedSolution.getMaxClassesMet()) >= event.getClassThreshold()
					&& precalculatedSolution.getMatches() != null) {

				// We compute the idToName map and add it to the solution
				Map<Integer, String[]> idToName = new HashMap<>();
				for (int classNumber = 0; classNumber < lvlClasses.length; classNumber++) {
					Integer[] studentsId = precalculatedSolution.getListClasses()[classNumber];
					for (int studentNumber = 0; studentNumber < lvlClasses[classNumber].length; studentNumber++) {
						idToName.put(studentsId[studentNumber], lvlClasses[classNumber][studentNumber]);
					}
				}

				precalculatedSolution.setIdToName(idToName);

				// We add the solution
				solutions.add(precalculatedSolution);
				if (precalculatedSolution.getGhost() != -1)
					lastLevelWithGhost = lvl;

				// We tell the frame that this solution is already found !
				EventManager.getInstance()
						.callEvent(new FinalSolutionFoundEvent(lvl, precalculatedSolution.getStudentsMet(),
								precalculatedSolution.getMaxStudentsMet(), precalculatedSolution.getClassesMet(),
								precalculatedSolution.getMaxClassesMet()));

				// Else we verify that it's feasible and add it to a list
			} else {
				TournamentSolver tournament = new TournamentSolver(lvlClasses);
				int feasibility = tournament.isProblemSolvable();
				if (feasibility == -1 || (feasibility == 0 && !event.isSoftConstraint())) {
					logs.writeErrorMessage("Impossible to solve for level group " + lvl);
					EventManager.getInstance().callEvent(
							new TournamentSolveImpossibleEvent("Le tournoi est impossible pour le niveau " + lvl));
					return;
				} else {
					LevelThread lvlThread = new LevelThread(lvlClasses, event.isSoftConstraint(),
							event.getClassThreshold(), event.getStudentThreshold(), event.getTimeout(), lvl,
							event.isVerbose());
					threads.add(new Thread(lvlThread));
					lvlThreads.add(lvlThread);
				}
			}
			lvl++;
		}

		for (Thread thread : threads) {
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

		// retrieving solutions from threads
		for (LevelThread lvlThread : lvlThreads) {
			Solution currentSolution = lvlThread.getSolution();
			solutions.add(currentSolution);
			if (currentSolution.getGhost() != -1 && lvlThread.getLevel() > lastLevelWithGhost)
				lastLevelWithGhost = lvlThread.getLevel();
		}

		// generating the PDF files
		String[] classNames = classesByLevel.values().iterator().next().keySet().toArray(new String[0]);
		File destinationFolder = FileManager.getInstance().getTournamentResDirectory(event.getTournamentName());
		if (destinationFolder == null) {
			EventManager.getInstance().callEvent(new TournamentSolveImpossibleEvent(
					"Impossible d'accéder au dossier de résultats...\nRedémarrez l'application et réessayez..."));
		}

		PdfGenerator pdfGen = new PdfGenerator(destinationFolder, solutions, classNames, nbClasses,
				event.getFirstTable(), lastLevelWithGhost);
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

			EventManager.getInstance().callEvent(new TournamentSolverFinishedEvent(event.getTournamentName()));
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onLevelGroupAdded(TournamentAddLevelGroupEvent event) {
		System.out.println("Level group " + event.getLevel() + " have been added!");
		put(event.getLevel(), event.getClasses());
	}

	@EventHandler
	public void onOpenedTournament(TournamentOpeningStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS)
			classesByLevel.clear();
	}

	private synchronized void put(int level, Map<String, String[][]> classes) {
		this.classesByLevel.put(level, classes);
	}

	private StudentListsClass createStudentClasses(Integer[] classes) {
		// We count the number of students
		int studentNumber = 0;
		for (int i = 0; i < classes.length; i++)
			studentNumber += classes[i];

		int id = 0;
		int[] studentClasses = new int[studentNumber];

		// If odd number of students, then we add a ghost
		if ((studentNumber & 0b1) == 1) {
			studentClasses = new int[++studentNumber];
			studentClasses[id++] = classes.length;
		} else
			studentClasses = new int[studentNumber];

		boolean unbalanced = false;
		Integer[][] listClassesId = new Integer[classes.length][];

		// Repartition for the first half
		for (int classNb = 0; classNb < classes.length; classNb++) {
			int numberInClass = classes[classNb];
			Integer[] listClassId = new Integer[numberInClass];
			Arrays.fill(listClassId, -1);
			for (int i = 0; i < numberInClass / 2; i++) {
				studentClasses[id] = classNb;
				listClassId[i] = id++;
			}

			if ((numberInClass & 0b1) == 1) {
				if (unbalanced) {
					studentClasses[id] = classNb;
					int middle = numberInClass / 2;

					listClassId[middle] = id++;
				}
				unbalanced = !unbalanced;
			}
			listClassesId[classNb] = listClassId;
		}

		// Repartition for the second half
		for (int classNb = 0; classNb < classes.length; classNb++) {
			for (int student = 0; student < listClassesId[classNb].length; student++) {
				if (listClassesId[classNb][student] == -1) {
					studentClasses[id] = classNb;
					listClassesId[classNb][student] = id++;
				}
			}
		}

		StudentListsClass listClasses = new StudentListsClass(listClassesId, studentClasses);

		return listClasses;
	}

	public static TournamentSolverManager getInstance() {
		return instance;
	}

	private static class StudentListsClass {

		private Integer[][] listClassesId;
		private int[] listStudents;

		public StudentListsClass(Integer[][] listClassesId, int[] listStudents) {
			this.listClassesId = listClassesId;
			this.listStudents = listStudents;
		}

		public Integer[][] getListClassesId() {
			return listClassesId;
		}

		public int[] getListStudents() {
			return listStudents;
		}

	}

}
