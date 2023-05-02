package ppc.projet.analysis;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomProblem {
	
	private int randomFactor;
	private Integer[][] prevClasses;
	private Integer[][] classes;

	public RandomProblem(int randomFactor, Integer[][] prevClasses) {
		this.randomFactor = randomFactor;
		this.prevClasses = prevClasses;
		this.generateClasses();
	}
	
	public Integer[][] getClasses() {
		return this.classes;
	}
	
	private void generateClasses() {
		classes = new Integer[prevClasses.length][];
		
		int studentId = 0;
		for (int classNb = 0; classNb < prevClasses.length; classNb++) {
			int classSize = 0;
			while (classSize < 1) {
				// randomAdd is the number of students to add between -randomFactor and +randomFactor
			    int randomAdd = new Random().nextInt((2 * randomFactor) + 1) - randomFactor;
			    classSize = prevClasses[classNb].length + randomAdd;
			}
		    int[] newClass = IntStream.rangeClosed(studentId, studentId + classSize - 1).toArray();
		    classes[classNb] = Arrays.stream(newClass).boxed().toArray(Integer[]::new);
		    studentId += classSize;
		}
	}
}
