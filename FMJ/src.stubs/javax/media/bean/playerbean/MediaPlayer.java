package javax.media.bean.playerbean;

import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.ClockStoppedException;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerListener;
import javax.media.GainControl;
import javax.media.IncompatibleSourceException;
import javax.media.IncompatibleTimeBaseException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Time;
import javax.media.TimeBase;
import javax.media.protocol.DataSource;

import net.sf.fmj.utility.LoggerSingleton;

public class MediaPlayer extends java.awt.Container implements Player, java.io.Externalizable
{	

	private static final Logger logger = LoggerSingleton.logger;

	// created the same vars that Sun's has, we aren't using many of them yet (or may might not ever)
	private PropertyChangeSupport changes;
	
	private String urlString;
	
	private MediaLocator mrl;
	
	private URL url;
	
	private AppletContext mpAppletContext;
	
	
	
	
	private boolean panelVisible;
	
	private boolean cachingVisible;
	
	private boolean fixedAspectRatio;
	
	//private boolean fitVideo;	
	
	private boolean looping;
	
	transient Player player;
	
	transient Panel panel;
	
	transient Panel vPanel;
	
	transient Panel newPanel;
	
	transient Component visualComponent;
	
	transient Component controlComponent;
	
	transient Component cachingComponent;
	private transient int controlPanelHeight;
	
	private transient int urlFieldHeight;
	
	private int preferredHeight;
	
	private int preferredWidth;
	
	private int state;
	
	private Vector controlListeners;
	
	private PopupMenu zoomMenu;
	
	private URL mpCodeBase;
	

	protected transient GainControl gainControl;

	protected transient String curVolumeLevel;

	protected transient float curVolumeValue;

	protected transient String curZoomLevel;

	protected transient float curZoomValue;

	protected transient Time mediaTime;
	
	// selfListener
	
	private long contentLength;
	
	//private boolean fixtedFirstTime;
	
	private boolean displayURL;
	
	private boolean isPopupActive;
	
	// urlName
	
	// mouseListen;
	
	// errMeth;
	
	public MediaPlayer()
	{	
	}

	public void addController(Controller newController) throws IncompatibleTimeBaseException
	{
		if (player == null)
			return;

		player.addController(newController);
	}

	public void addControllerListener(ControllerListener listener)
	{
		if (player == null)
			return;

		player.addControllerListener(listener);
	}

	public void close()
	{
		if (player == null)
			return;
		player.close();
	}

	public void deallocate()
	{
		if (player == null)
			return;
		player.deallocate();
	}

	public Control getControl(String forName)
	{
		if (player == null)
			return null;
		return player.getControl(forName);
	}

	public Component getControlPanelComponent()
	{
		if (player == null)
			return null;
		return player.getControlPanelComponent();
	}

	public Control[] getControls()
	{
		if (player == null)
			return new Control[0];
		return player.getControls();	// TODO: do we add extra controls?
	}

	public Time getDuration()
	{
		if (player == null)
			return DURATION_UNKNOWN;
		return player.getDuration();
	}

	public GainControl getGainControl()
	{
		if (player == null)
			return null;
		return player.getGainControl();
	}

	public long getMediaNanoseconds()
	{
		if (player == null)
			return Long.MAX_VALUE;
		return player.getMediaNanoseconds();
	}

	public Time getMediaTime()
	{
		if (player == null)
			return new Time(Long.MAX_VALUE);
		
		return player.getMediaTime();
	}

	public float getRate()
	{
		if (player == null)
			return 0.f;
		return player.getRate();
	}

	public Time getStartLatency()
	{
		if (player == null)
			return new Time(Long.MAX_VALUE);

		return player.getStartLatency();
	}

	public int getState()
	{
		if (player == null)
			return Unrealized;
		return player.getState();
	}

	public Time getStopTime()
	{
		if (player == null)
			return null;
		return player.getStopTime();	
	}

	public Time getSyncTime()
	{
		if (player == null)
			return new Time(Long.MAX_VALUE);

		return player.getSyncTime();
	}

	public int getTargetState()
	{
		if (player == null)
			return Unrealized;
		return player.getTargetState();
	}

	public TimeBase getTimeBase()
	{
		if (player == null)
			return null;
		return player.getTimeBase();
	}

	public Component getVisualComponent()
	{
		if (player == null)
			return null;
		
		return player.getVisualComponent();
	}

	public Time mapToTimeBase(Time t) throws ClockStoppedException
	{
		if (player == null)
			return new Time(Long.MAX_VALUE);

		return player.mapToTimeBase(t);
	}

