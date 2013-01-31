package net.sf.jdshow;

import java.awt.Canvas;

/**
 * Get a window handle for a particular window.
 * Adapted from http://www.javaworld.com/javaworld/javatips/jw-javatip86.html.
 * @author Ken Larson
 *
 */


public final class JAWTUtils
{
//    static
//	{
//		System.loadLibrary("jdshow");
//	}
    
    private JAWTUtils()
	{
		super();
	}


    public static native long getWindowHandle(Canvas canvas);
  
}

