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
			table.addCell(formatHeaderCell("Ronde " + game));
		}
		
		PdfPCell adversaireCell = formatHeaderCell("Adversaire");
		adversaireCell.setColspan(Tournament.NUMBER_MATCHES);
		table.addCell(adversaireCell);
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
	
	private void addRows(PdfPTable table) {
		int level = 1;
		int nbStudentsPrevLevels = 0;
		for (Solution solution : solutions) {
			PdfPCell levelSeparator = new PdfPCell(new Phrase("Niveau " + level++));
			levelSeparator.setHorizontalAlignment(Element.ALIGN_CENTER);
			levelSeparator.setBackgroundColor(BaseColor.LIGHT_GRAY);
			levelSeparator.setColspan(columnNumber);
			table.addCell(levelSeparator);
			
			Integer[][] matches = solution.getMatches();
			int firstRealStudent = (solution.getGhost() == -1) ? 0 : 1;
			int offset = (solution.getGhost() == -1) ? 1 : 0;
			int nbStudentsCurrLevel = 0;
			for (int student = firstRealStudent; student < matches.length; student++) {
				nbStudentsCurrLevel++;
				table.addCell("table");
				PdfPCell studentCell = new PdfPCell(new Phrase("" + (student + offset + nbStudentsPrevLevels)));
				studentCell.setBorderWidthRight(1);
				studentCell.setBorderWidthTop(0);
				studentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[student]));
				table.addCell(studentCell);
				
				for (Integer opponent : matches[student]) {
					PdfPCell opponentCell = new PdfPCell();
					if (opponent != solution.getGhost()) {
						opponentCell.setPhrase(new Phrase("" + (opponent + offset + nbStudentsPrevLevels)));
						opponentCell.setBackgroundColor(colourMap.get(solution.getStudentClasses()[opponent]));
					}
					else {
						opponentCell.setCellEvent(new CrossedOutCellEvent());
					}
					table.addCell(opponentCell);
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
		addTableHeader(table);
		addRows(table);
		
		document.add(title);
		document.add(date);
		document.add(Chunk.NEWLINE);
		document.add(table);
		document.close();
	}
}
