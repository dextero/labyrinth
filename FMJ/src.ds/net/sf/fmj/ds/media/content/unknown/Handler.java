package net.sf.fmj.ds.media.content.unknown;


import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.protocol.DataSource;

import net.sf.fmj.ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import net.sf.fmj.ejmf.toolkit.media.AbstractPlayer;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;
import net.sf.jdshow.Com;
import net.sf.jdshow.ComException;
import net.sf.jdshow.IGraphBuilder;
import net.sf.jdshow.IMediaControl;
import net.sf.jdshow.IMediaSeeking;
import net.sf.jdshow.IVideoWindow;
import net.sf.jdshow.JAWTUtils;
import net.sf.jdshow.WindowedRendering;

/**
 * 
 * @author Ken Larson
 *
 */
public class Handler extends AbstractPlayer
{

	private static final Logger logger = LoggerSingleton.logger;

	private boolean prefetchNeeded = true;
	
	private static final boolean TRACE = true;
	


	private IGraphBuilder graphBuilder;
	private IMediaControl mediaControl;
	private IMediaSeeking mediaSeeking;
	
    public void setSource(DataSource source) throws IncompatibleSourceException
	{
    	if (TRACE) logger.fine("DataSource: " + source);
		
   	
    	if (!source.getLocator().getProtocol().equals("file"))
    		throw new IncompatibleSourceException("Only file URLs supported: " + source);
    	
		// TODO: only handling file URLs right now.
		String path = URLUtils.extractValidPathFromFileUrl(source.getLocator().toExternalForm());
		if (path == null)
			throw new IncompatibleSourceException("Unable to extract valid file path from URL: " + source.getLocator().toExternalForm());
	
		try 
		{
			// because Java thinks that /C: is a valid way to start a path on Windows, we have to turn it
			// into something more normal.
			path = new File(path).getCanonicalPath();
		} 
		catch (IOException e1) 
		{
			final String msg = "Unable to get canonical path from " + path + ": " + e1;
			logger.log(Level.WARNING, msg, e1);
			throw new IncompatibleSourceException(msg);
		}
		
		
		logger.info("Path: " + path);
		
		try
		{
			System.loadLibrary("jdshow");
		}
		catch (Throwable e)
		{	logger.log(Level.WARNING, "" + e, e);
			throw new IncompatibleSourceException();
		}
		
		// TODO: check if media has visual
		visualComponent = new MyCanvas();
		
		visualComponent.addComponentListener(new MyComponentListener());
		
		try
		{
		   	Com.CoInitialize();
		    
		   	int hr;
		   	
			long[] p = new long[1];
			hr = Com.CoCreateInstance(Com.CLSID_FilterGraph, 0L, Com.CLSCTX_ALL, Com.IID_IGraphBuilder, p);
			if (Com.FAILED(hr))
				throw new ComException(hr);
			
			graphBuilder = new IGraphBuilder(p[0]);
			

    		
			hr = graphBuilder.RenderFile(path, "");
			if (Com.FAILED(hr))
				throw new ComException(hr);
	
			hr = graphBuilder.QueryInterface(Com.IID_IMediaControl, p);
			if (Com.FAILED(hr))
				throw new ComException(hr);
			mediaControl = new IMediaControl(p[0]);
			
			hr = graphBuilder.QueryInterface(Com.IID_IMediaSeeking, p);
			if (Com.FAILED(hr))
				throw new ComException(hr);
			mediaSeeking = new IMediaSeeking(p[0]);
			
	
			hr = graphBuilder.QueryInterface(Com.IID_IVideoWindow, p);
			if (Com.FAILED(hr))
				throw new ComException(hr);
			
			// determine video size:
			final IVideoWindow videoWindow = new IVideoWindow(p[0]);

			{
				long[] width = new long[1];
				hr = videoWindow.get_Width(width);
				if (Com.FAILED(hr))
					throw new ComException(hr);
				//logger.fine("width: " + width[0]);
				
				long[] height = new long[1];
				hr = videoWindow.get_Height(height);
				if (Com.FAILED(hr))
					throw new ComException(hr);
				//logger.fine("height: " + height[0]);
				
				videoSize = new Dimension((int) width[0], (int) height[0]);
			}
			
			videoWindow.Release();
			
			
		}
		catch (ComException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IncompatibleSourceException(e.getMessage());
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
		
		mediaControl.Release();
		graphBuilder.Release();
		mediaSeeking.Release();
		
		Com.CoUninitialize();
		
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
			long[] current = new long[1];
			long[] stop = new long[1];
			
			int hr = mediaSeeking.GetPositions(current, stop);
			if (Com.FAILED(hr))
				throw new ComException(hr);
			
			current[0] = t.getNanoseconds() / 100;	// TODO: this assumes REFERENCE_TIME format is being used.
			
			hr = mediaSeeking.SetPositions(current, IMediaSeeking.AM_SEEKING_AbsolutePositioning, stop, IMediaSeeking.AM_SEEKING_NoPositioning);
			if (Com.FAILED(hr))
				throw new ComException(hr);			
			
			
		}
		catch (ComException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			// TODO: handle
		}

	}

