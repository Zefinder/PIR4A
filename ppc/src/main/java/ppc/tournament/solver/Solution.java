package ppc.tournament.solver;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Stores information about the solution found. Assigns half of the students to
 * a permanent table.
 * </p>
 * 
 * @author Sarah Mousset
 *
 */
public class Solution {

	private Integer[][] matches;
	private int[] studentClasses;
	private Integer[][] listClasses;

	private Map<Integer, Integer> idToTable;
	private Map<Integer, String[]> idToName;
	private Integer[] classesConfiguration;

	private int ghost;
	private boolean softConstraint;
	private double runtime;
	private int studentsMet;
	private int maxStudentsMet;
	private int classesMet;
	private int maxClassesMet;
	private int table;

	public Solution(Integer[][] matches, int[] studentClasses, Integer[][] listClasses, Map<Integer, String[]> idToName,
			int ghost, boolean softConstraint, double runtime, int studentsMet, int maxStudentsMet, int classesMet,
			int maxClassesMet) {
		this.matches = matches;
		this.studentClasses = studentClasses;
		this.listClasses = listClasses;
		this.idToName = idToName;
		this.ghost = ghost;
		this.softConstraint = softConstraint;
		this.runtime = runtime;
		this.maxStudentsMet = maxStudentsMet;
		this.maxClassesMet = maxClassesMet;
		this.studentsMet = studentsMet;
		this.classesMet = classesMet;
	}

	private void initIdToTable() {
		this.idToTable = new HashMap<>();
		int firstStudent = (this.ghost == -1) ? 0 : 1;
		int offset = (this.ghost == -1) ? 1 : 0;
		table = 0;
		for (int student = firstStudent; student <= matches.length / 2 - offset; student++)
			this.idToTable.put(student, table++);
	}
	
	public int lastTable() {
		return table;
	}

	public Integer[] getClassesConfiguration() {
		if (this.classesConfiguration == null) {
			classesConfiguration = new Integer[listClasses.length];
			for (int classNb = 0; classNb < listClasses.length; classNb++) {
				classesConfiguration[classNb] = listClasses[classNb].length;
			}
			Arrays.sort(classesConfiguration, Collections.reverseOrder());
		}
		return this.classesConfiguration;
	}

	public Integer[][] getMatches() {
		return this.matches;
	}

	public int[] getStudentClasses() {
		return this.studentClasses;
	}

	public Integer[][] getListClasses() {
		return this.listClasses;
	}

	/**
	 * Returns the table number to which is associated a student id. The student id
	 * must be in the first half of the students.
	 * 
	 * @param id the student id
	 * @return the table to which is associated the student id
	 */
	public int getIdToTable(int id) {
		if (this.idToTable == null)
			this.initIdToTable();
		return this.idToTable.get(id);
	}

	public Map<Integer, Integer> getMap() {
		return idToTable;
	}

	public void setIdToName(Map<Integer, String[]> idToName) {
		this.idToName = idToName;
	}

	public String[] getIdToName(int id) {
		return this.idToName.get(id);
	}

	public int getGhost() {
		return this.ghost;
	}

	public boolean isSoftConstraint() {
		return softConstraint;
	}

	public double getRuntime() {
		return runtime;
	}

	public int getStudentsMet() {
		return studentsMet;
	}
	
	public int getMaxStudentsMet() {
		return maxStudentsMet;
	}

	public int getClassesMet() {
		return classesMet;
	}
	
	public int getMaxClassesMet() {
		return maxClassesMet;
	}
}
