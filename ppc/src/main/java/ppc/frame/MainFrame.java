package ppc.frame;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.TournamentCreationStatusEvent;
import ppc.manager.EventManager;
import ppc.manager.LogsManager;

public class MainFrame extends JFrame implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4584793474136955817L;

	/* Constants */
	private static final String CREATE_NEW_TOURNAMENT = "new";
	private static final String LOAD_TOURNAMENT = "load";
	private static final String CHECK_TOURNAMENT = "check";
	private static final String SETTINGS = "settings";

	private static final int NEW_TOURNAMENT_ACTION = 0x00;
	private static final int LOAD_TOURNAMENT_ACTION = 0x01;
	private static final int CHECK_TOURNAMENT_ACTION = 0x02;
	private static final int SETTINGS_ACTION = 0x03;

	/* Variables */
	private JPanel optionsPanel, informationsPanel;

	public MainFrame() {
		EventManager.getInstance().registerListener(this);

		this.setTitle("Tournois d'échecs");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = buildMainPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.setContentPane(mainPanel);
		this.setVisible(false);
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

		optionsPanel = buildChoosingOptionsPanel();
		informationsPanel = buildInformationsPanel();

		mainPanel.add(optionsPanel);
		mainPanel.add(new JSeparator(SwingConstants.VERTICAL));
		mainPanel.add(informationsPanel);

		return mainPanel;
	}

	// TODO Buttons on the left : Create new/Load/Check results of tournament and
	// settings ig
	private JPanel buildChoosingOptionsPanel() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 5, 00);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		JButton newTournament = new JButton("Créer un nouveau tournoi (WIP)");
		newTournament.addActionListener(new OptionsButtonAction(NEW_TOURNAMENT_ACTION));
		optionsPanel.add(newTournament, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton loadTournament = new JButton("Charger un tournoi (WIP)");
		loadTournament.addActionListener(new OptionsButtonAction(LOAD_TOURNAMENT_ACTION));
		optionsPanel.add(loadTournament, c);

		c.gridx = 0;
		c.gridy = 2;
		JButton checkTournament = new JButton("Fichiers générés pour les tournois (WIP)");
		checkTournament.addActionListener(new OptionsButtonAction(CHECK_TOURNAMENT_ACTION));
		optionsPanel.add(checkTournament, c);

		c.gridx = 0;
		c.gridy = 3;
		JButton settings = new JButton("Paramètres");
		settings.addActionListener(new OptionsButtonAction(SETTINGS_ACTION));
		optionsPanel.add(settings, c);

		return optionsPanel;
	}

	// TODO Panel on the right : CardLayout, used to put informations to create a
	// tournament or to select a file...
	private JPanel buildInformationsPanel() {
		JPanel informationsPanel = new JPanel();
		informationsPanel.setLayout(new CardLayout(10, 10));

		// New tournament
		JPanel newTournamentPanel = new CreateNewTournamentPanel();
		informationsPanel.add(newTournamentPanel, CREATE_NEW_TOURNAMENT);

		// Load tournament
		JPanel loadTournamentPanel = new ChooseTournamentPanel();
		informationsPanel.add(loadTournamentPanel, LOAD_TOURNAMENT);

		// Check results of tournament
		JPanel checkResultsPanel = new ChooseResultsPanel();
		informationsPanel.add(checkResultsPanel, CHECK_TOURNAMENT);

		// Settings
		JPanel settingsPanel = new SettingsPanel();
		informationsPanel.add(settingsPanel, SETTINGS);

		return informationsPanel;
	}

	public void initFrame() {
		this.setVisible(true);
	}

	private class OptionsButtonAction implements ActionListener {
		private int action;

		public OptionsButtonAction(int action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CardLayout cl = (CardLayout) informationsPanel.getLayout();

			switch (action) {
			case NEW_TOURNAMENT_ACTION:
				System.out.println("Create new tournament");
				cl.show(informationsPanel, CREATE_NEW_TOURNAMENT);
				break;

			case LOAD_TOURNAMENT_ACTION:
				System.out.println("Load tournament");
				cl.show(informationsPanel, LOAD_TOURNAMENT);
				break;

			case CHECK_TOURNAMENT_ACTION:
				System.out.println("Check tournaments' files");
				cl.show(informationsPanel, CHECK_TOURNAMENT);
				break;

			case SETTINGS_ACTION:
				System.out.println("Settings");
				cl.show(informationsPanel, SETTINGS);
				break;

			default:
				System.err.println("Error detected when chosing options (action=" + action + ")!");
				break;
			}
		}
	}

	@EventHandler
	public void onTournamentCreated(TournamentCreationStatusEvent event) {
		switch (event.getStatus()) {
		case CREATED:
			System.out.println("Tournament created!");
			JOptionPane.showMessageDialog(null, "Le tournoi a été créé !", "Tournoi créé",
					JOptionPane.INFORMATION_MESSAGE);
			break;

		case FILE_EXIST:
			LogsManager.getInstance().writeErrorMessage("Tournament's name already exists!");
			JOptionPane.showMessageDialog(null, "Ce nom de tournoi existe déjà !", "Erreur", JOptionPane.ERROR_MESSAGE);
			break;

		case NEGATIVE_TIME:
			LogsManager.getInstance().writeErrorMessage("Tournament's max time search can not be negative!");
			JOptionPane.showMessageDialog(null, "Le temps maximal de recherche ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			break;

		case NEGATIVE_STUDENT_THRESHOLD:
			LogsManager.getInstance().writeErrorMessage("Tournament's students threshold can not be negative!");
			JOptionPane.showMessageDialog(null, "Le seuil d'étudiants ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			break;

		case STUDENT_THRESHOLD_TOO_BIG:
			LogsManager.getInstance().writeErrorMessage("Tournament's students threshold can not be over 1!");
			JOptionPane.showMessageDialog(null, "Le seuil d'étudiants ne peut pas être supérieur à 100% !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			break;

		case NEGATIVE_CLASSES_THRESHOLD:
			LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold can not be negative!");
			JOptionPane.showMessageDialog(null, "Le seuil de classes ne peut pas être négatif !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			break;

		case CLASSES_THRESHOLD_TOO_BIG:
			LogsManager.getInstance().writeErrorMessage("Tournament's classes threshold can not be over 1!");
			JOptionPane.showMessageDialog(null, "Le seuil de classes ne peut pas être supérieur à 100% !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

}
