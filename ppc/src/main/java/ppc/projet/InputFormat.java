package ppc.projet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputFormat {
	public static void main(String[] args) throws IOException, ParseException {
		File csv = new File(args[0]);
		System.out.println(Arrays.deepToString(parseCsv(csv)));
	}

	/**
	 * <p>
	 * Parse the csv file to a matrix of students divided into classes. <br/>
	 * </p>
	 * <p>
	 * CSV are formatted like
	 * 
	 * <pre>
	 * name - surname - classNumber
	 * </pre>
	 * </p>
	 * 
	 * @param csvFile the CSV file to parse
	 * @throws IOException    if the file does not exist
	 * @throws ParseException if the file is corrupted
	 */
	public static Integer[][] parseCsv(File csvFile) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader(csvFile));

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
			String[] splited = line.split(",|;");
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

	private static Integer[][] processCsv(List<List<String>> csvParsed) throws ParseException {
		List<Integer> classes = new ArrayList<>();

		int maxClass = 0;
		for (List<String> line : csvParsed) {
			if (line.size() != 3)
				throw new ParseException("Not enough arguments for line in CSV file", 0);

			int classId = Integer.valueOf(line.get(2));
			if (classId > maxClass)
				maxClass = classId;

			classes.add(classId);
		}

		List<List<Integer>> listslist = new ArrayList<>();
		for (int i = 0; i < maxClass + 1; i++)
			listslist.add(new ArrayList<>());

		for (int i = 0; i < classes.size(); i++) {
			listslist.get(classes.get(i)).add(i);
		}

		Integer[][] classesArray = listslist.stream().map(l -> l.stream().toArray(Integer[]::new))
				.toArray(Integer[][]::new);

		return classesArray;

	}

}
