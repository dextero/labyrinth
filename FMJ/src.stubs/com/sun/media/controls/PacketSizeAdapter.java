package com.sun.media.controls;

import java.awt.Component;

import javax.media.Codec;
import javax.media.control.PacketSizeControl;

/**
 * TODO: Stub
 * @author Ken Larson
 *
 */
public class PacketSizeAdapter implements PacketSizeControl
{

	protected Codec owner;
	protected boolean settable;
	protected int packetSize;
	
	public PacketSizeAdapter(Codec owner, int size, boolean settable)
	{
		super();
		this.owner = owner;
		packetSize = size;
		this.settable = settable;
	}

	public int getPacketSize()
	{
		return packetSize;
	}

	public int setPacketSize(int numBytes)
	{
		throw new UnsupportedOperationException(); // TODO
	}

	public Component getControlComponent()
	{
		throw new UnsupportedOperationException(); // TODO
	}

}
