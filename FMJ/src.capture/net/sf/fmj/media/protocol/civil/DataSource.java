package net.sf.fmj.media.protocol.civil;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.Format;
import javax.media.Time;
import javax.media.control.FormatControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

import net.sf.fmj.utility.FPSCounter;
import net.sf.fmj.utility.LoggerSingleton;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.Image;
import com.lti.utils.synchronization.CloseableThread;
import com.lti.utils.synchronization.ProducerConsumerQueue;
import com.lti.utils.synchronization.SynchronizedBoolean;
import com.lti.utils.synchronization.SynchronizedObjectHolder;
import com.lti.utils.synchronization.ThreadGroupMgr;

/**
 * DataSource for CIVIL video.
 * @author Ken Larson
 *
 */
public class DataSource extends PushBufferDataSource implements CaptureDevice
{
	private static final boolean TRACE = true;
	private static final Logger logger = LoggerSingleton.logger;
	private CaptureSystem system;
	private String deviceId;
	private CaptureStream captureStream;
	private MyPushBufferStream pushBufferStream;
	/**
	 * The JMF video output format we are delivering.
	 */
	private VideoFormat outputVideoFormat = null;	
	
	private boolean connected;
	private static final int BUFFER_QUEUE_SIZE = 2; // after this many items in queue, we will drop frames.
												  	// the frame dropping allows us to stay going in real-time.
													// TODO: this might not be good for some applications.
													// TODO: we are still dropping some frames even after
													// optimization.
	private final ProducerConsumerQueue bufferQueue = new ProducerConsumerQueue(BUFFER_QUEUE_SIZE);
//    private boolean bufferQueueEnabled = false;	// synch on bufferQueue to use.  if false, only 1 item is kept in buffer queue.
//    											// this allows us to not buffer frames before we are reading them, to minimize the
//    											// latency between the images coming in, and the images going out.
    
	private static final boolean TRACE_FPS = false;
	
	//@Override
	public void connect() throws IOException
	{
		if (TRACE) logger.fine("connect");
		// Normally, we allow a re-connection even if we are connected, due to an oddity in the way Manager works. See comments there 
		// in createPlayer(MediaLocator sourceLocator).
		// however, because capture devices won't go back to previous data even if we reconnect, there is no point in reconnecting.
		if (connected)
			return;
		try
		{
		    CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();
			system = factory.createCaptureSystem();
			system.init();	// TODO: dispose of properly
		    
			if (TRACE) logger.fine("Opening " + getLocator().getRemainder());
			
			// see if it is an ordinal URL like civil:0 or civil:/0
			final int ordinal = ordinal(getLocator().getRemainder());
			if (ordinal >= 0)
			{	deviceId = deviceIdFromOrdinal(ordinal);
				if (deviceId == null)
					throw new IOException("Unable to convert ordinal " + ordinal + " to a capture device");
			}
			else
			{
				deviceId = getLocator().getRemainder();
			}
			
			captureStream = system.openCaptureDeviceStream(deviceId);
			captureStream.getVideoFormat();
			outputVideoFormat = convertCivilFormat(captureStream.getVideoFormat());
			
			captureStream.setObserver(new MyCaptureObserver());
			
			pushBufferStream = new MyPushBufferStream();
			
		}
		catch (CaptureException e)
		{	logger.log(Level.WARNING, "" + e, e);
			throw new IOException("" + e);
		}
		
		connected = true;
		
	}

	
	// handle ordinal locators, like civil:0 or civil:/0
	private static int ordinal(String remainder)
	{
		try
		{
			// allow either with slash or without
			if (remainder.startsWith("/"))
				remainder = remainder.substring(1);
			return Integer.parseInt(remainder);
		}
		catch (Exception e)
		{	return -1;
		}
	}
	
	private String deviceIdFromOrdinal(int index) throws CaptureException
	{
		final List list = system.getCaptureDeviceInfoList();
		if (index < 0 || index >= list.size())
			return null;
				
		com.lti.civil.CaptureDeviceInfo info = (com.lti.civil.CaptureDeviceInfo) list.get(index);
		return info.getDeviceID();
		
	}
	
	//@Override
	public void disconnect()
	{
		if (TRACE) logger.fine("disconnect");
		if (!connected)
			return;
		
		if (pushBufferStream != null)
			pushBufferStream.dispose();
		
		try
		{
			stop();
		} catch (IOException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		}
		
		if (captureStream != null)
		{
			try
			{
				captureStream.dispose();
			} catch (CaptureException e)
			{
				logger.log(Level.WARNING, "" + e, e);
			}
			finally
			{	captureStream = null;
			}
		}
		
		connected = false;
	}

	private static final String CONTENT_TYPE = ContentDescriptor.RAW;
	//@Override
	public String getContentType()
	{
		return CONTENT_TYPE;	// TODO: what should this be?
	}

	//@Override
	public Object getControl(String controlType)
	{
		return null;
	}

	//@Override
	public Object[] getControls()
	{
		return new Object[0];
	}

