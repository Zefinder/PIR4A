package ppc.frame.choose;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.mainpanel.TournamentCreationStatusEvent;
import ppc.event.mainpanel.TournamentOpenEvent;
import ppc.event.mainpanel.TournamentRemovingStatusEvent;
import ppc.frame.TournamentListRenderer;
import ppc.manager.EventManager;
import ppc.manager.TournamentManager;

public class ChooseTournamentPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1288814079619469030L;

	private DefaultListModel<String> model;
	private JList<String> list;
	private JButton confirm;

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
		model.addAll(Stream.of(TournamentManager.getInstance().getTournaments()).collect(Collectors.toList()));

		list.setCellRenderer(new TournamentListRenderer());
		list.setFixedCellHeight(25);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 1)
					confirm.setEnabled(true);
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

		list.setOpaque(false);
		JScrollPane scrollpane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollpane.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Choisir un tournoi"));
		scrollpane.setOpaque(false);
		
		JPanel listPanel = new JPanel();
		listPanel.add(scrollpane);
		listPanel.setBorder(new EmptyBorder(5, 8, 7, 8));
		listPanel.setBackground(new Color(255, 255, 255, 200));
		
		this.add(listPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		confirm = new JButton("Ouvrir le tournoi");
		confirm.setEnabled(false);
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
	
	@EventHandler
	public void onTournamentRemoved(TournamentRemovingStatusEvent event) {
		if (event.getStatus() == EventStatus.SUCCESS) {
			model.removeElement(event.getTournamentName());
			if (model.getSize() > 15)
				list.setVisibleRowCount(15);
			else
				list.setVisibleRowCount(model.getSize());
		}
	}

}
