package net.sf.fmj.media.codec.audio.alaw;

import junit.framework.TestCase;

/**
 * 
 * @author Ken Larson
 *
 */
public class ALawEncoderDecoderTest extends TestCase 
{


	public void testTable()
	{
		for (int i = 0; i <= 255; ++i)
		{
			byte b = (byte) i;
			test(b);
		}
	}

	private void test(byte b)
	{
		short s = ALawDecoderUtil.aLawDecode(b);
		byte bEncoded = ALawEncoderUtil.aLawEncode(s);
		//if (b != bEncoded)
		//	System.err.println("b != bEncoded " + b + "  " + bEncoded + " s=" + s);
		assertEquals(b, bEncoded);
	}
}
