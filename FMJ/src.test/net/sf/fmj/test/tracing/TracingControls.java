package net.sf.fmj.test.tracing;

import javax.media.Controls;

/**
 * 
 * @author Ken Larson
 *
 */
public class TracingControls implements Controls
{

	protected final StringBuffer b = new StringBuffer();
	
	protected void trace(String s)
	{
		//System.out.println(s);
		b.append(s + "\n");
	}
	
	public StringBuffer getStringBuffer() {return b;}

	
	public Object getControl(String controlType)
	{
		trace("getControl");
		return null;
	}

	public Object[] getControls()
	{
		trace("getControls");
		return null;
	}

}
