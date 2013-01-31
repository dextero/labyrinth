package net.sf.fmj.test.compat.misc;

import java.io.File;
import java.net.MalformedURLException;

import javax.media.MediaLocator;

import junit.framework.TestCase;

public class MediaLocatorTest extends TestCase
{

	public void testMediaLocator() throws MalformedURLException
	{
		File f = new File("test.txt");
		System.out.println(f.toURI());
		System.out.println(f.toURI().toURL());
		MediaLocator l = new MediaLocator(f.toURI().toURL());
		
	}
}
