package com.sun.media.parser;

import java.io.IOException;

import javax.media.Demultiplexer;
import javax.media.Duration;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.Track;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

import com.sun.media.BasicPlugIn;

/**
 * TODO: stub.
 * @author Ken Larson
 *
 */
public abstract class RawParser extends BasicPlugIn implements Demultiplexer
{
	static final String NAME = "Raw parser";
	
	protected DataSource source;
	
	ContentDescriptor[] supported = new ContentDescriptor[]{
			new ContentDescriptor(ContentDescriptor.RAW)
	};
	
	public String getName()
	{	return NAME;
	}
	
	public RawParser()
	{
	}
	
	public ContentDescriptor[] getSupportedInputContentDescriptors()
	{	return supported;
	}
	
	public boolean isPositionable()
	{
		return false;
	}
	
	public boolean isRandomAccess()
	{
		return false;
	}
	
	public Track[] getTracks()
	{
		return null;
	}
	
	public Time getMediaTime()
	{
		
		return Time.TIME_UNKNOWN;		
	}
	
	public Time getDuration()
	{
		if (source != null)
			return source.getDuration();
		return Duration.DURATION_UNKNOWN;	
	}
	
	public void reset()
	{
	}
	
	public Time setPosition(Time when, int round)
	{
		return null;	// TODO
	}
	
	public Object[] getControls()
	{
		return null;
	}
	
	public abstract void stop();
	
	public abstract void start() throws IOException;
	
	public abstract void setSource(DataSource arg) throws IOException, IncompatibleSourceException;
	
}
