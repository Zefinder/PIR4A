package ppc.frame.open;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class OpenedTournamentPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2182862369280914967L;
	private JList<String> listClasses;
	private List<Map<String, String[][]>> data;
	private List<JScrollPane> scrollPanes;

	public OpenedTournamentPanel(List<Map<String, String[][]>> data) {
		this.data = data;
		this.scrollPanes = new ArrayList<>();
		setLayout(new BorderLayout());

//		createListPane();
//		createCSVPanes();

		if (!scrollPanes.isEmpty()) {
			JScrollPane firstScrollPane = scrollPanes.get(0);
			add(firstScrollPane, BorderLayout.CENTER);
			listClasses.setSelectedIndex(0);
		}
	}

	public void loadTournament(String tournamentName) {

	}

	private void createListPane() {
		String[] classes = data.get(0).keySet().toArray(new String[0]);
		listClasses = new JList<>(classes);

		listClasses.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					int index = listClasses.locationToIndex(evt.getPoint());
					if (index >= 0 && index < scrollPanes.size()) {
						JScrollPane selectedScrollPane = scrollPanes.get(index);
						removeAll();
						add(selectedScrollPane, BorderLayout.CENTER);
						add(new JScrollPane(listClasses), BorderLayout.WEST);
						revalidate();
						repaint();
					}
				}
			}
		});

		JScrollPane listScrollPane = new JScrollPane(listClasses);
		add(listScrollPane, BorderLayout.WEST);
	}

	private void createCSVPanes() {
		List<DefaultTableModel> tableModels = new ArrayList<>();
		for (int i = 0; i < data.get(0).keySet().size(); i++) {
			DefaultTableModel tableModel = new DefaultTableModel();
			tableModel.addColumn("Prénom");
			tableModel.addColumn("Nom");
			tableModel.addColumn("Niveau");
			tableModels.add(tableModel);
		}

		int level = 1;
		for (Map<String, String[][]> levelMap : data) {
			int classNb = 0;
			for (String[][] students : levelMap.values()) {
				DefaultTableModel tableModel = tableModels.get(classNb++);
				for (String[] student : students) {
					String firstName = student[0];
					String lastName = student[1];
					
					tableModel.addRow(new String[] { firstName, lastName, level + "" });
				}
			}
			level++;
		}

		for (DefaultTableModel tableModel : tableModels)
			scrollPanes.add(new JScrollPane(new JTable(tableModel)));
	}

//	public static void main(String[] args) {
//		// Create the data for the levels
//		// niveau 1 rencontre 2019
//		Map<String, String[]> classesLvl1 = new LinkedHashMap<>();
//		classesLvl1.put("Prof Avec un super long nom", new String[] { "Anthonin Lulu", "Lilian", "Nathan", "Maël" });
//		classesLvl1.put("Prof 2", new String[] { "Gabi", "Guney", "Izye" });
//		classesLvl1.put("Prof 3", new String[] { "Louna", "Maxine", "Enzo", "Maéva", "Alexandre", "Kaissy", "Elie" });
//		classesLvl1.put("Prof 4",
//				new String[] { "Adam", "Nathan", "Chloé", "Jules", "Lucile", "Brian", "Loan", "Lohenn" });
//		classesLvl1.put("Prof 5", new String[] { "Amaury", "Noé", "Leïla", "Matylio" });
//
//		// niveau 2 rencontre 2019
//		Map<String, String[]> classesLvl2 = new LinkedHashMap<>();
//		classesLvl2.put("Prof Avec un super long nom",
//				new String[] { "Valentin", "Vaea", "Thomas", "Manon", "Lucas", "Jeanne", "Juliette", "Romane", "Agnese",
//						"Pauline", "Camille", "Loan", "Inès", "Manon", "Elsa", "Matéo" });
//		classesLvl2.put("Prof 2",
//				new String[] { "Lilou", "Lena", "Lucas", "Lilou", "Mirtille", "Judith", "Paul", "Pedago", "Kenji",
//						"Tao", "Lison", "William", "Yaelle", "Yanis", "Kynian", "Sacha", "Gauthier", "Jules",
//						"Erwan" });
//		classesLvl2.put("Prof 3", new String[] { "Coraline", "Chloé", "Loan", "Charlot", "Lana", "Sans prénom", "Imène",
//				"Loris", "Benjamin", "Re sans prénom" });
//		classesLvl2.put("Prof 4", new String[] { "Rafael", "Pablo", "Léna", "Rafael", "Malon", "Lisa", "Lino", "Lilou",
//				"Loane", "Bixente" });
//		classesLvl2.put("Prof 5", new String[] { "Arnaud", "Matili", "Lyse", "Clément", "Julian", "Lou", "Elise",
//				"Turis", "Célia", "Sans nom le retour", "Maxime" });
//
//		// niveau 3 rencontre 2019
//		Map<String, String[]> classesLvl3 = new LinkedHashMap<>();
//		classesLvl3.put("Prof Avec un super long nom",
//				new String[] { "Milane", "Nathan", "Adrien", "Léna", "Abdellah" });
//		classesLvl3.put("Prof 2", new String[] { "Céci", "Emma", "Luca", "Julian", "Manon" });
//		classesLvl3.put("Prof 3", new String[] { "Dine", "Augustin", "Fay", "Nicolas", "Louane", "Rayan" });
//		classesLvl3.put("Prof 4",
//				new String[] { "Nayah", "Angie", "Erika", "Ela", "Mathias", "Périne", "Cécile", "Maylie" });
//		classesLvl3.put("Prof 5", new String[] { "Thomas", "Maïssa", "Inès", "Lise", "Manon", "Maëly", "Johan",
//				"Maïssane", "Lili", "Ella" });
//
//		List<Map<String, String[]>> classesByLevel = new ArrayList<>(
//				Arrays.asList(classesLvl1, classesLvl2, classesLvl3));
//
//		SwingUtilities.invokeLater(() -> {
//			JFrame frame = new JFrame("Listes classes");
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setSize(800, 600);
//
////			OpenedTournamentPanel panel = new OpenedTournamentPanel(classesByLevel);
//			frame.getContentPane().add(panel);
//
//			frame.setVisible(true);
//		});
//	}
}
