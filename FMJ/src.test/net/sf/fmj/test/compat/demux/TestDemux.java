package net.sf.fmj.test.compat.demux;

import javax.media.protocol.ContentDescriptor;

import net.sf.fmj.test.tracing.TracingDemultiplexer;

/**
 * 
 * @author Ken Larson
 *
 */
public class TestDemux extends TracingDemultiplexer
{

	public static TestDemux instance;
	public TestDemux()
	{
		super();
		instance = this;
	}

	public ContentDescriptor[] getSupportedInputContentDescriptors()
	{
		return new ContentDescriptor[] {new ContentDescriptor("audio.test")};
	}
}
