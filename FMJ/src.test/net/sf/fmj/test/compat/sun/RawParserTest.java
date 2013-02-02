package net.sf.fmj.test.compat.sun;

import java.io.IOException;

import javax.media.Duration;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.protocol.DataSource;

import net.sf.fmj.test.tracing.TracingDataSource;

import junit.framework.TestCase;

import com.sun.media.parser.RawParser;

/**
 * 
 * @author Ken Larson
 *
 */
public class RawParserTest extends TestCase
{

	public void testRawParser() throws Exception
	{
		MyRawParser p = new MyRawParser();
		assertEquals(p.getName(), "Raw parser");
		assertEquals(p.isPositionable(), false);
		assertEquals(p.isRandomAccess(), false);
		assertEquals(p.getTracks(), null);
		assertEquals(p.getMediaTime(), Time.TIME_UNKNOWN);
		assertEquals(p.getDuration(), Duration.DURATION_UNKNOWN);
		//assertEquals(p.getControls(), null);	// NPE
		
		assertEquals(p.getSupportedInputContentDescriptors().length, 1);
		assertEquals(p.getSupportedInputContentDescriptors()[0].getContentType(), "raw");
		assertEquals(p.getSupportedInputContentDescriptors()[0].getEncoding(), "raw");
		assertEquals(p.getSupportedInputContentDescriptors()[0].getDataType(), byte[].class);
	
		{
			TracingDataSource ds = new TracingDataSource();
			p.setSource(ds);
			
			assertEquals(p.getName(), "Raw parser");
			assertEquals(p.isPositionable(), false);
			assertEquals(p.isRandomAccess(), false);
			assertEquals(p.getTracks(), null);
			assertEquals(p.getMediaTime(), Time.TIME_UNKNOWN);
			assertEquals(p.getDuration(), Duration.DURATION_UNKNOWN);
			
			assertEquals(ds.getStringBuffer().toString(), "");	// does not appear to call the data source to get these
			
		
		}
		
		
	}
	

	static class MyRawParser extends RawParser
	{

		//@Override
		public void setSource(DataSource arg) throws IOException, IncompatibleSourceException
		{
		}

		//@Override
		public void start() throws IOException
		{
		}

		//@Override
		public void stop()
		{
		}

		//@Override
		public void close()
		{
		}

		//@Override
		public void open() throws ResourceUnavailableException
		{
		}
	}
}
