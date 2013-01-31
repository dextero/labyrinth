package com.sun.media.parser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Demultiplexer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.Track;
import javax.media.TrackListener;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.SourceStream;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * TODO: stub
 * @author Ken Larson
 *
 */
public class RawPullBufferParser extends RawPullStreamParser
{
	private static final Logger logger = LoggerSingleton.logger;

	static final String NAME = "Raw pull stream parser";	// name is wrong in JMF too.
	
	public RawPullBufferParser()
	{	super();
	}
	
	public String getName()
	{	return NAME;
	}
	
	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{	
		if (!(source instanceof PullBufferDataSource))
			throw new IncompatibleSourceException("DataSource not supported: " + source);	// TODO: what types of DataSource are supported?

		final PullBufferDataSource pullBufferDataSource = (PullBufferDataSource) source;
		
		streams = pullBufferDataSource.getStreams();
		if (streams == null)
			throw new IOException("Got a null stream from the DataSource");
		
		if (streams.length == 0)
			throw new IOException("Got a empty stream array from the DataSource");
		
		for (int i = 0; i < streams.length; ++i)
		{
			if (streams[i] == null)
				throw new IncompatibleSourceException("DataSource not supported: " + source);
		}
		
		this.source = source;
		
		
	}
	protected boolean supports(SourceStream[] streams)
	{	throw new UnsupportedOperationException();	// TODO
	}
	
	public void open()
	{	// TODO
		// JMF calls getFormat on each stream, tracks is set.
		
		tracks = new Track[streams.length];
		for (int i = 0; i < streams.length; ++i)
		{
			PullBufferStream s = (PullBufferStream) streams[i];
			
			tracks[i] = new FrameTrack(this, s);
			
		}
	}
	
	class FrameTrack implements Track
	{
		Demultiplexer parser;
		PullBufferStream pbs;
		boolean enabled;
		Format format;
		TrackListener listener;
		Integer stateReq;
		FrameTrack(Demultiplexer parser, PullBufferStream pbs)
		{
			this.parser = parser;
			this.pbs = pbs;
			this.format = pbs.getFormat();
		}
		public Format getFormat()
		{	return format;
		}
		public void setEnabled(boolean t)
		{	this.enabled = t;
		}
		
		public boolean isEnabled()
		{	return enabled;
		}
		public Time getDuration()
		{
			return Duration.DURATION_UNKNOWN;	// TODO
		}
		public Time getStartTime()
		{
			return TIME_UNKNOWN; 	// TODO
		}
		
		public void setTrackListener(TrackListener l)
		{
			this.listener = l;
		}
		
		public void readFrame(Buffer buffer)
		{
			try
			{
				pbs.read(buffer);
			} catch (IOException e)
			{
				logger.log(Level.WARNING, "" + e, e);	// TODO: what to do?
			}
			
		}
		public int mapTimeToFrame(Time t)
		{	return FRAME_UNKNOWN;	// TODO
		}
		
		public Time mapFrameToTime(int frameNumber)
		{	return TIME_UNKNOWN; 	// TODO
		}
		
	}
}
