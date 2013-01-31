package net.sf.fmj.ffmpeg_java;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;

import net.sf.ffmpeg_java.AVCodecLibrary;
import net.sf.ffmpeg_java.AVFormatLibrary;
import net.sf.ffmpeg_java.AVUtilLibrary;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodec;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodecContext;
import net.sf.ffmpeg_java.AVCodecLibrary.AVFrame;
import net.sf.ffmpeg_java.AVFormatLibrary.AVFormatContext;
import net.sf.ffmpeg_java.AVFormatLibrary.AVInputFormat;
import net.sf.ffmpeg_java.AVFormatLibrary.AVOutputFormat;
import net.sf.ffmpeg_java.AVFormatLibrary.AVPacket;
import net.sf.ffmpeg_java.AVFormatLibrary.AVStream;
import net.sf.ffmpeg_java.FFMPEGLibrary.AVRational;
import net.sf.ffmpeg_java.custom_protocol.CallbackURLProtocolMgr;
import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;

import com.lti.utils.collections.Queue;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * 
 * Demultiplexer which uses ffmpeg.java native wrapper around ffmpeg (libavformat, libavcodec, libavutil).
 * @author Ken Larson
 *
 */
public class FFMPEGParser extends AbstractDemultiplexer 
{
	private static final Logger logger = LoggerSingleton.logger;

	private static final boolean PROCEED_IF_NO_AUDIO_CODEC = true; // if true, we'll play back video only if we are missing an audio codec.  Typical example: Mpeg-4 AAC
	
	private final AVFormatLibrary AVFORMAT;
	private final AVCodecLibrary AVCODEC;
	private final AVUtilLibrary AVUTIL;
	private AVFormatContext formatCtx;
	
	// if USE_DATASOURCE_URL_ONLY is true, this is a bit of a hack - we don't really use the DataSource, we just grab its URL.  So arbitrary data sources won't work.
	// otherwise, we register a custom URLHandler with ffmpeg, which calls us back to get the data.
	private final boolean USE_DATASOURCE_URL_ONLY = false;
	
	private ContentDescriptor[] supportedInputContentDescriptors = null;
	
	static final String FIRST_FFMPEG_DEMUX_NAME = "aac";
	
	public FFMPEGParser()
	{
		try
		{
			AVFORMAT = AVFormatLibrary.INSTANCE;
			AVCODEC = AVCodecLibrary.INSTANCE;
			AVUTIL = AVUtilLibrary.INSTANCE;
			
			AVFORMAT.av_register_all();
			queryInputContentDescriptors();
		}
		catch (Throwable t)
		{
			logger.log(Level.WARNING, "Unable to initialize ffmpeg libraries: " + t);
			throw new RuntimeException(t);
		}
	}
	
	private static final Object AV_SYNC_OBJ = new Boolean(true);	// synchronize on this before using the libraries, to prevent threading problems.
	
	private PullDataSource source;
	
	private PullSourceStreamTrack[] tracks;
	
	private Queue[] packetQueues;	// Queue of AVPacket
	
	//@Override
	public ContentDescriptor[] getSupportedInputContentDescriptors()
	{
		return supportedInputContentDescriptors;
	}

	protected void queryInputContentDescriptors(){
		// get content descriptors from ffmpeg
		List contentDescriptors = new ArrayList();
		int i = 1;
		AVInputFormat avInputFormat = AVFORMAT.av_find_input_format(FIRST_FFMPEG_DEMUX_NAME);
		while (avInputFormat != null) {
			String mimeType = null;
			AVOutputFormat avOutputFormat = AVFORMAT.guess_format(avInputFormat.name, null,null);
			if (avOutputFormat != null && avOutputFormat.mime_type != null && avOutputFormat.mime_type.length() > 0) {
				mimeType = avOutputFormat.mime_type;
			}else{
				mimeType = "ffmpeg/"+avInputFormat.name;
			}
			logger.log(Level.FINEST, i++ + ". " + avInputFormat.long_name + " : " + mimeType);
			contentDescriptors.add(new ContentDescriptor(ContentDescriptor.mimeTypeToPackageName(mimeType)));
			if (avInputFormat.next != null && avInputFormat.next.isValid()) {
				avInputFormat = new AVInputFormat(avInputFormat.next);
			}else{
				avInputFormat = null;
			}
		}
		// add content types which ffmpeg supports but doesn't report
		contentDescriptors.add(new ContentDescriptor("video.quicktime"));
		contentDescriptors.add(new ContentDescriptor("video.x_ms_wmv"));
		contentDescriptors.add(new ContentDescriptor("video.mp4"));
		contentDescriptors.add(new ContentDescriptor("video.3gpp"));
		contentDescriptors.add(new ContentDescriptor("video.mp2p"));
		
		supportedInputContentDescriptors = (ContentDescriptor[])contentDescriptors.toArray(new ContentDescriptor[0]);
	}
	
