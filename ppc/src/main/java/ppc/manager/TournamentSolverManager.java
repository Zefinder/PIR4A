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
	}

	private void loadPreData() {
		this.precalculatedSolutions = new HashMap<>();
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

				StudentListsClass listClasses = createStudentClasses(configuration);

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

				precalculatedSolutions.put(Arrays.toString(configuration),
						new Solution(opponentsMatrix, listClasses.getListStudents(), listClasses.getListClassesId(),
								null, ghost, softConstraint, runtime, nbStudentsMet, nbClassesMet));
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
			Solution precalculatedSolution = precalculatedSolutions.get(Arrays.toString(configuration));
			if (precalculatedSolution != null
					&& precalculatedSolution.getMaxStudentsMet() >= event.getStudentThreshold()
					&& precalculatedSolution.getMaxClassesMet() >= event.getClassThreshold()) {

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

//		System.out.println(Arrays.toString(studentClasses));
//		System.out.println(Arrays.deepToString(listClassesId));

		StudentListsClass listClasses = new StudentListsClass(listClassesId, studentClasses);

		return listClasses;
	}

//	public static void main(String[] args) throws InterruptedException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		Launch.init();
//
//		String[][][] lvlClasses = new String[][][] {
//				{ { "Adr", "ien" }, { "sa", "rah" }, { "san", "dro" }, { "Pi", "R" }, { "A", "a" }, { "B", "b" } },
//				{ { "Pa", "ul" }, { "As", "mun" }, { "vale", "ntin" }, { "Nic", "olas" }, { "C", "c" }, { "D", "d" } },
//				{ { "Marie", "Jo" }, { "Dan", "iel" }, { "Marie", "Agnès" }, { "Rom", "ain" }, { "E", "e" },
//						{ "F", "f" } } };
//		LevelThread lvlThread = new LevelThread(lvlClasses, false, 1, 1, 60, 1, true);
//		Thread t = new Thread(lvlThread);
//		t.start();
//		System.out.println("AAAAAAAAAAH");
//		t.join();
//		System.out.println("AAAH");
//
//		Solution sol = lvlThread.getSolution();
//		Integer[][] matches = sol.getMatches();
//
//		Integer[][] listClasses = instance.createStudentClasses(new int[] { 6, 6, 6 });
//		instance.precalculatedSolutions = new LinkedHashMap<>();
//		instance.classesByLevel = new HashMap<>();
//
//		instance.precalculatedSolutions.put(Arrays.toString(new Integer[] { 6, 6, 6 }),
//				new Solution(matches, sol.getStudentClasses(), listClasses, null, -1, false, 60, 2, 2));
//
//		Map<String, String[][]> names = new HashMap<>();
//		names.put("prof1", new String[][] { { "Adr", "ien" }, { "sa", "rah" }, { "san", "dro" }, { "Pi", "R" },
//				{ "A", "a" }, { "B", "b" } });
//		names.put("prof2", new String[][] { { "Pa", "ul" }, { "As", "mun" }, { "vale", "ntin" }, { "Nic", "olas" },
//				{ "C", "c" }, { "D", "d" } });
//		names.put("prof3", new String[][] { { "Marie", "Jo" }, { "Dan", "iel" }, { "Marie", "Agnès" }, { "Rom", "ain" },
//				{ "E", "e" }, { "F", "f" } });
//
//		TournamentAddLevelGroupEvent eventAdd = new TournamentAddLevelGroupEvent(names, 1);
//		instance.onLevelGroupAdded(eventAdd);
//
//		TournamentSolveEvent event = new TournamentSolveEvent(false, 1, 1, 60, 10, true);
//		instance.onSolverCalled(event);
//
//	}

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
