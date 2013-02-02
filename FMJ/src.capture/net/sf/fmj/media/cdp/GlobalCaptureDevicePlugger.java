package net.sf.fmj.media.cdp;

/**
 * Global singleton CaptureDevicePlugger.  Calls all specific CaptureDevicePluggers.
 * Dynamically adds CaptureDeviceInfo to the CaptureDeviceManager.  Does not commit.
 * @author Ken Larson
 *
 */
public final class GlobalCaptureDevicePlugger
{
	private GlobalCaptureDevicePlugger()
	{	// Do nothing.
	}
	public static void addCaptureDevices()
	{
		// This is hard-coded to call all known capture device pluggers.  Would be nice to
		// make this more dynamic.
		new net.sf.fmj.media.cdp.civil.CaptureDevicePlugger().addCaptureDevices();
		new net.sf.fmj.media.cdp.javasound.CaptureDevicePlugger().addCaptureDevices();

	}

}