	//@Override
	public Track[] getTracks() throws IOException, BadHeaderException
	{
		return tracks;
	}

	//@Override
	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{
		final String protocol = source.getLocator().getProtocol();
		
		if (USE_DATASOURCE_URL_ONLY)
		{
			if (!(protocol.equals("file") || protocol.equals("http")))
				throw new IncompatibleSourceException();
			
			// TODO: ffmpeg appears to support multiple file protocols, for example: file: pipe: udp: rtp: tcp: http:
			// we should also allow those.
			// TODO: would be best to query this dynamically from ffmpeg
		}
		else
		{

		
		if (!(source instanceof PullDataSource))
			throw new IncompatibleSourceException();
//       	if (!(source instanceof SourceCloneable))
//    		throw new IncompatibleSourceException();
		}
		
		this.source = (PullDataSource) source;
		
	}
	
	
	
	//@Override
	public void open() throws ResourceUnavailableException
	{
		synchronized (AV_SYNC_OBJ)
		{
			try
			{
				AVCODEC.avcodec_init();	// TODO: everything seems to be fine if we don't call this...
			}
			catch (Throwable t)
			{	
				logger.log(Level.WARNING, "" + t, t);
				throw new ResourceUnavailableException("avcodec_init or av_register_all failed");
			}
			
			// not sure what the consequences of such a mismatch are, but it is worth logging a warning:
			if (AVCODEC.avcodec_version() != AVCodecLibrary.LIBAVCODEC_VERSION_INT)
				logger.warning("ffmpeg-java and ffmpeg versions do not match: avcodec_version=" + AVCODEC.avcodec_version() + " LIBAVCODEC_VERSION_INT=" + AVCodecLibrary.LIBAVCODEC_VERSION_INT);
			
			
			
			
			final String urlStr;
			
			if (USE_DATASOURCE_URL_ONLY)
			{
				// just use the URL from the datasource
				
				// FMJ supports relative file URLs, but FFMPEG does not.  So we'll rewrite the URL here:
				// TODO: perhaps we should only do this if FFMPEG has a problem (av_open_input_file returns nonzero).
				if (source.getLocator().getProtocol().equals("file"))
					urlStr = URLUtils.createAbsoluteFileUrl(source.getLocator().toExternalForm());
				else
					urlStr = source.getLocator().toExternalForm(); 
			}
			else
			{	// use the real java datasource, via callbacks.
				CallbackURLProtocolMgr.register(AVFORMAT);
				
				// TODO: do this in start?
				final String callbackURL = CallbackURLProtocolMgr.addCallbackURLProtocolHandler(new PullDataSourceCallbackURLProtocolHandler(source));
				
				// TODO: we need to remove the handler when we are done.
				
				urlStr = callbackURL;
			}
			
			final PointerByReference ppFormatCtx = new PointerByReference();
			
			// Open video file
			final int ret = AVFORMAT.av_open_input_file(ppFormatCtx, urlStr, null, 0, null);
			if (ret != 0)
			    throw new ResourceUnavailableException("av_open_input_file failed: " + ret); // Couldn't open file
			
			
			formatCtx = new AVFormatContext(ppFormatCtx.getValue());
			//System.out.println(new String(formatCtx.filename));
			
			
			// Retrieve stream information
			if (AVFORMAT.av_find_stream_info(formatCtx) < 0)
			    throw new ResourceUnavailableException("Couldn't find stream information"); // Couldn't find stream information
		
			AVFORMAT.dump_format(formatCtx, 0, urlStr, 0);
		
			
			VideoTrack videoTrack = null;
			AudioTrack audioTrack = null;
		    for (int i = 0; i < formatCtx.nb_streams; i++)
		    {   final AVStream stream = new AVStream(formatCtx.getStreams()[i]);
		    	final AVCodecContext codecCtx = new AVCodecContext(stream.codec);
	    	    if (codecCtx.codec_id == 0)
	    	    {	logger.info("Codec id is zero (no codec) - skipping stream " + i);
	    	    	continue;
	    	    }
	    	    if (codecCtx.codec_type == AVCodecLibrary.CODEC_TYPE_VIDEO && videoTrack == null)
		        {
	    	    	videoTrack = new VideoTrack(i, stream, codecCtx);
		        }
		    	else if (codecCtx.codec_type == AVCodecLibrary.CODEC_TYPE_AUDIO && audioTrack == null)
		    	{
		    		try
		    		{
		    			audioTrack = new AudioTrack(i, stream, codecCtx);
		    		}
		    		catch (ResourceUnavailableException e)
		    		{	
		    			if (!PROCEED_IF_NO_AUDIO_CODEC)
		    				throw e;
		    			logger.log(Level.WARNING, "Skipping audio track: " + e, e);
		    		}
		    	}
		    	else
		    	{	//throw new ResourceUnavailableException("Unknown track codec type " + codecCtx.codec_type + " for track " + i);
		    	}
		    	
		    }
		    
		    if (audioTrack == null && videoTrack == null)
		    	throw new ResourceUnavailableException("No audio or video track found");
		    else if (audioTrack != null && videoTrack != null)
		    	tracks = new PullSourceStreamTrack[] {videoTrack, audioTrack};
		    else if (audioTrack != null)
		    	tracks = new PullSourceStreamTrack[] {audioTrack};
		    else
		    	tracks = new PullSourceStreamTrack[] {videoTrack};

		    packetQueues = new Queue[formatCtx.nb_streams];
		    for (int i = 0; i < packetQueues.length; ++i)
		    	packetQueues[i] = new Queue();
		    
		}
	    
		super.open();
		
	}

