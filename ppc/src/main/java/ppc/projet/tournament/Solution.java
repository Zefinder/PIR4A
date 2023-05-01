package ppc.projet.tournament;

import java.util.HashMap;
import java.util.Map;

public class Solution {
	
	private Integer[][] matches;
	private int[] studentClasses;
	private Integer[][] listClasses;
	private Map<Integer, Integer> idToTable;
	private Map<Integer, String> idToName;
	private int ghost;
	
	public Solution(Integer[][] matches, int[] studentClasses, Integer[][] listClasses, Map<Integer, String> idToName, int ghost) {
		this.matches = matches;
		this.studentClasses = studentClasses;
		this.listClasses = listClasses;
		this.idToName = idToName;
		this.ghost = ghost;
		this.initIdToTable();
	}
	
	private void initIdToTable() {
		this.idToTable = new HashMap<>();
		int firstStudent = (this.ghost == -1) ? 0 : 1;
		int offset = (this.ghost == -1) ? 1 : 0;
		int table = 1;
		for (int student = firstStudent; student <= matches.length / 2 - offset; student++)
			this.idToTable.put(student, table++);
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
	
	public int getIdToTable(int id) {
		return this.idToTable.get(id);
	}
	
	public String getIdToName(int id) {
		return this.idToName.get(id);
	}
	
	public int getGhost() {
		return this.ghost;
	}
}
