package ppc.frame.open;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.SolutionFoundEvent;
import ppc.event.TournamentSolveEvent;

public class SolutionSearchDialog extends JDialog implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4843477171080105523L;
	
	// just to test, not needed
	public static List<LoadingPanel> loadingPanels = new ArrayList<>();
	
	public SolutionSearchDialog() {
		//EventManager.getInstance().registerListener(this);
		setModal(true);
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
		revalidate();
        repaint();
	}
	
	public static void main(String[] args) throws InterruptedException {
		SolutionSearchDialog dialog = new SolutionSearchDialog();

		TournamentSolveEvent event = new TournamentSolveEvent(7, false, 70, 100, 50, 0, false);
		dialog.onTournamentSolve(event);
		
		dialog.setLocationRelativeTo(null);
		dialog.pack();
		dialog.setVisible(true);
		
		Thread.sleep(2000);

		SolutionFoundEvent solutionEvent = new SolutionFoundEvent(1, 70, 100, 50, 100);
		loadingPanels.get(1).onSolutionFound(solutionEvent);

		Thread.sleep(2000);

		solutionEvent = new SolutionFoundEvent(1, 80, 100, 86, 100);
		loadingPanels.get(1).onSolutionFound(solutionEvent);

		Thread.sleep(2000);

		solutionEvent = new SolutionFoundEvent(0, 90, 100, 20, 100);
		loadingPanels.get(0).onSolutionFound(solutionEvent);
		
		Thread.sleep(5000);
		dialog.dispose();
		System.out.println("done :)");
	}
}
