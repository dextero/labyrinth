package net.sf.fmj.media.protocol.javasound;

import java.awt.Component;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.control.FormatControl;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import net.sf.fmj.media.renderer.audio.JavaSoundRenderer;
import net.sf.fmj.utility.LoggerSingleton;

import com.lti.utils.synchronization.CloseableThread;
import com.lti.utils.synchronization.SynchronizedBoolean;
import com.lti.utils.synchronization.SynchronizedObjectHolder;

/**
 * DataSource for JavaSound audio recording.
 * @author Ken Larson
 *
 */
public class DataSource extends PushBufferDataSource implements CaptureDevice
{
	private static final boolean TRACE = true;
	private static final Logger logger = LoggerSingleton.logger;
	private MyPushBufferStream pushBufferStream;
	private TargetDataLine targetDataLine;
	private AudioInputStream audioInputStream;
	private javax.sound.sampled.AudioFormat javaSoundAudioFormat;
	private javax.media.format.AudioFormat jmfAudioFormat;
	
	
	private void setJavaSoundAudioFormat(javax.sound.sampled.AudioFormat f)
	{
		javaSoundAudioFormat = f;
		jmfAudioFormat = JavaSoundRenderer.convertFormat(javaSoundAudioFormat);
	}
	
	private void setJMFAudioFormat(javax.media.format.AudioFormat f)
	{
		jmfAudioFormat = f;
		javaSoundAudioFormat = JavaSoundRenderer.convertFormat(jmfAudioFormat);
	}
	

	private boolean connected;
	
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
		   

			if (jmfAudioFormat == null)
			{
				if (getSupportedFormats().length <= 0)
					throw new IOException("No supported formats available");
				
				
				javax.media.format.AudioFormat initialAudioFormat = null;
				// If the URL specifies any format options, find a format that matches:
				try
				{
					final javax.media.format.AudioFormat parsedAudioFormat = JavaSoundUrlParser.parse(getLocator().toExternalForm());
					for (int i = 0; i < getSupportedFormats().length; ++i)
					{	
						if (getSupportedFormats()[i].matches(parsedAudioFormat))
						{	initialAudioFormat = (javax.media.format.AudioFormat) getSupportedFormats()[i];
							break;
						}
					}
				
				}
				catch (Exception e)
				{
					// just use the default if we have any problems.
					logger.log(Level.WARNING, "" + e, e);
				}
				
				// if none found, set the default format to be the first supported one.  These are in sorted order by quality, so this should
				// be the best quality one.
				if (initialAudioFormat == null)
					initialAudioFormat = (javax.media.format.AudioFormat) getSupportedFormats()[0];
				
				
				setJMFAudioFormat(initialAudioFormat);

			}

			/* Now, we are trying to get a TargetDataLine. The
			   TargetDataLine is used later to read audio data from it.
			   If requesting the line was successful, we are opening
			   it (important!).
			*/
			final DataLine.Info info = new DataLine.Info(TargetDataLine.class, javaSoundAudioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			targetDataLine.open(javaSoundAudioFormat);

			audioInputStream = new AudioInputStream(targetDataLine);
			
			pushBufferStream = new MyPushBufferStream();
			
		}
		catch (LineUnavailableException e)
		{	logger.log(Level.WARNING, "" + e, e);
			throw new IOException("" + e);
		}
		
