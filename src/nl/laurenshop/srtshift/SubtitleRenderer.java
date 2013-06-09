package nl.laurenshop.srtshift;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SubtitleRenderer extends JLabel implements TableCellRenderer
{
	private static final long serialVersionUID = 1L;

	private SubtitleFile subs;

	public SubtitleRenderer(SubtitleFile subs)
	{
		this.subs = subs;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
	{
		Subtitle subtitle = subs.getSubtitles().get(row);
		setText(value.toString());
		if (column == 2 && !subtitle.isFixed())
			setForeground(Color.LIGHT_GRAY);
		else
			setForeground(Color.BLACK);
		return this;
	}

}
