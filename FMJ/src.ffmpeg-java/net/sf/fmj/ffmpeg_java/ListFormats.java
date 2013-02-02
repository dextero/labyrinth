package net.sf.fmj.ffmpeg_java;
import java.awt.Dimension;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.JPEGFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.ContentDescriptor;

import net.sf.ffmpeg_java.AVCodecLibrary;
import net.sf.ffmpeg_java.AVFormatLibrary;
import net.sf.ffmpeg_java.AVUtilLibrary;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodec;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodecContext;
import net.sf.ffmpeg_java.AVFormatLibrary.AVOutputFormat;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Stephan Goetter
 *
 */
public class ListFormats
{
	
	private static final Logger logger = LoggerSingleton.logger;

	static final int CODEC_TYPE_UNKNOWN = -1;
	static final int CODEC_TYPE_VIDEO = 0;
	static final int CODEC_TYPE_AUDIO = 1;
	static final int CODEC_TYPE_DATA = 2;
	static final int CODEC_TYPE_SUBTITLE = 3;
	static final int CODEC_TYPE_NB = 4;

	static final AVFormatLibrary AVFORMAT = AVFormatLibrary.INSTANCE;
	static 	final AVCodecLibrary AVCODEC = AVCodecLibrary.INSTANCE;
	static final AVUtilLibrary AVUTIL = AVUtilLibrary.INSTANCE;

	public static void main(String[] args) throws Exception
	{
		AVFORMAT.av_register_all();
		
		listMuxes();
		listDecoder();
		listEncoder();
	}
	
	static final String FIRST_FFMPEG_MUX_NAME = "ac3";
	static final String FIRST_FFMPEG_DECODER_NAME = "aasc";
	static final String FIRST_FFMPEG_ENCODER_NAME = "asv1";
	
	public static ContentDescriptor[] getSupportedOutputContentDescriptors(Format[] formats){
		// get content descriptors from ffmpeg
		List contentDescriptors = listMuxes();
		return (ContentDescriptor[])contentDescriptors.toArray(new ContentDescriptor[0]);
	}
	
	static List listMuxes(){
		List contentDescriptors = new ArrayList();
		
		int i = 1;

		AVOutputFormat avOutputFormat = AVFORMAT.guess_format(FIRST_FFMPEG_MUX_NAME, null,null);
		while (avOutputFormat != null){
			String mimeType = null;
			if (avOutputFormat.mime_type != null && avOutputFormat.mime_type.length() > 0) {
				mimeType = avOutputFormat.mime_type;
			}else{
				mimeType = "ffmpeg/"+avOutputFormat.name;
			}
			System.out.println(i++ + ". " + avOutputFormat.name + " - " +avOutputFormat.long_name + " : " + mimeType);
			logger.log(Level.FINEST, i++ + ". " + avOutputFormat.name + " - " + avOutputFormat.long_name + " : " + mimeType);
			contentDescriptors.add(ContentDescriptor.mimeTypeToPackageName(mimeType));
			if (avOutputFormat.next != null && avOutputFormat.next.isValid()) {
				avOutputFormat = new AVOutputFormat(avOutputFormat.next);
			}else{
				avOutputFormat = null;
			}
		}
		return contentDescriptors;
	}
	
	static void listDecoder(){
		int i = 1;
		AVCodec avCodec = AVCODEC.avcodec_find_decoder_by_name(FIRST_FFMPEG_DECODER_NAME);
		while (avCodec != null){
			logger.log(Level.FINEST, i++ + ". " + avCodec.name + " (" + getCodecType(avCodec.type) + ")");
			if (avCodec.next != null && avCodec.next.isValid()) {
				avCodec = new AVCodec(avCodec.next);
			}else{
				avCodec = null;
			}
		}
	}
	
	static void listEncoder(){
		int i = 1;
		AVCodec avCodec = AVCODEC.avcodec_find_decoder_by_name(FIRST_FFMPEG_ENCODER_NAME);
		while (avCodec != null){
			logger.log(Level.FINEST, i++ + ". " + avCodec.name + " (" + getCodecType(avCodec.type) + ")");
			if (avCodec.next != null && avCodec.next.isValid()) {
				avCodec = new AVCodec(avCodec.next);
			}else{
				avCodec = null;
			}
		}
	}
	
	static protected String getCodecType(int codecType){
		String result = null;
		switch (codecType){
		case CODEC_TYPE_UNKNOWN:
			result = "unknown";
			break;
		case CODEC_TYPE_VIDEO:
			result = "video";
			break;
		case CODEC_TYPE_AUDIO:
			result = "audio";
			break;
		case CODEC_TYPE_DATA:
			result = "data";
			break;
		case CODEC_TYPE_SUBTITLE:
			result = "subtitle";
			break;
		case CODEC_TYPE_NB:
			result = "nb";
			break;
		}
		return result;
	}
	
	final static int JPEG_QUALITY = 100;
	
