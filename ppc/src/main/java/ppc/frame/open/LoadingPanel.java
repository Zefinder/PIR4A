package ppc.frame.open;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.SolutionFoundEvent;
import ppc.manager.EventManager;

public class LoadingPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2798203968951488959L;

	private JLabel levelLabel;
	private JProgressBar progressBarStudents;
	private JProgressBar progressBarClasses;
	private JButton stopButton;

	public LoadingPanel() {
		EventManager.getInstance().registerListener(this);

		GridLayout gridLayout = new GridLayout(4, 1);
		gridLayout.setVgap(10);
		setLayout(gridLayout);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// TODO: set max panel size
		
		// TODO: specific level
		levelLabel = new JLabel("Niveau X");
		levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(levelLabel);

		progressBarStudents = new JProgressBar();
		progressBarStudents.setStringPainted(true);
		add(progressBarStudents);

		progressBarClasses = new JProgressBar();
		progressBarClasses.setStringPainted(true);
		add(progressBarClasses);

		stopButton = new JButton("Arrêter la recherche");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: stop search
				searchIsStopped();
			}
		});
		add(stopButton);
	}
	
	// TODO: call when final solution found
	private void searchIsStopped() {
		// TODO: specific level
		levelLabel.setText("Niveau X - recherche terminée");
		stopButton.setEnabled(false);
	}

	private void updateProgress(int progressStudents, int progressClasses) {
		progressBarStudents.setValue(progressStudents);
		progressBarClasses.setValue(progressClasses);
	}

	@EventHandler
	public void onSolutionFound(SolutionFoundEvent event) {
		this.updateProgress(event.getStudentsMet() / event.getMaxStudentsMet() * 100,
				event.getClassesMet() / event.getMaxClassesMet() * 100);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Loading Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		LoadingPanel loadingPanel = new LoadingPanel();
		loadingPanel.updateProgress(50, 30);

		frame.getContentPane().add(loadingPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