	public void close()
	{

		synchronized (AV_SYNC_OBJ)
		{
			if (tracks != null)
			{
				for (int i = 0; i < tracks.length; ++i)
				{	if (tracks[i] != null)
					{	tracks[i].deallocate();
						tracks[i] = null;
					}
				}
				tracks = null;
			}
			
		    // Close the video file
		    if (formatCtx != null)
		    {	AVFORMAT.av_close_input_file(formatCtx);
		    	formatCtx = null;
		    }
		}
		super.close();
	}

	//@Override
	public void start() throws IOException
	{

	}
	
	// TODO: should we stop data source in stop?
//	// @Override
//	public void stop()
//	{
//		try 
//		{
//			source.stop();
//		} catch (IOException e) 
//		{
//			logger.log(Level.WARNING, "" + e, e);
//		}
//	}
	
	
	//@Override
	public boolean isPositionable()
	{
		return true;
	}
	
	//@Override
	public Time setPosition(Time where, int rounding)
	{
		// TODO: how to use rounding?
		synchronized (AV_SYNC_OBJ)
		{	
			
			// when stream is -1, units are AV_TIME_BASE.
			// TODO: tutorial 7 on www.dranger.com suggests that the -1 can sometimes cause problems...
			final int result = AVFORMAT.av_seek_frame(formatCtx, -1, where.getNanoseconds() / 1000L, 0);
			if (result < 0)
			{	logger.severe("av_seek_frame failed with code " + result);
				// TODO: what to return if error?
			}
			return where; // TODO: what to return
		
			// TODO: we have to reset the frame counters on the tracks....
		}
	}

	//@Override
	public boolean isRandomAccess()
	{
		return super.isRandomAccess();	// TODO: can we determine this from the data source?
	}
	
