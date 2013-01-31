package net.sf.fmj.gst.media.content.unknown;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.protocol.DataSource;

import net.sf.fmj.ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import net.sf.fmj.ejmf.toolkit.media.AbstractPlayer;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;

import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.PlayBin;
import org.gstreamer.swing.GstVideoComponent;


/**
 * 
 * Handler for GStreamer, which bypasses most of JMF (parsers, codecs).
 * TODO: properly indicate EOM.
 * @author Ken Larson
 *
 */
public class Handler extends AbstractPlayer
{

	private static final Logger logger = LoggerSingleton.logger;

	private boolean prefetchNeeded = true;
	
	private static final boolean TRACE = true;
	
    private PlayBin playbin;
    private GstVideoComponent videoComponent;
	// TODO: audio component?
	
	private static boolean gstInitialized;
	private static void initGstreamer()
	{
		if (gstInitialized)
			return;
		
		logger.info("Initializing gstreamer");
   	
		try
		{
			Gst.init("FMJ GStreamer Handler", new String[] {});
			
	   		Thread t = new Thread() 
	   		{
	   			public void run()
	   			{
	   				new GMainLoop().run(); // TODO: what do we do with this?
	   		   	}
	   		};
	   		t.setDaemon(true);
	   		t.start();
		   		
		
			gstInitialized = true;
		}
		catch (Throwable t)
		{
			logger.log(Level.WARNING, "Unable to initialize gstreamer: " + t);
		}
	}
	
	
    public void setSource(DataSource source) throws IncompatibleSourceException
	{
   		if (TRACE) logger.fine("DataSource: " + source);

   		initGstreamer();
   		if (!gstInitialized)
   			throw new IncompatibleSourceException("Unable to initialize gstreamer");
   		
	    super.setSource(source);
	    
	}
    
	//@Override
	public void doPlayerClose()
	{
		// TODO
		logger.info("Handler.doPlayerClose");
	}

	//@Override
	public boolean doPlayerDeallocate()
	{
		logger.info("Handler.doPlayerDeallocate");
//		if (playbin != null)
//			playbin.dispose();	// causes JVM crash
		return true;
	}

	//@Override
	public boolean doPlayerPrefetch()
	{
		if( ! prefetchNeeded ) return true;
		 
		prefetchNeeded = false;
 
		return true;
	}

	//@Override
	public boolean doPlayerRealize()
	{
		// TODO: support arbitrary URL/URIs
//		URI uri;
//		try {
//			uri = getSource().getLocator().getURL().toURI();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return false;
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			return false;
//		}
//		System.out.println("URI: " + uri);

		final String path = URLUtils.extractValidPathFromFileUrl(getSource().getLocator().toExternalForm());
		// logic copied from GstVideoPlayer
		URI uri = parseURI(path);
        playbin = new PlayBin(uri.toString());
        playbin.setURI(uri);
        videoComponent = new GstVideoComponent();
        playbin.setVideoSink(videoComponent.getElement());
        
        videoComponent.setPreferredSize(new Dimension(640, 480));	// TODO: get media size
        return true;
	}
	
	/** Copied from GstVideoPlayer */
    private static URI parseURI(String uri) {
        try {
            URI u = new URI(uri);
            if (u.getScheme() == null) {
                throw new URISyntaxException(uri, "Invalid URI scheme");
            }
            return u;
        } catch (URISyntaxException e) {
            File f = new File(uri);
            if (!f.exists()) {
                throw new IllegalArgumentException("Invalid URI/file " + uri, e);
            }
            return f.toURI();
        }
    }


	//@Override
	public void doPlayerSetMediaTime(Time t)
	{
		logger.info("Handler.doPlayerSetMediaTime " + t.getNanoseconds());
		// TODO: untested since getPosition is always returning 0, the FMJStudio app does not allow us to seek.
		playbin.setPosition(new org.gstreamer.Time(t.getNanoseconds()));
		
	}

	//@Override
	public float doPlayerSetRate(float rate)
	{
		logger.info("Handler.doPlayerSetRate " + rate);
		return rate;	// TODO
	}

	//@Override
	public boolean doPlayerStop()
	{
        playbin.stop();
		return true;
	}

	//@Override
	public boolean doPlayerSyncStart(Time t)
	{
		logger.info("Handler.doPlayerSyncStart" + t);

        if (!playbin.isPlaying()) 
        {
            playbin.play();
        }


		return true; 
	}

	//@Override
	public Time getPlayerDuration()
	{
        if (getState() < Realized)
		{
			return DURATION_UNKNOWN;
		} 
       	else
       	{
       		final long durationNanos = playbin.getDuration().longValue();
       		if (durationNanos <= 0)	// TODO: why are we getting 0?
       			return DURATION_UNKNOWN;	
       		return new Time(durationNanos);
       	}
	}

	//@Override
	public synchronized Time getMediaTime()
	{
   		if (getState() < Realized)
		{
			return super.getMediaTime();
		} 
       	else
       	{
       		return new Time(playbin.getPosition().longValue());
       	}
	}



	// @Override
	public Time getPlayerStartLatency()
	{
		return new Time(0);
	}

	//@Override
	public Component getVisualComponent()
	{
		return videoComponent;
	}



//	@Override
//	public Component getControlPanelComponent()
//	{
//		if (qtcMovieController == null)
//			return null;
//		return qtcMovieController.asComponent();
//	}
	
	// until we figure out how to isolate GST's control panel component (get it without the movie panel),
	// we'll use FMJ's control panel.  See GstVideoPlayer
    public Component getControlPanelComponent() {
        Component c = super.getControlPanelComponent();

        if( c == null ) {
            c = new StandardControlPanel(this, StandardControlPanel.USE_START_CONTROL | StandardControlPanel.USE_STOP_CONTROL | StandardControlPanel.USE_PROGRESS_CONTROL);
            setControlPanelComponent(c);
        }

        return c;
    }

}
