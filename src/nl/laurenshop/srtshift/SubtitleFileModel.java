package nl.laurenshop.srtshift;

import javax.swing.table.AbstractTableModel;

public class SubtitleFileModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	private SubtitleFile subs;

	public SubtitleFileModel(SubtitleFile subs)
	{
		this.subs = subs;
	}

	@Override
	public int getRowCount()
	{
		return subs.getSubtitles().size();
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Subtitle subtitle = subs.getSubtitles().get(rowIndex);
		switch (columnIndex)
		{
			case 0:
				return subtitle.getText().get(0);
			case 1:
				return SRTShift.doubleToTime(subtitle.getOriginalFrom());
			case 2:
				return SRTShift.doubleToTime(subtitle.getFrom());
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		Subtitle subtitle = subs.getSubtitles().get(rowIndex);
		switch (columnIndex)
		{
			case 0:
				subtitle.setTextAsOneLine(aValue.toString());
				break;
			case 1:
				subtitle.setOriginalFrom(SRTShift.timeToDouble(aValue.toString()));
				break;
			case 2:

				if (aValue.toString().equals(""))
				{
					if (rowIndex == 0 || rowIndex == subs.getSubtitles().size() - 1)
						subs.modifyFrom(rowIndex, SRTShift.timeToDouble(aValue.toString()));
					else
						subs.removeFrom(rowIndex);
				}
				else if (!aValue.toString().equals(SRTShift.doubleToTime(subtitle.getFrom())))
				{
					subs.modifyFrom(rowIndex, SRTShift.timeToDouble(aValue.toString()));
				}
				break;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 2;
	}

	@Override
	public String getColumnName(int column)
	{
		switch (column)
		{
			case 0:
				return "Subtitle";
			case 1:
				return "Original";
			case 2:
				return "New";
		}
		return null;
	}
}