    public static VideoFormat convertCodecPixelFormat(int pixFmt, int width, int height)
    {
    	VideoFormat result = null;
    	final int red, green, blue, bitsPerPixel, pixelStride, lineStride, endianess;

    	switch (pixFmt) {
	    	case AVCodecLibrary.PIX_FMT_NONE:
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_YUV420P:   ///< Planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
	    		// TODO: test it
	    		result = new YUVFormat(YUVFormat.YUV_420);
	    		break;
	    	case AVCodecLibrary.PIX_FMT_YUYV422:   ///< Packed YUV 4:2:2, 16bpp, Y0 Cb Y1 Cr
	    		// TODO:
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_RGB24:     ///< Packed RGB 8:8:8, 24bpp, RGBRGB...
	    		// TODO: masks
	        	red = 1;
	        	green = 2;
	        	blue = 3;
	    		bitsPerPixel = 24;
	        	result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR24:     ///< Packed RGB 8:8:8, 24bpp, BGRBGR...
	    		// TODO: masks
	        	red = 1;
	        	green = 2;
	        	blue = 3;
	    		bitsPerPixel = 24;
	        	result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUV422P:   ///< Planar YUV 4:2:2, 16bpp, (1 Cr & Cb sample per 2x1 Y samples)
	    		// TODO: test it
	    		result = new YUVFormat(YUVFormat.YUV_422);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUV444P:   ///< Planar YUV 4:4:4, 24bpp, (1 Cr & Cb sample per 1x1 Y samples)
	    		// TODO:
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_RGB32:     ///< Packed RGB 8:8:8, 32bpp, (msb)8A 8R 8G 8B(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 32;
	    		endianess = getCpuEndianess();
	    		pixelStride = 4;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUV410P:   ///< Planar YUV 4:1:0,  9bpp, (1 Cr & Cb sample per 4x4 Y samples)
	    		// TODO:
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_YUV411P:   ///< Planar YUV 4:1:1, 12bpp, (1 Cr & Cb sample per 4x1 Y samples)
	    		// TODO: test it
	    		result = new YUVFormat(YUVFormat.YUV_411);
    			break;
	    	case AVCodecLibrary.PIX_FMT_RGB565:    ///< Packed RGB 5:6:5, 16bpp, (msb)   5R 6G 5B(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 16;
	    		endianess = getCpuEndianess();
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_RGB555:    ///< Packed RGB 5:5:5, 16bpp, (msb)1A 5R 5G 5B(lsb), in cpu endianness most significant bit to 0
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 16;
	    		endianess = getCpuEndianess();
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_GRAY8:     ///<        Y        ,  8bpp
	    		// TODO: test masks
	    		red = 0xFF;
	    		green = 0xFF;
	    		blue = 0xFF;
	    		bitsPerPixel = 8;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_MONOWHITE: ///<        Y        ,  1bpp, 0 is white, 1 is black
	    		// TODO: test mask
	    		// Not supported?
	    		red = 1;
	    		green = 1;
	    		blue = 1;
	    		bitsPerPixel = 1;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_MONOBLACK: ///<        Y        ,  1bpp, 0 is black, 1 is white
	    		// TODO: test mask
	    		red = 1;
	    		green = 1;
	    		blue = 1;
	    		bitsPerPixel = 1;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_PAL8:      ///< 8 bit with PIX_FMT_RGB32 palette
	    		// TODO: mask arrays
	    		// result = new IndexedColorFormat(new Dimension(width, height), -1, byte[].class, 1.f, );
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_YUVJ420P:  ///< Planar YUV 4:2:0, 12bpp, full scale (jpeg)
	    		// TODO: test it
	    		result = new JPEGFormat(new Dimension(width, height), -1, byte[].class, 1.f, JPEG_QUALITY, JPEGFormat.DEC_420);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUVJ422P:  ///< Planar YUV 4:2:2, 16bpp, full scale (jpeg)
	    		// TODO: test it
	    		result = new JPEGFormat(new Dimension(width, height), -1, byte[].class, 1.f, JPEG_QUALITY, JPEGFormat.DEC_422);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUVJ444P:  ///< Planar YUV 4:4:4, 24bpp, full scale (jpeg)
	    		// TODO: test it
	    		result = new JPEGFormat(new Dimension(width, height), -1, byte[].class, 1.f, JPEG_QUALITY, JPEGFormat.DEC_444);
    			break;
	    	case AVCodecLibrary.PIX_FMT_XVMC_MPEG2_MC: ///< XVideo Motion Acceleration via common packet passing(xvmc_render.h)
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_XVMC_MPEG2_IDCT:
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_UYVY422:   ///< Packed YUV 4:2:2, 16bpp, Cb Y0 Cr Y1
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_UYYVYY411: ///< Packed YUV 4:1:1, 12bpp, Cb Y0 Y1 Cr Y2 Y3
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_BGR32:     ///< Packed RGB 8:8:8, 32bpp, (msb)8A 8B 8G 8R(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 32;
	    		endianess = getCpuEndianess();
	    		pixelStride = 4;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR565:    ///< Packed RGB 5:6:5, 16bpp, (msb)   5B 6G 5R(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 16;
	    		endianess = getCpuEndianess();
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR555:    ///< Packed RGB 5:5:5, 16bpp, (msb)1A 5B 5G 5R(lsb), in cpu endianness most significant bit to 1
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 16;
	    		endianess = getCpuEndianess();
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR8:      ///< Packed RGB 3:3:2,  8bpp, (msb)2B 3G 3R(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 8;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR4:      ///< Packed RGB 1:2:1,  4bpp, (msb)1B 2G 1R(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 4;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR4_BYTE: ///< Packed RGB 1:2:1,  8bpp, (msb)1B 2G 1R(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 8;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_RGB8:      ///< Packed RGB 3:3:2,  8bpp, (msb)2R 3G 3B(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 8;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_RGB4:      ///< Packed RGB 1:2:1,  4bpp, (msb)2R 3G 3B(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 4;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_RGB4_BYTE: ///< Packed RGB 1:2:1,  8bpp, (msb)2R 3G 3B(lsb)
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 8;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    			break;
	    	case AVCodecLibrary.PIX_FMT_NV12:      ///< Planar YUV 4:2:0, 12bpp, 1 plane for Y and 1 for UV
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_NV21:      ///< as above, but U and V bytes are swapped
	    		// TODO
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_RGB32_1:   ///< Packed RGB 8:8:8, 32bpp, (msb)8R 8G 8B 8A(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 32;
	    		endianess = getCpuEndianess();
	    		pixelStride = 4;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_BGR32_1:   ///< Packed RGB 8:8:8, 32bpp, (msb)8B 8G 8R 8A(lsb), in cpu endianness
	    		// TODO: masks
	    		red = 1;
	    		green = 2;
	    		blue = 3;
	    		bitsPerPixel = 32;
	    		endianess = getCpuEndianess();
	    		pixelStride = 4;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, endianess);
    			break;
	    	case AVCodecLibrary.PIX_FMT_GRAY16BE:  ///<        Y        , 16bpp, big-endian
	    		// TODO: test masks
	    		red = 0xFFFF;
	    		green = 0xFFFF;
	    		blue = 0xFFFF;
	    		bitsPerPixel = 16;
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, RGBFormat.BIG_ENDIAN);
    			break;
	    	case AVCodecLibrary.PIX_FMT_GRAY16LE:  ///<        Y        , 16bpp, little-endian
	    		// TODO: test masks
	    		red = 0xFFFF;
	    		green = 0xFFFF;
	    		blue = 0xFFFF;
	    		bitsPerPixel = 16;
	    		pixelStride = 2;
	    		lineStride = width*pixelStride;
	    		result = new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue, pixelStride, lineStride, Format.FALSE, RGBFormat.LITTLE_ENDIAN);
    			break;
	    	case AVCodecLibrary.PIX_FMT_YUV440P:   ///< Planar YUV 4:4:0 (1 Cr & Cb sample per 1x2 Y samples)
	    		// TODO:
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_YUVJ440P:  ///< Planar YUV 4:4:0 full scale (jpeg)
	    		// TODO: not supported by JPEGFormat
	    		//result = new JPEGFormat(new Dimension(width, height), -1, byte[].class, 1.f, JPEG_QUALITY, JPEGFormat.DEC_440);
    			//break;
	    		throw new IllegalArgumentException();
	    	case AVCodecLibrary.PIX_FMT_NB:        ///< number of pixel formats, DO NOT USE THIS if you want to link with shared libav* because the number of formats might differ between versions
	    		throw new IllegalArgumentException();
    		default:
	    		throw new IllegalArgumentException();
    	}
    		
    	return result;
    }
    
    public static AudioFormat convertCodecAudioFormat(AVCodecContext codecCtx)
    {
    	
    	// ffmpeg appears to always decode audio into 16 bit samples, regardless of the source.
    	return new AudioFormat(AudioFormat.LINEAR, codecCtx.sample_rate, 16, codecCtx.channels);	/// TODO: endian, signed?
    	
    }

    public static final int getCpuEndianess(){
    	return ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()) ? RGBFormat.BIG_ENDIAN : RGBFormat.LITTLE_ENDIAN;
    }
    
    public static VideoFormat convertCodecPixelFormat2(int pixFmt, int width, int height)
    {
    	final int bitsPerPixel;
    	if (pixFmt == AVCodecLibrary.PIX_FMT_RGB24)
    		bitsPerPixel = 24;
//    	else if (pixFmt == AVCodecLibrary.PIX_FMT_RGB32) // TODO: see comments on PIX_FMT_RGB32 in libavutil/avutil.h
//    		bitsPerPixel = 32;
    	else
    		throw new IllegalArgumentException();	// TODO: support other formats
    	final int red, green, blue;
    	red = 1;
    	green = 2;
    	blue = 3;
    	
    		
    	return new RGBFormat(new Dimension(width, height), -1, byte[].class, 1.f, bitsPerPixel, red, green, blue);
    	
    }
    
}
