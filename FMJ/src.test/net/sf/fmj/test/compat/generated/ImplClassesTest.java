package net.sf.fmj.test.compat.generated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class ImplClassesTest extends TestCase
{

	public void test_javax_media_pim_PlugInManager() throws Exception
	{
		assertEquals(javax.media.pim.PlugInManager.class.getModifiers(), 1);
		assertTrue(!javax.media.pim.PlugInManager.class.isInterface());
		assertTrue(javax.media.pim.PlugInManager.class.getSuperclass().equals(javax.media.PlugInManager.class));
		// Static fields: 
		assertTrue(javax.media.pim.PlugInManager.DEMULTIPLEXER == 1);
		assertTrue(javax.media.pim.PlugInManager.CODEC == 2);
		assertTrue(javax.media.pim.PlugInManager.EFFECT == 3);
		assertTrue(javax.media.pim.PlugInManager.RENDERER == 4);
		assertTrue(javax.media.pim.PlugInManager.MULTIPLEXER == 5);

		// Methods (reflection): 
		if (true) {
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("commit", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("getPlugInList", new Class[]{javax.media.Format.class, javax.media.Format.class, int.class});
			assertEquals(m.getReturnType(), java.util.Vector.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("setPlugInList", new Class[]{java.util.Vector.class, int.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("addPlugIn", new Class[]{java.lang.String.class, javax.media.Format[].class, javax.media.Format[].class, int.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("removePlugIn", new Class[]{java.lang.String.class, int.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("getSupportedInputFormats", new Class[]{java.lang.String.class, int.class});
			assertEquals(m.getReturnType(), javax.media.Format[].class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("getSupportedOutputFormats", new Class[]{java.lang.String.class, int.class});
			assertEquals(m.getReturnType(), javax.media.Format[].class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("hashCode", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 257);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("getClass", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Class.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("wait", new Class[]{long.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("wait", new Class[]{long.class, int.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("wait", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("equals", new Class[]{java.lang.Object.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("notify", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("notifyAll", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pim.PlugInManager.class.getMethod("toString", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.String.class);
			assertEquals(m.getModifiers(), 1);
		}
		}

		// Constructors (reflection): 
		// No need to allow constructor to be public (Acceptable FMJ/JMF difference)
//		if (true) {
//		{
//			final Constructor c = javax.media.pim.PlugInManager.class.getConstructor(new Class[]{});
//			assertEquals(c.getModifiers(), 1);
//		}
//		}

		// Methods (compilation): 
		if (false) {
		javax.media.pim.PlugInManager o = null;
		javax.media.pim.PlugInManager.commit();
		javax.media.pim.PlugInManager.getPlugInList((javax.media.Format) null, (javax.media.Format) null, 0);
		javax.media.pim.PlugInManager.setPlugInList((java.util.Vector) null, 0);
		javax.media.pim.PlugInManager.addPlugIn((java.lang.String) null, (javax.media.Format[]) null, (javax.media.Format[]) null, 0);
		javax.media.pim.PlugInManager.removePlugIn((java.lang.String) null, 0);
		javax.media.pim.PlugInManager.getSupportedInputFormats((java.lang.String) null, 0);
		javax.media.pim.PlugInManager.getSupportedOutputFormats((java.lang.String) null, 0);
		o.hashCode();
		o.getClass();
		o.wait(0L);
		o.wait(0L, 0);
		o.wait();
		o.equals((java.lang.Object) null);
		o.notify();
		o.notifyAll();
		o.toString();
		}
		// Constructors (compilation): 
		// No need to allow constructor to be public (Acceptable FMJ/JMF difference)
//		if (false) {
//		javax.media.pim.PlugInManager o = null;
//		new javax.media.pim.PlugInManager();
//		}
	}
	public void test_javax_media_pm_PackageManager() throws Exception
	{
		assertEquals(javax.media.pm.PackageManager.class.getModifiers(), 17);
		assertTrue(!javax.media.pm.PackageManager.class.isInterface());
		assertTrue(javax.media.pm.PackageManager.class.getSuperclass().equals(javax.media.PackageManager.class));
		// Methods (reflection): 
		if (true) {
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("getProtocolPrefixList", new Class[]{});
			assertEquals(m.getReturnType(), java.util.Vector.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("setProtocolPrefixList", new Class[]{java.util.Vector.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("commitProtocolPrefixList", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("getContentPrefixList", new Class[]{});
			assertEquals(m.getReturnType(), java.util.Vector.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("setContentPrefixList", new Class[]{java.util.Vector.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("commitContentPrefixList", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("hashCode", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 257);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("getClass", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Class.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("wait", new Class[]{long.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("wait", new Class[]{long.class, int.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("wait", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("equals", new Class[]{java.lang.Object.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("notify", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("notifyAll", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.pm.PackageManager.class.getMethod("toString", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.String.class);
			assertEquals(m.getModifiers(), 1);
		}
		}

		// Constructors (reflection): 
		if (true) {
		{
			final Constructor c = javax.media.pm.PackageManager.class.getConstructor(new Class[]{});
			assertEquals(c.getModifiers(), 1);
		}
		}

		// Methods (compilation): 
		if (false) {
		javax.media.pm.PackageManager o = null;
		javax.media.pm.PackageManager.getProtocolPrefixList();
		javax.media.pm.PackageManager.setProtocolPrefixList((java.util.Vector) null);
		javax.media.pm.PackageManager.commitProtocolPrefixList();
		javax.media.pm.PackageManager.getContentPrefixList();
		javax.media.pm.PackageManager.setContentPrefixList((java.util.Vector) null);
		javax.media.pm.PackageManager.commitContentPrefixList();
		o.hashCode();
		o.getClass();
		o.wait(0L);
		o.wait(0L, 0);
		o.wait();
		o.equals((java.lang.Object) null);
		o.notify();
		o.notifyAll();
		o.toString();
		}
		// Constructors (compilation): 
		if (false) {
		javax.media.pm.PackageManager o = null;
		new javax.media.pm.PackageManager();
		}
	}
	public void test_com_sun_media_format_WavAudioFormat() throws Exception
	{
		assertEquals(com.sun.media.format.WavAudioFormat.class.getModifiers(), 1);
		assertTrue(!com.sun.media.format.WavAudioFormat.class.isInterface());
		assertTrue(com.sun.media.format.WavAudioFormat.class.getSuperclass().equals(javax.media.format.AudioFormat.class));
		// Static fields: 
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_PCM == 1);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_ADPCM == 2);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_ALAW == 6);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_MULAW == 7);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_OKI_ADPCM == 16);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_DIGISTD == 21);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_DIGIFIX == 22);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_GSM610 == 49);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_IBM_FORMAT_MULAW == 257);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_IBM_FORMAT_ALAW == 258);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_IBM_FORMAT_ADPCM == 259);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_DVI_ADPCM == 17);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_SX7383 == 7175);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_DSPGROUP_TRUESPEECH == 34);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_MSNAUDIO == 50);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_MSG723 == 66);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_MPEG_LAYER3 == 85);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_AC8 == 112);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_AC10 == 113);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_AC16 == 114);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_AC20 == 115);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_METAVOICE == 116);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_METASOUND == 117);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_RT29H == 118);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_VR12 == 119);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_VR18 == 120);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_TQ40 == 121);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_VOXWARE_TQ60 == 129);
		assertTrue(com.sun.media.format.WavAudioFormat.WAVE_FORMAT_MSRT24 == 130);
		// TODO: test formatMapper of type java.util.Hashtable
		// TODO: test reverseFormatMapper of type java.util.Hashtable
		assertTrue(com.sun.media.format.WavAudioFormat.BIG_ENDIAN == 1);
		assertTrue(com.sun.media.format.WavAudioFormat.LITTLE_ENDIAN == 0);
		assertTrue(com.sun.media.format.WavAudioFormat.SIGNED == 1);
		assertTrue(com.sun.media.format.WavAudioFormat.UNSIGNED == 0);
		assertTrue(com.sun.media.format.WavAudioFormat.LINEAR.equals("LINEAR"));
		assertTrue(com.sun.media.format.WavAudioFormat.ULAW.equals("ULAW"));
		assertTrue(com.sun.media.format.WavAudioFormat.ULAW_RTP.equals("ULAW/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.ALAW.equals("alaw"));
		assertTrue(com.sun.media.format.WavAudioFormat.IMA4.equals("ima4"));
		assertTrue(com.sun.media.format.WavAudioFormat.IMA4_MS.equals("ima4/ms"));
		assertTrue(com.sun.media.format.WavAudioFormat.MSADPCM.equals("msadpcm"));
		assertTrue(com.sun.media.format.WavAudioFormat.DVI.equals("dvi"));
		assertTrue(com.sun.media.format.WavAudioFormat.DVI_RTP.equals("dvi/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.G723.equals("g723"));
		assertTrue(com.sun.media.format.WavAudioFormat.G723_RTP.equals("g723/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.G728.equals("g728"));
		assertTrue(com.sun.media.format.WavAudioFormat.G728_RTP.equals("g728/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.G729.equals("g729"));
		assertTrue(com.sun.media.format.WavAudioFormat.G729_RTP.equals("g729/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.G729A.equals("g729a"));
		assertTrue(com.sun.media.format.WavAudioFormat.G729A_RTP.equals("g729a/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.GSM.equals("gsm"));
		assertTrue(com.sun.media.format.WavAudioFormat.GSM_MS.equals("gsm/ms"));
		assertTrue(com.sun.media.format.WavAudioFormat.GSM_RTP.equals("gsm/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.MAC3.equals("MAC3"));
		assertTrue(com.sun.media.format.WavAudioFormat.MAC6.equals("MAC6"));
		assertTrue(com.sun.media.format.WavAudioFormat.TRUESPEECH.equals("truespeech"));
		assertTrue(com.sun.media.format.WavAudioFormat.MSNAUDIO.equals("msnaudio"));
		assertTrue(com.sun.media.format.WavAudioFormat.MPEGLAYER3.equals("mpeglayer3"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREAC8.equals("voxwareac8"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREAC10.equals("voxwareac10"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREAC16.equals("voxwareac16"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREAC20.equals("voxwareac20"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREMETAVOICE.equals("voxwaremetavoice"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREMETASOUND.equals("voxwaremetasound"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWARERT29H.equals("voxwarert29h"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREVR12.equals("voxwarevr12"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWAREVR18.equals("voxwarevr18"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWARETQ40.equals("voxwaretq40"));
		assertTrue(com.sun.media.format.WavAudioFormat.VOXWARETQ60.equals("voxwaretq60"));
		assertTrue(com.sun.media.format.WavAudioFormat.MSRT24.equals("msrt24"));
		assertTrue(com.sun.media.format.WavAudioFormat.MPEG.equals("mpegaudio"));
		assertTrue(com.sun.media.format.WavAudioFormat.MPEG_RTP.equals("mpegaudio/rtp"));
		assertTrue(com.sun.media.format.WavAudioFormat.DOLBYAC3.equals("dolbyac3"));
		assertTrue(com.sun.media.format.WavAudioFormat.NOT_SPECIFIED == -1);
		assertTrue(com.sun.media.format.WavAudioFormat.TRUE == 1);
		assertTrue(com.sun.media.format.WavAudioFormat.FALSE == 0);
		// TODO: test intArray of type java.lang.Class
		// TODO: test shortArray of type java.lang.Class
		// TODO: test byteArray of type java.lang.Class
		// TODO: test formatArray of type java.lang.Class

		// Methods (reflection): 
		if (true) {
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("clone", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Object.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("equals", new Class[]{java.lang.Object.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("matches", new Class[]{javax.media.Format.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("intersects", new Class[]{javax.media.Format.class});
			assertEquals(m.getReturnType(), javax.media.Format.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getAverageBytesPerSecond", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getCodecSpecificHeader", new Class[]{});
			assertEquals(m.getReturnType(), byte[].class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("toString", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.String.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getSampleRate", new Class[]{});
			assertEquals(m.getReturnType(), double.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getSampleSizeInBits", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getChannels", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getEndian", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getSigned", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getFrameSizeInBits", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getFrameRate", new Class[]{});
			assertEquals(m.getReturnType(), double.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("computeDuration", new Class[]{long.class});
			assertEquals(m.getReturnType(), long.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getEncoding", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.String.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getDataType", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Class.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("isSameEncoding", new Class[]{javax.media.Format.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("isSameEncoding", new Class[]{java.lang.String.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("relax", new Class[]{});
			assertEquals(m.getReturnType(), javax.media.Format.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("hashCode", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 257);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("getClass", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Class.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("wait", new Class[]{long.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("wait", new Class[]{long.class, int.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("wait", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("notify", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = com.sun.media.format.WavAudioFormat.class.getMethod("notifyAll", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		}

		// Constructors (reflection): 
		if (true) {
		{
			final Constructor c = com.sun.media.format.WavAudioFormat.class.getConstructor(new Class[]{java.lang.String.class});
			assertEquals(c.getModifiers(), 1);
		}
		{
			final Constructor c = com.sun.media.format.WavAudioFormat.class.getConstructor(new Class[]{java.lang.String.class, double.class, int.class, int.class, int.class, int.class, byte[].class});
			assertEquals(c.getModifiers(), 1);
		}
		{
			final Constructor c = com.sun.media.format.WavAudioFormat.class.getConstructor(new Class[]{java.lang.String.class, double.class, int.class, int.class, int.class, int.class, int.class, int.class, float.class, java.lang.Class.class, byte[].class});
			assertEquals(c.getModifiers(), 1);
		}
		}

		// Methods (compilation): 
		if (false) {
		com.sun.media.format.WavAudioFormat o = null;
		o.clone();
		o.equals((java.lang.Object) null);
		o.matches((javax.media.Format) null);
		o.intersects((javax.media.Format) null);
		o.getAverageBytesPerSecond();
		o.getCodecSpecificHeader();
		o.toString();
		o.getSampleRate();
		o.getSampleSizeInBits();
		o.getChannels();
		o.getEndian();
		o.getSigned();
		o.getFrameSizeInBits();
		o.getFrameRate();
		o.computeDuration(0L);
		o.getEncoding();
		o.getDataType();
		o.isSameEncoding((javax.media.Format) null);
		o.isSameEncoding((java.lang.String) null);
		o.relax();
		o.hashCode();
		o.getClass();
		o.wait(0L);
		o.wait(0L, 0);
		o.wait();
		o.notify();
		o.notifyAll();
		}
		// Constructors (compilation): 
		if (false) {
		com.sun.media.format.WavAudioFormat o = null;
		new com.sun.media.format.WavAudioFormat((java.lang.String) null);
		new com.sun.media.format.WavAudioFormat((java.lang.String) null, 0.0, 0, 0, 0, 0, (byte[]) null);
		new com.sun.media.format.WavAudioFormat((java.lang.String) null, 0.0, 0, 0, 0, 0, 0, 0, 0.f, (java.lang.Class) null, (byte[]) null);
		}
	}
	public void test_javax_media_cdm_CaptureDeviceManager() throws Exception
	{
		assertEquals(javax.media.cdm.CaptureDeviceManager.class.getModifiers(), 1);
		assertTrue(!javax.media.cdm.CaptureDeviceManager.class.isInterface());
		assertTrue(javax.media.cdm.CaptureDeviceManager.class.getSuperclass().equals(javax.media.CaptureDeviceManager.class));
		// Methods (reflection): 
		if (true) {
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("commit", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("getDeviceList", new Class[]{});
			assertEquals(m.getReturnType(), java.util.Vector.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("getDeviceList", new Class[]{javax.media.Format.class});
			assertEquals(m.getReturnType(), java.util.Vector.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("getDevice", new Class[]{java.lang.String.class});
			assertEquals(m.getReturnType(), javax.media.CaptureDeviceInfo.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("addDevice", new Class[]{javax.media.CaptureDeviceInfo.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("removeDevice", new Class[]{javax.media.CaptureDeviceInfo.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 41);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("hashCode", new Class[]{});
			assertEquals(m.getReturnType(), int.class);
			assertEquals(m.getModifiers(), 257);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("getClass", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.Class.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("wait", new Class[]{long.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("wait", new Class[]{long.class, int.class});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("wait", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 17);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("equals", new Class[]{java.lang.Object.class});
			assertEquals(m.getReturnType(), boolean.class);
			assertEquals(m.getModifiers(), 1);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("notify", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("notifyAll", new Class[]{});
			assertEquals(m.getReturnType(), void.class);
			assertEquals(m.getModifiers(), 273);
		}
		{
			final Method m = javax.media.cdm.CaptureDeviceManager.class.getMethod("toString", new Class[]{});
			assertEquals(m.getReturnType(), java.lang.String.class);
			assertEquals(m.getModifiers(), 1);
		}
		}

		// Constructors (reflection): 
		if (true) {
		{
			final Constructor c = javax.media.cdm.CaptureDeviceManager.class.getConstructor(new Class[]{});
			assertEquals(c.getModifiers(), 1);
		}
		}

		// Methods (compilation): 
		if (false) {
		javax.media.cdm.CaptureDeviceManager o = null;
		javax.media.cdm.CaptureDeviceManager.commit();
		javax.media.cdm.CaptureDeviceManager.getDeviceList();
		javax.media.cdm.CaptureDeviceManager.getDeviceList((javax.media.Format) null);
		javax.media.cdm.CaptureDeviceManager.getDevice((java.lang.String) null);
		javax.media.cdm.CaptureDeviceManager.addDevice((javax.media.CaptureDeviceInfo) null);
		javax.media.cdm.CaptureDeviceManager.removeDevice((javax.media.CaptureDeviceInfo) null);
		o.hashCode();
		o.getClass();
		o.wait(0L);
		o.wait(0L, 0);
		o.wait();
		o.equals((java.lang.Object) null);
		o.notify();
		o.notifyAll();
		o.toString();
		}
		// Constructors (compilation): 
		if (false) {
		javax.media.cdm.CaptureDeviceManager o = null;
		new javax.media.cdm.CaptureDeviceManager();
		}
	}

}
