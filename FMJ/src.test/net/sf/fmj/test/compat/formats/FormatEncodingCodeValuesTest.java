package net.sf.fmj.test.compat.formats;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.H261Format;
import javax.media.format.H263Format;
import javax.media.format.IndexedColorFormat;
import javax.media.format.JPEGFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;

import junit.framework.TestCase;
import net.sf.fmj.codegen.CGUtils;

import com.sun.media.format.WavAudioFormat;

/**
 * 
 * Make sure encoding codes for equality comparisons are specific values.
 * @author Ken Larson
 *
 */
public class FormatEncodingCodeValuesTest extends TestCase
{
	
	public void testAll() throws Exception
	{
		//gen();
		
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.RGBFormat.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.YUVFormat.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.LINEAR), 47944718450L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.ULAW), 14076023L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.ULAW_RTP), 236156482432304L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.ALAW), 8833143L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.IMA4), 10934356L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.IMA4_MS), 2866375883635L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSADPCM), 3147700570349L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.DVI), 150953L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.DVI_RTP), 2532575227184L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G723), 10318995L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G723_RTP), 173124012158256L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G728), 10319000L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G728_RTP), 173124096044336L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729), 10319001L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729_RTP), 173124112821552L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729A), 660416097L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729A_RTP), 11079943513386288L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM), 163053L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM_MS), 42743430003L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM_RTP), 2735579540784L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MAC3), 11933907L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MAC6), 11933910L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.TRUESPEECH), 951058165990316264L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSNAUDIO), 201466746129007L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MPEGLAYER3), 824324187402689683L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC8), 986256949875448024L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC10), 7780212570900018256L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC16), 7780212570900018262L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC20), 7780212570900018320L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREMETAVOICE), 2985208465502148837L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREMETASOUND), 2985208465451867044L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWARERT29H), -128485452267051416L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREVR12), 7780212570905584722L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWAREVR18), 7780212570905584728L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWARETQ40), 7780212570905056528L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.VOXWARETQ60), 7780212570905056656L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSRT24), 49187341460L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MPEG), 12880065248774767L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.MPEG_RTP), 6476693354317819184L);
		assertEquals(getEncodingCode(javax.media.format.AudioFormat.DOLBYAC3), 161607319951571L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.H261Format.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.H263Format.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.IndexedColorFormat.INDEO50), 10970448L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.CINEPAK), 9398884L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.JPEG), 11209063L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.JPEG_RTP), 188056875248944L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.MPEG), 11995495L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.MPEG_RTP), 201251014782256L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.H261), 10560913L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.H261_RTP), 177182722698544L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.H263), 10560915L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.H263_RTP), 177182756252976L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.H263_1998_RTP), 7263538286046424368L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.RGB), 207330L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.YUV), 236918L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.IRGB), 10955234L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.SMC), 211811L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.RLE), 207653L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.RPZA), 13307553L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.MJPG), 11971623L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.MJPEGA), 11971617L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.MJPEGB), 11971618L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.INDEO32), 10970322L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.INDEO41), 10970385L);
		assertEquals(getEncodingCode(javax.media.format.JPEGFormat.INDEO50), 10970448L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.LINEAR), 47944718450L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.ULAW), 14076023L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.ULAW_RTP), 236156482432304L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.ALAW), 8833143L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.IMA4), 10934356L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.IMA4_MS), 2866375883635L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MSADPCM), 3147700570349L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.DVI), 150953L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.DVI_RTP), 2532575227184L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G723), 10318995L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G723_RTP), 173124012158256L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G728), 10319000L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G728_RTP), 173124096044336L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G729), 10319001L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G729_RTP), 173124112821552L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G729A), 660416097L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.G729A_RTP), 11079943513386288L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.GSM), 163053L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.GSM_MS), 42743430003L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.GSM_RTP), 2735579540784L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MAC3), 11933907L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MAC6), 11933910L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.TRUESPEECH), 951058165990316264L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MSNAUDIO), 201466746129007L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MPEGLAYER3), 824324187402689683L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREAC8), 986256949875448024L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREAC10), 7780212570900018256L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREAC16), 7780212570900018262L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREAC20), 7780212570900018320L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREMETAVOICE), 2985208465502148837L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREMETASOUND), 2985208465451867044L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWARERT29H), -128485452267051416L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREVR12), 7780212570905584722L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWAREVR18), 7780212570905584728L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWARETQ40), 7780212570905056528L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.VOXWARETQ60), 7780212570905056656L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MSRT24), 49187341460L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MPEG), 12880065248774767L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.MPEG_RTP), 6476693354317819184L);
		assertEquals(getEncodingCode(com.sun.media.format.WavAudioFormat.DOLBYAC3), 161607319951571L);

	}
	public void gen() throws Exception
	{
		gen(Format.class);
		gen(RGBFormat.class);
		gen(YUVFormat.class);
		gen(VideoFormat.class);
		gen(AudioFormat.class);
		gen(H261Format.class);
		gen(H263Format.class);
		gen(IndexedColorFormat.class);
		gen(JPEGFormat.class);
		gen(WavAudioFormat.class);
	}
	
	void gen(Class formatClass) throws Exception
	{
		for (int i = 0; i < formatClass.getFields().length; ++i)
		{
			final Field field = formatClass.getFields()[i];
		
			if (!Modifier.isStatic(field.getModifiers()))
				continue;
			if (field.getType() != String.class)
				continue;
			
			final String value = (String) field.get(null);
			
			System.out.println("assertEquals(getEncodingCode(" + formatClass.getName() + "." + field.getName() + "), " + CGUtils.toLiteral(getEncodingCode(value)) + ");");
		}
		
	}
	
	private long getEncodingCode(String s) throws Exception
	{
		final Format f = new Format(null);
		
		return FormatPrivateTest.getEncodingCode(f, s);
	}
}
