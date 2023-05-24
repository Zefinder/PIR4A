package ppc.frame.open;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicArrowButton;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentAddClassEvent;
import ppc.event.TournamentAddClassStatusEvent;
import ppc.event.TournamentDeleteClassStatusEvent;
import ppc.event.TournamentOpeningStatusEvent;
import ppc.frame.MainFrame;
import ppc.manager.EventManager;
import ppc.manager.FileManager;
import ppc.manager.TournamentManager;
import ppc.tournament.InputFormat;
import ppc.tournament.Tournament;

public class OpenedTournamentPanel extends JPanel implements Listener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2182862369280914967L;

	private String tournamentName;
	private int roundsNumber;
	private int groupsNumber;
	private int classNumber;

	private CSVPanel csvPanel;

	private JLabel tournamentNameLabel;
	private JLabel roundsNumberLabel;
	private JLabel groupsNumberLabel;
	private JLabel classesNumberLabel;

	private JTextField studentsThreshold;
	private JTextField classesThreshold;
	private JTextField timeField;
	private JTextField tableOffset;
	private JCheckBox softBox;
	private JCheckBox verboseBox;
	private JButton estimateButton;
	private JButton searchButton;

	private int fileClassCount;

	public OpenedTournamentPanel() {
		EventManager.getInstance().registerListener(this);

		fileClassCount = 0;
		buildPanel();

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	private void showMainFrame() {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
		frame.showMainPanel();
	}

	private void buildPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		
		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.LINE_AXIS));

		JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
		buttonPanel.setPreferredSize(new Dimension(25, 25));
		buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());
	    BasicArrowButton backButton = new BasicArrowButton(BasicArrowButton.WEST);
	    backButton.addActionListener(e -> showMainFrame());
	    buttonPanel.add(backButton);
	    containerPanel.add(buttonPanel);
	    containerPanel.add(Box.createHorizontalGlue());
        leftPanel.add(containerPanel);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
        
		JPanel informationPanel = new JPanel();
		informationPanel.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridheight = 1;
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		informationPanel.add(buildInfoPanel(), c);

		c.gridx = 0;
		c.gridy = 1;
		informationPanel.add(buildAddRemoveClassPanel(), c);

		c.gridx = 0;
		c.gridy = 2;
		informationPanel.add(buildSearchPanel(), c);
		leftPanel.add(informationPanel);
		
		this.add(leftPanel);

		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		this.add(separator);

		csvPanel = new CSVPanel();
		this.add(csvPanel);
	}

	private JPanel buildInfoPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		tournamentNameLabel = new JLabel("Nom du tournoi");
		panel.add(tournamentNameLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		roundsNumberLabel = new JLabel("Nombre de manches");
		panel.add(roundsNumberLabel, c);

		c.gridx = 0;
		c.gridy = 2;
		groupsNumberLabel = new JLabel("Nombre de groupes de niveau");
		panel.add(groupsNumberLabel, c);

		c.gridx = 0;
		c.gridy = 3;
		classesNumberLabel = new JLabel("Nombre de classes");
		panel.add(classesNumberLabel, c);

		panel.setBorder(BorderFactory.createTitledBorder("Informations générales"));

		return panel;
	}

	private JPanel buildAddRemoveClassPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 5, 0);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		JButton addClass = new JButton("Ajouter une classe...");
		addClass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Choosing a class file to add it...");

				JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Fichiers CSV (.csv)";
					}

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".csv");
					}
				});
				int answer = chooser.showOpenDialog(null);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File chosen = chooser.getSelectedFile();
					System.out.println("File choosen: " + chosen.getAbsolutePath());
					EventManager.getInstance()
							.callEvent(new TournamentAddClassEvent(tournamentName, chosen, fileClassCount));
				} else {
					System.out.println("Choosing class file canceled ");
				}

			}
		});
		panel.add(addClass, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton removeClass = new JButton("Retirer la classe");
		removeClass.addActionListener(e -> csvPanel.removeClass());
		panel.add(removeClass, c);

		panel.setBorder(BorderFactory.createTitledBorder("Gestion des classes"));

		return panel;
	}

	private JPanel buildSearchPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		JLabel studentsThresholdLabel = new JLabel("Seuil d'étudiants pour arrêter la recherche");
		panel.add(studentsThresholdLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		studentsThreshold = new JTextField(10);
		panel.add(studentsThreshold, c);

		c.gridx = 0;
		c.gridy = 1;
		JLabel classesThresholdLabel = new JLabel("Seuil de classes pour arrêter la recherche");
		panel.add(classesThresholdLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		classesThreshold = new JTextField(10);
		panel.add(classesThreshold, c);

		c.gridx = 0;
		c.gridy = 2;
		JLabel timeLabel = new JLabel("Temps maximal de recherche");
		panel.add(timeLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		timeField = new JTextField(10);
		panel.add(timeField, c);

		c.gridx = 0;
		c.gridy = 3;
		JLabel tableLabel = new JLabel("Numéro de la première table");
		panel.add(tableLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		tableOffset = new JTextField(10);
		panel.add(tableOffset, c);

		c.gridx = 0;
		c.gridy = 4;
		JLabel softLabel = new JLabel("Autoriser la rencontre de mêmes étudiants");
		panel.add(softLabel, c);

		c.gridx = 1;
		c.gridy = 4;
		softBox = new JCheckBox();
		softBox.setSelected(false);
		panel.add(softBox, c);

		c.gridx = 0;
		c.gridy = 5;
		JLabel verboseLabel = new JLabel("Afficher les résultats pendant la recherche");
		panel.add(verboseLabel, c);

		c.gridx = 1;
		c.gridy = 5;
		verboseBox = new JCheckBox();
		verboseBox.setSelected(true);
		panel.add(verboseBox, c);

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 6;
		estimateButton = new JButton("Estimer le résultat");
		estimateButton.addActionListener(e -> estimateResult());
		panel.add(estimateButton, c);

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 7;
		searchButton = new JButton("Lancer la recherche");
		searchButton.addActionListener(e -> launchSolver());
		panel.add(searchButton, c);

		panel.setBorder(BorderFactory.createTitledBorder("Paramètres de recherche"));

		return panel;
	}

	@EventHandler
	public void loadTournament(TournamentOpeningStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			System.out.println("Loading tournament properties");
			Tournament tournament = TournamentManager.getInstance().getTournament(event.getTournamentName());
			tournamentName = event.getTournamentName();
			roundsNumber = tournament.getRoundsNumber();
			groupsNumber = tournament.getGroupsNumber();
			classNumber = 0;
			csvPanel.setTounamentName(tournamentName);

			loadData(tournament);

			tournamentNameLabel.setText("Nom du tournoi : " + tournamentName);
			roundsNumberLabel.setText("Nombre de manches : " + roundsNumber);
			groupsNumberLabel.setText("Nombre de groupes de niveau : " + groupsNumber);
			classesNumberLabel.setText("Nombre de classes : " + classNumber);

			studentsThreshold.setText(String.valueOf(tournament.getStudentsThreshold() * 100) + "%");
			classesThreshold.setText(String.valueOf(tournament.getClassesThreshold() * 100) + "%");
			timeField.setText(String.valueOf(tournament.getMaxTime()));
			tableOffset.setText("0");
		}
	}

	@EventHandler
	public void onAddedClass(TournamentAddClassStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			classNumber++;
			fileClassCount++;
			classesNumberLabel.setText("Nombre de classes : " + classNumber);

			try {
				List<Map<String, String[][]>> classData = InputFormat.parseCsv(event.getCSVFile());
				csvPanel.addClass(classData, Integer.parseInt(event.getCSVFile().getName().substring(5, 6)));
				System.out.println("Class added");
			} catch (IOException | ParseException e) {
				e.printStackTrace();
				System.err.println("An error occured when parsing file " + event.getCSVFile().getAbsolutePath());
			}

		} else {
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	@EventHandler
	public void onRemovedClass(TournamentDeleteClassStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			classesNumberLabel.setText("Nombre de classes : " + --classNumber);
		} else {
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadData(Tournament tournament) {
		System.out.println("Loading tournament data files");
		File[] files = FileManager.getInstance().getTournamentData(tournament.getTournamentName());

		for (File csvfile : files) {
			System.out.println("Reading file " + csvfile.getName());
			try {
				List<Map<String, String[][]>> classData = InputFormat.parseCsv(csvfile);
				csvPanel.addClass(classData, Integer.parseInt(csvfile.getName().substring(5, 6)));
				System.out.println("File added");
				fileClassCount++;
				classNumber++;
			} catch (IOException | ParseException e) {
				e.printStackTrace();
				System.err.println("An error occured when parsing file " + csvfile.getAbsolutePath());
			}
		}
	}
	
	private void estimateResult() {
		
	}
	
	private void launchSolver() {
		
	}
}
