package ppc.frame.open;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.solver.FinalSolutionFoundEvent;
import ppc.event.solver.SolutionFoundEvent;
import ppc.event.solver.StopSearchEvent;
import ppc.event.solver.TournamentSolveImpossibleEvent;
import ppc.manager.EventManager;

public class LoadingPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2798203968951488959L;

	private int level;

	private JLabel levelLabel;
	private JProgressBar progressBarStudents;
	private JProgressBar progressBarClasses;
	private JButton stopButton;

	public LoadingPanel(int level) {
		EventManager.getInstance().registerListener(this);
		this.level = level;

		GridLayout gridLayout = new GridLayout(4, 1);
		gridLayout.setVgap(10);
		setLayout(gridLayout);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		levelLabel = new JLabel("Niveau " + (level + 1));
		levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(levelLabel);

		progressBarStudents = new JProgressBar();
		progressBarStudents.setStringPainted(true);
		progressBarStudents.setString(String.format("Élèves: 0%%"));
		progressBarStudents.setToolTipText("Maximisation des élèves rencontrés");
		add(progressBarStudents);

		progressBarClasses = new JProgressBar();
		progressBarClasses.setStringPainted(true);
		progressBarClasses.setString(String.format("Classes: 0%%"));
		progressBarClasses.setToolTipText("Maximisation des classes rencontrées");
		add(progressBarClasses);

		stopButton = new JButton("Arrêter la recherche");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(null,
						"Êtes-vous sûr de vouloir arrêter la recherche pour le niveau " + (level + 1) + " ?",
						"Confirmation", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					System.out.println("Stopping search for level " + level);
					EventManager.getInstance().callEvent(new StopSearchEvent(level));
					searchIsStopped();
				}
			}
		});
		add(stopButton);
	}

	private void searchIsStopped() {
		levelLabel.setText("Niveau " + (level + 1) + " - terminé");
		stopButton.setEnabled(false);
	}

	private void updateProgress(float progressStudents, float progressClasses) {
		progressBarStudents.setString(String.format("Élèves: %.2f%%", progressStudents));
		progressBarStudents.setValue((int) progressStudents);
		progressBarClasses.setString(String.format("Classes: %.2f%%", progressClasses));
		progressBarClasses.setValue((int) progressClasses);
	}

	@EventHandler
	public void onSolutionFound(SolutionFoundEvent event) {
		if (event.getLevel() == this.level) {
			this.updateProgress((float) event.getStudentsMet() / event.getMaxStudentsMet() * 100,
					(float) event.getClassesMet() / event.getMaxClassesMet() * 100);
			repaint();
		}
	}

	@EventHandler
	public void onFinalSolutionFound(FinalSolutionFoundEvent event) {
		if (event.getLevel() == this.level) {
			this.updateProgress((float) event.getStudentsMet() / event.getMaxStudentsMet() * 100,
					(float) event.getClassesMet() / event.getMaxClassesMet() * 100);
			searchIsStopped();
		}
	}
	
	@EventHandler
	public void onImpossibleLevel(TournamentSolveImpossibleEvent event) {
		if (event.getLevel() == this.level)
			searchIsStopped();
	}
}
