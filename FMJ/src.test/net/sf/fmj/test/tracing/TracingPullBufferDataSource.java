package net.sf.fmj.test.tracing;

import java.io.IOException;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.PullBufferStream;

/**
 * 
 * @author Ken Larson
 *
 */
public class TracingPullBufferDataSource extends javax.media.protocol.PullBufferDataSource
{
	private final StringBuffer b = new StringBuffer();
	
	public StringBuffer getStringBuffer() {return b;}

	//@Override
	public void connect() throws IOException
	{
		b.append("connect\n");
	}

	//@Override
	public void disconnect()
	{
		b.append("disconnect\n");
	}

	//@Override
	public String getContentType()
	{
		b.append("getContentType\n");
		return null;
	}

	//@Override
	public Object getControl(String controlType)
	{
		b.append("getControl\n");
		return null;
	}

	//@Override
	public Object[] getControls()
	{
		b.append("getControls\n");
		return null;
	}

	//@Override
	public Time getDuration()
	{
		b.append("getDuration\n");
		return null;
	}

	//@Override
	public void start() throws IOException
	{
		b.append("start\n");
	}

	//@Override
	public void stop() throws IOException
	{
		b.append("stop\n");
	}

	public PullBufferStream[] streams;
	//@Override
	public PullBufferStream[] getStreams()
	{
		b.append("getStreams\n");
		return streams;
	}

	//@Override
	public MediaLocator getLocator()
	{
		b.append("getLocator\n");
		return super.getLocator();
	}

	//@Override
	protected void initCheck()
	{
		b.append("initCheck\n");
		super.initCheck();
	}

	//@Override
	public void setLocator(MediaLocator source)
	{
		b.append("setLocator\n");
		super.setLocator(source);
	}
	
	
	//
	
	
}