	private final SynchronizedBoolean started = new SynchronizedBoolean(false);
	//@Override
	public void start() throws IOException
	{
		if (TRACE) logger.fine("start");
		
		if (started.getValue())
		{	logger.warning("Civil DataSource.start called while already started, ignoring");
			return;
		}

		
		try 
		{
			captureStream.start();
		} catch (CaptureException e) 
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IOException("" + e);
		}	
		started.setValue(true);
	}

	//@Override
	public void stop() throws IOException
	{
		if (TRACE) logger.fine("stop");
		if (!started.getValue())
			return;
		
		try
		{
			if (captureStream != null)
			{	captureStream.stop();	
			}
			
			synchronized (bufferQueue)
			{
				while (!bufferQueue.isEmpty())
					bufferQueue.get();
//				if (!bufferQueueEnabled)
//				{	
//					// now that we not reading, stop queuing.
//					bufferQueueEnabled = false;
//				}
			}
		} catch (CaptureException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IOException("" + e);
		}
		catch (InterruptedException e)
		{	throw new InterruptedIOException("" + e);
		}
		finally
		{
			started.setValue(false);
		}
	}

	//@Override
	public Time getDuration()
	{
		return DURATION_UNBOUNDED;
	}

	public PushBufferStream[] getStreams()
	{
		if (TRACE) logger.fine("getStreams");
		if (pushBufferStream == null)
			return new PushBufferStream[0];
		return new PushBufferStream[] {pushBufferStream};
	}
	
	private class MyCaptureObserver implements CaptureObserver
	{

		public void onError(CaptureStream sender, CaptureException e)
		{
			logger.log(Level.WARNING, "" + e, e); // TODO: how to handle?
		}

		private long firstImageTimestamp = -1L;	// used to make timestamps start with zero
		// This is needed for proper playback when using JMF.
		
		private final FPSCounter fpsCounter = new FPSCounter();

		public void onNewImage(CaptureStream sender, Image image)
		{
			
			if (started.getValue())
			{
				// TODO: should always be the same as outputVideoFormat:
				final VideoFormat format = convertCivilFormat(image.getFormat());
				final Buffer buffer = new Buffer();

				buffer.setData(image.getBytes());
				buffer.setOffset(0);
				if (firstImageTimestamp < 0)
					firstImageTimestamp = image.getTimestamp();
				// if we don't set the timestamp, JMF is not happy.
				if (image.getTimestamp() >= 0)
					buffer.setTimeStamp((image.getTimestamp() - firstImageTimestamp) * 1000000L);	// timestamps will start with zero
				//logger.fine("      Timestamp=" + image.getTimestamp());
				buffer.setLength(image.getBytes().length);
				buffer.setFormat(format);

				// this is what causes us to drop frames if we can't keep up:
				try 
				{
					synchronized (bufferQueue)
					{
//						if (!bufferQueueEnabled)
//						{	// if we are not buffering multiple items, clear out all old ones:
//							while (!bufferQueue.isEmpty())
//								bufferQueue.get();
//						}
						if (bufferQueue.isFull())
							bufferQueue.get();	// drop a frame.
						bufferQueue.put(buffer);
					}
					
					if (pushBufferStream != null)
						pushBufferStream.notifyTransferHandlerAsync();
					
				} 
				catch (InterruptedException e) 
				{
					return;
				}
				


				if (TRACE_FPS)
				{
					fpsCounter.nextFrame();
					if (fpsCounter.getNumFrames() >= 50)
					{	System.out.println(fpsCounter);
						fpsCounter.reset();
					}
				}

			}
		}
	}
	    
    public static VideoFormat convertCivilFormat(com.lti.civil.VideoFormat civilVideoFormat)
    {
    	final int bitsPerPixel;
    	if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB24)
    		bitsPerPixel = 24;
    	else if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB32)
    		bitsPerPixel = 32;
    	else
    		throw new IllegalArgumentException();
    	final int red, green, blue;
    	red = 3;
    	green = 2;
    	blue = 1;
    	
    	final float fps = civilVideoFormat.getFPS();
    	final float frameRate;
    	if (fps < 0)
    		frameRate = Format.NOT_SPECIFIED;
    	else
    		frameRate = fps;
    	
    	return new RGBFormat(new Dimension(civilVideoFormat.getWidth(), civilVideoFormat.getHeight()), -1, byte[].class, frameRate, bitsPerPixel, red, green, blue);
    	
    }
    


	
	private class MyPushBufferStream implements PushBufferStream
	{

		public boolean endOfStream()
		{
			return false;
		}

		public ContentDescriptor getContentDescriptor()
		{
			return new ContentDescriptor(ContentDescriptor.RAW);	// It confuses me that we provide both this, and the correct format below (getFormat)
		}

		public long getContentLength()
		{
			return LENGTH_UNKNOWN;
		}

		public Object getControl(String controlType)
		{
			return null;
		}

		public Object[] getControls()
		{
			return new Object[0];
		}

		public Format getFormat()
		{
			if (outputVideoFormat == null )
				logger.warning("outputVideoFormat == null, video format unknown.");
			return outputVideoFormat;
		}

		public void read(Buffer buffer) throws IOException
		{
			Buffer nextBuffer = null;
			try
			{
				synchronized (bufferQueue)
				{
//					if (!bufferQueueEnabled)
//					{	
//						// now that we are reading, start queuing.
//						bufferQueueEnabled = true;
//					}

					nextBuffer = (Buffer) bufferQueue.get();
				}
			} catch (InterruptedException e)
			{
				throw new InterruptedIOException("" + e);
			}
			
			
			buffer.copy(nextBuffer);
			
			
		}

		private final SynchronizedObjectHolder transferHandlerHolder = new SynchronizedObjectHolder();
		public void setTransferHandler(BufferTransferHandler transferHandler)
		{	transferHandlerHolder.setObject(transferHandler);
		}
		
		void notifyTransferHandlerSync()
		{
			final BufferTransferHandler handler = (BufferTransferHandler) transferHandlerHolder.getObject();
			if (handler != null)
				handler.transferData(this);
		}
		
		void notifyTransferHandlerAsync() throws InterruptedException
		{
			if (notifyTransferHandlerThread == null)
			{	notifyTransferHandlerThread = new NotifyTransferHandlerThread(ThreadGroupMgr.getDefaultThreadGroup(), "NotifyTransferHandlerThread for " + DataSource.this);
				notifyTransferHandlerThread.start();
			}
			
			notifyTransferHandlerThread.notifyTransferHandlerAsync();
		}
		
		void dispose()
		{
			if (notifyTransferHandlerThread != null)
			{
				notifyTransferHandlerThread.close();
				try 
				{
					notifyTransferHandlerThread.waitUntilClosed();
				} catch (InterruptedException e) 
				{
					logger.log(Level.WARNING, "" + e, e);
				}
				finally
				{
					notifyTransferHandlerThread = null;
				}
			}
		}
		
		private NotifyTransferHandlerThread notifyTransferHandlerThread;
	
		// doing the transfer notifications in a different thread keeps the 
		// capture thread from being tied up.  Seems to avoid some deadlocks when
		// JMF is ahead in the classpath as well.
		class NotifyTransferHandlerThread extends CloseableThread
		{
			
			public NotifyTransferHandlerThread(ThreadGroup group, String threadName) 
			{
				super(group, threadName);
			}

			private final ProducerConsumerQueue q = new ProducerConsumerQueue(); 
			public void notifyTransferHandlerAsync() throws InterruptedException
			{
				q.put(Boolean.TRUE);
			}
			
			@Override
			public void run()
			{
				try
				{
					while (!isClosing())
					{
						if (q.get() == null)
							break;
						
						notifyTransferHandlerSync();
					}
				}
				catch (InterruptedException e)
				{
				}
				finally
				{
					setClosed();
				}
			}
		}
		
	}
	
	// implementation of CaptureDevice:
	// From Andrew Rowley:
	// 1) I am using JMF to perform processing, and using the civil library for the capture devices.  If I set the video resolution too high (say 640x480 or more), sometimes it looks like the capturing is taking place, but nothing happens.  When I try to stop the sending, I get an error (Error: Unable to prefetch com.sun.media.ProcessEngine@1ea0252).  This does not happen if I use a vfw device at any resolution, so I think it is something to do with civil, possibly to do with an assumption about the maximum video size?  I will look into this myself to make sure that it is not something in JMF, but it would be good to know that there is not something going wrong in civil too.
	// It turns out that if you make the civil DataSource (net.sf.fmj.media.protocol.civil.DataSource) implement javax.media.protocol.CaptureDevice, then the problem goes away as JMF does not then do prefetching	
	
    public CaptureDeviceInfo getCaptureDeviceInfo()
	{
    	// TODO: use name instead of device id:
    	// TODO: since we can query output formats from CIVIL, we should offer them all here:
		return new CaptureDeviceInfo(deviceId, getLocator(), new Format[] {outputVideoFormat});
	}

	public FormatControl[] getFormatControls()
	{
		return new FormatControl[] {new CivilFormatControl()};
	}
	
	private boolean enabled = true;
	
	private class CivilFormatControl implements FormatControl
	{

		public Component getControlComponent()
		{
			return null;
		}

		public Format getFormat()
		{
			return outputVideoFormat;
		}

		public Format[] getSupportedFormats()
		{
			// TODO: since we can query output formats from CIVIL, we should offer them all here:
			return new Format[] {outputVideoFormat};	
		}

		public boolean isEnabled()
		{
			return enabled;
		}

		public void setEnabled(boolean enabled)
		{	DataSource.this.enabled = enabled;
		}

		public Format setFormat(Format format)
		{
			// TODO: we shouldn't just accept anything that comes in...
			outputVideoFormat = (VideoFormat) format;
			// TODO: return specific format if passed in format is partially unspecified
			return outputVideoFormat;
		}
	}
}
