/*
 * @(#)RTPDataStream.java
 * Created: 01-Dec-2005
 * Version: 1-1-alpha3
 * Copyright (c) 2005-2006, University of Manchester All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials 
 * provided with the distribution. Neither the name of the University of 
 * Manchester nor the names of its contributors may be used to endorse or 
 * promote products derived from this software without specific prior written
 * permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sf.fmj.media.rtp;

import java.awt.Component;
import java.io.IOException;
import java.util.Timer;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.GlobalReceptionStats;
import javax.media.rtp.RTPControl;
import javax.media.rtp.ReceptionStats;

/**
 * A generic RTP Data Stream
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public abstract class RTPDataStream implements PushBufferStream, RTPControl {
    
    // The format of the data
    protected Format format = null;
    
    // The clock rate of the RTP clock for the format
    protected double clockRate = 90000;
    
    // The transfer Handler
    protected BufferTransferHandler handler = null;
    
    // The timer object for sending the next item
    protected Timer timer = new Timer();
    
    // The ssrc of the stream
    protected long ssrc = 0;
    
    // The threshold of the buffer before any playout is done in ms
    protected int threshold = 100;
    
    // The controls of the stream
    protected Object[] controls = new Object[0];
    
    // The reception statistics
    protected RTPReceptionStats receptionStats = new RTPReceptionStats();
    
    // The inter-arrival jitter of the packets
    protected long jitter = 0;
    
    // The last packet delay time
    private long lastDelay = -1;
    
    // The time of arrival of the last rtp packet
    private long lastRTPReceiveTime = -1;
    
    // The rtp timestamp of the last rtp packet
    private long lastRTPTimestamp = -1;
    
    // The first sequence number seen
    protected long firstSequence = -1;
    
    // The last sequence number seen
    protected long lastSequence = -1;
    
    /**
     * Creates a new RTPDataStream
     * @param format The format of the stream
     */
    public RTPDataStream(long ssrc, Format format) {
        this.ssrc = ssrc;
        this.format = format;
    }
    
    protected abstract void addPacket(RTPHeader header, byte[] data, 
            int offset, int length);
    
    public Format getFormat() {
        return format;
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.handler = transferHandler;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }

    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return controls;
    }

    public Object getControl(String controlType) {
        if (controlType.equals("javax.media.rtp.RTPControl")) {
            return this;
        }
        return null;
    }

    public abstract void read(Buffer buffer) throws IOException;

    public void addFormat(Format fmt, int payload) {
        // Does Nothing
    }

    public ReceptionStats getReceptionStats() {
        return receptionStats;
    }

    public GlobalReceptionStats getGlobalStats() {
        return null;
    }

    public Format[] getFormatList() {
        return new Format[0];
    }

    public Format getFormat(int payload) {
        return format;
    }

    public Component getControlComponent() {
        return null;
    }
    
    /**
     * Performs jitter calculations
     */
    protected void calculateJitter(long rtpTimestamp) {
        if (lastRTPReceiveTime == -1) {
            lastRTPReceiveTime = System.currentTimeMillis();
            lastRTPTimestamp = rtpTimestamp;
        } else if (lastDelay == -1) {
            lastRTPReceiveTime = System.currentTimeMillis();
            lastRTPTimestamp = rtpTimestamp;
            long expChange = (long) ((System.currentTimeMillis() - 
                    lastRTPReceiveTime) * clockRate);
            long actualChange = rtpTimestamp - lastRTPTimestamp;
            lastDelay = expChange - actualChange;
        } else {
            lastRTPReceiveTime = System.currentTimeMillis();
            lastRTPTimestamp = rtpTimestamp;
            long expChange = (long) ((System.currentTimeMillis() - 
                    lastRTPReceiveTime) * clockRate);
            long actualChange = rtpTimestamp - lastRTPTimestamp;
            long delay = expChange - actualChange;
            long delaydiff = Math.abs(delay - lastDelay);
            lastDelay = delay;
            jitter = jitter + ((delaydiff - jitter) / 16);
        }
    }
    
    /**
     * Returns the jitter calculation
     */
    public long getJitter() {
        return jitter;
    }
    
    /**
     * Returns the last sequence number seen
     */
    public long getLastSequence() {
        return lastSequence;
    }
    
    /**
     * Returns the first sequence number seen
     */
    public long getFirstSequence() {
        return firstSequence;
    }
}
