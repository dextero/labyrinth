package net.sf.fmj.test.compat.datasources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.DataSource;

import junit.framework.TestCase;

/**
 * 
 * @author Ken Larson
 *
 */
public class DataSourceTest extends TestCase
{
	public void testDataSource()
	{
		{
			MyMediaLocator m = new MyMediaLocator("asdf");
			MyDataSource d = new MyDataSource(m);
			d.doInitCheck();
			// make sure no unexpected methods were called:
			assertEquals(m.getStringBuffer().toString(), "");
			assertEquals(d.getStringBuffer().toString(), "");
		}
		try
		{
			MyDataSource d = new MyDataSource(null);
			d.doInitCheck();
			assertTrue(false);
		}
		catch (Error t)
		{
			assertTrue(t.getClass() == Error.class);
			assertEquals(t.getMessage(), "Uninitialized DataSource error.");
		}
		
		
	}
	
	class MyMediaLocator extends MediaLocator
	{
		private final StringBuffer stringBuffer = new StringBuffer();
		
		public StringBuffer getStringBuffer()
		{
			return stringBuffer;
		}
		
		public MyMediaLocator(String arg0)
		{
			super(arg0);
		}

		public MyMediaLocator(URL arg0)
		{
			super(arg0);
		}

		//@Override
		public String getProtocol()
		{
			stringBuffer.append("getProtocol");
			return super.getProtocol();
		}

		//@Override
		public String getRemainder()
		{
			stringBuffer.append("getRemainder");
			return super.getRemainder();
		}

		//@Override
		public URL getURL() throws MalformedURLException
		{
			stringBuffer.append("getURL");
			return super.getURL();
		}

		//@Override
		public String toExternalForm()
		{
			return super.toExternalForm();
		}

		//@Override
		public String toString()
		{
			return super.toString();
		}
		
	}
	
	class MyDataSource extends DataSource
	{
		
		private final StringBuffer stringBuffer = new StringBuffer();
		
		

		public StringBuffer getStringBuffer()
		{
			return stringBuffer;
		}
		public void doInitCheck()
		{
			initCheck();
		}
		public MyDataSource()
		{
			super();
		}

		public MyDataSource(MediaLocator arg0)
		{
			super(arg0);
		}

		//@Override
		public void connect() throws IOException
		{
			stringBuffer.append("connect");
		}

		//@Override
		public void disconnect()
		{
			stringBuffer.append("disconnect");
		}

		//@Override
		public String getContentType()
		{
			stringBuffer.append("getContentType");
			return null;
		}

		//@Override
		public Object getControl(String arg0)
		{
			stringBuffer.append("getControl");
			return null;
		}

		//@Override
		public Object[] getControls()
		{
			stringBuffer.append("getControls");
			return null;
		}

		//@Override
		public Time getDuration()
		{
			stringBuffer.append("getDuration");
			return null;
		}

		//@Override
		public void start() throws IOException
		{
			stringBuffer.append("start");
		}

		//@Override
		public void stop() throws IOException
		{
			stringBuffer.append("stop");
		}
		
	}
}
