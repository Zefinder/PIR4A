package ppc.tournament.output;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ppc.tournament.solver.Solution;
import ppc.tournament.solver.TournamentSolver;

public class PdfGenerator {

	private List<Solution> solutions;
	private int nbClasses;
	private final int listMatchesColumnNb = 2 + TournamentSolver.NUMBER_MATCHES;
	private String[] classNames;
	private Map<Integer, BaseColor> colourMap = new HashMap<>();
	private int firstTable;
	private int lastLevelWithGhost;
	private boolean needsGhosts = false;

	public PdfGenerator(List<Solution> solutions, String[] classNames, int nbClasses, int firstTable, int lastLevelWithGhost) {
		this.solutions = solutions;
		this.classNames = classNames;
		this.nbClasses = nbClasses;
		this.firstTable = firstTable;
		this.lastLevelWithGhost = lastLevelWithGhost;
		if (lastLevelWithGhost != -1) {
			this.needsGhosts = true;
		}
		this.initColourMap();
	}

	public void initColourMap() {
		colourMap.put(0, new BaseColor(255, 179, 186));
		colourMap.put(1, new BaseColor(186, 225, 255));
		colourMap.put(2, new BaseColor(186, 255, 201));
		colourMap.put(3, new BaseColor(255, 255, 186));
		colourMap.put(4, new BaseColor(195, 177, 225));
		colourMap.put(5, new BaseColor(255, 223, 186));
		colourMap.put(6, new BaseColor(245, 215, 242));
		colourMap.put(7, new BaseColor(191, 204, 181));
		colourMap.put(8, new BaseColor(191, 217, 214));
		colourMap.put(9, new BaseColor(235, 227, 230));

		// Generate random background colours for other classes
		Random rand = new Random();
		float minHue = 0.0f;
		float maxHue = 1.0f;
		float minSaturation = 0.2f;
		float maxSaturation = 0.4f;
		float minBrightness = 0.9f;
		float maxBrightness = 1.0f;
		for (int classNb = 10; classNb < nbClasses; classNb++) {
			float hue = rand.nextFloat() * (maxHue - minHue) + minHue;
			float saturation = rand.nextFloat() * (maxSaturation - minSaturation) + minSaturation;
			float brightness = rand.nextFloat() * (maxBrightness - minBrightness) + minBrightness;
			Color colour = Color.getHSBColor(hue, saturation, brightness);
			BaseColor baseColor = new BaseColor(colour.getRed(), colour.getGreen(), colour.getBlue());
			colourMap.put(classNb, baseColor);
		}
	}

	private void addPdfTitleDate(Document document) throws DocumentException {
		Paragraph title = new Paragraph("Titre rencontre",
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK));
		Paragraph date = new Paragraph(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

		document.add(title);
		document.add(date);
		document.add(Chunk.NEWLINE);
	}

	private PdfPCell formatHeaderCell(String cellText) {
		PdfPCell headerCell = new PdfPCell(new Phrase(cellText));
		headerCell.setBorderWidth(1);
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		return headerCell;
	}

	private void addTableHeader(PdfPTable table) {
		PdfPCell tableCell = formatHeaderCell("Table");
		tableCell.setRowspan(2);
		tableCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		table.addCell(tableCell);

		PdfPCell studentCell = formatHeaderCell("Élève");
		studentCell.setRowspan(2);
		studentCell.setBorderWidthRight(2);
		studentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		table.addCell(studentCell);

		for (int game = 1; game <= TournamentSolver.NUMBER_MATCHES; game++) {
			table.addCell(formatHeaderCell("Partie " + game));
		}

		PdfPCell adversaireCell = formatHeaderCell("Adversaire");
		adversaireCell.setColspan(TournamentSolver.NUMBER_MATCHES);
		table.addCell(adversaireCell);
	}

	private void addGhostTableHeader(PdfPTable ghostTable) {
		ghostTable.addCell(formatHeaderCell("Partie"));
		ghostTable.addCell(formatHeaderCell("Niveau"));

		for (int game = 1; game <= TournamentSolver.NUMBER_MATCHES; game++)
			ghostTable.addCell(formatHeaderCell("Partie " + game));
	}