	public void prefetch()
	{
		if (player == null)
			return;
		
		player.prefetch();
	}

	public void realize()
	{
		if (player == null)
			return;
		player.realize();
	}

	public void removeController(Controller oldController)
	{
		if (player == null)
			return;
		player.removeController(oldController);
	}

	public void removeControllerListener(ControllerListener listener)
	{
		if (player == null)
			return;
		player.removeControllerListener(listener);
	}

	public void setMediaTime(Time now)
	{
		if (player == null)
			return;
		player.setMediaTime(now);
	}

	public float setRate(float factor)
	{
		if (player == null)
			return 0.f;
		return player.setRate(factor);
	}

	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{
		if (player == null)
			return;
		player.setSource(source);
	}

	public void setStopTime(Time stopTime)
	{
		if (player == null)
			return;
		player.setStopTime(stopTime);
	}

	public void setTimeBase(TimeBase master) throws IncompatibleTimeBaseException
	{
		if (player == null)
			return;
		player.setTimeBase(master);
	}

	public void start()
	{
		if (player == null)
			return;
		player.start();
	}

	public void stop()
	{
		if (player == null)
			return;
		player.stop();
	}

	public void syncStart(Time at)
	{
		if (player == null)
			return;
		player.syncStart(at);
	}

	public String getMediaLocation()
	{	
		if (player == null)
			return " ";
		throw new UnsupportedOperationException();	// TODO
	}

	protected MediaLocator getMediaLocator(String filename)
	{	return new MediaLocator("file://" + filename);
	}

	public void setMediaLocation(String location)
	{	throw new UnsupportedOperationException();	// TODO
	}

	
	public void setMediaLocator(MediaLocator locator)
	{	
		try
		{
			player = Manager.createPlayer(locator);
		} catch (NoPlayerException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		} catch (IOException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		}
		
	}

	public void setDataSource(DataSource ds)
	{	
		try
		{
			player = Manager.createPlayer(ds);
		} catch (NoPlayerException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		} catch (IOException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	public void setPlayer(Player newPlayer)
	{	this.player = newPlayer;
	}

		
	

	public boolean getPlaybackLoop()
	{	return looping;
	}

	public void setPlaybackLoop(boolean val)
	{	looping = val;
	}

	public boolean isPlayBackLoop()
	{	return looping;
	}

	public void setZoomTo(String scale)
	{	this.curZoomLevel = scale;	// TODO: convert to float
	}

	public String getZoomTo()
	{	return curZoomLevel;
	}

	public int getControlPanelHeight()
	{	return controlPanelHeight;
	}

	public int getMediaLocationHeight()
	{	return urlFieldHeight;
	}

	public void setVolumeLevel(String volumeString)
	{	curVolumeLevel = volumeString;	// TODO: float conversion
	}

	public String getVolumeLevel()
	{	return curVolumeLevel;
	}

	public boolean isMediaLocationVisible()
	{	return displayURL;
	}

	public void setMediaLocationVisible(boolean val)
	{	displayURL = val;
	}

	public boolean isControlPanelVisible()
	{	return panelVisible;
	}

	public void setControlPanelVisible(boolean isVisible)
	{	panelVisible = isVisible;
	}

	public boolean isCachingControlVisible()
	{	return cachingVisible;
	}

	public void setCachingControlVisible(boolean isVisible)
	{	cachingVisible = isVisible;
	}

	public boolean isFixedAspectRatio()
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void setFixedAspectRatio(boolean isFixed)
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void setPopupActive(boolean isActive)
	{	isPopupActive = isActive;
	}


	public Player getPlayer()
	{	return player;
	}



	public void stopAndDeallocate()
	{	
		stop();
		deallocate();
		// TODO: is it more complicated than this?
	}

	

	// synchronized in reference impl
	public synchronized void waitForState(int s)
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void readExternal(ObjectInput in)
			throws java.io.IOException, ClassNotFoundException
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void writeExternal(ObjectOutput out)
			throws java.io.IOException
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void setBounds(int x, int y, int w, int h)
	{	throw new UnsupportedOperationException();	// TODO
	}

	public java.awt.Dimension getPreferredSize()
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener c)
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener c)
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void setCodeBase(java.net.URL cb)
	{	mpCodeBase = cb;
	}

	public void saveMediaTime()
	{	throw new UnsupportedOperationException();	// TODO
	}

	public void restoreMediaTime()
	{	throw new UnsupportedOperationException();	// TODO
	}
	
	
	
}
