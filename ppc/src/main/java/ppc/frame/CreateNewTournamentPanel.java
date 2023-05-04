package ppc.frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ppc.event.TournamentCreateEvent;
import ppc.manager.EventManager;
import ppc.manager.LogsManager;
import ppc.manager.SettingsManager;

public class CreateNewTournamentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3391834139143580886L;

	private JTextField tournamentName;
	private JTextField timeValue;
	private JTextField studentsValue;
	private JTextField classesValue;

	public CreateNewTournamentPanel() {

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(8, 8, 8, 8);

		c.gridx = 0;
		c.gridy = 0;
		JPanel panel = createFormPanel();
		this.add(panel, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton button = new JButton("Créer le tournoi");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = tournamentName.getText();
				String timeString = timeValue.getText();
				String studentThresholdString = studentsValue.getText();
				String classesThresholdString = classesValue.getText();

				if (name.isBlank()) {
					LogsManager.getInstance().writeErrorMessage("Tournament's name cannot be empty");
					JOptionPane.showMessageDialog(null, "Le nom du tournoi ne peut pas être vide !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				int time;
				try {
					time = Integer.valueOf(timeString);
				} catch (Exception e1) {
					LogsManager.getInstance().writeErrorMessage("Error when parsing searching time...");
					JOptionPane.showMessageDialog(null, "Impossible de lire la valeur du temps de recherche", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				studentThresholdString = studentThresholdString.substring(0, studentThresholdString.length() - 1);
				float studentThreshold;
				try {
					studentThreshold = Float.valueOf(studentThresholdString);
				} catch (Exception e1) {
					LogsManager.getInstance().writeErrorMessage("Error when parsing student's threshold...");
					JOptionPane.showMessageDialog(null, "Impossible de lire la valeur de seuil d'étudiants", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				studentThreshold /= 100f;

				classesThresholdString = classesThresholdString.substring(0, classesThresholdString.length() - 1);
				float classesThreshold;
				try {
					classesThreshold = Float.valueOf(classesThresholdString);
				} catch (Exception e1) {
					LogsManager.getInstance().writeErrorMessage("Error when parsing classes threshold...");
					JOptionPane.showMessageDialog(null, "Impossible de lire la valeur de seuil de classes", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				classesThreshold /= 100f;

				TournamentCreateEvent event = new TournamentCreateEvent(name, time, studentThreshold, classesThreshold);
				EventManager.getInstance().callEvent(event);
			}
		});
		this.add(button, c);
	}

	private JPanel createFormPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Create a new tournament"));

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(8, 8, 8, 8);

		c.gridx = 0;
		c.gridy = 0;
		JLabel tournamentLabel = new JLabel("Nom du tournoi");
		panel.add(tournamentLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		tournamentName = new JTextField(15);
		panel.add(tournamentName, c);

		c.gridx = 0;
		c.gridy = 1;
		JLabel timeLabel = new JLabel("Temps max de recherche (en s)");
		panel.add(timeLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		timeValue = new JTextField(String.valueOf(SettingsManager.getInstance().getMaxTime()), 10);
		panel.add(timeValue, c);

		c.gridx = 0;
		c.gridy = 2;
		JLabel studentsLabel = new JLabel("Seuil d'élèves différents rencontrés (en %)");
		panel.add(studentsLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		studentsValue = new JTextField(
				String.valueOf(SettingsManager.getInstance().getStudentsMetThreshold() * 100 + "%"), 10);
		panel.add(studentsValue, c);

		c.gridx = 0;
		c.gridy = 3;
		JLabel classesLabel = new JLabel("Seuil de classes différentes rencontrées (en %)");
		panel.add(classesLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		classesValue = new JTextField(
				String.valueOf(SettingsManager.getInstance().getClassesMetThreshold() * 100 + "%"), 10);
		panel.add(classesValue, c);

		return panel;
	}

}
