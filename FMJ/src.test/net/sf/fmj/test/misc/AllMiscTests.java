package net.sf.fmj.test.misc;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.fmj.media.codec.audio.RateConverterTest;
import net.sf.fmj.media.codec.audio.alaw.ALawEncoderDecoderTest;
import net.sf.fmj.media.codec.audio.ulaw.MuLawEncoderDecoderTest;
import net.sf.fmj.media.codec.video.jpeg.JpegRTPHeaderTest;
import net.sf.fmj.media.datasink.rtp.RTPUrlParserTest;
import net.sf.fmj.media.protocol.javasound.JavaSoundUrlParserTest;

/**
 * 
 * @author Ken Larson
 *
 */
public class AllMiscTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for net.sf.fmj.test.misc");
		//$JUnit-BEGIN$

		suite.addTestSuite(RTPUrlParserTest.class);
		suite.addTestSuite(MuLawEncoderDecoderTest.class);
		suite.addTestSuite(ALawEncoderDecoderTest.class);
		suite.addTestSuite(RateConverterTest.class);
		suite.addTestSuite(JavaSoundUrlParserTest.class);
		suite.addTestSuite(JpegRTPHeaderTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
