package ppc.frame;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ppc.event.Listener;
import ppc.frame.choose.MainPanel;
import ppc.frame.open.OpenedTournamentPanel;
import ppc.manager.EventManager;
import ppc.manager.FileManager;

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
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closing frame and app...");
				try {
					System.out.println("Writing logs...");
					FileManager.getInstance().writeLogs();
				} catch (IOException e1) {
					e1.printStackTrace();
					System.err.println("Impossible to write logs...");
				}
				System.exit(0);
			}
			
		});

		cardPanel = buildCardPanel();

		this.add(cardPanel);
		this.setVisible(false);
	}

	private JPanel buildCardPanel() {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());

		MainPanel mainPanel = new MainPanel();
		cardPanel.add(mainPanel, MAIN_PANEL);

		OpenedTournamentPanel openPanel = new OpenedTournamentPanel(null);
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
		showMainPanel();
		this.setVisible(true);
	}

}
