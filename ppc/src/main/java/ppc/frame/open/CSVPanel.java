package ppc.frame.open;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ppc.annotation.EventHandler;
import ppc.event.Listener;
import ppc.event.TournamentDeleteClassEvent;
import ppc.event.TournamentDeleteClassStatusEvent;
import ppc.frame.TournamentListRenderer;
import ppc.frame.TournamentTableModel;
import ppc.manager.EventManager;
import ppc.manager.LogsManager;

public class CSVPanel extends JPanel implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2424923812357180732L;

	private JList<String> listClasses;
	private DefaultListModel<String> model;
	private JPanel csvPanel;

	private List<List<Map<String, String[][]>>> data;
	private List<JScrollPane> scrollList;

	private int classNumber;
	private String tournamentName;

	public CSVPanel() {
		EventManager.getInstance().registerListener(this);

		classNumber = 0;
		data = new ArrayList<>();
		scrollList = new ArrayList<>();
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
		JButton addStudent = new JButton("Ajouter un élève");
		panel.add(addStudent, c);

		c.gridx = 0;
		c.gridy = 2;
		JButton removeStudent = new JButton("Retirer un élève");
		panel.add(removeStudent, c);

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

	public void addClass(List<Map<String, String[][]>> classData) {
		// Getting prof's name
		String profName = classData.get(0).keySet().toArray(String[]::new)[0];

		// Adding to data list
		data.add(classData);

		// Adding to names' list
		model.addElement("Classe de " + profName);
		if (model.getSize() > 15)
			listClasses.setVisibleRowCount(15);
		else
			listClasses.setVisibleRowCount(model.getSize());

		// Adding table to CardLayout
		addTableToPanel(classData, profName + " " + classNumber++);
	}

	public void removeClass() {
		int selectedIndex = listClasses.getSelectedIndex();
		if (selectedIndex != -1) {
			String[] panelName = scrollList.get(selectedIndex).getName().split(" ");
			int classIndex = Integer.valueOf(panelName[panelName.length - 1]);
			
			TournamentDeleteClassEvent event = new TournamentDeleteClassEvent(tournamentName, classIndex, classIndex);
			EventManager.getInstance().callEvent(event);
		} else
			LogsManager.getInstance().writeWarningMessage("No class were selected before removing...");
	}
	
	public void setTounamentName(String tournamentName) {
		this.tournamentName = tournamentName;
	}
	
	@EventHandler
	public void onClassRemoved(TournamentDeleteClassStatusEvent event) {
		int selectedIndex = event.getListIndex();
		
		System.out.println("AAAAAH");
		
		// Remove from list
		model.remove(selectedIndex);
		
		// Remove from data
		data.remove(selectedIndex);
		
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
		
		// Revalidate frame
		revalidate();
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

		JTable defaultTable = new JTable(tableModel);
		defaultTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		defaultTable.setPreferredScrollableViewportSize(defaultTable.getPreferredSize());

		JScrollPane defaultTablePanel = new JScrollPane(defaultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		defaultTablePanel.setName(profName);

		scrollList.add(defaultTablePanel);
		csvPanel.add(defaultTablePanel, profName);
	}

}
