package com.sun.media.parser;

import java.io.IOException;

import javax.media.IncompatibleSourceException;
import javax.media.Track;
import javax.media.protocol.DataSource;
import javax.media.protocol.SourceStream;

/**
 * TODO: stub.
 * @author Ken Larson
 *
 */
public class RawPullStreamParser extends RawParser
{
	protected SourceStream[] streams;
	protected Track[] tracks;
	static final String NAME = "Raw pull stream parser";
	public String getName()
	{	return NAME;
	}
	public RawPullStreamParser()
	{
		super();
	}
	
	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{
		throw new IncompatibleSourceException("DataSource not supported: " + source);	// TODO: what types of DataSource are supported?
	}
	protected boolean supports(SourceStream[] streams)
	{
		return false;	// TODO
	}
	
	public Track[] getTracks()
	{
		return tracks;	
	}
	
	public void open()
	{
		// TODO
	}
	
	public void close()
	{
		// TODO
	}
	
	public void start() throws IOException
	{
		source.start();	// JMF does this
	}
	
	public void stop()
	{
		
	}
}
