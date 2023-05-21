package ppc.tournament;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that parses a CSV file and returns everything in the format used by the
 * tournament solver.
 * 
 * @author Adrien Jakubiak
 *
 */
public class InputFormat {

	/**
	 * <p>
	 * Parse the csv file to a matrix of students divided into classes. <br/>
	 * </p>
	 * <p>
	 * CSV are formatted like
	 * 
	 * <pre>
	 * name - surname - groupNumber
	 * </pre>
	 * </p>
	 * 
	 * @param csvFile the CSV file to parse
	 * @throws IOException    if the file does not exist
	 * @throws ParseException if the file is corrupted
	 */
	public static List<Map<String, String[][]>> parseCsv(File csvFile) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8));

		List<List<String>> csvParsed = new ArrayList<>();

		String line;
		int lineNumber = 1;
		while ((line = reader.readLine()) != null) {
			// If number of double quotes is odd, then the file is corrupted !
			if ((line.chars().filter(value -> value == '"').count() & 0x1) == 1) {
				reader.close();
				throw new ParseException("Parsing has encountered an exception at line " + lineNumber, lineNumber);
			}

			List<String> words = new ArrayList<>();
			String[] splited = line.split(";|,");
			String word = "";
			boolean isQuoted = false;
			for (String split : splited) {
				// If contains quotes then we check multiple things
				if (split.contains("\"")) {
					long quotesCount = split.chars().filter(value -> value == '"').count();

					// If we are not between quotes but it doesn't start with one, then it's an
					// error
					if (!isQuoted && !split.startsWith("\"")) {
						reader.close();
						throw new ParseException("Parsing has encountered an exception at line " + lineNumber,
								lineNumber);
					}

					// If we are not between quotes but it starts with one then it's ok
					if (!isQuoted && split.startsWith("\"")) {
						isQuoted = true;
						split = split.substring(1);
						quotesCount--;
					}

					// From here isQuoted is true
					// If the quotes number is odd then we must check if it ends with a quote
					if ((quotesCount & 1) == 1) {
						// If it finishes by a single quote, it's ok !
						if (split.endsWith("\"")) {
							split = split.substring(0, split.length() - 1);
							quotesCount--;
							isQuoted = false;
						}
						// If quoted and doesn't finish with a quote, then it's an error
						else {
							reader.close();
							throw new ParseException("Parsing has encountered an exception at line " + lineNumber,
									lineNumber);
						}
					}

					// Here the number of quotes is even, but it might be a trap !

					// We replace all "" by "
					split = split.replace("\"\"", "\"");

					// if number of quotes isn't half the number of quotes before, then it's an
					// error
					if (split.chars().filter(value -> value == '"').count() != quotesCount / 2) {
						reader.close();
						throw new ParseException("Parsing has encountered an exception at line " + lineNumber,
								lineNumber);
					}
				}

				// After quotes processing, if we are in quotes, then we don't add the word for
				// now...
				if (isQuoted) {
					word += split + ";";
				}
				// If we aren't then we can add it
				else {
					word += split;
					words.add(word);
					word = "";

				}
			}

			// We add the line to the list of lines
			csvParsed.add(words);
			lineNumber++;
		}
		reader.close();

		return processCsv(csvParsed);
	}

	public static void writeCSV(File csvFile, String[][] elements, String profName) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8));

		writer.append(
				"A GARDER EN FORMAT CSV;;\r\n" + "Nom du prof;" + profName + ";\r\n" + ";;\r\n" + "Prénom;Nom;Niveau\n");

		for (int row = 0; row < elements.length; row++) {
			for (int column = 0; column < elements[row].length; column++) {
				String element = elements[row][column];
				if (element.contains(";") || element.contains("\"")) {
					element = element.replace("\"", "\"\"");
					element = "\"" + element + "\"";
				}

				if (column == elements[row].length - 1) {
					writer.append(element);
				} else
					writer.append(element + ";");
			}
			writer.append("\n");
		}

		writer.close();
	}

	private static List<Map<String, String[][]>> processCsv(List<List<String>> csvParsed) throws ParseException {
		Map<String[], Integer> classes = new LinkedHashMap<>();

		String profName = "";
		int maxGroup = 0;
		int minGroup = Integer.MAX_VALUE;
		int lineNumber = 0;
		for (List<String> line : csvParsed) {
			// First line = prof name
			if (lineNumber == 1)
				profName = line.get(1);

			else if (lineNumber > 3) {
				// If not 3 arguments, error !
				if (line.size() != 3)
					throw new ParseException("Not enough arguments for line in CSV file", 0);

				// We get the level and check for max level
				int groupNumber = Integer.valueOf(line.get(2));
				if (groupNumber > maxGroup)
					maxGroup = groupNumber;

				if (groupNumber < minGroup)
					minGroup = groupNumber;

				classes.put(new String[] { line.get(0), line.get(1) }, groupNumber);
			}

			lineNumber++;
		}

		List<Map<String, String[][]>> listMap = new ArrayList<>();
		List<List<String[]>> namesList = new ArrayList<>();

		// We init structures
		for (int i = minGroup; i <= maxGroup; i++) {
			listMap.add(new LinkedHashMap<>());
			namesList.add(new ArrayList<>());
		}

		// For each person we add him into the good list
		for (Map.Entry<String[], Integer> entry : classes.entrySet()) {
			String[] key = entry.getKey();
			Integer val = entry.getValue() - minGroup;
			namesList.get(val).add(key);
		}

		String[][][] studentsName = namesList.stream().map(l -> l.stream().toArray(String[][]::new))
				.toArray(String[][][]::new);

		// Putting it into the good map
		for (int i = 0; i < studentsName.length; i++) {
			listMap.get(i).put(profName, studentsName[i]);
		}

		return listMap;
	}

}