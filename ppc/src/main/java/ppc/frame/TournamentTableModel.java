package ppc.frame;

import javax.swing.table.DefaultTableModel;

public class TournamentTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6092983174525590432L;

	public TournamentTableModel() {
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 2)
			return true;
		
		return false;
	}

	
}
