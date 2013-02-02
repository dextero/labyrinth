package net.sf.fmj.test.tracing;

import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.DataSource;

/**
 * 
 * @author Ken Larson
 *
 */
public class TracingDataSource extends DataSource
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

}
