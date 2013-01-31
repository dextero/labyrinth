package net.sf.fmj.test.compat;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.fmj.test.compat.buffers.BufferTest;
import net.sf.fmj.test.compat.buffers.BufferToImageTest;
import net.sf.fmj.test.compat.buffers.BufferToImageTest_Int;
import net.sf.fmj.test.compat.codec.AudioCodecTest;
import net.sf.fmj.test.compat.datasources.DataSourceTest;
import net.sf.fmj.test.compat.datasources.URLDataSourceTest;
import net.sf.fmj.test.compat.demux.DemuxTest;
import net.sf.fmj.test.compat.formats.AudioFormatTest;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest1;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest2;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest3;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest4;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest5;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest6;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest7;
import net.sf.fmj.test.compat.formats.BigGeneratedAudioFormatTest8;
import net.sf.fmj.test.compat.formats.FormatCrossFormatTest;
import net.sf.fmj.test.compat.formats.FormatEncodingCodeTest;
import net.sf.fmj.test.compat.formats.FormatEncodingCodeValuesTest;
import net.sf.fmj.test.compat.formats.FormatMatchesIntersectsTest;
import net.sf.fmj.test.compat.formats.FormatPrivateTest;
import net.sf.fmj.test.compat.formats.FormatTest;
import net.sf.fmj.test.compat.formats.H261FormatTest;
import net.sf.fmj.test.compat.formats.H263FormatTest;
import net.sf.fmj.test.compat.formats.IndexedColorFormatTest;
import net.sf.fmj.test.compat.formats.JPEGFormatTest;
import net.sf.fmj.test.compat.formats.RGBFormatTest;
import net.sf.fmj.test.compat.formats.SerializableTest;
import net.sf.fmj.test.compat.formats.VideoFormatTest;
import net.sf.fmj.test.compat.formats.WavAudioFormatTest;
import net.sf.fmj.test.compat.formats.YUVFormatTest;
import net.sf.fmj.test.compat.generated.ConcreteClassesTest;
import net.sf.fmj.test.compat.generated.ImplClassesTest;
import net.sf.fmj.test.compat.generated.InterfaceClassesTest;
import net.sf.fmj.test.compat.misc.CaptureDeviceManagerTest;
import net.sf.fmj.test.compat.misc.FMJTest;
import net.sf.fmj.test.compat.misc.InputSourceStreamTest;
import net.sf.fmj.test.compat.misc.ManagerTest;
import net.sf.fmj.test.compat.misc.PackageManagerTest;
import net.sf.fmj.test.compat.misc.ProcessorModelTest;
import net.sf.fmj.test.compat.playerbean.MediaPlayerBeanInfoTest;
import net.sf.fmj.test.compat.plugins.PlugInManagerTest;
import net.sf.fmj.test.compat.rtp.RTPHeaderTest;
import net.sf.fmj.test.compat.rtp.RTPManagerTest;
import net.sf.fmj.test.compat.rtp.RTPSessionMgrTest;
import net.sf.fmj.test.compat.rtp.SessionAddressTest;
import net.sf.fmj.test.compat.sun.AWTRendererTest;
import net.sf.fmj.test.compat.sun.AviVideoFormatTest;
import net.sf.fmj.test.compat.sun.BasicCodecTest;
import net.sf.fmj.test.compat.sun.BasicPlugInTest;
import net.sf.fmj.test.compat.sun.BitMapInfoTest;
import net.sf.fmj.test.compat.sun.JavaSoundRendererTest;
import net.sf.fmj.test.compat.sun.MimeManagerTest;
import net.sf.fmj.test.compat.sun.RTPSyncBufferMuxTest;
import net.sf.fmj.test.compat.sun.RawBufferMuxTest;
import net.sf.fmj.test.compat.sun.RawParserTest;
import net.sf.fmj.test.compat.sun.RawPullBufferParserTest;
import net.sf.fmj.test.compat.sun.RawPullStreamParserTest;
import net.sf.fmj.test.compat.sun.ULawDePacketizerTest;
import net.sf.fmj.test.compat.sun.VideoCodecTest;

/**
 * 
 * @author Ken Larson
 *
 */
public class AllCompatabilityTests
{

