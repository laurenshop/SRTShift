package nl.laurenshop;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SRTShiftTest
{

	@Test
	public void testTimeToDouble()
	{
		assertEquals(0d, SRTShift.timeToDouble("00:00:00,000"), 0.001);
		assertEquals(1d, SRTShift.timeToDouble("00:00:01,000"), 0.001);
		assertEquals(30d, SRTShift.timeToDouble("00:00:30,000"), 0.001);
		assertEquals(61d, SRTShift.timeToDouble("00:01:01,000"), 0.001);
		assertEquals(3661d, SRTShift.timeToDouble("01:01:01,000"), 0.001);
		assertEquals(1.5d, SRTShift.timeToDouble("00:00:01,500"), 0.001);
		assertEquals(1.5d, SRTShift.timeToDouble("00:01,500"), 0.001);
		assertEquals(1.5d, SRTShift.timeToDouble("01,500"), 0.001);
		assertEquals(1.5d, SRTShift.timeToDouble("1,500"), 0.001);
		assertEquals(61.5d, SRTShift.timeToDouble("1:01,500"), 0.001);
		assertEquals(-1.5d, SRTShift.timeToDouble("-00:00:01,500"), 0.001);
	}

	@Test
	public void testDoubleToTime()
	{
		assertEquals("00:00:00,000", SRTShift.doubleToTime(0d));
		assertEquals("00:00:01,000", SRTShift.doubleToTime(1d));
		assertEquals("00:00:30,000", SRTShift.doubleToTime(30d));
		assertEquals("00:01:01,000", SRTShift.doubleToTime(61d));
		assertEquals("01:01:01,000", SRTShift.doubleToTime(3661d));
		assertEquals("00:00:01,500", SRTShift.doubleToTime(1.5d));
		assertEquals("-00:00:01,500", SRTShift.doubleToTime(-1.5d));
	}

}