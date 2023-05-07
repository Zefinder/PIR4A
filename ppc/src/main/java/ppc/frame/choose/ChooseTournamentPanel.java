package ppc.frame.choose;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import ppc.event.TournamentOpenEvent;
import ppc.manager.EventManager;
import ppc.manager.FileManager;

public class ChooseTournamentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1288814079619469030L;

	public ChooseTournamentPanel() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(8, 8, 8, 8);

		c.gridx = 0;
		c.gridy = 0;

		JList<String> list = new JList<>(Stream.of(FileManager.getInstance().getTournamentFiles())
				.map(file -> file.getName().substring(0, file.getName().length() - 4)).toArray(String[]::new));
		list.setCellRenderer(new TournamentListRenderer());
		list.setFixedCellHeight(25);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() >= 2) {
					JList<?> list = (JList<?>) e.getSource();
					String tournamentName = list.getSelectedValue().toString();
					openTournament(tournamentName);
				}
			};
		});
		this.add(list, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton confirm = new JButton("Ouvrir le tournoi");
		confirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String tournamentName = list.getSelectedValue().toString();
				openTournament(tournamentName);
			}
		});
		this.add(confirm, c);
	}

	private void openTournament(String tournamentName) {
		System.out.println("Opening tournament " + tournamentName + "...");
		EventManager.getInstance().callEvent(new TournamentOpenEvent(tournamentName));
	}

}
