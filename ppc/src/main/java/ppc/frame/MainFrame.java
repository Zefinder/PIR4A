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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

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
		JPanel loadTournamentPanel = new JPanel();
		loadTournamentPanel.setBackground(Color.BLUE);
		informationsPanel.add(loadTournamentPanel, LOAD_TOURNAMENT);

		// Check results of tournament
		JPanel checkResultsPanel = new JPanel();
		checkResultsPanel.setBackground(Color.GREEN);
		informationsPanel.add(checkResultsPanel, CHECK_TOURNAMENT);

		// Settings
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBackground(Color.BLACK);
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

}
