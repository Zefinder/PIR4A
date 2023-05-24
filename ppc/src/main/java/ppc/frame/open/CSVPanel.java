package ppc.frame.open;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;

import ppc.annotation.EventHandler;
import ppc.event.EventStatus;
import ppc.event.Listener;
import ppc.event.TournamentAddLevelGroupEvent;
import ppc.event.TournamentClassCopyEvent;
import ppc.event.TournamentClassCopyStatusEvent;
import ppc.event.TournamentDeleteClassEvent;
import ppc.event.TournamentDeleteClassStatusEvent;
import ppc.event.TournamentSolveEvent;
import ppc.frame.TournamentListRenderer;
import ppc.frame.TournamentTableModel;
import ppc.manager.EventManager;
import ppc.manager.FileManager;
import ppc.manager.LogsManager;
import ppc.tournament.InputFormat;

public class CSVPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2424923812357180732L;

	private JList<String> listClasses;
	private DefaultListModel<String> model;
	private JPanel csvPanel;

	private JButton addStudent;
	private JButton removeStudent;

	private List<JScrollPane> scrollList;
	private List<TournamentTableModel> tableModelList;

	private String tournamentName;

	public CSVPanel() {
		EventManager.getInstance().registerListener(this);

		scrollList = new ArrayList<>();
		tableModelList = new ArrayList<>();

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		this.add(Box.createHorizontalStrut(10));
		JPanel listPanel = buildListClassPanel();
		this.add(listPanel);

		this.add(Box.createHorizontalStrut(10));

		JPanel csvPanel = buildCSVPanel();
		this.add(csvPanel);

	}

	private JPanel buildListClassPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;

		model = new DefaultListModel<>();
		listClasses = new JList<>(model);
		listClasses.setCellRenderer(new TournamentListRenderer());
		listClasses.setFixedCellHeight(20);

		if (model.getSize() > 15)
			listClasses.setVisibleRowCount(15);
		else
			listClasses.setVisibleRowCount(model.getSize());

		listClasses.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					int selectedClass = listClasses.getSelectedIndex();

					if (selectedClass != -1) {
						System.out.println("Showing " + listClasses.getSelectedValue());
						CardLayout cl = (CardLayout) csvPanel.getLayout();
						cl.show(csvPanel, scrollList.get(selectedClass).getName());

						addStudent.setEnabled(true);
						removeStudent.setEnabled(true);

						repaint();
					}
				}
			}
		});

		JScrollPane scrollpane = new JScrollPane(listClasses, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollpane.setBorder(BorderFactory.createTitledBorder("Liste des classes"));
		panel.add(scrollpane, c);

		c.gridx = 0;
		c.gridy = 1;
		addStudent = new JButton("Ajouter un élève");
		addStudent.setEnabled(false);
		panel.add(addStudent, c);
		addStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedClass = listClasses.getSelectedIndex();
				if (selectedClass == -1) {
					JOptionPane.showMessageDialog(null, "Veuillez sélectionner une classe.", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				} else {
					JScrollPane selectedScrollPane = scrollList.get(selectedClass);
					JTable selectedTable = (JTable) ((JViewport) selectedScrollPane.getComponent(0)).getView();
					AddStudentDialog dialog = new AddStudentDialog((TournamentTableModel) selectedTable.getModel());
					dialog.setVisible(true);
				}
			}
		});

		c.gridx = 0;
		c.gridy = 2;
		removeStudent = new JButton("Retirer l'élève");
		removeStudent.setEnabled(false);
		panel.add(removeStudent, c);
		removeStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedClass = listClasses.getSelectedIndex();
				if (selectedClass == -1) {
					JOptionPane.showMessageDialog(null, "Veuillez sélectionner une classe.", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				} else {
					JScrollPane selectedScrollPane = scrollList.get(selectedClass);
					JTable selectedTable = (JTable) ((JViewport) selectedScrollPane.getComponent(0)).getView();
					int selectedRow = selectedTable.getSelectedRow();
					if (selectedRow == -1) {
						JOptionPane.showMessageDialog(null, "Veuillez sélectionner un élève à supprimer.", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					} else {
						String firstName = selectedTable.getValueAt(selectedRow, 0).toString();
						String lastName = selectedTable.getValueAt(selectedRow, 1).toString();
						int choice = JOptionPane.showConfirmDialog(null,
								"Êtes-vous sûr de vouloir supprimer l'élève " + firstName + " " + lastName + " ?",
								"Confirmation", JOptionPane.YES_NO_OPTION);
						if (choice == JOptionPane.YES_OPTION) {
							TournamentTableModel selectedTableModel = (TournamentTableModel) selectedTable.getModel();
							selectedTableModel.removeRow(selectedRow);
						}
					}
				}
			}
		});

		return panel;
	}

	private JPanel buildCSVPanel() {
		csvPanel = new JPanel();
		csvPanel.setLayout(new CardLayout());

		TournamentTableModel tableModel = new TournamentTableModel();
		tableModel.addColumn("Prénom");
		tableModel.addColumn("Nom");
		tableModel.addColumn("Niveau");

		JTable defaultTable = new JTable(tableModel);
		defaultTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		defaultTable.setPreferredScrollableViewportSize(defaultTable.getPreferredSize());

		JScrollPane defaultTablePanel = new JScrollPane(defaultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		csvPanel.add(defaultTablePanel, "");

		return csvPanel;
	}
	
	public void reset() {
		// Clearing lists
		scrollList = new ArrayList<>();
		tableModelList = new ArrayList<>();
		model.removeAllElements();
		
		// Disable buttons
		addStudent.setEnabled(false);
		removeStudent.setEnabled(false);
		
		// Clearing panel and refilling it
		csvPanel.removeAll();
		csvPanel.setLayout(new CardLayout());

		TournamentTableModel tableModel = new TournamentTableModel();
		tableModel.addColumn("Prénom");
		tableModel.addColumn("Nom");
		tableModel.addColumn("Niveau");

		JTable defaultTable = new JTable(tableModel);
		defaultTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		defaultTable.setPreferredScrollableViewportSize(defaultTable.getPreferredSize());

		JScrollPane defaultTablePanel = new JScrollPane(defaultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		csvPanel.add(defaultTablePanel, "");
	}

	public void addClass(List<Map<String, String[][]>> classData, int classNumber) {
		// Getting prof's name
		String profName = classData.get(0).keySet().toArray(String[]::new)[0];

		// Adding to names' list
		model.addElement("Classe de " + profName);
		if (model.getSize() > 15)
			listClasses.setVisibleRowCount(15);
		else
			listClasses.setVisibleRowCount(model.getSize());

		// Adding table to CardLayout
		addTableToPanel(classData, profName + " " + classNumber);

	}

	public void removeClass() {
		int selectedIndex = listClasses.getSelectedIndex();
		if (selectedIndex != -1) {
			String[] panelName = scrollList.get(selectedIndex).getName().split(" ");
			int classIndex = Integer.valueOf(panelName[panelName.length - 1]);

			TournamentDeleteClassEvent event = new TournamentDeleteClassEvent(tournamentName, classIndex,
					selectedIndex);
			EventManager.getInstance().callEvent(event);
		} else
			LogsManager.getInstance().writeWarningMessage("No class were selected before removing...");
	}

	public void setTounamentName(String tournamentName) {
		this.tournamentName = tournamentName;
	}

	public void launchSolver(int classNumber, int groupsNumber, boolean soft, float studentsThreshold,
			float classesThreshold, int timeout, int firstTable, boolean verbose) {
		// List of groups of classes (K x N)
		List<Map<String, String[][]>> listClasses = new ArrayList<>();
		for (int level = 0; level < groupsNumber; level++) {
			listClasses.add(new LinkedHashMap<>());
		}

		// Parse final CSV to get lists
		File[] csvFiles = FileManager.getInstance().getTournamentData(tournamentName);

		for (File csvfile : csvFiles) {
			System.out.println("Reading file " + csvfile.getName());
			try {
				List<Map<String, String[][]>> classData = InputFormat.parseCsv(csvfile, groupsNumber);
				for (int level = 0; level < groupsNumber; level++) {
					listClasses.get(level).putAll(classData.get(level));
				}

			} catch (IOException | ParseException e) {
				e.printStackTrace();
				System.err.println("An error occured when parsing file " + csvfile.getAbsolutePath());
			}
		}

		for (int level = 0; level < groupsNumber; level++)
			EventManager.getInstance().callEvent(new TournamentAddLevelGroupEvent(listClasses.get(level), level));

		EventManager.getInstance().callEvent(
				new TournamentSolveEvent(soft, classesThreshold, studentsThreshold, timeout, firstTable, verbose));
	}

	@EventHandler
	public void onClassRemoved(TournamentDeleteClassStatusEvent event) {
		int selectedIndex = event.getListIndex();

		// Remove from list
		model.remove(selectedIndex);

		// Remove from table model list
		tableModelList.remove(selectedIndex);

		// Remove from CardLayout
		CardLayout cl = (CardLayout) csvPanel.getLayout();
		cl.removeLayoutComponent(scrollList.get(selectedIndex));

		// Remove from JPanel list
		scrollList.remove(selectedIndex);

		// Resize list
		if (model.getSize() > 15)
			listClasses.setVisibleRowCount(15);
		else
			listClasses.setVisibleRowCount(model.getSize());

		// Unselecting buttons
		addStudent.setEnabled(false);
		removeStudent.setEnabled(false);

		// Revalidate frame
		repaint();

	}

	@EventHandler
	public void onClassCopied(TournamentClassCopyStatusEvent event) {
		if (event.getStatus() == EventStatus.ERROR) {
			JOptionPane.showMessageDialog(null, event.getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		} else {
			System.out.println("Changes saved!");
		}

		// Deleting temp file
		FileManager.getInstance().deleteTemporaryFile(event.getFileToCopy().getName());
	}

	private void addTableToPanel(List<Map<String, String[][]>> classData, String profName) {
		TournamentTableModel tableModel = new TournamentTableModel();
		tableModel.addColumn("Prénom");
		tableModel.addColumn("Nom");
		tableModel.addColumn("Niveau");

		int level = 1;
		for (Map<String, String[][]> map : classData) {
			for (String[] name : map.values().toArray(String[][][]::new)[0]) {
				tableModel.addRow(new String[] { name[0], name[1], String.valueOf(level) });
			}
			level++;
		}

		tableModel.addTableModelListener(e -> {
			try {
				tableModified((TournamentTableModel) e.getSource());
			} catch (IOException e1) {
				System.err.println("An error occured when modifying the table...");
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Une erreur est survenue lors de la modification du fichier de classe", "Erreur",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		JTable defaultTable = new JTable(tableModel);
		defaultTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		defaultTable.setPreferredScrollableViewportSize(defaultTable.getPreferredSize());

		JScrollPane defaultTablePanel = new JScrollPane(defaultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		defaultTablePanel.setName(profName);

		scrollList.add(defaultTablePanel);
		tableModelList.add(tableModel);
		csvPanel.add(defaultTablePanel, profName);
	}

	private void tableModified(TournamentTableModel model) throws IOException {
		int editedTable = listClasses.getSelectedIndex();
		System.out.println(String.format("Table n°%d edited, preparing to save in file", editedTable));

		String[] panelName = scrollList.get(editedTable).getName().split(" ");
		String profName = "";
		for (int i = 0; i < panelName.length - 1; i++)
			profName += panelName[i] + " ";

		profName = profName.strip();

		int classIndex = Integer.valueOf(panelName[panelName.length - 1]);
		File tmpFile = FileManager.getInstance().createTemporaryFile();

		// Put elements of table in an matrix of Strings
		String[][] elements = new String[model.getRowCount()][model.getColumnCount()];

		for (int row = 0; row < model.getRowCount(); row++) {
			for (int column = 0; column < model.getColumnCount(); column++) {
				elements[row][column] = model.getValueAt(row, column).toString();
			}
		}

		System.out.println("Saving in tmp file...");
		InputFormat.writeCSV(tmpFile, elements, profName);
		System.out.println("Saved!");

		// Call event
		System.out.println("Saving to data folder...");
		TournamentClassCopyEvent event = new TournamentClassCopyEvent(tournamentName, tmpFile, classIndex);
		EventManager.getInstance().callEvent(event);
	}

	private class AddStudentDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1665505915114573795L;
		private TournamentTableModel tableModel;
		private JTextField firstNameField;
		private JTextField lastNameField;
		private JTextField levelField;

		public AddStudentDialog(TournamentTableModel tableModel) {
			this.tableModel = tableModel;

			this.setModal(true);
			this.setTitle("Ajouter un élève");
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setSize(300, 200);
			this.setLocationRelativeTo(null);

			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BorderLayout(10, 10));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.setContentPane(contentPanel);

			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new GridBagLayout());
			contentPanel.add(inputPanel, BorderLayout.CENTER);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(5, 5, 5, 5);

			JLabel firstNameLabel = new JLabel("Prénom :");
			gbc.gridx = 0;
			gbc.gridy = 0;
			inputPanel.add(firstNameLabel, gbc);

			firstNameField = new JTextField();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			inputPanel.add(firstNameField, gbc);

			JLabel lastNameLabel = new JLabel("Nom :");
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0.0;
			inputPanel.add(lastNameLabel, gbc);

			lastNameField = new JTextField();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			inputPanel.add(lastNameField, gbc);

			JLabel levelLabel = new JLabel("Niveau :");
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0.0;
			inputPanel.add(levelLabel, gbc);

			levelField = new JTextField();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			inputPanel.add(levelField, gbc);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			contentPanel.add(buttonPanel, BorderLayout.SOUTH);

			JButton addButton = new JButton("Ajouter");
			addButton.addActionListener(e -> addStudent());
			buttonPanel.add(addButton);

			JButton cancelButton = new JButton("Annuler");
			cancelButton.addActionListener(e -> dispose());
			buttonPanel.add(cancelButton);
		}

		private void addStudent() {
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			String levelText = levelField.getText();

			if (firstName.isEmpty() || lastName.isEmpty() || levelText.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis !", "Erreur",
						JOptionPane.ERROR_MESSAGE);
			} else {
				int level;
				try {
					level = Integer.parseInt(levelText);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Le niveau doit être un nombre entier !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				tableModel.addRow(new String[] { firstName, lastName, String.valueOf(level) });
				dispose();
			}
		}
	}
}
