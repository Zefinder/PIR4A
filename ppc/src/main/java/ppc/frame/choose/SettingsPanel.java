package ppc.frame.choose;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.mainpanel.SettingsChangeEvent;
import ppc.event.mainpanel.TournamentCreationStatusEvent;
import ppc.event.mainpanel.TournamentRemoveEvent;
import ppc.event.mainpanel.TournamentRemovingStatusEvent;
import ppc.manager.EventManager;
import ppc.manager.SettingsManager;
import ppc.manager.TournamentManager;

public class SettingsPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726322560210668868L;

	private JTextField resultsPath;
	private JButton choosePath;
	private JComboBox<String> colorBoxes;
	private JComboBox<String> removeTournament;
	private JCheckBox newFolderOnCopy;
	private JTextField matchesValue;
	private JTextField levelsValue;
	private JTextField timeValue;
	private JTextField studentsValue;
	private JTextField classesValue;

	public SettingsPanel() {
		EventManager.getInstance().registerListener(this);

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
		JButton button = new JButton("Sauvegarder les changements");
		button.addActionListener(e -> changeSettings());
		this.add(button, c);

		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}

	@EventHandler
	public void onTournamentCreated(TournamentCreationStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS)
			removeTournament.addItem(event.getTournamentName());
	}

	@EventHandler
	public void onTournamentRemoved(TournamentRemovingStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS)
			removeTournament.removeItem(event.getTournamentName());
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
		JLabel resultsLabel = new JLabel("Chemin du dossier résultats");
		generalSettingsPanel.add(resultsLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		resultsPath = new JTextField(20);
		resultsPath.setEditable(false);
		resultsPath.setText(SettingsManager.getInstance().getResultsPath());
		generalSettingsPanel.add(resultsPath, c);

		c.gridx = 2;
		c.gridy = 0;
		choosePath = new JButton("...");
		choosePath.addActionListener(e -> searchNewResultsFolder());

		generalSettingsPanel.add(choosePath, c);

		c.gridx = 0;
		c.gridy = 1;
		JLabel newFolderLabel = new JLabel("Créer un dossier avant de copier les fichiers");
		generalSettingsPanel.add(newFolderLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		newFolderOnCopy = new JCheckBox();
		newFolderOnCopy.setSelected(SettingsManager.getInstance().createFolderWhenCopy());
		newFolderOnCopy.setOpaque(false);
		newFolderOnCopy.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				getTopLevelAncestor().repaint();
			}
		});

		generalSettingsPanel.add(newFolderOnCopy, c);

		c.gridx = 0;
		c.gridy = 2;
		JLabel colorLabel = new JLabel("Couleur de la bar de progression");
		generalSettingsPanel.add(colorLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		colorBoxes = new JComboBox<>(new String[] { "Défaut", "Vert", "Violet" });
		generalSettingsPanel.add(colorBoxes, c);
		switch (SettingsManager.getInstance().getProgressBarColor().toLowerCase()) {
		case "défaut":
		case "default":
			colorBoxes.setSelectedIndex(0);
			break;

		case "vert":
		case "green":
			colorBoxes.setSelectedIndex(1);
			break;

		case "violet":
			colorBoxes.setSelectedIndex(2);
			break;

		default:
			break;
		}
		colorBoxes.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}
		});

		c.gridx = 0;
		c.gridy = 3;
		JLabel removeLabel = new JLabel("Supprimer un tournoi");
		generalSettingsPanel.add(removeLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		removeTournament = new JComboBox<>();
		removeTournament.addItem("Choisir un tournoi...");
		Stream.of(TournamentManager.getInstance().getTournaments())
				.forEach(tournament -> removeTournament.addItem(tournament));

		removeTournament.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				getTopLevelAncestor().repaint();
			}
		});

		removeTournament.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (removeTournament.getSelectedIndex() != 0) {
					System.out.println("Preparing tournament remove...");
					String tournamentName = removeTournament.getSelectedItem().toString();
					int res = JOptionPane.showConfirmDialog(null,
							"Voulez-vous vraiment supprimer le tournoi " + tournamentName + " ?",
							"Supprimer le tournoi ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (res == JOptionPane.YES_OPTION) {
						System.out.println("Removing " + tournamentName);
						EventManager.getInstance().callEvent(new TournamentRemoveEvent(tournamentName));
					} else {
						System.out.println("Cancelled!");
					}
					removeTournament.setSelectedIndex(0);
				}
			}
		});
		generalSettingsPanel.add(removeTournament, c);

		generalSettingsPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Paramètres généraux"));

		JPanel defaultValuesPanel = new JPanel();
		defaultValuesPanel.setLayout(new GridBagLayout());
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		JLabel matchesLabel = new JLabel("Nombre de parties");
		defaultValuesPanel.add(matchesLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		matchesValue = new JTextField(String.valueOf(SettingsManager.getInstance().getMatchesNumber()));
		defaultValuesPanel.add(matchesValue, c);

		c.gridx = 0;
		c.gridy = 1;
		JLabel levelsLabel = new JLabel("Nombre de groupes");
		defaultValuesPanel.add(levelsLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		levelsValue = new JTextField(String.valueOf(SettingsManager.getInstance().getGroupsNumber()));
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
		defaultValuesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				"Valeurs de création de tournoi par défaut"));

		c.gridx = 0;
		c.gridy = 0;
		generalSettingsPanel.setOpaque(false);
		panel.add(generalSettingsPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		defaultValuesPanel.setOpaque(false);
		panel.add(defaultValuesPanel, c);

		panel.setBackground(new Color(255, 255, 255, 200));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return panel;
	}

	private void changeSettings() {
		String createString = newFolderOnCopy.isSelected() ? "1" : "0";
		String studentString = String.valueOf(Float.valueOf(studentsValue.getText().replace("%", "")) / 100);
		String classesString = String.valueOf(Float.valueOf(classesValue.getText().replace("%", "")) / 100);

		SettingsChangeEvent event = new SettingsChangeEvent(resultsPath.getText(), createString, matchesValue.getText(),
				levelsValue.getText(), colorBoxes.getSelectedItem().toString(), timeValue.getText(), studentString,
				classesString);

		EventManager.getInstance().callEvent(event);
	}

	private void searchNewResultsFolder() {
		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int choose = chooser.showOpenDialog(this);
		if (choose == JFileChooser.APPROVE_OPTION) {
			File chosenDir = chooser.getSelectedFile();
			resultsPath.setText(chosenDir.getAbsolutePath());
		}
	}

}
