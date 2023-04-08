package ppc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ppc.projet.InputFormat;

class CSVFormatting {

	private static final File CORRECT_FILE = new File("src/test/java/ppc/res/parseOk.csv");
	private static final File WRONG_FILE = new File("src/test/java/ppc/res/parseError.csv");
	private static final File WRONG_FILE_PROVIDER = new File("src/test/java/ppc/res/parseError.txt");

	@BeforeAll
	public static void init() throws Exception {
		if (!CORRECT_FILE.exists())
			throw new Exception("No correct file to test parser!");

		if (WRONG_FILE.exists())
			WRONG_FILE.delete();
	}

	@Test
	public void parserOkTest() throws IOException, ParseException {
		Integer[][] classes = InputFormat.parseCsv(CORRECT_FILE);
		Integer[][] expected = { { 0, 7, 8, 9 }, { 1 , 10 }, { 2 }, { 4 }, { 5 }, { 3 }, {}, { 6 } };

		assertArrayEquals(expected, classes);
	}

	@ParameterizedTest
	@MethodSource("parsingErrorProvider")
	public void errorDuplicatedNamesTest(String incorrectLine) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(WRONG_FILE));
		writer.write(incorrectLine);
		writer.close();

		assertThrows(ParseException.class, () -> InputFormat.parseCsv(WRONG_FILE));
	}

	@SuppressWarnings("resource")
	private static Stream<String> parsingErrorProvider() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(WRONG_FILE_PROVIDER));
		Stream<String> stream = reader.lines();

		return stream;
	}

	@AfterAll
	public static void cleanUp() {
		WRONG_FILE.delete();
	}
}