	public static Test suite()
	{
		// set up properties so that 
		// A) the registry will contain the same things that JMF does
		// B) we use a different file for the registry so it doesn't interfere with normal use
		System.setProperty("net.sf.fmj.utility.JmfRegistry.disableLoad", "true");
		System.setProperty("net.sf.fmj.utility.JmfRegistry.JMFDefaults", "true");
		System.setProperty("javax.media.pim.PlugInManagerInitializer.JMFDefaults", "true");
		System.setProperty("net.sf.fmj.utility.JmfRegistry.filename", ".fmj.registry.junit.xml");
		
		TestSuite suite = new TestSuite("Test for com.lti.fmj.test");

		//$JUnit-BEGIN$
		
		// buffers:
		suite.addTestSuite(BufferTest.class);
		suite.addTestSuite(BufferToImageTest_Int.class);
		suite.addTestSuite(BufferToImageTest.class);
		
		// datasources:
		suite.addTestSuite(DataSourceTest.class);
		suite.addTestSuite(URLDataSourceTest.class);
		
		// demux:
		suite.addTestSuite(DemuxTest.class);
		
		// formats:
		suite.addTestSuite(AudioFormatTest.class);
		suite.addTestSuite(FormatCrossFormatTest.class);
		suite.addTestSuite(FormatEncodingCodeTest.class);
		suite.addTestSuite(FormatEncodingCodeValuesTest.class);
		suite.addTestSuite(FormatMatchesIntersectsTest.class);
		suite.addTestSuite(FormatPrivateTest.class);
		suite.addTestSuite(FormatTest.class);
		suite.addTestSuite(H261FormatTest.class);
		suite.addTestSuite(H263FormatTest.class);
		suite.addTestSuite(IndexedColorFormatTest.class);
		suite.addTestSuite(JPEGFormatTest.class);
		suite.addTestSuite(RGBFormatTest.class);
		suite.addTestSuite(SerializableTest.class);
		suite.addTestSuite(VideoFormatTest.class);
		suite.addTestSuite(WavAudioFormatTest.class);
		suite.addTestSuite(YUVFormatTest.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest1.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest2.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest3.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest4.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest5.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest6.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest7.class);
		suite.addTestSuite(BigGeneratedAudioFormatTest8.class);

		// generated:
		suite.addTestSuite(ConcreteClassesTest.class);
		suite.addTestSuite(ImplClassesTest.class);
		suite.addTestSuite(InterfaceClassesTest.class);

		// misc:
		suite.addTestSuite(CaptureDeviceManagerTest.class);
		suite.addTestSuite(FMJTest.class);
		suite.addTestSuite(InputSourceStreamTest.class);
		suite.addTestSuite(ManagerTest.class);
		suite.addTestSuite(PackageManagerTest.class);
		suite.addTestSuite(ProcessorModelTest.class);

		// plugins:
		suite.addTestSuite(PlugInManagerTest.class);
		
		// rtp:
		suite.addTestSuite(RTPHeaderTest.class);
		suite.addTestSuite(RTPManagerTest.class);
		suite.addTestSuite(RTPSessionMgrTest.class);
		suite.addTestSuite(SessionAddressTest.class);
		
		
		// playerbean:
		suite.addTestSuite(MediaPlayerBeanInfoTest.class);
		
		// sun:
		suite.addTestSuite(AviVideoFormatTest.class);
		suite.addTestSuite(AWTRendererTest.class);
		suite.addTestSuite(BasicCodecTest.class);
		suite.addTestSuite(BasicPlugInTest.class);
		suite.addTestSuite(VideoCodecTest.class);
		suite.addTestSuite(RawParserTest.class);
		suite.addTestSuite(RawPullStreamParserTest.class);
		suite.addTestSuite(RawPullBufferParserTest.class);
		suite.addTestSuite(MimeManagerTest.class);
		suite.addTestSuite(BitMapInfoTest.class);
		suite.addTestSuite(JavaSoundRendererTest.class);
		suite.addTestSuite(ULawDePacketizerTest.class);
		suite.addTestSuite(RawBufferMuxTest.class);
		suite.addTestSuite(RTPSyncBufferMuxTest.class);
		
		
		// codecs:
		suite.addTestSuite(AudioCodecTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
