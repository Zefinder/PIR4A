package ppc.frame.choose;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ppc.manager.SettingsManager;

public class SettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726322560210668868L;
	
	private JTextField tournamentName;
	private JTextField matchesValue;
	private JTextField levelsValue;
	private JTextField timeValue;
	private JTextField studentsValue;
	private JTextField classesValue;

	public SettingsPanel() {
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
		this.add(button, c);
		
	}
	
	private JPanel createFormPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(8, 8, 8, 8);

		JPanel generalSettingsPanel = new JPanel();
		generalSettingsPanel.setLayout(new GridBagLayout());
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel tournamentLabel = new JLabel("Nom du tournoi");
		generalSettingsPanel.add(tournamentLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		tournamentName = new JTextField(15);
		generalSettingsPanel.add(tournamentName, c);
		generalSettingsPanel.setBorder(BorderFactory.createTitledBorder("Paramètres généraux"));

		JPanel defaultValuesPanel = new JPanel();
		defaultValuesPanel.setLayout(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		JLabel matchesLabel = new JLabel("Nombre de parties");
		defaultValuesPanel.add(matchesLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		matchesValue = new JTextField("6");
		defaultValuesPanel.add(matchesValue, c);

		c.gridx = 0;
		c.gridy = 1;
		JLabel levelsLabel = new JLabel("Nombre de groupes");
		defaultValuesPanel.add(levelsLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		levelsValue = new JTextField("3");
		defaultValuesPanel.add(levelsValue, c);

		c.gridx = 0;
		c.gridy = 2;
		JLabel timeLabel = new JLabel("Temps max de recherche (en s)");
		defaultValuesPanel.add(timeLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		timeValue = new JTextField(String.valueOf(SettingsManager.getInstance().getMaxTime()), 10);
		defaultValuesPanel.add(timeValue, c);

		c.gridx = 0;
		c.gridy = 3;
		JLabel studentsLabel = new JLabel("Seuil d'élèves différents rencontrés (en %)");
		defaultValuesPanel.add(studentsLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		studentsValue = new JTextField(
				String.valueOf(SettingsManager.getInstance().getStudentsMetThreshold() * 100 + "%"), 10);
		defaultValuesPanel.add(studentsValue, c);

		c.gridx = 0;
		c.gridy = 4;
		JLabel classesLabel = new JLabel("Seuil de classes différentes rencontrées (en %)");
		defaultValuesPanel.add(classesLabel, c);

		c.gridx = 1;
		c.gridy = 4;
		classesValue = new JTextField(
				String.valueOf(SettingsManager.getInstance().getClassesMetThreshold() * 100 + "%"), 10);
		defaultValuesPanel.add(classesValue, c);
		defaultValuesPanel.setBorder(BorderFactory.createTitledBorder("Valeurs de création de tournoi par défaut"));

		c.gridx = 0;
		c.gridy = 0;
		panel.add(generalSettingsPanel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		panel.add(defaultValuesPanel, c);
		
		return panel;
	}
	
}
