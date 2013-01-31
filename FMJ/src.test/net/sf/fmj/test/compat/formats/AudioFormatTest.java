package net.sf.fmj.test.compat.formats;

import javax.media.Format;
import javax.media.format.AudioFormat;

import junit.framework.TestCase;

/**
 * 
 * @author Ken Larson
 *
 */
public class AudioFormatTest extends TestCase
{


	public void testConstructors()
	{
		
		{
			final AudioFormat f = new AudioFormat("abc");
			assertEquals(f.getChannels(), -1);
			assertEquals(f.getDataType(), byte[].class);
			assertEquals(f.getEncoding(), "abc");
			assertEquals(f.getEndian(), -1);
			assertEquals(f.getFrameRate(), -1.0);
			assertEquals(f.getFrameSizeInBits(), -1);
			assertEquals(f.getSampleRate(), -1.0);
			assertEquals(f.getSampleSizeInBits(), -1);
			assertEquals(f.getSigned(), -1);
			
		}
		
		{
			final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3);
			assertEquals(f.getChannels(), 3);
			assertEquals(f.getDataType(), byte[].class);
			assertEquals(f.getEncoding(), "abc");
			assertEquals(f.getEndian(), -1);
			assertEquals(f.getFrameRate(), -1.0);
			assertEquals(f.getFrameSizeInBits(), -1);
			assertEquals(f.getSampleRate(), 1.0);
			assertEquals(f.getSampleSizeInBits(), 2);
			assertEquals(f.getSigned(), -1);
			
		}
		
		{
			final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3, 4, 5);
			assertEquals(f.getChannels(), 3);
			assertEquals(f.getDataType(), byte[].class);
			assertEquals(f.getEncoding(), "abc");
			assertEquals(f.getEndian(), 4);
			assertEquals(f.getFrameRate(), -1.0);
			assertEquals(f.getFrameSizeInBits(), -1);
			assertEquals(f.getSampleRate(), 1.0);
			assertEquals(f.getSampleSizeInBits(), 2);
			assertEquals(f.getSigned(), 5);
			
		}
		
		{
			final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3, 4, 5, 6, 7, int[].class);
			assertEquals(f.getChannels(), 3);
			assertEquals(f.getDataType(), int[].class);
			assertEquals(f.getEncoding(), "abc");
			assertEquals(f.getEndian(), 4);
			assertEquals(f.getFrameRate(), 7.0);
			assertEquals(f.getFrameSizeInBits(), 6);
			assertEquals(f.getSampleRate(), 1.0);
			assertEquals(f.getSampleSizeInBits(), 2);
			assertEquals(f.getSigned(), 5);
			
		}
		
	}
	
	public void testMatchesNull()
	{
		assertFalse(new AudioFormat("speex/rtp", 8000.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray).matches(null));
		assertFalse(new Format("speex/rtp").matches(null));
		
	}
	
	
	private void assertEquals(double a, double b)
	{
		assertTrue(a == b);
	}
}
