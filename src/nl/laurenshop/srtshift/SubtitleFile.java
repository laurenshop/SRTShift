package nl.laurenshop.srtshift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SubtitleFile
{
	private List<Subtitle> subtitles = new ArrayList<Subtitle>();

	private File file;

	public SubtitleFile(File infile) throws IOException, IllegalArgumentException
	{
		file = infile;
		BufferedReader reader =
			new BufferedReader(new InputStreamReader(new FileInputStream(infile), "ISO-8859-1"));
		String line = reader.readLine();
		while (line != null)
		{
			ArrayList<String> lines = new ArrayList<String>();
			while (line != null && line.trim().length() != 0)
			{
				lines.add(line);
				line = reader.readLine();
			}

			if (lines.size() > 2 && !lines.get(2).startsWith("Downloaded From")
				&& !lines.get(2).startsWith("Gedownload van")
				&& !lines.get(2).startsWith("vertaling:") && !lines.get(2).startsWith("Dutch"))
			{
				subtitles.add(new Subtitle(lines.subList(1, lines.size())));
			}
			line = reader.readLine();
		}
		reader.close();
		subtitles.get(0).setFixed(true);
		subtitles.get(subtitles.size() - 1).setFixed(true);
	}

	public void writeToFile(File outfile) throws IOException
	{
		PrintWriter writer = new PrintWriter(outfile, "ISO-8859-1");
		for (int i = 0; i < subtitles.size(); i++)
		{
			Subtitle subtitle = subtitles.get(i);
			writer.println(i);
			writer.println(subtitle.getTimeLine());
			for (String line : subtitle.getText())
				writer.println(line);
			writer.println();
		}
		writer.close();
	}

	public void save() throws IOException
	{
		String name = file.getName();
		File bakfile = new File(name + ".bak");
		int num = 0;
		while (bakfile.exists())
			bakfile = new File(name + ".bak" + num++);
		copy(file, bakfile);
		writeToFile(file);
	}

	public List<Subtitle> getSubtitles()
	{
		return subtitles;
	}

	private int findFixedBefore(int index)
	{
		int lower = index - 1;
		while (lower > 0 && !subtitles.get(lower).isFixed())
			lower--;
		return lower;
	}

	private int findFixedAfter(int index)
	{
		int upper = index + 1;
		while (upper < subtitles.size() - 1 && !subtitles.get(upper).isFixed())
			upper++;
		return upper;
	}

	public void modifyFrom(int index, double from)
	{
		subtitles.get(index).setFrom(from);
		subtitles.get(index).setFixed(true);
		if (index > 0)
		{
			int lower = findFixedBefore(index);
			realign(lower, index);
		}
		if (index < subtitles.size() - 1)
		{
			int upper = findFixedAfter(index);
			realign(index, upper);
		}

	}

	public void removeFrom(int index)
	{
		subtitles.get(index).setFixed(false);
		int lower = index - 1;
		while (lower > 0 && !subtitles.get(lower).isFixed())
			lower--;

		int upper = index + 1;
		while (upper < subtitles.size() - 1 && !subtitles.get(upper).isFixed())
			upper++;
		realign(lower, upper);
	}

	private void realign(int lower, int upper)
	{
		Subtitle lowerSub = subtitles.get(lower);
		Subtitle upperSub = subtitles.get(upper);
		double shift = lowerSub.getFrom() - lowerSub.getOriginalFrom();
		double stretch =
			(upperSub.getFrom() - lowerSub.getFrom())
				/ (upperSub.getOriginalFrom() - lowerSub.getOriginalFrom());
		for (int index = lower; index < upper; index++)
		{
			Subtitle sub = subtitles.get(index);
			// from = start + shift + (from - start) * stretch;
			// to = start + shift + (to - start) * stretch;

			sub.setFrom(lowerSub.getOriginalFrom() + shift
				+ (sub.getOriginalFrom() - lowerSub.getOriginalFrom()) * stretch);
			sub.setTo(lowerSub.getOriginalFrom() + shift
				+ (sub.getOriginalTo() - lowerSub.getOriginalFrom()) * stretch);
		}
	}

	private static void copy(File from, File to) throws IOException
	{
		BufferedReader reader =
			new BufferedReader(new InputStreamReader(new FileInputStream(from), "ISO-8859-1"));
		PrintWriter writer = new PrintWriter(to, "ISO-8859-1");
		String line = reader.readLine();
		while (line != null)
		{
			writer.println(line);
			line = reader.readLine();
		}
		reader.close();
		writer.close();
	}
}
