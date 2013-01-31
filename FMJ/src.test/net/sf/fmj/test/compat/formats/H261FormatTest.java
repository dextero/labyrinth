package net.sf.fmj.test.compat.formats;

import java.awt.Dimension;

import javax.media.format.H261Format;

import junit.framework.TestCase;

public class H261FormatTest extends TestCase
{
	public void testConstructors()
	{
		{
			final H261Format f1 = new H261Format();
			assertEquals(f1.getDataType(), byte[].class);
			assertEquals(f1.getFrameRate(), -1.f);
			assertEquals(f1.getMaxDataLength(), -1);
			assertEquals(f1.getSize(), null);
			assertEquals(f1.getStillImageTransmission(), -1);
		}
		
		
		{
			final Dimension d = new Dimension(1, 2);
			final H261Format f1 = new H261Format(d, 1, int[].class, 2.f, 3);
			assertEquals(f1.getDataType(), int[].class);
			assertEquals(f1.getFrameRate(), 2.f);
			assertEquals(f1.getMaxDataLength(), 1);
			assertEquals(f1.getSize(), new Dimension(1, 2));
			assertTrue(f1.getSize() != d);
			assertEquals(f1.getStillImageTransmission(), 3);
		}
	}
	
	private void assertEquals(double a, double b)
	{
		assertTrue(a == b);
	}
}
