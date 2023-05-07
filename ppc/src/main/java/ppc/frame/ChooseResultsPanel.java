package ppc.frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

import ppc.event.TournamentCopyEvent;
import ppc.manager.EventManager;
import ppc.manager.FileManager;

public class ChooseResultsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4228356464166011904L;

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

		JList<String> list = new JList<>(Stream.of(FileManager.getInstance().getResultFiles())
				.map(file -> file.getName()).toArray(String[]::new));
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
		this.add(list, c);

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

			TournamentCopyEvent event = new TournamentCopyEvent(tournamentName, chosenDir);
			EventManager.getInstance().callEvent(event);

		} else {
			System.out.println("Choosing cancelled by the user!");
		}

	}

}
