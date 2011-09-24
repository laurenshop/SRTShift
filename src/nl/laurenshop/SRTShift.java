package nl.laurenshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SRTShift
{
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		File infile = new File(args[0]);
		File bakfile = new File(args[0] + ".bak");
		int num = 0;
		while (bakfile.exists())
			bakfile = new File(args[0] + ".bak" + num++);

		infile.renameTo(bakfile);
		File outfile = infile;
		infile = bakfile;

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile), "ISO-8859-1"));
		ArrayList<ArrayList<String>> subtitles = new ArrayList<ArrayList<String>>();
		String line = reader.readLine();
		while (line != null)
		{
			ArrayList<String> subtitle = new ArrayList<String>();
			while (line != null && line.trim().length() != 0)
			{
				subtitle.add(line);
				line = reader.readLine();
			}
			if (!subtitle.get(2).startsWith("Downloaded From")
					&& !subtitle.get(2).startsWith("Gedownload van")
					&& !subtitle.get(2).startsWith("vertaling:")
					&& !subtitle.get(2).startsWith("Dutch"))
			{
				subtitles.add(subtitle);
			}
			line = reader.readLine();
		}
		reader.close();
		System.out.println(String.format("%d subtitles loaded", subtitles.size()));

		double start = timeToDouble(subtitles.get(0).get(1).split(" --> ")[0]);
		double newStart = start;
		double end = timeToDouble(subtitles.get(subtitles.size() - 1).get(1).split(" --> ")[0]);
		double newEnd = end;

		double shift = newStart - start;
		double stretch = (newEnd - newStart) / (end - start);

		for (int i = 1; i < args.length; i++)
		{
			if (args[i].equals("-s"))
			{
				i++;
				String[] oldnew = args[i].split("=");
				start = timeToDouble(oldnew[0]);
				newStart = timeToDouble(oldnew[1]);
				if (end == newEnd)
					newEnd = end + newStart - start;
			}
			if (args[i].equals("-e"))
			{
				i++;
				String[] oldnew = args[i].split("=");
				end = timeToDouble(oldnew[0]);
				newEnd = timeToDouble(oldnew[1]);
			}
			if (args[i].equals("-sh"))
			{
				i++;
				shift = timeToDouble(args[i]);
			}
			if (args[i].equals("-st"))
			{
				i++;
				stretch = Double.parseDouble(args[i]);
			}
		}

		if (shift == 0 && stretch == 1)
		{
			for (int i = 0; i < 4 && i < subtitles.size(); i++)
			{
				ArrayList<String> sub = subtitles.get(i);
				String[] times = sub.get(1).split(" --> ");
				System.out.println(String.format("%s %s %s", i == 0 ? "-> " : "   ", times[0],
						sub.get(2)));
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter time [" + doubleToTime(start) + "]: ");
			String startTime = in.readLine();
			if (startTime != null && startTime.length() > 0)
				newStart = timeToDouble(startTime);

			for (int i = subtitles.size() - 4; i < subtitles.size(); i++)
			{
				ArrayList<String> sub = subtitles.get(i);
				String[] times = sub.get(1).split(" --> ");
				System.out.println(String.format("%s %s %s", i == subtitles.size() - 1 ? "-> "
						: "   ", times[0], sub.get(2)));
			}

			System.out.print("Enter time [" + doubleToTime(end) + "]: ");
			String endTime = in.readLine();
			if (endTime != null && endTime.length() > 0)
				newEnd = timeToDouble(endTime);

		}

		System.out.println(String.format("%s = %s, %s = %s", doubleToTime(start),
				doubleToTime(newStart), doubleToTime(end), doubleToTime(newEnd)));
		if (newStart != start || newEnd != end)
		{
			shift = newStart - start;
			stretch = (newEnd - newStart) / (end - start);
		}

		System.out.println(String.format("Shift: %s\nStretch: %.5f (%s per minute)",
				doubleToTime(shift, true), stretch, doubleToTime((stretch - 1) * 60, true)));

		int count = 0;
		for (ArrayList<String> sub : subtitles)
		{
			String[] times = sub.get(1).split(" --> ");
			double from = timeToDouble(times[0]);
			double to = timeToDouble(times[1]);
			// if (from >= start && from <= end)
			{
				count++;
				from = start + shift + (from - start) * stretch;
				to = start + shift + (to - start) * stretch;
				sub.set(1, doubleToTime(from) + " --> " + doubleToTime(to));
			}
		}

		System.out.println(String.format("%d subtitles realigned", count));

		PrintWriter writer = new PrintWriter(outfile);
		for (ArrayList<String> sub : subtitles)
		{
			for (String l : sub)
				writer.println(l);
			writer.println();
		}
		writer.flush();
		writer.close();
	}

	public static double timeToDouble(String string)
	{
		String time = string;
		double signum = 1;
		if (string.startsWith("-") || string.startsWith("+"))
			time = string.substring(1);
		if (string.startsWith("-"))
			signum = -1;
		String[] hms = time.split(":");

		int i = 0;
		double h = hms.length > 2 ? Double.parseDouble(hms[i++]) : 0;
		double m = hms.length > 1 ? Double.parseDouble(hms[i++]) : 0;
		double s = Double.parseDouble(hms[i].replace(',', '.'));
		return signum * (3600 * h + 60 * m + s);
	}

	public static String doubleToTime(double d)
	{
		return doubleToTime(d, false);
	}

	public static String doubleToTime(double d, boolean signum)
	{
		double v = Math.abs(d);
		String sign = "";
		if (signum || d < 0)
			if (d > 0)
				sign = "+";
			else if (d < 0)
				sign = "-";

		return sign
				+ String.format("%02.0f:%02.0f:%06.3f", Math.floor(v / 3600d),
						Math.floor(v / 60d % 60d), v % 60);
	}

}
