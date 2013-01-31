package net.sf.fmj.media.cdp.civil;

import com.lti.civil.*;
import net.sf.fmj.utility.LoggerSingleton;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dynamically adds CaptureDeviceInfo to the CaptureDeviceManager.  Does not commit.
 * @author Ken Larson
 *
 */
public class CaptureDevicePlugger
{
	private static final Logger logger = LoggerSingleton.logger;

    public static javax.media.format.VideoFormat convertCivilFormat(com.lti.civil.VideoFormat civilVideoFormat)
    {

        int width = civilVideoFormat.getWidth();
        int height = civilVideoFormat.getHeight();
        float fps = civilVideoFormat.getFPS();
        int type = civilVideoFormat.getFormatType();
        Class dataTypeClass = Format.byteArray;
        Dimension size = new Dimension(width, height);

        if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB24) {
            return new RGBFormat(size, Format.NOT_SPECIFIED, dataTypeClass, fps,
                    24, 3, 2, 1);
        } else if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB32) {
            if (dataTypeClass == Format.byteArray) {
                return new RGBFormat(size, Format.NOT_SPECIFIED, dataTypeClass, fps,
                        32, 3, 2, 1);
            }
            return new RGBFormat(size, Format.NOT_SPECIFIED, dataTypeClass, fps,
                    32, 0x00FF0000, 0x0000FF00, 0x000000FF);
        }

        throw new RuntimeException("Unsupported video format");
    }

	public void addCaptureDevices()
	{
		try
		{
			final CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();
			final CaptureSystem system = factory.createCaptureSystem();
			system.init();
			final List list = system.getCaptureDeviceInfoList();
			for (int i = 0; i < list.size(); ++i)
			{
				final com.lti.civil.CaptureDeviceInfo civilInfo = (com.lti.civil.CaptureDeviceInfo) list.get(i);
				
				{
					//String name, MediaLocator locator, Format[] formats
					// TODO: more accurate format
					// TODO: don't add if already there.

                    MediaLocator locator = new MediaLocator("civil:" + civilInfo.getDeviceID());
                    List<VideoFormat> formatsList = system.openCaptureDeviceStream(civilInfo.getDeviceID()).enumVideoFormats();
                    Format[] videoFormats = new Format[1];//formatsList.size()];
                    for (int fmt = 0; fmt < videoFormats.length /* formatsList.size() */; ++fmt) {
                        VideoFormat ltiCivilFormat = formatsList.get(i);
                        System.out.printf("RGB format: %dx%d, %f fps, type %d\n", ltiCivilFormat.getWidth(), ltiCivilFormat.getHeight(), ltiCivilFormat.getFPS(), ltiCivilFormat.getFormatType());
                        videoFormats[i] = convertCivilFormat(ltiCivilFormat);
                    }

					final CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo(civilInfo.getDescription(), locator, videoFormats);
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
		}
		catch (CaptureException e)
		{
			logger.log(Level.WARNING, "" + e, e);
		}
	}
}