		connected = true;
		
	}

	//@Override
	public void disconnect()
	{
		// TODO: what should happen in disconnect and what should happen in stop?
		
		if (TRACE) logger.fine("disconnect");
		
		if (!connected)
			return;
	
		try
		{
			stop();
			
			if (targetDataLine != null)
			{	targetDataLine.close();
			}

		} catch (IOException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		}
		
		

		finally
		{
			targetDataLine = null;
			audioInputStream = null;
			pushBufferStream = null;
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
			return;
		
		targetDataLine.start();
		pushBufferStream.startAvailabilityThread();

		
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
			if (pushBufferStream != null)
			{	pushBufferStream.stopAvailabilityThread();
			}
			if (targetDataLine != null)
			{	targetDataLine.stop();
				targetDataLine.flush();
			}
//			if (audioInputStream != null)
//				audioInputStream.close();

		} 
		catch (InterruptedException e) 
		{
			throw new InterruptedIOException();
		} 
		finally
		{
			//audioInputStream = null;
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
		return new PushBufferStream[] {pushBufferStream};
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
			return jmfAudioFormat;
		}

		int bufferSize = 10000;	// TODO: this is arbitrary.
		// this should be adjusted based on the sample rate.
		// should not be too big, otherwise latency problems appear.
		
		long totalBytesRead = 0;
		public void read(Buffer buffer) throws IOException
		{
			synchronized (MyPushBufferStream.this)
			{
				if (!started.getValue())
				{	buffer.setOffset(0);
					buffer.setLength(0);
					buffer.setDiscard(true);
					return;
				}
				byte[] data = (byte []) buffer.getData();
				if (data == null || data.length < bufferSize)
				{	data = new byte[bufferSize]; 
					buffer.setData(data);
				}
				
				final int actuallyRead = audioInputStream.read(data, 0, bufferSize);
				
				if (actuallyRead < 0)
				{
					buffer.setEOM(true);	// TODO: is this correct?
					buffer.setDiscard(true);
					buffer.setOffset(0);
					buffer.setLength(0);
				}
				else
				{
					totalBytesRead += actuallyRead;
					buffer.setLength(actuallyRead);
	                //buffer.setTimeStamp(System.currentTimeMillis() * 1000000);	// TODO: should timestamp be set here or below?  based on actual time, or number of bytes so far?
					buffer.setOffset(0);
					buffer.setFormat(jmfAudioFormat);
					// calculate timestamp for this buffer, if we can.
					if (javaSoundAudioFormat.getFrameSize() > 0 && javaSoundAudioFormat.getFrameRate() > 0.f)
					{
						buffer.setTimeStamp(bytesToNanos(totalBytesRead));
						buffer.setDuration(bytesToNanos(actuallyRead));
					}
					
				}
			}
		
		}
		
		private AvailabilityThread availabilityThread;
		
		public void startAvailabilityThread()
		{
			availabilityThread = new AvailabilityThread();
			availabilityThread.setName("AvailabilityThread for " + MyPushBufferStream.this);
			availabilityThread.start();
		}
		
		public void stopAvailabilityThread() throws InterruptedException
		{
			if (availabilityThread == null)
				return;
			availabilityThread.close();
			availabilityThread.waitUntilClosed();
			availabilityThread = null;
		}
		
		private class AvailabilityThread extends CloseableThread
		{
			
			
			public void close() 
			{
				// no need to interrupt transfer handler.
				setClosing();
			}

			public void run()
			{
				try
				{
					while (!isClosing())
					{
						final int available;
						
						synchronized (MyPushBufferStream.this)
						{
							try 
							{
								available = audioInputStream.available();
							} catch (IOException e) {
								
								logger.log(Level.WARNING, "" + e , e);
								continue;
							}
						}
						
						if (available > 0)
						{
							notifyTransferHandler();
						}
						else
						{
							Thread.sleep(20);
						}
						
					}
				}
				catch (InterruptedException e)
				{
					return;
				}
				finally
				{
					setClosed();
				}
			}
		}
		


		private final SynchronizedObjectHolder transferHandlerHolder = new SynchronizedObjectHolder();
		public void setTransferHandler(BufferTransferHandler transferHandler)
		{	transferHandlerHolder.setObject(transferHandler);
		}
		
		void notifyTransferHandler()
		{
			final BufferTransferHandler handler = (BufferTransferHandler) transferHandlerHolder.getObject();
			if (handler != null)
				handler.transferData(this);
		}
		
	}
	
	/**
	 * 
	 * @return -1L if cannot convert, because frame size and frame rate are not known.
	 */
	private long bytesToNanos(long bytes)
	{
		if (javaSoundAudioFormat.getFrameSize() > 0 && javaSoundAudioFormat.getFrameRate() > 0.f)
		{
			final long frames = bytes / javaSoundAudioFormat.getFrameSize();
			final double seconds = frames / javaSoundAudioFormat.getFrameRate();
			final double nanos = secondsToNanos(seconds);
			return (long) nanos;
		}
		else
			return -1L;
	}
	
	private static final double secondsToNanos(double secs)
	{	return secs * 1000000000.0;
	}
	
	private Format[] formatsArray;
	
	private Format[] getSupportedFormats()
	{
		if (formatsArray != null)
			return formatsArray;
		
		formatsArray = querySupportedFormats();
		
		return formatsArray;
	}
	
	public static Format[] querySupportedFormats()
	{
		// query audio formats formats from javasound:
		final List formats = new ArrayList();
		
		
		final DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);
		final Line.Info[] infos = AudioSystem.getTargetLineInfo(info);
		for (int i = 0; i < infos.length; ++i)
		{
			//System.out.println("" + infos[i]);
			
			final DataLine.Info iCast = (DataLine.Info) infos[i];
			for (int j = 0; j < iCast.getFormats().length; ++j)
			{	
				final javax.sound.sampled.AudioFormat javaSoundAudioFormat = iCast.getFormats()[j];
				//System.out.println(javaSoundAudioFormat);
				final javax.media.format.AudioFormat jmfAudioFormat = JavaSoundRenderer.convertFormat(javaSoundAudioFormat);
				if (jmfAudioFormat.getFrameRate() == javax.media.format.AudioFormat.NOT_SPECIFIED)
				{
					// for some reason, JavaSound doesn't specify the frame rates.  So we'll just add the common ones.
					// TODO: how do we know that these sample rates are actually supported?  we should really find this out
					// from JavaSound.
					final double[] commonSampleRates = new double[] {44100.0, 22050.0, 11025.0, 8000.0};
					for (int k = 0; k < commonSampleRates.length; ++k)
					{
						double sampleRate = commonSampleRates[k];
						final javax.media.format.AudioFormat jmfAudioFormatSpecific 
							= new javax.media.format.AudioFormat(
									jmfAudioFormat.getEncoding(), 
									sampleRate, 
									jmfAudioFormat.getSampleSizeInBits(),
									jmfAudioFormat.getChannels(), 
									jmfAudioFormat.getEndian(), 
									jmfAudioFormat.getSigned(), 
									jmfAudioFormat.getFrameSizeInBits(), 
									jmfAudioFormat.getChannels() == javax.media.format.AudioFormat.NOT_SPECIFIED ? javax.media.format.AudioFormat.NOT_SPECIFIED : sampleRate * jmfAudioFormat.getChannels(), 
									jmfAudioFormat.getDataType());
						
						if (!formats.contains(jmfAudioFormatSpecific))
							formats.add(jmfAudioFormatSpecific);
					}
				}
				else
				{
					if (!formats.contains(jmfAudioFormat))
						formats.add(jmfAudioFormat);
				}
			}
			
		}
		
		// sort by quality:
		Collections.sort(formats, Collections.reverseOrder(new AudioFormatComparator()));
		
		
		// convert to an array:
		final Format[] formatsArray = new Format[formats.size()];
		for (int i = 0; i < formats.size(); ++i)
		{	formatsArray[i] = (Format) formats.get(i);
		}
		
		
		return formatsArray;
	}
	

	public CaptureDeviceInfo getCaptureDeviceInfo()
	{
		// TODO: JMF returns javasound://44100 for the media locator, why not just javasound://? 
		return new CaptureDeviceInfo("JavaSound audio capture", new MediaLocator("javasound://44100"), getSupportedFormats());
	}

	public FormatControl[] getFormatControls()
	{
		return new FormatControl[] {new JavaSoundFormatControl()};
	}
	
	private boolean enabled = true;
	
	private class JavaSoundFormatControl implements FormatControl
	{

		public Component getControlComponent()
		{
			return null;
		}

		public Format getFormat()
		{
			return jmfAudioFormat;
		}

		public Format[] getSupportedFormats()
		{
			return DataSource.this.getSupportedFormats();
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
			setJMFAudioFormat((javax.media.format.AudioFormat) format);
			// TODO: return specific format if passed in format is partially unspecified
			return jmfAudioFormat;
		}
	}
	
}
