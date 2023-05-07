package ppc.frame;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ppc.event.Listener;
import ppc.frame.choose.MainPanel;
import ppc.frame.open.OpenedTournamentPanel;
import ppc.manager.EventManager;

public class MainFrame extends JFrame implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4584793474136955817L;

	private static final String MAIN_PANEL = "main";
	private static final String OPENED_PANEL = "opened";

	private JPanel cardPanel;

	public MainFrame() {
		EventManager.getInstance().registerListener(this);

		this.setTitle("Echec et Match !");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cardPanel = buildCardPanel();

		this.add(new MainPanel());
		this.setVisible(false);
	}

	private JPanel buildCardPanel() {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());

		MainPanel mainPanel = new MainPanel();
		cardPanel.add(mainPanel, MAIN_PANEL);

		OpenedTournamentPanel openPanel = new OpenedTournamentPanel();
		cardPanel.add(openPanel, OPENED_PANEL);

		return cardPanel;
	}

	public void showMainPanel() {
		CardLayout cl = (CardLayout) cardPanel.getLayout();
		cl.show(cardPanel, MAIN_PANEL);
	}

	public void showOpenTournament() {
		CardLayout cl = (CardLayout) cardPanel.getLayout();
		cl.show(cardPanel, OPENED_PANEL);
	}

	public void initFrame() {
		this.setVisible(true);
	}

}
