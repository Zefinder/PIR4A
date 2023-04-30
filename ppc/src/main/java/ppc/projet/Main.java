package ppc.projet;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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

		// niveau 2 rencontre 2019
		Integer[][] classesLvl2 = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {12, 13, 14,
								15, 16, 17, 18, 19, 20, 21}, {22, 23, 24, 25, 26, 27}};

		// niveau 3 rencontre 2019
		 Integer[][] classesLvl3 = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, {11, 12, 13, 14,
			 					15, 16, 17, 18, 19, 20, 21, 22}, {23, 24, 25, 26, 27, 28, 29}};
		
		// tournoi MJ
		// Integer[][] classesLvl1 = { { 0, 1, 2, 3, 4, 5, 6, 7 }, { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 },
		//		{ 20, 21, 22, 23, 24, 25, 26, 27, 28 }, { 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41 },
		//		{ 42, 43, 44, 45, 46, 47, 48 }, { 49, 50, 51, 52, 53, 54 } };
		
		Integer[][][] classesByLevel = new Integer[][][] {classesLvl2, classesLvl3};
		boolean softConstraint = true;
		int timeout = 60;
		int nbClasses = 3;
		
		int level = 1;
		List<Thread> threads = new ArrayList<>();
		List<LevelThread> lvlThreads = new ArrayList<>();
		for (Integer[][] lvlClasses : classesByLevel) {
			LevelThread lvlThread = new LevelThread(lvlClasses, level++, softConstraint, timeout);
			Thread thread = new Thread(lvlThread);
			thread.start();
			threads.add(thread);
			lvlThreads.add(lvlThread);
		}
		
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<Solution> solutions = new ArrayList<>();
		for (LevelThread lvlThread : lvlThreads) {
			solutions.add(lvlThread.getSolution());
		}
		
		PdfGenerator pdfGen = new PdfGenerator(solutions, nbClasses);
		try {
			System.out.print("creating pdf... ");
			pdfGen.createPdf();
			System.out.println("done");
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
