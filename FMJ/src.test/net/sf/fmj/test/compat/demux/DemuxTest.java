package net.sf.fmj.test.compat.demux;

import java.io.File;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;

import junit.framework.TestCase;
import net.sf.fmj.utility.PlugInUtility;

import com.sun.media.MimeManager;

/**
 * 
 * @author Ken Larson
 *
 */
public class DemuxTest extends TestCase
{

	public void testDemux() throws Exception
	{
		
		synchronized (javax.media.PlugInManager.class)	// so that tests that modify the plugins don't conflict.
		{

			synchronized (MimeManager.class)	// so that tests modifying the mime manager don't conflict
			{
				PlugInUtility.registerPlugIn(TestDemux.class.getName());
		
				MimeManager.addMimeType("test", "audio/test");
		
				File f = File.createTempFile("test", ".test");
				f.deleteOnExit();
				assertTrue(f.exists());
				
				// TODO: JMF and FMJ behave differently with unknown/missing files
				
				{
					Player p = Manager.createPlayer(new MediaLocator("file://" + f.getAbsolutePath()));
					assertEquals(TestDemux.instance.getStringBuffer().toString(), "setSource\ngetDuration\n");
				}
		
				
				{
					try
					{
						Player p = Manager.createRealizedPlayer(new MediaLocator("file://" + f.getAbsolutePath()));
						assertTrue(false);
					}
					catch (CannotRealizeException e)
					{
					}
					Thread.sleep(1000);	// let the state transition thread do its thing
					
					assertTrue(TestDemux.instance.getStringBuffer().toString().startsWith("setSource" + "\n" +
							"getDuration" + "\n" +
							"open" + "\n" +
							"start" + "\n" +
							"getTracks" + "\n" +
							"close" + "\n" 
		//					+
		//					"close" + "\n"	// JMF closes one extra time
							));
		
				}
				
				MimeManager.removeMimeType("test");
				PlugInManager.removePlugIn(TestDemux.class.getName(), PlugInManager.DEMULTIPLEXER);
				
			
			}
		}
		
	}
}
