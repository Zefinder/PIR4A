package ppc.frame.open;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import ppc.manager.LogsManager;
import ppc.manager.TournamentManager;
import ppc.tournament.InputFormat;
import ppc.tournament.Tournament;
import ppc.tournament.TournamentSolveImpossibleEvent;

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

	private Image backgroundImage = new ImageIcon(this.getClass().getResource("../chess_background.jpg")).getImage();

	public OpenedTournamentPanel() {
		EventManager.getInstance().registerListener(this);

		fileClassCount = 0;
		buildPanel();

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
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
		containerPanel.setOpaque(false);

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
		informationPanel.setOpaque(false);

		leftPanel.add(informationPanel);
		leftPanel.setBackground(new Color(0, 0, 0, 100));
		this.add(leftPanel);

		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setBackground(new Color(0, 0, 0, 200));
		separator.setForeground(new Color(0, 0, 0, 200));
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

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				"Informations générales"));
		panel.setBackground(new Color(255, 255, 255, 150));

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

		panel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Gestion des classes"));
		panel.setBackground(new Color(255, 255, 255, 150));

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
		softBox.setSelected(true);
		softBox.setOpaque(false);
		softBox.addMouseListener(new MouseListener() {
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
		panel.add(softBox, c);

		c.gridx = 0;
		c.gridy = 5;
		JLabel verboseLabel = new JLabel("Afficher les résultats pendant la recherche");
		panel.add(verboseLabel, c);

		c.gridx = 1;
		c.gridy = 5;
		verboseBox = new JCheckBox();
		verboseBox.setSelected(true);
		verboseBox.setOpaque(false);
		verboseBox.addMouseListener(new MouseListener() {
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

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				"Paramètres de recherche"));
		panel.setBackground(new Color(255, 255, 255, 150));

		return panel;
	}

	@EventHandler
	public void loadTournament(TournamentOpeningStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			System.out.println("CSV Panel reset!");
			csvPanel.reset();

			System.out.println("Loading tournament properties...");
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
			tableOffset.setText("1");
		}
	}

	@EventHandler
	public void onAddedClass(TournamentAddClassStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			classNumber++;
			fileClassCount++;
			classesNumberLabel.setText("Nombre de classes : " + classNumber);

			try {
				List<Map<String, String[][]>> classData = InputFormat.parseCsv(event.getCSVFile(), groupsNumber);
				csvPanel.addClass(classData, Integer.parseInt(event.getCSVFile().getName().substring(5, 6)));
				System.out.println("Class added");
				getTopLevelAncestor().repaint();
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
			getTopLevelAncestor().repaint();
		} else {
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@EventHandler
	public void onImpossibleTournament(TournamentSolveImpossibleEvent event) {
		JOptionPane.showMessageDialog(null, "Le tournoi est impossible pour le niveau " + event.getLevel(),
				"Tournoi impossible", JOptionPane.ERROR_MESSAGE);
	}

	private void loadData(Tournament tournament) {
		System.out.println("Loading tournament data files");
		File[] files = FileManager.getInstance().getTournamentData(tournament.getTournamentName());

		for (File csvfile : files) {
			System.out.println("Reading file " + csvfile.getName());
			try {
				List<Map<String, String[][]>> classData = InputFormat.parseCsv(csvfile, groupsNumber);
				csvPanel.addClass(classData, Integer.parseInt(csvfile.getName().substring(5, 6)));
				System.out.println("File added");
				fileClassCount++;
				classNumber++;
				getTopLevelAncestor().repaint();
			} catch (IOException | ParseException e) {
				e.printStackTrace();
				System.err.println("An error occured when parsing file " + csvfile.getAbsolutePath());
			}
		}
	}

	private void estimateResult() {
		csvPanel.launchEstimation(groupsNumber);
	}

	private void launchSolver() {
		boolean soft = softBox.isSelected();
		boolean verbose = verboseBox.isSelected();

		int time = getTimeValue();
		if (time == -1)
			return;

		float studentThreshold = getStudentsThreshold();
		if (studentThreshold == -1)
			return;

		float classesThreshold = getClassesThreshold();
		if (classesThreshold == -1)
			return;

		int tableOffset = getTableOffset();
		if (tableOffset == -1)
			return;

		csvPanel.launchSolver(classNumber, groupsNumber, soft, studentThreshold, classesThreshold, time, tableOffset,
				verbose);
	}

	private int getTimeValue() {
		int time;
		try {
			time = Integer.valueOf(timeField.getText());
		} catch (Exception e1) {
			LogsManager.getInstance().writeErrorMessage("Error when parsing searching time...");
			JOptionPane.showMessageDialog(null, "Impossible de lire la valeur du temps de recherche", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		if (time < 0) {
			LogsManager.getInstance().writeErrorMessage("Time value cannot be negative...");
			JOptionPane.showMessageDialog(null, "Le temps de recherche ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		return time;
	}

	private float getStudentsThreshold() {
		String studentThresholdString = studentsThreshold.getText().substring(0,
				studentsThreshold.getText().length() - 1);
		float studentThreshold;
		try {
			studentThreshold = Float.valueOf(studentThresholdString);
		} catch (Exception e1) {
			LogsManager.getInstance().writeErrorMessage("Error when parsing student's threshold...");
			JOptionPane.showMessageDialog(null, "Impossible de lire la valeur de seuil d'étudiants", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		studentThreshold /= 100f;

		if (studentThreshold < 0f) {
			LogsManager.getInstance().writeErrorMessage("Tournament's students threshold cannot be negative!");
			JOptionPane.showMessageDialog(null, "Le seuil d'étudiants ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		if (studentThreshold > 1f) {
			LogsManager.getInstance().writeErrorMessage("Tournament's students threshold cannot be over 1!");
			JOptionPane.showMessageDialog(null, "Le seuil d'étudiants ne peut pas être supérieur à 100% !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		return studentThreshold;
	}

	private float getClassesThreshold() {
		String classesThresholdString = classesThreshold.getText().substring(0,
				classesThreshold.getText().length() - 1);
		float classesThreshold;
		try {
			classesThreshold = Float.valueOf(classesThresholdString);
		} catch (Exception e1) {
			LogsManager.getInstance().writeErrorMessage("Error when parsing classes threshold...");
			JOptionPane.showMessageDialog(null, "Impossible de lire la valeur de seuil de classes", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		classesThreshold /= 100f;

		if (classesThreshold < 0f) {
			LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold cannot be negative!");
			JOptionPane.showMessageDialog(null, "Le seuil de classes ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		if (classesThreshold > 1f) {
			LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold cannot be over 1!");
			JOptionPane.showMessageDialog(null, "Le seuil de classes ne peut pas être supérieur à 100% !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		return classesThreshold;
	}

	private int getTableOffset() {
		int tableOffset;
		try {
			tableOffset = Integer.valueOf(this.tableOffset.getText());
		} catch (Exception e1) {
			LogsManager.getInstance().writeErrorMessage("Error when parsing table offset...");
			JOptionPane.showMessageDialog(null, "Impossible de lire la valeur du numéro de la première table", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		if (tableOffset < 0) {
			LogsManager.getInstance().writeErrorMessage("Table offset value cannot be negative...");
			JOptionPane.showMessageDialog(null, "Le numéro de la première table ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		return tableOffset;
	}
}
