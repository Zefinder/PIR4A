package ppc.frame.open;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.TournamentSolveEvent;
import ppc.manager.EventManager;

public class SolutionSearchDialog extends JDialog implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4843477171080105523L;

	// just to test, not needed
	private List<LoadingPanel> loadingPanels = new ArrayList<>();

	public SolutionSearchDialog() {
		EventManager.getInstance().registerListener(this);
		setModal(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@EventHandler
	public void onTournamentSolve(TournamentSolveEvent event) {
		int nbLevels = event.getNbLevels();
		this.setLayout(new GridLayout((int) Math.ceil((double) nbLevels / 3), 3));
		for (int level = 0; level < nbLevels; level++) {
			LoadingPanel levelPanel = new LoadingPanel(level);
			add(levelPanel);
			loadingPanels.add(levelPanel);
		}

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		revalidate();
		repaint();
	}
}
