package net.sf.fmj.test.tracing;

import java.io.IOException;

import javax.media.BadHeaderException;
import javax.media.Demultiplexer;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.Track;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

/**
 * 
 * @author Ken Larson
 *
 */
public class TracingDemultiplexer extends TracingPlugIn implements Demultiplexer
{

	public Time getDuration()
	{
		trace("getDuration");
		return DURATION_UNKNOWN;
	}

	public Time getMediaTime()
	{
		trace("getMediaTime");
		return null;
	}

	public ContentDescriptor[] getSupportedInputContentDescriptors()
	{
		trace("getSupportedInputContentDescriptors");
		return null;
	}

	public Track[] getTracks() throws IOException, BadHeaderException
	{
		trace("getTracks");
		return null;
	}

	public boolean isPositionable()
	{
		trace("isPositionable");
		return false;
	}

	public boolean isRandomAccess()
	{
		trace("isRandomAccess");
		return false;
	}

	public Time setPosition(Time where, int rounding)
	{
		trace("setPosition");
		return null;
	}

	public void start() throws IOException
	{
		trace("start");
	}

	public void stop()
	{
		trace("stop");
	}

	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{
		trace("setSource");
	}

}
