package ppc.frame.choose;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentCreationStatusEvent;
import ppc.event.TournamentOpenEvent;
import ppc.manager.EventManager;
import ppc.manager.TournamentManager;

public class ChooseTournamentPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1288814079619469030L;

	private DefaultListModel<String> model;
	private JList<String> list;

	public ChooseTournamentPanel() {
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

		model = new DefaultListModel<>();
		list = new JList<>(model);
		model.addAll(Stream.of(TournamentManager.getInstance().getTournaments()).toList());

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
		
		if (model.getSize() > 15)
			list.setVisibleRowCount(15);
		else
			list.setVisibleRowCount(model.getSize());
		
		JScrollPane scrollpane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollpane.setBorder(BorderFactory.createTitledBorder("Choisir un tournoi"));
		this.add(scrollpane, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton confirm = new JButton("Ouvrir le tournoi");
		confirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedValue() == null)
					return;

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

	@EventHandler
	public void onCreatedTournament(TournamentCreationStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			String tournamentName = event.getTournamentName();
			model.addElement(tournamentName);
			if (model.getSize() > 15)
				list.setVisibleRowCount(15);
			else
				list.setVisibleRowCount(model.getSize());
		}
	}

}
