package ppc.projet.tournament;

public class Solution {
	
	private Integer[][] matches;
	private int[] studentClasses;
	private int ghost;
	
	public Solution(Integer[][] matches, int[] studentClasses, int ghost) {
		this.matches = matches;
		this.studentClasses = studentClasses;
		this.ghost = ghost;
	}
	
	public Integer[][] getMatches() {
		return this.matches;
	}
	
	public int[] getStudentClasses() {
		return this.studentClasses;
	}
	
	public int getGhost() {
		return this.ghost;
	}
}
