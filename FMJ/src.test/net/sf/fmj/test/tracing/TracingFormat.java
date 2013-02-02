/**
 * 
 */
package net.sf.fmj.test.tracing;

import javax.media.Format;

/**
 * 
 * @author Ken Larson
 *
 */
public class TracingFormat extends Format
{
	private final StringBuffer b = new StringBuffer();
	
	public StringBuffer getStringBuffer() {return b;}

	public TracingFormat(String arg0, Class arg1)
	{
		super(arg0, arg1);
	}

	public TracingFormat(String arg0)
	{
		super(arg0);
	}

	//@Override
	public Object clone()
	{
		b.append("clone\n");
		return super.clone();
	}

	//@Override
	protected void copy(Format arg0)
	{
		b.append("copy\n");
		super.copy(arg0);
	}

	//@Override
	public boolean equals(Object arg0)
	{
		b.append("equals\n");
		return super.equals(arg0);
	}

	//@Override
	public Class getDataType()
	{
		b.append("getDataType\n");
		return super.getDataType();
	}

	//@Override
	public String getEncoding()
	{
		b.append("getEncoding\n");
		return super.getEncoding();
	}

	//@Override
	public Format intersects(Format arg0)
	{
		b.append("intersects\n");
		return super.intersects(arg0);
	}

	//@Override
	public boolean isSameEncoding(Format arg0)
	{
		b.append("isSameEncoding\n");
		return super.isSameEncoding(arg0);
	}

	//@Override
	public boolean isSameEncoding(String arg0)
	{
		b.append("isSameEncoding\n");
		return super.isSameEncoding(arg0);
	}

	//@Override
	public boolean matches(Format arg0)
	{
		b.append("matches\n");
		return super.matches(arg0);
	}

	//@Override
	public Format relax()
	{
		b.append("relax\n");
		return super.relax();
	}


}