    public static VideoFormat convertCodecPixelFormat(int pixFmt, int width, int height, double frameRate)
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
    	
    		
    	return new RGBFormat(new Dimension(width, height), -1, byte[].class, (float) frameRate, bitsPerPixel, red, green, blue);
    	
    }
    
    static AVRational getTimeBase(AVStream stream, AVCodecContext codecCtx)
    {
    	// code adapted from ffmpeg utils.c: dump_format
    	if (stream.r_frame_rate.num != 0 && stream.r_frame_rate.den != 0)
    	{	AVRational result = new AVRational();
    		result.num = stream.r_frame_rate.den;
    		result.den = stream.r_frame_rate.num;
    		return result;
    	}
    	else if (stream.time_base.num != 0 && stream.time_base.den != 0)
    		return stream.time_base;
    	else
    		return codecCtx.time_base;
    }
    
    static double getFPS(AVStream stream, AVCodecContext codecCtx)
    {
    	final AVRational time_base = getTimeBase(stream, codecCtx);
    	return (double) time_base.den / (double) time_base.num;
    }
    
    
    static long getTimestamp(final AVFrame frame, final AVStream stream, final AVCodecContext codecCtx, long frameNo, long packetDts)
    {
    	// from AVFrame, regarding int64_t pts:
	     /**
	      * presentation timestamp in time_base units (time when frame should be shown to user)
	      * If AV_NOPTS_VALUE then frame_rate = 1/time_base will be assumed.
	      */
    	
    	// from AVCodecContext, regarding time_base:
    	
        /**
         * This is the fundamental unit of time (in seconds) in terms
         * of which frame timestamps are represented. For fixed-fps content,
         * timebase should be 1/framerate and timestamp increments should be
         * identically 1.
         */
    	
    	// the time base here is used for calculating based on frame number.
    	// TODO: if other calculations are used, using pts/dts, then this may not be correct.
        final AVRational time_base = getTimeBase(stream, codecCtx);//codecCtx.time_base;
    	
        // TODO: the frame rate is in frames, where half of an interlaced frame counts as 1.  so for interlaced video,
        // this has to be taken into account.
        // for example safexmas.move is reported as :
//        Duration: 00:00:16.4, start: 0.000000, bitrate: 1730 kb/s
//        Stream #0.0(eng): Video: cinepak, yuv420p, 320x200, 30.00 fps(r)
        // and it has 220 frames.  But 220/16.4=13.4.
        // see http://www.dranger.com/ffmpeg/tutorial05.html
        // for a good discussion on pts.
        // TODO: for now, we'll just use the packetDts, since pts seems to always be zero.
        
        if (packetDts == AVCodecLibrary.AV_NOPTS_VALUE) // TODO: with some movies, pts is just always zero, so we'll handle it the same way.
    	{	// If AV_NOPTS_VALUE then frame_rate = 1/time_base will be assumed.
    		// therefore we need to know the frame #
    		return (1000000000L * frameNo * (long) time_base.num) / (long) time_base.den;
    		
    	}
    	else
		{
    		// TODO: the code to do the calculation based on the dts is wrong, so we'll just use the frame number based
    		// calculation for now.
    		// not sure how to calculate the correct dts for a frame.
    		// try 4harmonic.mpg for an example of this.
    		return (1000000000L * frameNo * (long) time_base.num) / (long) time_base.den;
    		//return ( 1000000000L * packetDts * (long) time_base.num) / (long) time_base.den;
    		// TODO: is this correct?  it appears to be based on the AVFrame comment, but has not been tested yet.
    		
    		
		}
    }
    
    public static AudioFormat convertCodecAudioFormat(AVCodecContext codecCtx)
    {
    	// ffmpeg appears to always decode audio into 16 bit samples, regardless of the source.
    	return new AudioFormat(AudioFormat.LINEAR, codecCtx.sample_rate, 16, codecCtx.channels);	/// TODO: endian, signed?
    	
    }
    

	private abstract class PullSourceStreamTrack extends AbstractTrack
	{
		public abstract void deallocate();
	}
	

	/** 
	 * 
	 * @param streamIndex the track/stream index
	 * @return null on EOM
	 */
	private AVPacket nextPacket(int streamIndex)
	{
		// because ffmpeg has a single function that gets the next packet, without regard to track/stream, we have
		// to queue up packets that are for a different track/stream.
		
		synchronized (AV_SYNC_OBJ)
		{
			if (!packetQueues[streamIndex].isEmpty())
				return (AVPacket) packetQueues[streamIndex].dequeue(); // we already have one in the queue for this stream
			
		    while (true)
		    {
		    	final AVPacket packet = new AVPacket();
			    if (AVFORMAT.av_read_frame(formatCtx, packet) < 0)
		    		break;	// TODO: distinguish between EOM and error?
		    	
		    	// Is this a packet from the desired stream?
		        if (packet.stream_index == streamIndex)
		        {	return packet;
		        }
		        else
		        {
		        	// TODO: This has been observed in other code that uses ffmpeg, not sure if it is needed.
		        	//if (AVFORMAT.av_dup_packet(packet) < 0)
		        	//	throw new RuntimeException("av_dup_packet failed");
		        	
		        	packetQueues[packet.stream_index].enqueue(packet);
		        }
		    }
		    
		    return null;
		}
	}
	
	
	private class VideoTrack extends PullSourceStreamTrack
	{
		// TODO: track listener
		
		private final int videoStreamIndex;
		private AVStream stream;
		private AVCodecContext codecCtx;
		private AVCodec codec;
		private AVFrame frame;
		private AVFrame frameRGB;
		private final VideoFormat format;
		private Pointer buffer;
		/**
		 * We have to keep track of frame number ourselves.
		 * frame.display_picture_number seems to often always be zero.
		 * See: http://lists.mplayerhq.hu/pipermail/ffmpeg-user/2005-September/001244.html
		 */
		private long frameNo;
		
		public VideoTrack(int videoStreamIndex, AVStream stream, AVCodecContext codecCtx) throws ResourceUnavailableException
		{
			super();
		
			this.videoStreamIndex = videoStreamIndex;
			this.stream = stream;
			this.codecCtx = codecCtx;
			
			
			synchronized (AV_SYNC_OBJ)
	    	{

			    // Find the decoder for the video stream
			    this.codec = AVCODEC.avcodec_find_decoder(codecCtx.codec_id);
			    if (codec == null)
			        throw new ResourceUnavailableException("Codec not found for codec_id " + codecCtx.codec_id + " (0x" + Integer.toHexString(codecCtx.codec_id) + ")"); // Codec not found - see AVCodecLibrary.CODEC_ID constants
			    
			    // Open codec
			    if (AVCODEC.avcodec_open(codecCtx, codec) < 0)
			    	 throw new ResourceUnavailableException("Could not open codec"); // Could not open codec
				
				
			    // Allocate video frame
			    frame = AVCODEC.avcodec_alloc_frame();
			    if (frame == null)
			    	throw new ResourceUnavailableException("Could not allocate frame");
			    
			    // Allocate an AVFrame structure
			    frameRGB = AVCODEC.avcodec_alloc_frame();
			    if (frameRGB == null)
			    	throw new ResourceUnavailableException("Could not allocate frame");
			    
			    // Determine required buffer size and allocate buffer
			    final int numBytes = AVCODEC.avpicture_get_size(AVCodecLibrary.PIX_FMT_RGB24, codecCtx.width, codecCtx.height);
			    buffer = AVUTIL.av_malloc(numBytes);
			    
			    // Assign appropriate parts of buffer to image planes in pFrameRGB
			    AVCODEC.avpicture_fill(frameRGB, buffer, AVCodecLibrary.PIX_FMT_RGB24, codecCtx.width, codecCtx.height);
			    
			    // set format
			    format = convertCodecPixelFormat(AVCodecLibrary.PIX_FMT_RGB24, codecCtx.width, codecCtx.height, getFPS(stream, codecCtx));
	    	}
		}
		
		//@Override
		public void deallocate()
		{
			synchronized (AV_SYNC_OBJ)
	    	{
			    // Close the codec
				if (codecCtx != null)
				{	AVCODEC.avcodec_close(codecCtx);
					codecCtx = null;
				}
				
			    // Free the RGB image
				if (frameRGB != null)
				{
					AVUTIL.av_free(frameRGB.getPointer());
					frameRGB = null;
				}
				
			    // Free the YUV frame
				if (frame != null)
				{
					AVUTIL.av_free(frame.getPointer());
					frame = null;
				}
				
				if (buffer != null)
				{
					AVUTIL.av_free(buffer);
					buffer = null;
				}
	    	}

		}

		// TODO: implement seeking using av_seek_frame
		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException
		{
			return 0;
			
		}
		
		public boolean canSkipNanos()
		{
			return false;
		}

		public Format getFormat()
		{
			return format;
		}

//		  TODO: from JAVADOC:
//		   This method might block if the data for a complete frame is not available. It might also block if the stream contains intervening data for a different interleaved Track. Once the other Track is read by a readFrame call from a different thread, this method can read the frame. If the intervening Track has been disabled, data for that Track is read and discarded.
//
//			Note: This scenario is necessary only if a PullDataSource Demultiplexer implementation wants to avoid buffering data locally and copying the data to the Buffer passed in as a parameter. Implementations might decide to buffer data and not block (if possible) and incur data copy overhead. 
		 
		public void readFrame(Buffer buffer)
		{
			// will be set to the minimum dts of all packets that make up a frame.
			// TODO: this is not correct in all cases, see comments in getTimestamp.
			long dts = -1;
			
			final AVPacket packet = nextPacket(videoStreamIndex);
		    if (packet != null)
		    {
		    	synchronized (AV_SYNC_OBJ)
		    	{
			    	final IntByReference frameFinished = new IntByReference();
		            // Decode video frame
			    	
		        	AVCODEC.avcodec_decode_video(codecCtx, frame, frameFinished, packet.data, packet.size);
		        	if (dts == -1 || packet.dts < dts)
		        		dts = packet.dts;
	
		            // Did we get a video frame?
		            if (frameFinished.getValue() != 0)
		            {
		                // Convert the image from its native format to RGB
		                AVCODEC.img_convert(frameRGB, AVCodecLibrary.PIX_FMT_RGB24, frame, codecCtx.pix_fmt, codecCtx.width, codecCtx.height);
		                
		                final byte[] data = frameRGB.data0.getByteArray(0, codecCtx.height * frameRGB.linesize[0]);
		                buffer.setData(data);
		                buffer.setLength(data.length);
		                buffer.setOffset(0);
		                buffer.setEOM(false);
		                buffer.setDiscard(false);
		                buffer.setTimeStamp(getTimestamp(frame, stream, codecCtx, frameNo++, dts));
		                //System.out.println("frameNo=" + frameNo + " dts=" + dts + " timestamp=" + buffer.getTimeStamp());
		                dts = -1;
		                
		            }
		            else
		            {
		            	buffer.setLength(0);
			        	buffer.setDiscard(true);
		            }

			        // Free the packet that was allocated by av_read_frame
			        // AVFORMAT.av_free_packet(packet.getPointer()) - cannot be called because it is an inlined function.
			        // so we'll just do the JNA equivalent of the inline:
			        if (packet.destruct != null)
			        	packet.destruct.callback(packet);
		    	}




		    }
		    else
		    {	// TODO: error? EOM?
		    	buffer.setLength(0);
		    	buffer.setEOM(true);
		    	return;
		    }
		    

			
		}

		
		public Time mapFrameToTime(int frameNumber)
		{
			return TIME_UNKNOWN;	
		}

		public int mapTimeToFrame(Time t)
		{	
			return FRAME_UNKNOWN;		
		}
		
	
		
		public Time getDuration()
		{
			if (formatCtx.duration <= 0)
				return Duration.DURATION_UNKNOWN;	// not sure what formatCtx.duration is set to for unknown/unspecified lengths, but this seems like a reasonable check.
			// formatCtx.duration is in AV_TIME_BASE, is in 1/1000000 sec.  Multiply by 1000 to get nanos.
			return new Time(formatCtx.duration * 1000L);
		}

	}

	
	private class AudioTrack extends PullSourceStreamTrack
	{
		// TODO: track listener
		
		private final int audioStreamIndex;
		AVStream stream;
		private AVCodecContext codecCtx;
		private final AVCodec codec;
		private Pointer buffer;
		private int bufferSize;
		private final AudioFormat format;
		
		/**
		 * We have to keep track of frame number ourselves.
		 * frame.display_picture_number seems to often always be zero.
		 * See: http://lists.mplayerhq.hu/pipermail/ffmpeg-user/2005-September/001244.html
		 */
		private long frameNo;
		
		public AudioTrack(int audioStreamIndex, AVStream stream, AVCodecContext codecCtx) throws ResourceUnavailableException
		{
			super();
		
			this.audioStreamIndex = audioStreamIndex;
			this.stream = stream;
			this.codecCtx = codecCtx;

			synchronized (AV_SYNC_OBJ)
	    	{

			
			    // Find the decoder for the video stream
			    this.codec = AVCODEC.avcodec_find_decoder(codecCtx.codec_id);
			    if (codec == null)
			        throw new ResourceUnavailableException("Codec not found for codec_id " + codecCtx.codec_id + " (0x" + Integer.toHexString(codecCtx.codec_id) + ")"); // Codec not found - see AVCodecLibrary.CODEC_ID constants
			    
			    // Open codec
			    if (AVCODEC.avcodec_open(codecCtx, codec) < 0)
			    	 throw new ResourceUnavailableException("Could not open codec"); // Could not open codec
				
			    // actually appears to be used as a short array.
			    bufferSize = AVCodecLibrary.AVCODEC_MAX_AUDIO_FRAME_SIZE;
			    buffer = AVUTIL.av_malloc(bufferSize);
			   
			    format = convertCodecAudioFormat(codecCtx);
	    	}
		    

		}
		
		//@Override
		public void deallocate()
		{
			synchronized (AV_SYNC_OBJ)
	    	{
			    // Close the codec
				if (codecCtx != null)
				{	AVCODEC.avcodec_close(codecCtx);
					codecCtx = null;
				}
				
				if (buffer != null)
				{
					AVUTIL.av_free(buffer);
					buffer = null;
				}
	    	}

		}

		// TODO: implement seeking using av_seek_frame
		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException
		{
			return 0;
			
		}
		
		public boolean canSkipNanos()
		{
			return false;
		}

		public Format getFormat()
		{
			return format;
		}

//		  TODO: from JAVADOC:
//		   This method might block if the data for a complete frame is not available. It might also block if the stream contains intervening data for a different interleaved Track. Once the other Track is read by a readFrame call from a different thread, this method can read the frame. If the intervening Track has been disabled, data for that Track is read and discarded.
//
//			Note: This scenario is necessary only if a PullDataSource Demultiplexer implementation wants to avoid buffering data locally and copying the data to the Buffer passed in as a parameter. Implementations might decide to buffer data and not block (if possible) and incur data copy overhead. 
		 
		public void readFrame(Buffer buffer)
		{
			
			// TODO: the reading of packets needs to be done centrally for all tracks
			final AVPacket packet = nextPacket(audioStreamIndex);
		    if (packet != null)
		    {
		    	
		    	synchronized (AV_SYNC_OBJ)
		    	{
		        	final IntByReference frameSize = new IntByReference();
		        	// It is not very clear from the documentation, but it appears that we set the initial frame size to be the size of this.buffer in bytes, not in "shorts".
		        	frameSize.setValue(this.bufferSize);
		            // Decode 
		        	AVCODEC.avcodec_decode_audio2(codecCtx, this.buffer, frameSize, packet.data, packet.size);
	
		            // Did we get a audio data?
		        	if (frameSize.getValue() < 0)
		        	{	throw new RuntimeException("Failed to read audio frame");	// TODO: how to handle this error?
		        	}
		        	else if (frameSize.getValue() > 0)
		            {
		            	if (frameSize.getValue() > this.bufferSize)
		            	{	// realloc buffer to make room:
		            		// we already allocate the maximum size, so this should never happen.
		            		AVUTIL.av_free(this.buffer);
		            		this.bufferSize = frameSize.getValue();
		         		    this.buffer = AVUTIL.av_malloc(this.bufferSize);
		            	}
		            	
		                final byte[] data = this.buffer.getByteArray(0, frameSize.getValue());
		                buffer.setData(data);
		                buffer.setLength(data.length);
		                buffer.setOffset(0);
		                buffer.setEOM(false);
		                buffer.setDiscard(false);
		                buffer.setTimeStamp(System.currentTimeMillis()); // TODO
		                
		            }
		            else
		            {
		            	buffer.setLength(0);
			        	buffer.setDiscard(true);
		            }

			        // Free the packet that was allocated by av_read_frame
			        // AVFORMAT.av_free_packet(packet.getPointer()) - cannot be called because it is an inlined function.
			        // so we'll just do the JNA equivalent of the inline:
			        if (packet.destruct != null)
			        	packet.destruct.callback(packet);
		    	}


		    }
		    else
		    {	// TODO: error? EOM?
		    	buffer.setLength(0);
		    	buffer.setEOM(true);
		    	return;
		    }
		    

			
		}

		
		public Time mapFrameToTime(int frameNumber)
		{
			return TIME_UNKNOWN;	
		}

		public int mapTimeToFrame(Time t)
		{	
			return FRAME_UNKNOWN;		
		}
		
	
		
		public Time getDuration()
		{
			if (formatCtx.duration <= 0)
				return Duration.DURATION_UNKNOWN;	// not sure what formatCtx.duration is set to for unknown/unspecified lengths, but this seems like a reasonable check.
			// formatCtx.duration is in AV_TIME_BASE, which means it is in milliseconds.  Multiply by 1000 to get nanos.
			return new Time(formatCtx.duration * 1000L);
		}

	}
	
	
}
