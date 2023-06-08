package ppc.frame.choose;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.TournamentRemovingStatusEvent;
import ppc.event.TournamentResultsCopyEvent;
import ppc.frame.TournamentListRenderer;
import ppc.manager.EventManager;
import ppc.manager.FileManager;

public class ChooseResultsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4228356464166011904L;

	private DefaultListModel<String> model;
	private JList<String> list;

	public ChooseResultsPanel() {
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
		model.addAll(Stream.of(FileManager.getInstance().getResultFiles()).map(file -> file.getName()).toList());

		list.setCellRenderer(new TournamentListRenderer());
		list.setFixedCellHeight(25);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() >= 2) {
					JList<?> list = (JList<?>) e.getSource();
					String tournamentName = list.getSelectedValue().toString();
					copyTournamentResults(tournamentName);
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

		scrollpane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				"Copier les pdf générés"));
		scrollpane.setOpaque(false);

		JPanel listPanel = new JPanel();
		listPanel.add(scrollpane);
		listPanel.setBorder(new EmptyBorder(5, 8, 7, 8));
		listPanel.setBackground(new Color(255, 255, 255, 200));

		this.add(listPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		JButton confirm = new JButton("Copier les résultats...");
		confirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedValue() == null)
					return;

				String tournamentName = list.getSelectedValue().toString();
				copyTournamentResults(tournamentName);
			}
		});
		this.add(confirm, c);

	}

	private void copyTournamentResults(String tournamentName) {
		System.out.println("Choosing where to copy " + tournamentName + "'s results...");

		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int choose = chooser.showOpenDialog(this);
		if (choose == JFileChooser.APPROVE_OPTION) {
			File chosenDir = chooser.getSelectedFile();
			System.out.println("Directory choosen: " + chosenDir.getAbsolutePath());

			TournamentResultsCopyEvent event = new TournamentResultsCopyEvent(tournamentName, chosenDir);
			EventManager.getInstance().callEvent(event);

		} else {
			System.out.println("Choosing cancelled by the user!");
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
