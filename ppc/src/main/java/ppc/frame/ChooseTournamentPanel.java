package ppc.frame;

import javax.swing.JList;
import javax.swing.JPanel;

public class ChooseTournamentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1288814079619469030L;
	
	public ChooseTournamentPanel() {
		JList<String> list = new JList<>(new String[]{"A", "B", "C", "D"});
		list.setCellRenderer(new TournamentListRenderer());
		this.add(list);
	}

}
