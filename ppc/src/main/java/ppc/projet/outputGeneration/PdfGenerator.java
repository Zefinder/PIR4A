package ppc.projet.outputGeneration;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
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

import ppc.projet.tournament.Solution;
import ppc.projet.tournament.Tournament;

public class PdfGenerator {
	
	private List<Solution> solutions;
	private final int columnNumber = 2 + Tournament.NUMBER_MATCHES;
	private Map<Integer, BaseColor> colourMap = new HashMap<>();
	private boolean needsGhosts = false;

	public PdfGenerator(List<Solution> solutions, int nbClasses) {
		this.solutions = solutions;
		this.initColourMap(nbClasses);
	}
	
	public void initColourMap(int nbClasses) {
		colourMap.put(0, new BaseColor(255, 179, 186));
		colourMap.put(1, new BaseColor(186, 225, 255));
		colourMap.put(2, new BaseColor(186, 255, 201));
		colourMap.put(3, new BaseColor(255, 255, 186));
		colourMap.put(4, new BaseColor(255, 223, 186));
		colourMap.put(5, new BaseColor(195, 177, 225));
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
		studentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		table.addCell(studentCell);
		
		for (int game = 1; game <= Tournament.NUMBER_MATCHES; game++) {
			table.addCell(formatHeaderCell("Partie " + game));
		}
		
		PdfPCell adversaireCell = formatHeaderCell("Adversaire");
		adversaireCell.setColspan(Tournament.NUMBER_MATCHES);
		table.addCell(adversaireCell);
	}
	
	private void addGhostTableHeader(PdfPTable ghostTable) {
		ghostTable.addCell(formatHeaderCell("Partie"));
		ghostTable.addCell(formatHeaderCell("Niveau"));
		
		for (int game = 1; game <= Tournament.NUMBER_MATCHES; game++) {
			ghostTable.addCell(formatHeaderCell("Partie " + game));
		}
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
	
	private void addRows(PdfPTable table, PdfPTable ghostTable) {
		int level = 0;
		int nbStudentsPrevLevels = 0;
		for (Solution solution : solutions) {
			PdfPCell levelSeparator = new PdfPCell(new Phrase("Niveau " + ++level));
			levelSeparator.setHorizontalAlignment(Element.ALIGN_CENTER);
			levelSeparator.setBackgroundColor(BaseColor.LIGHT_GRAY);
			levelSeparator.setColspan(columnNumber);
			table.addCell(levelSeparator);
			
			int ghost = solution.getGhost();
			if (ghost != -1) {
				needsGhosts = true;
				ghostTable.addCell(new PdfPCell(new Phrase("Élève seul(e)")));
				PdfPCell levelCell = new PdfPCell(new Phrase("" + level));
				levelCell.setBorderWidthRight(1);
				ghostTable.addCell(levelCell);
			}
			int[] ghostOpponents = new int[Tournament.NUMBER_MATCHES];
			
			Integer[][] matches = solution.getMatches();
			int firstRealStudent = (solution.getGhost() == -1) ? 0 : 1;
			int offset = (solution.getGhost() == -1) ? 1 : 0;
			int nbStudentsCurrLevel = 0;
			int tableNb = 1;
			for (int student = firstRealStudent; student < matches.length; student++) {
				nbStudentsCurrLevel++;
				
				if (student <= matches.length / 2 - offset)
					table.addCell("table " + (tableNb++));
				else 
					table.addCell("bouge");
				
				PdfPCell studentCell = new PdfPCell(new Phrase("" + (student + offset + nbStudentsPrevLevels)));
				studentCell.setBorderWidthRight(1);
				studentCell.setBorderWidthTop(0);
				studentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[student]));
				table.addCell(studentCell);
				
				int match = 0;
				for (Integer opponent : matches[student]) {
					PdfPCell opponentCell = new PdfPCell();
					if (opponent != ghost) {
						opponentCell.setPhrase(new Phrase("" + (opponent + offset + nbStudentsPrevLevels)));
						opponentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[opponent]));
					}
					else {
						opponentCell.setCellEvent(new CrossedOutCellEvent());
						ghostOpponents[match] = student;
					}
					table.addCell(opponentCell);
					match++;
				}
			}
			
			if (ghost != -1) {
				for (int ghostOpponent : ghostOpponents) {
					PdfPCell ghostCell = new PdfPCell(new Phrase("" + (ghostOpponent + offset + nbStudentsPrevLevels)));
					ghostCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[ghostOpponent]));
					ghostTable.addCell(ghostCell);
				}
			}
			nbStudentsPrevLevels += nbStudentsCurrLevel;
		}
	}
	
	public void createPdf() throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("ListeMatches.pdf"));

		document.open();
		Paragraph title = new Paragraph("Titre rencontre", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK));
		Paragraph date = new Paragraph(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		
		PdfPTable table = new PdfPTable(columnNumber);
        table.setWidthPercentage(100);
        PdfPTable ghostTable = new PdfPTable(new float[] { 4, 3, 3, 3, 3, 3, 3, 3 });
        ghostTable.setWidthPercentage(100);
        
		addTableHeader(table);
		addGhostTableHeader(ghostTable);
		addRows(table, ghostTable);
		
		document.add(title);
		document.add(date);
		document.add(Chunk.NEWLINE);
		document.add(table);
		
		if (needsGhosts) {
			document.add(Chunk.NEWLINE);
			Paragraph ghostExplanation = new Paragraph("Besoin de joueurs adultes additionnels pour les parties:");
			ghostExplanation.setSpacingAfter(6);
			document.add(ghostExplanation);
			document.add(ghostTable);
		}
		
		document.close();
	}
}
