package ppc.frame.choose;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.mainpanel.TournamentCreationStatusEvent;
import ppc.event.mainpanel.TournamentOpenEvent;
import ppc.event.mainpanel.TournamentOpeningStatusEvent;
import ppc.event.mainpanel.TournamentResultsCopyStatusEvent;
import ppc.frame.MainFrame;
import ppc.manager.EventManager;

public class MainPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6299701397203754886L;

	/* Constants */
	private static final String CREATE_NEW_TOURNAMENT = "new";
	private static final String LOAD_TOURNAMENT = "load";
	private static final String CHECK_TOURNAMENT = "check";
	private static final String SETTINGS = "settings";

	private static final int NEW_TOURNAMENT_ACTION = 0x00;
	private static final int LOAD_TOURNAMENT_ACTION = 0x01;
	private static final int CHECK_TOURNAMENT_ACTION = 0x02;
	private static final int SETTINGS_ACTION = 0x03;

	private Image backgroundImage = new ImageIcon(this.getClass().getClassLoader().getResource("chess_background.jpg")).getImage();

	/* Variables */
	private JPanel optionsPanel, informationsPanel;

	public MainPanel() {
		EventManager.getInstance().registerListener(this);

		buildMainPanel();
	}

	private void buildMainPanel() {
	    this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

	    JPanel optionsAndLabelsPanel = new JPanel();
	    optionsAndLabelsPanel.setLayout(new BorderLayout(0,0));

	    optionsPanel = buildChoosingOptionsPanel();

	    optionsAndLabelsPanel.add(optionsPanel, BorderLayout.CENTER);
	    optionsAndLabelsPanel.setBackground(new Color(255, 255, 255, 100));

	    JPanel authorsPanel = new JPanel();
	    authorsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    authorsPanel.setOpaque(false);
	    JLabel authorsLabel = new JLabel("Adrien Jakubiak & Sarah Mousset");
	    authorsPanel.add(authorsLabel);

	    optionsAndLabelsPanel.add(authorsPanel, BorderLayout.SOUTH);

	    this.add(optionsAndLabelsPanel);

	    informationsPanel = buildInformationsPanel();
	    this.add(informationsPanel);
	}


	private JPanel buildChoosingOptionsPanel() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 5, 0);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		JButton newTournament = new JButton("Créer un nouveau tournoi");
		newTournament.addActionListener(new OptionsButtonAction(NEW_TOURNAMENT_ACTION));
		optionsPanel.add(newTournament, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton loadTournament = new JButton("Charger un tournoi");
		loadTournament.addActionListener(new OptionsButtonAction(LOAD_TOURNAMENT_ACTION));
		optionsPanel.add(loadTournament, c);

		c.gridx = 0;
		c.gridy = 2;
		JButton checkTournament = new JButton("Fichiers générés pour les tournois");
		checkTournament.addActionListener(new OptionsButtonAction(CHECK_TOURNAMENT_ACTION));
		optionsPanel.add(checkTournament, c);

		c.gridx = 0;
		c.gridy = 3;
		JButton settings = new JButton("Paramètres");
		settings.addActionListener(new OptionsButtonAction(SETTINGS_ACTION));
		optionsPanel.add(settings, c);

		optionsPanel.setOpaque(false);

		return optionsPanel;
	}

	private JPanel buildInformationsPanel() {
		JPanel informationsPanel = new JPanel();
		informationsPanel.setLayout(new CardLayout(10, 10));

		// New tournament
		JPanel newTournamentPanel = new CreateNewTournamentPanel();
		newTournamentPanel.setOpaque(false);
		informationsPanel.add(newTournamentPanel, CREATE_NEW_TOURNAMENT);

		// Load tournament
		JPanel loadTournamentPanel = new ChooseTournamentPanel();
		loadTournamentPanel.setOpaque(false);
		informationsPanel.add(loadTournamentPanel, LOAD_TOURNAMENT);

		// Check results of tournament
		JPanel checkResultsPanel = new ChooseResultsPanel();
		checkResultsPanel.setOpaque(false);
		informationsPanel.add(checkResultsPanel, CHECK_TOURNAMENT);

		// Settings
		JPanel settingsPanel = new SettingsPanel();
		settingsPanel.setOpaque(false);
		informationsPanel.add(settingsPanel, SETTINGS);

		informationsPanel.setBackground(new Color(0, 0, 0, 100));

		return informationsPanel;
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
				System.out.println("Showing new tournament panel");
				cl.show(informationsPanel, CREATE_NEW_TOURNAMENT);
				break;

			case LOAD_TOURNAMENT_ACTION:
				System.out.println("Showing load tournament panel");
				cl.show(informationsPanel, LOAD_TOURNAMENT);
				break;

			case CHECK_TOURNAMENT_ACTION:
				System.out.println("Showing check tournament panel");
				cl.show(informationsPanel, CHECK_TOURNAMENT);
				break;

			case SETTINGS_ACTION:
				System.out.println("Showing settings panel");
				cl.show(informationsPanel, SETTINGS);
				break;

			default:
				System.err.println("Error detected when chosing options (action=" + action + ")!");
				break;
			}

			repaint();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
	}

	@EventHandler
	public void onTournamentCreated(TournamentCreationStatusEvent event) {
		switch (event.getStatus()) {
		case SUCCESS:
			System.out.println("Tournament successfully created!");
			int response = JOptionPane.showConfirmDialog(null, "Le tournoi a été créé, voulez-vous l'ouvrir ?",
					"Tournoi créé", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (response == JOptionPane.YES_OPTION) {
				System.out.println("Opening tournament " + event.getTournamentName() + "...");
				EventManager.getInstance().callEvent(new TournamentOpenEvent(event.getTournamentName()));
			}
			break;

		case ERROR:
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

	@EventHandler
	public void onTournamentOpened(TournamentOpeningStatusEvent event) {
		switch (event.getStatus()) {
		case SUCCESS:
			System.out.println("Tournament opened !");
			MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
			frame.showOpenTournament();
			break;

		case ERROR:
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

	@EventHandler
	public void onResultsCopied(TournamentResultsCopyStatusEvent event) {
		switch (event.getStatus()) {
		case SUCCESS:
			System.out.println("Files copied!");
			JOptionPane.showMessageDialog(null, "Les fichiers de résultats ont bien été copiés !", "Fichiers copiés",
					JOptionPane.INFORMATION_MESSAGE);
			break;

		case ERROR:
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

}