	public class CrossedOutCellEvent implements PdfPCellEvent {
		@Override
		public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
			PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
			canvas.moveTo(position.getLeft(), position.getBottom());
			canvas.lineTo(position.getRight(), position.getTop());
			canvas.stroke();
			canvas.moveTo(position.getRight(), position.getBottom());
			canvas.lineTo(position.getLeft(), position.getTop());
			canvas.stroke();
		}
	}

	private void addRowsMatches(Solution solution, int nbStudentsPrevLevels, int nbTablesPrevLevels, int level, PdfPTable table,
			PdfPTable ghostTable) {
		int ghost = solution.getGhost();
		if (ghost != -1) {
			PdfPCell studentCell = new PdfPCell(new Phrase("Élève seul(e)"));
			PdfPCell levelCell = new PdfPCell(new Phrase("" + (level + 1)));
			studentCell.setBorderWidth(0.25f);
			levelCell.setBorderWidth(0.25f);
			studentCell.setBorderWidthLeft(1);
			levelCell.setBorderWidthRight(1);
			if (level == lastLevelWithGhost) {
				studentCell.setBorderWidthBottom(1);
				levelCell.setBorderWidthBottom(1);
			}
			ghostTable.addCell(studentCell);
			ghostTable.addCell(levelCell);
		}
		int[] ghostOpponents = new int[TournamentSolver.NUMBER_MATCHES];

		Integer[][] matches = solution.getMatches();
		int firstRealStudent = (solution.getGhost() == -1) ? 0 : 1;
		int offset = (solution.getGhost() == -1) ? 1 : 0;
		for (int student = firstRealStudent; student < matches.length; student++) {
			PdfPCell tableCell = new PdfPCell();
			PdfPCell studentCell = new PdfPCell(new Phrase("" + (student + offset + nbStudentsPrevLevels)));

			tableCell.setBorderWidth(0.25f);
			studentCell.setBorderWidth(0.25f);
			tableCell.setBorderWidthLeft(1);
			if (student <= matches.length / 2 - offset) {
				tableCell.setPhrase(new Phrase("table " + (solution.getIdToTable(student) + nbTablesPrevLevels)));
			}

			if (student == matches.length - 1) {
				tableCell.setBorderWidthBottom(1);
				studentCell.setBorderWidthBottom(1);
			}

			studentCell.setBorderWidthRight(2);
			studentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[student]));

			table.addCell(tableCell);
			table.addCell(studentCell);

			int match = 0;
			for (int game = 0; game < matches[student].length; game++) {
				Integer opponent = matches[student][game];
				PdfPCell opponentCell = new PdfPCell();

				if (opponent != ghost) {
					opponentCell.setPhrase(new Phrase("" + (opponent + offset + nbStudentsPrevLevels)));
					opponentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[opponent]));
				} else {
					opponentCell.setCellEvent(new CrossedOutCellEvent());
					ghostOpponents[match] = student;
				}

				opponentCell.setBorderWidth(0.25f);
				if (game == matches[student].length - 1)
					opponentCell.setBorderWidthRight(1);
				if (student == matches.length - 1)
					opponentCell.setBorderWidthBottom(1);

				table.addCell(opponentCell);
				match++;
			}
		}

		if (ghost != -1) {
			for (int game = 0; game < ghostOpponents.length; game++) {
				int ghostOpponent = ghostOpponents[game];
				PdfPCell ghostCell = new PdfPCell(new Phrase("" + (ghostOpponent + offset + nbStudentsPrevLevels)));
				ghostCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[ghostOpponent]));
				ghostCell.setBorderWidth(0.25f);
				if (game == ghostOpponents.length - 1)
					ghostCell.setBorderWidthRight(1);
				if (level == lastLevelWithGhost)
					ghostCell.setBorderWidthBottom(1);
				ghostTable.addCell(ghostCell);
			}
		}
	}

	public void createPdfListeMatches() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("ListeMatches.pdf"));

		document.open();
		addPdfTitleDate(document);

		PdfPTable ghostTable = new PdfPTable(new float[] { 4, 3, 3, 3, 3, 3, 3, 3 });
		ghostTable.setWidthPercentage(100);
		addGhostTableHeader(ghostTable);

		int nbStudents = 0;
		int nbTables = firstTable;
		for (int level = 0; level < solutions.size(); level++) {
			Solution solution = solutions.get(level);
			PdfPTable table = new PdfPTable(listMatchesColumnNb);
			table.setWidthPercentage(100);
			addTableHeader(table);

			addRowsMatches(solution, nbStudents, nbTables, level, table, ghostTable);

			nbStudents += solution.getMatches().length - ((solution.getGhost() == -1) ? 0 : 1);
			nbTables += solution.getMatches().length / 2;
			Paragraph lvlPar = new Paragraph(
					new Phrase("Niveau " + (level + 1), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
			lvlPar.setSpacingAfter(10);
			document.add(lvlPar);
			document.add(table);
			if (level < solutions.size() - 1)
				document.add(Chunk.NEXTPAGE);
		}

		if (needsGhosts) {
			document.add(Chunk.NEWLINE);
			Paragraph ghostExplanation = new Paragraph("Besoin de joueurs adultes additionnels pour les parties:");
			ghostExplanation.setSpacingAfter(6);
			document.add(ghostExplanation);
			document.add(ghostTable);
		}

		document.close();
	}

	private PdfPTable createClassTable(int classNb) {
		PdfPTable table = new PdfPTable(new float[] { 1, 5, 1 });
		table.setWidthPercentage(100);

		PdfPCell classCell = formatHeaderCell("Classe " + (classNb + 1) + " : " + classNames[classNb]);
		classCell.setColspan(table.getNumberOfColumns());
		classCell.setBackgroundColor(colourMap.get(classNb));
		table.addCell(classCell);

		table.addCell(formatHeaderCell("ID élève"));
		table.addCell(formatHeaderCell("Nom de l'élève"));
		table.addCell(formatHeaderCell("Niveau"));
		return table;
	}

	private void addRowsClasses(List<PdfPTable> classTables, Solution solution, int level, int nbStudentsPrevLevels) {
		Integer[][] listClasses = solution.getListClasses();
		int offset = (solution.getGhost() == -1) ? 1 : 0;
		for (int classNb = 0; classNb < nbClasses; classNb++) {
			for (int student : listClasses[classNb]) {
				PdfPCell idCell = new PdfPCell(new Phrase("" + (student + offset + nbStudentsPrevLevels)));
				String[] name = solution.getIdToName(student);
				PdfPCell nameCell = new PdfPCell(new Phrase(name[0] + " " + name[1]));
				PdfPCell lvlCell = new PdfPCell(new Phrase("" + (level + 1)));

				idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				lvlCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				classTables.get(classNb).addCell(idCell);
				classTables.get(classNb).addCell(nameCell);
				classTables.get(classNb).addCell(lvlCell);
			}
		}
	}

	public void createPdfListeClasses() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("ListeClasses.pdf"));

		document.open();
		addPdfTitleDate(document);

		List<PdfPTable> classTables = new ArrayList<>();
		for (int classNb = 0; classNb < nbClasses; classNb++)
			classTables.add(createClassTable(classNb));

		int nbStudents = 0;
		for (int level = 0; level < solutions.size(); level++) {
			Solution currentSolution = solutions.get(level);
			addRowsClasses(classTables, currentSolution, level, nbStudents);
			nbStudents += currentSolution.getMatches().length - ((currentSolution.getGhost() == -1) ? 0 : 1);
		}

		for (PdfPTable classTable : classTables) {
			document.add(classTable);
			document.add(Chunk.NEXTPAGE);
		}

		document.close();
	}

	private void addRowsNiveaux(PdfPTable table, Solution solution) {
		Integer[][] listClasses = solution.getListClasses();
		for (int student = 0; student < listClasses[0].length; student++) {
			for (int classNb = 0; classNb < nbClasses; classNb++) {
				PdfPCell studentCell = new PdfPCell();
				if (student < listClasses[classNb].length) {
					String[] name = solution.getIdToName(listClasses[classNb][student]);
					studentCell.setPhrase(new Phrase(name[0] + " " + name[1]));
				}
				
				studentCell.setBorderWidth(0.25f);
				if (classNb == nbClasses - 1)
					studentCell.setBorderWidthRight(1);
				if (student == listClasses[0].length - 1)
					studentCell.setBorderWidthBottom(1);
				table.addCell(studentCell);
			}
		}
	}

	public void createPdfListeNiveaux() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("ListeNiveaux.pdf"));

		document.open();
		addPdfTitleDate(document);

		for (int level = 0; level < solutions.size(); level++) {
			Solution lvlSolution = solutions.get(level);

			Paragraph lvlPar = new Paragraph(
					new Phrase("Niveau " + (level + 1), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
			lvlPar.setSpacingAfter(10);
			document.add(lvlPar);

			PdfPTable lvlTable = new PdfPTable(1 + nbClasses);
			lvlTable.setWidthPercentage(100);
			
			PdfPCell classHeader = formatHeaderCell("Classe");
			classHeader.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			lvlTable.addCell(classHeader);
			for (int classNb = 0; classNb < nbClasses; classNb++) {
				PdfPCell classCell = formatHeaderCell("Classe " + (classNb + 1) + " : " + classNames[classNb]);
				classCell.setBackgroundColor(colourMap.get(classNb));
				lvlTable.addCell(classCell);
			}
			PdfPCell studentsCell = formatHeaderCell("Élèves");
			studentsCell.setRowspan(lvlSolution.getListClasses()[0].length);
			studentsCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			lvlTable.addCell(studentsCell);

			addRowsNiveaux(lvlTable, lvlSolution);

			document.add(lvlTable);
			document.add(Chunk.NEXTPAGE);
		}

		document.close();
	}
	
	private PdfPTable createFicheProfTables(int classNb, int level) {
		float[] columns = new float[3 + TournamentSolver.NUMBER_MATCHES];
		columns[0] = 3;
		columns[1] = TournamentSolver.NUMBER_MATCHES * 2;
		for (int col = 2; col < 2 + TournamentSolver.NUMBER_MATCHES; col++)
			columns[col] = 2;
		columns[2 + TournamentSolver.NUMBER_MATCHES] = 3;
		PdfPTable table = new PdfPTable(columns);
		table.setWidthPercentage(100);
		
		PdfPCell classCell = formatHeaderCell("Classe " + (classNb + 1) + " : " + classNames[classNb]);
		classCell.setColspan(table.getNumberOfColumns());
		classCell.setBackgroundColor(colourMap.get(classNb));
		table.addCell(classCell);
		
		PdfPCell lvlCell = formatHeaderCell("Niveau " + (level + 1));
		lvlCell.setColspan(2);
		table.addCell(lvlCell);
		
		PdfPCell gameCell = formatHeaderCell("Parties");
		gameCell.setColspan(6);
		table.addCell(gameCell);
		
		PdfPCell totalCell = formatHeaderCell("Total");
		totalCell.setRowspan(2);
		totalCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		table.addCell(totalCell);
		
		table.addCell(formatHeaderCell("ID"));
		table.addCell(formatHeaderCell("Nom de l'élève"));
		
		for (int game = 0; game < TournamentSolver.NUMBER_MATCHES; game++)
			table.addCell(formatHeaderCell("" + (game + 1)));
		
		return table;
	}
	
	private void addRowsFicheProfTables(List<PdfPTable> lvlTables, Solution solution, int levels, int level, int nbStudentsPrevLevels) {
		for (int classNb = 0; classNb < nbClasses; classNb++) {
			PdfPTable table = lvlTables.get(level + (classNb * levels));
			Integer[] currClass = solution.getListClasses()[classNb];
			for (int i = 0; i < currClass.length; i++) {
				int id = currClass[i];
				PdfPCell idCell = new PdfPCell(new Phrase("" + (id + nbStudentsPrevLevels)));
				String[] name = solution.getIdToName(id);
				PdfPCell nameCell = new PdfPCell(new Phrase(name[0] + " " + name[1]));

				idCell.setBorderWidth(0.25f);
				nameCell.setBorderWidth(0.25f);
				
				if (i == currClass.length - 1) {
					idCell.setBorderWidthBottom(1);
					nameCell.setBorderWidthBottom(1);
				}
				
				idCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				idCell.setBorderWidthLeft(1);
				table.addCell(idCell);
				
				nameCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				table.addCell(nameCell);
				
				for (int empty = 0; empty < TournamentSolver.NUMBER_MATCHES + 1; empty++) {
					PdfPCell emptyCell = new PdfPCell();
					emptyCell.setBorderWidth(0.25f);
					if (i == currClass.length - 1)
						emptyCell.setBorderWidthBottom(1);
					if (empty == TournamentSolver.NUMBER_MATCHES) 
						emptyCell.setBorderWidthRight(1);
					table.addCell(emptyCell);
				}
			}
		}
	}
	
	public void createPdfProfs() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("FichesProfs.pdf"));

		document.open();

		List<PdfPTable> lvlTables = new ArrayList<>();
		for (int classNb = 0; classNb < nbClasses ; classNb++)
			for (int level = 0; level < solutions.size(); level++)
				lvlTables.add(createFicheProfTables(classNb, level));

		int nbStudents = 0;
		for (int level = 0; level < solutions.size(); level++) {
			Solution currentSolution = solutions.get(level);
			addRowsFicheProfTables(lvlTables, currentSolution, solutions.size(), level, nbStudents);
			nbStudents += currentSolution.getMatches().length - ((currentSolution.getGhost() == -1) ? 0 : 1);
		}
		
		int pdfTableNb = 1;
		addPdfTitleDate(document);
		for (PdfPTable classTable : lvlTables) {
			document.add(classTable);
			if (pdfTableNb % solutions.size() == 0 && pdfTableNb < lvlTables.size()) {
				document.add(Chunk.NEXTPAGE);
				addPdfTitleDate(document);
			}
			else
				document.add(Chunk.NEWLINE);
			pdfTableNb++;
		}

		document.close();
	}
	
	private void createStudentTables(Solution solution, List<List<PdfPTable>> studentTablesList, int nbStudentsPrevLevels) {
		float[] columns = new float[3 + TournamentSolver.NUMBER_MATCHES];
		columns[0] = 4;
		columns[1] = 2;
		for (int col = 2; col < 2 + TournamentSolver.NUMBER_MATCHES; col++)
			columns[col] = 1;
		columns[2 + TournamentSolver.NUMBER_MATCHES] = 2;
		
		Integer[][] matches = solution.getMatches();
		int firstRealStudent = (solution.getGhost() == -1) ? 0 : 1;
		int offset = (solution.getGhost() == -1) ? 1 : 0;
		for (int studentId = firstRealStudent; studentId < matches.length; studentId++) {
			PdfPTable table = new PdfPTable(columns);
			table.setWidthPercentage(100);
			
			int studentClass = solution.getStudentClasses()[studentId];
			
			PdfPCell idCell = new PdfPCell(new Phrase("" + (studentId + offset + nbStudentsPrevLevels)));
			idCell.setBackgroundColor(colourMap.get(studentClass));
			idCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			table.addCell(idCell);
			
			table.addCell(new PdfPCell(new Phrase("Partie")));
			
			for (int game = 1; game <= TournamentSolver.NUMBER_MATCHES; game++)
				table.addCell(new PdfPCell(new Phrase("" + game)));
			PdfPCell invisibleCell = new PdfPCell();
			invisibleCell.setBorderWidth(0);
			table.addCell(invisibleCell);
			
			String[] name = solution.getIdToName(studentId);
			PdfPCell nameCell = new PdfPCell(new Phrase(name[0] + " " + name[1]));
			nameCell.setRowspan(3);
			nameCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			nameCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			table.addCell(nameCell);
			
			table.addCell(new PdfPCell(new Phrase("Table")));
//			System.out.println(solution.getMap().toString());
//			System.out.println("First table: " + firstTable);
			for (int game = 0; game < TournamentSolver.NUMBER_MATCHES; game++) {
				if (studentId < matches.length / 2)
					table.addCell(new PdfPCell(new Phrase("" + (solution.getIdToTable(studentId) + firstTable))));
				else
					table.addCell(new PdfPCell(new Phrase("" + (solution.getIdToTable(matches[studentId][game]) + firstTable))));
			}
			table.addCell(invisibleCell);
			
			table.addCell(new PdfPCell(new Phrase("Couleur")));
			for (int game = 0; game < TournamentSolver.NUMBER_MATCHES; game++) {
				PdfPCell colourCell = new PdfPCell();
				if ((studentId < matches.length / 2 && game % 2 == 0) || (studentId >= matches.length / 2 && game % 2 == 1))
					colourCell.setBackgroundColor(BaseColor.BLACK);
				table.addCell(colourCell);
			}
			table.addCell(new PdfPCell(new Phrase("Total")));
			
			table.addCell(new PdfPCell(new Phrase("Résultat")));
			for (int game = 0; game < TournamentSolver.NUMBER_MATCHES + 1; game++)
				table.addCell(new PdfPCell());
			
			studentTablesList.get(studentClass).add(table);
		}
	}
	
	public void createPdfEleves() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("FichesEleves.pdf"));

		document.open();
		List<List<PdfPTable>> studentTablesByClass = new ArrayList<>();
		for (int classNb = 0; classNb < nbClasses; classNb++)
			studentTablesByClass.add(new ArrayList<>());
		
		int nbStudents = 0;
		for (Solution currentSolution : solutions) {
			createStudentTables(currentSolution, studentTablesByClass, nbStudents);
			nbStudents += currentSolution.getMatches().length - ((currentSolution.getGhost() == -1) ? 0 : 1);
		}
		
		for (List<PdfPTable> studentTables : studentTablesByClass) {
			for (PdfPTable table : studentTables) {
				document.add(table);
				document.add(new Paragraph());
				document.add(Chunk.NEWLINE);
			}
			document.add(Chunk.NEXTPAGE);
		}
		
		document.close();
	}
}
