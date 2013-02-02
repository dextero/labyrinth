package net.sf.fmj.qt.media.content.unknown;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.protocol.DataSource;

import net.sf.fmj.ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import net.sf.fmj.ejmf.toolkit.media.AbstractPlayer;
import net.sf.fmj.qt.utils.QTSessionCheck;
import net.sf.fmj.utility.LoggerSingleton;
import quicktime.QTException;
import quicktime.app.view.MoviePlayer;
import quicktime.app.view.QTFactory;
import quicktime.app.view.QTJComponent;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.media.DataRef;

/**
 * 
 * Handler for Quicktime for Java, which bypasses most of JMF (parsers, codecs).
 * @author Ken Larson
 *
 */
public class Handler extends AbstractPlayer
{

	private static final Logger logger = LoggerSingleton.logger;

	private boolean prefetchNeeded = true;
	
	private static final boolean TRACE = true;
	
	private Movie m;
	private MovieController mc;	
	private QTJComponent qtcMovieController = null;
	private QTJComponent qtcMovie = null;
	

        
	// This will resize the window to the size of the new movie
	public void createNewMovieFromURL (String theURL) throws QTException
	{

		logger.fine(theURL);
		// create the DataRef that contains the information about where the movie is
		DataRef urlMovie = new DataRef(theURL);
		
		// create the movie 
		m = Movie.fromDataRef (urlMovie,StdQTConstants.newMovieActive);
        
        if (qtcMovie == null)
        {
            qtcMovie = QTFactory.makeQTJComponent(new MoviePlayer(m));
           
        } else {
        		qtcMovie.setMoviePlayer(new MoviePlayer(m));
        }
        
        // create the movie controller
        //mc = new MovieController (m);
       
        // TODO: how can we create a control-panel component - just the playback bar?
        //mc.setVisible(false);	// hides the playback bar
        
        // create and add a QTComponent if we haven't done so yet, otherwise set qtc's movie controller
//        if (qtcMovieController == null)
//        {
//            qtcMovieController = QTFactory.makeQTComponent(mc);
//           
//        } else {
//            qtcMovieController.setMovieController(mc);
//        }

	}
	

	
    public void setSource(DataSource source) throws IncompatibleSourceException
	{

    	
    		if (TRACE) logger.fine("DataSource: " + source);
    		
    		try {
    				QTSessionCheck.check();
    			
				createNewMovieFromURL(source.getLocator().toExternalForm());
			} catch (QTException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new IncompatibleSourceException();
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new IncompatibleSourceException();
			}
	 
		
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
	    
		return true;
	}
	



	//@Override
	public void doPlayerSetMediaTime(Time t)
	{
		logger.info("Handler.doPlayerSetMediaTime" + t);
		
		try
		{
			m.setTime(new TimeRecord(m.getTimeScale(), (long) (t.getSeconds() * m.getTimeScale())));
		} catch (StdQTException e)
		{
			// TODO handle
			logger.log(Level.WARNING, "" + e, e);
		} catch (QTException e)
		{
			// TODO handle
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	//@Override
	public float doPlayerSetRate(float rate)
	{
		logger.info("Handler.doPlayerSetRate " + rate);
		try
		{
			if (false)
				m.setRate(rate);	// TODO: setting this to 1.0 starts the movie
			return rate;
		} catch (StdQTException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			return getRate();
		}
	}

	//@Override
	public boolean doPlayerStop()
	{
		logger.info("Handler.doPlayerStop");
		try {
			if (m != null)
				m.stop();
		} catch (QTException err) {
			logger.log(Level.WARNING, "" + err, err);
			return false;
		}
		return true;
	}

	//@Override
	public boolean doPlayerSyncStart(Time t)
	{
		logger.info("Handler.doPlayerSyncStart" + t);
		try {
			m.start();
		} catch (StdQTException e) {
			logger.log(Level.WARNING, "" + e, e);
			return false;
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
        	else if (m != null)
		{
			try
			{
				final double totalSeconds = (double) m.getDuration() / (double) m.getTimeScale();
				return new Time(totalSeconds);

			} catch (StdQTException e)
			{
				logger.log(Level.WARNING, "" + e, e);
				return DURATION_UNKNOWN;
			}

		} 
        	else
        	{
			return DURATION_UNKNOWN;	
        	}
	}

	//@Override
	public synchronized Time getMediaTime()
	{
    		if (getState() < Realized)
		{
			return super.getMediaTime();
		} 
        	else if (m != null)
		{
			try
			{
				final double totalSeconds = (double) m.getTime() / (double) m.getTimeScale();
				return new Time(totalSeconds);

			} catch (StdQTException e)
			{
				logger.log(Level.WARNING, "" + e, e);
				return super.getMediaTime();
			}

		} 
        	else
        	{
        		return super.getMediaTime();
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
		if (qtcMovie == null)
			return null;
		return qtcMovie.asJComponent();
	}



//	@Override
//	public Component getControlPanelComponent()
//	{
//		if (qtcMovieController == null)
//			return null;
//		return qtcMovieController.asComponent();
//	}
	
	// until we figure out how to isolate QT's control panel component (get it without the movie panel),
	// we'll use FMJ's control panel.
    public Component getControlPanelComponent() {
        Component c = super.getControlPanelComponent();

        if( c == null ) {
            c = new StandardControlPanel(this, StandardControlPanel.USE_START_CONTROL | StandardControlPanel.USE_STOP_CONTROL | StandardControlPanel.USE_PROGRESS_CONTROL);
            setControlPanelComponent(c);
        }

        return c;
    }

}
