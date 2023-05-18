package ppc.projet;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.DocumentException;

import ppc.projet.outputGeneration.PdfGenerator;
import ppc.projet.tournament.LevelThread;
import ppc.projet.tournament.Solution;

public class Main {
	public static void main(String[] args) {
		// works
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16, 17 } };

		// to test maximisation + better maxClassesMet for paul
		// Integer[][] classes = { { 0, 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9, 10, 11 }, {
		// 12, 13, 14, 15, 16 },
		// { 17, 18, 19, 20 }, { 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 } };

		// works with the ghost player!
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16 } };

		// tests paul vs adrien: fonctionne pas pour adrien !
		// Integer[][] classes = { { 0, 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11 }, { 12,
		// 13, 14, 15, 16 }, { 17, 18, 19, 20} };

		// rencontre pélissier marche pas, besoin de soft :
		// Integer[][] classes = { {0, 1, 2, 3}, {4, 5, 6}, {7, 8, 9, 10, 11} };
		
		//niveau 1 rencontre 2019
		Map<String, String[][]> classesLvl1 = new LinkedHashMap<>();
		classesLvl1.put("Prof 1", new String[][]{ {"Anthonin", ""}, {"Lilian", "Nom"}, {"Nathan", ""}, {"Maël", "Tututu"} });
		classesLvl1.put("Prof 2", new String[][]{ {"Gabi", ""}, {"Guney", "hehe"}, {"Izye","oooh"} });
		classesLvl1.put("Prof 3", new String[][]{ {"Louna", ""}, {"Maxine", ""}, {"Enzo", ""}, {"Maéva", ""}, {"Alexandre", ""}, {"Kaissy", ""}, {"Elie", ""} });
		classesLvl1.put("Prof 4", new String[][]{ {"Adam", ""}, {"Nathan", "Lers"}, {"Chloé", "Met"}, {"Jules", "Bol"}, {"Lucile", "hehe"}, {"Brian", ""}, {"Loan", ""}, {"Lohenn", ""} });
		classesLvl1.put("Prof 5", new String[][]{ {"Amaury", ""}, {"Noé", "Larche"}, {"Leïla", ""}, {"Matylio", ""} });

		// niveau 2 rencontre 2019
		Map<String, String[][]> classesLvl2 = new LinkedHashMap<>();
		classesLvl2.put("Prof 1", new String[][]{ {"Valentin", "Gard"}, {"Vaea", ""}, {"Thomas", ""}, {"Manon", ""}, {"Lucas", "Ma"}, {"Jeanne", ""}, {"Juliette", "Rom"}, {"Romane", ""}, {"Agnese", ""}, {"Pauline", "Dau"},
			{"Camille", ""}, {"Loan", ""}, {"Inès", "Lon"}, {"Manon", ""}, {"Elsa", ""}, {"Matéo", "Ban"} });
		classesLvl2.put("Prof 2", new String[][]{ {"Lilou", ""}, {"Lena", "Son"}, {"Lucas", ""}, {"Lilou", "Encore"}, {"Mirtille", ""}, {"Judith", ""}, {"Paul", "Sta"}, {"Pedago", "gique"}, {"Kenji", ""}, {"Tao", ""}, {"Lison", ""},
			{"William", ""}, {"Yaelle", ""}, {"Yanis", ""}, {"Kynian", ""}, {"Sacha", "Duc"}, {"Gauthier", ""}, {"Jules", ""}, {"Erwan", ""} });
		classesLvl2.put("Prof 3", new String[][]{ {"Coraline", ""}, {"Chloé", ""}, {"Loan", ""}, {"Charlot", "te"}, {"Lana", ""}, {"Sans prénom", "sans nom"}, {"Imène", ""}, {"Loris", ""}, {"Benjamin", "Mett"},
			{"Re sans prénom", ""} });
		classesLvl2.put("Prof 4", new String[][]{ {"Rafael", ""}, {"Pablo", "Sanz"}, {"Léna", ""}, {"Rafael", "Nal"}, {"Malon", ""}, {"Lisa", ""}, {"Lino", ""}, {"Lilou", ""}, {"Loane", ""}, {"Bixente", ""} });
		classesLvl2.put("Prof 5", new String[][]{ {"Arnaud", ""}, {"Matili", ""}, {"Lyse", ""}, {"Clément", "Rel"}, {"Julian", ""}, {"Lou", ""}, {"Elise", ""}, {"Turis", ""}, {"Célia", "Daux"},
			{"Sans nom le retour", ""}, {"Maxime", ""} });

		// niveau 3 rencontre 2019
		Map<String, String[][]> classesLvl3 = new LinkedHashMap<>();
		classesLvl3.put("Prof 1", new String[][]{{"Milane", ""}, {"Nathan", ""}, {"Adrien", "Jak"}, {"Léna", ""}, {"Abdellah", ""}});
		classesLvl3.put("Prof 2", new String[][]{{"Céci", "le"}, {"Emma", "Dem"}, {"Luca", ""}, {"Julian", ""}, {"Manon", ""}});
		classesLvl3.put("Prof 3", new String[][]{{"Dine", ""}, {"Augustin", ""}, {"Fay", ""}, {"Nicolas", "Ral"}, {"Louane", ""}, {"Rayan", ""}});
		classesLvl3.put("Prof 4", new String[][]{{"Nayah", ""}, {"Angie", ""}, {"Erika", ""}, {"Ela", ""}, {"Mathias", ""}, {"Périne", ""}, {"Cécile", "Ale"}, {"Maylie", ""}});
		classesLvl3.put("Prof 5", new String[][]{{"Thomas", ""}, {"Maïssa", ""}, {"Inès", ""}, {"Lise", ""}, {"Manon", ""}, {"Maëly", "Di"}, {"Johan", ""}, {"Maïssane", ""}, {"Lili", ""}, {"Ella", ""}});
		
		// tournoi MJ
		// Integer[][] classesLvl1 = { { 0, 1, 2, 3, 4, 5, 6, 7 }, { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 },
		//		{ 20, 21, 22, 23, 24, 25, 26, 27, 28 }, { 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41 },
		//		{ 42, 43, 44, 45, 46, 47, 48 }, { 49, 50, 51, 52, 53, 54 } };
		
		List<Map<String, String[][]>> classesByLevel = new ArrayList<>(Arrays.asList(classesLvl1, classesLvl2, classesLvl3));
		boolean softConstraint = false;
		int timeout = 30;
		int firstTable = 2;
		
		int nbClasses = classesByLevel.get(0).size();
		int level = 1;
		List<Thread> threads = new ArrayList<>();
		List<LevelThread> lvlThreads = new ArrayList<>();
		for (Map<String, String[][]> lvl : classesByLevel) {
			String[][][] lvlClasses = new String[nbClasses][][];
			int classNb = 0;
			for (String[][] classes : lvl.values())
				lvlClasses[classNb++] = classes;
			LevelThread lvlThread = new LevelThread(lvlClasses, level++, softConstraint, timeout, firstTable);
			Thread thread = new Thread(lvlThread);
			thread.start();
			threads.add(thread);
			lvlThreads.add(lvlThread);
		}
		
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int lastLevelWithGhost = -1;
		List<Solution> solutions = new ArrayList<>();
		for (int lvl = 0; lvl < lvlThreads.size(); lvl++) {
			Solution currentSolution = lvlThreads.get(lvl).getSolution();
			solutions.add(currentSolution);
			if (currentSolution.getGhost() != -1)
				lastLevelWithGhost = lvl;
		}

		String[] classNames = classesByLevel.get(0).keySet().toArray(new String[0]);
		PdfGenerator pdfGen = new PdfGenerator(solutions, classNames, nbClasses, lastLevelWithGhost);
		try {
			System.out.print("creating pdf ListeMatches... ");
			pdfGen.createPdfListeMatches();
			System.out.println("done");
			System.out.print("creating pdf ListeClasses... ");
			pdfGen.createPdfListeClasses();
			System.out.println("done");
			System.out.print("creating pdf ListeNiveaux... ");
			pdfGen.createPdfListeNiveaux();
			System.out.println("done");
			System.out.print("creating pdf FicheProf... ");
			pdfGen.createPdfProfs();
			System.out.println("done");
			System.out.print("creating pdf FichesEleves... ");
			pdfGen.createPdfEleves();
			System.out.println("done");
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}
}