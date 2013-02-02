package net.sf.fmj.media.cdp.javasound;

import java.util.logging.Logger;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;

import net.sf.fmj.media.protocol.javasound.DataSource;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Dynamically adds CaptureDeviceInfo to the CaptureDeviceManager.  Does not commit.
 * @author Ken Larson
 *
 */
public class CaptureDevicePlugger
{
	private static final Logger logger = LoggerSingleton.logger;

	public void addCaptureDevices()
	{
		// TODO: detect number of lines.

		final Format[] formats = DataSource.querySupportedFormats();
		
		final CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo("JavaSound", new MediaLocator("javasound:" + "//"), formats);
		if (CaptureDeviceManager.getDevice(jmfInfo.getName()) == null)
		{
			CaptureDeviceManager.addDevice(jmfInfo);
			logger.fine("CaptureDevicePlugger: Added " + jmfInfo.getLocator());
		}
		else
		{
			logger.fine("CaptureDevicePlugger: Already present, skipping " + jmfInfo.getLocator());
		}

	}
}
