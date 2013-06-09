package nl.laurenshop.srtshift;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subtitle
{
	private double from;

	private double to;

	private List<String> text;

	private boolean fixed;

	private double originalFrom;

	private double originalTo;

	@Override
	public String toString()
	{
		return getTimeLine() + "\n" + join(text, "\n");
	}

	public String getTimeLine()
	{
		return SRTShift.doubleToTime(from) + " --> " + SRTShift.doubleToTime(to);
	}

	public static String join(List<String> strings, String separator)
	{
		StringBuilder builder = new StringBuilder();
		for (String string : strings)
		{
			if (builder.length() != 0)
				builder.append(separator);
			builder.append(string);
		}
		return builder.toString();
	}

	private static final String TIME = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";

	private static final Pattern TIMELINE = Pattern.compile("^(" + TIME + ") --> (" + TIME + ")$");

	public Subtitle(List<String> lines) throws IllegalArgumentException
	{
		Matcher matcher = TIMELINE.matcher(lines.get(0));
		if (!matcher.matches())
			throw new IllegalArgumentException("Invalid subtitle time line: \"" + lines.get(0)
				+ "\"");

		from = SRTShift.timeToDouble(matcher.group(1));
		to = SRTShift.timeToDouble(matcher.group(2));
		originalFrom = from;
		originalTo = to;
		fixed = false;
		text = lines.subList(1, lines.size());
	}

	public double getFrom()
	{
		return from;
	}

	public void setFrom(double from)
	{
		this.from = from;
	}

	public double getTo()
	{
		return to;
	}

	public void setTo(double to)
	{
		this.to = to;
	}

	public List<String> getText()
	{
		return text;
	}

	public void setText(List<String> text)
	{
		this.text = text;
	}

	public String getTextAsOneLine()
	{
		return join(text, "//");
	}

	public void setTextAsOneLine(String line)
	{
		text = Arrays.asList(line.split("//"));
	}

	public boolean isFixed()
	{
		return fixed;
	}

	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
	}

	public double getOriginalFrom()
	{
		return originalFrom;
	}

	public void setOriginalFrom(double originalFrom)
	{
		this.originalFrom = originalFrom;
	}

	public double getOriginalTo()
	{
		return originalTo;
	}

	public void setOriginalTo(double originalTo)
	{
		this.originalTo = originalTo;
	}

}
