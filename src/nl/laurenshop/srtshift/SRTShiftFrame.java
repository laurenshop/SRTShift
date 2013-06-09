package nl.laurenshop.srtshift;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

public class SRTShiftFrame extends JFrame implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JTable table;

	private SubtitleFile subs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException
	{

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					SRTShiftFrame frame = new SRTShiftFrame();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error message",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public SRTShiftFrame() throws IllegalArgumentException, IOException
	{
		super("SRTShift subtitle shifter");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		table = new JTable();
		table.setGridColor(Color.LIGHT_GRAY);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);

		JButton btnOpen = new JButton("Open");
		btnOpen.setActionCommand("open");
		btnOpen.addActionListener(this);
		panel.add(btnOpen);
		JButton btnSave = new JButton("Save");
		btnSave.setActionCommand("save");
		btnSave.addActionListener(this);
		panel.add(btnSave);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			if ("open".equals(event.getActionCommand()))
				open();
			else if ("save".equals(event.getActionCommand()))
				save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void open() throws IOException
	{
		FileDialog fd = new FileDialog(this);
		fd.setFilenameFilter(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".srt");
			}
		});
		fd.setDirectory(System.getProperty("user.dir"));
		fd.setVisible(true);
		String file = fd.getFile();
		if (file == null)
			return;

		subs = new SubtitleFile(new File(file));
		table.setModel(new SubtitleFileModel(subs));
		table.setDefaultRenderer(Object.class, new SubtitleRenderer(subs));
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(75);
		table.getColumnModel().getColumn(2).setPreferredWidth(75);
	}

	public void save() throws IOException
	{
		subs.save();
	}

}