	//@Override
	public float doPlayerSetRate(float rate)
	{
		logger.info("Handler.doPlayerSetRate " + rate);
		
		try
		{
			int hr = mediaSeeking.SetRate(rate);
			if (Com.FAILED(hr))
				throw new ComException(hr);
		}
		catch (ComException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			return getRate();	// TODO: what to return?
		}
		
		return rate;
	}

	//@Override
	public boolean doPlayerStop()
	{
		logger.info("Handler.doPlayerStop");

		mediaControl.Stop();
		
		return true;
	}

	//@Override
	public boolean doPlayerSyncStart(Time t)
	{
		logger.info("Handler.doPlayerSyncStart" + t);
		
		try
		{
			int hr = mediaControl.Run();
			if (Com.FAILED(hr))
				throw new ComException(hr);
		}
		catch (ComException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			return false;
		}
		return true;
	}

	// @Override
	public Time getPlayerDuration()
	{
		if (getState() < Realized)
		{
			return DURATION_UNKNOWN;
		}
		else if (mediaSeeking != null)
		{
			try
			{
				long[] duration = new long[1];
				
				int hr = mediaSeeking.GetDuration(duration);
				if (Com.FAILED(hr))
					throw new ComException(hr);
				
				return new Time(duration[0] * 100);	// TODO: this assumes REFERENCE_TIME format is being used.
				
			}
			catch (ComException e)
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

	// @Override
	public synchronized Time getMediaTime()
	{
		if (mediaSeeking != null)
		{
			try
			{
				long[] current = new long[1];
				
				int hr = mediaSeeking.GetCurrentPosition(current);
				if (Com.FAILED(hr))
					throw new ComException(hr);
				
				return new Time(current[0] * 100);	// TODO: this assumes REFERENCE_TIME format is being used.
				
			}
			catch (ComException e)
			{
				logger.log(Level.WARNING, "" + e, e);
				return DURATION_UNKNOWN;
			}
		}
		return super.getMediaTime();
 	}



	// @Override
	public Time getPlayerStartLatency()
	{
		return new Time(0);
	}

	private MyCanvas visualComponent;
	
	//@Override
	public Component getVisualComponent()
	{
		return visualComponent;
	}

	private class MyComponentListener implements ComponentListener
	{

		public void componentHidden(ComponentEvent e)
		{
			logger.fine("componentHidden");
		}

		public void componentMoved(ComponentEvent e)
		{
			int x = ComponentEvent.COMPONENT_MOVED;
			logger.fine("componentMoved: " + e.getID());
			bindVisualComponent();
			
		}

		public void componentResized(ComponentEvent e)
		{
			logger.fine("componentResized");
			bindVisualComponent();			
		}

		public void componentShown(ComponentEvent e)
		{
			logger.fine("componentShown");
			
		}

	}
	
	private boolean visualComponentBound = false;
	private Dimension videoSize;
	private boolean bindVisualComponent()
	{
		if (visualComponentBound)
			return false;
		  final long hwnd = JAWTUtils.getWindowHandle(visualComponent);
	      //logger.fine("HWND: " + hwnd);
	      
	    try
	    {
	    	final int hr = WindowedRendering.InitWindowedRendering2(hwnd, graphBuilder);
			if (Com.FAILED(hr))
				throw new ComException(hr);
	    }
	    catch (ComException e)
	    {
	    	logger.log(Level.WARNING, "" + e, e);
	    	return false;
	    }
		visualComponentBound = true;
		return true;

	}
	
	private class MyCanvas extends Canvas
	{

		//@Override
		public Dimension getPreferredSize()
		{
			if (videoSize == null)
				return new Dimension(0, 0);
			return videoSize;
			//return new Dimension(400, 300);	// TODO
		}




	}

    public Component getControlPanelComponent() {
        Component c = super.getControlPanelComponent();

        if( c == null ) {
            c = new StandardControlPanel(this, StandardControlPanel.USE_START_CONTROL | StandardControlPanel.USE_STOP_CONTROL | StandardControlPanel.USE_PROGRESS_CONTROL);
            setControlPanelComponent(c);
        }

        return c;
    }

}
