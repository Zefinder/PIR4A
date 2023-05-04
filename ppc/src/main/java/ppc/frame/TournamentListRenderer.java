package ppc.frame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

public class TournamentListRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7554051951927620728L;

	public TournamentListRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (index % 2 == 0) {
			c.setBackground(new Color(0xE2, 0xE2, 0xE2));
		} else {
			c.setBackground(new Color(0xFF, 0xFF, 0xFF));
		}

		if (isSelected) {
			c.setBackground(UIManager.getDefaults().getColor("List.selectionBackground"));
		}

		return c;
	}

}
