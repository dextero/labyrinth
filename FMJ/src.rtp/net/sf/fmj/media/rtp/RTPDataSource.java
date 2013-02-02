/*
 * @(#)RTPDataSource.java
 * Created: 27-Oct-2005
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

import javax.media.Format;
import javax.media.Time;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.ReceptionStats;

/**
 * Represents an RTP Data Source
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPDataSource extends PushBufferDataSource {
    
    // The RTP data stream for this data source
    private PushBufferStream stream = null;
    
    // True if the data source is connected
    private boolean connected = false;
    
    // True if the data source is started
    private boolean started = false;
    
    /**
     * Creates a new RTPDataSource
     * @param format The format of the data
     */
    public RTPDataSource(long ssrc, Format format) {
        if (format instanceof VideoFormat) {
            stream = new RTPVideoDataStream(ssrc, format);
        } else if (format instanceof AudioFormat) {
            stream = new RTPAudioDataStream(ssrc, format);
        }
    }

    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{stream};
    }

    public String getContentType() {
        if (!connected) {
            return null;
        }
        return "raw";
    }

    public void connect() {
        connected = true;
    }

    public void disconnect() {
        if (started) {
            stop();
        }
        connected = false;
    }

    public void start()  {
        started = true;
    }

    public void stop()  {
        started = false;
    }

    public Object getControl(String arg0) {
        return stream.getControl(arg0);
    }

    public Object[] getControls() {
        return stream.getControls();
    }

    public Time getDuration() {
        return DURATION_UNBOUNDED;
    }
    
    /**
     * Handles an RTP data packet
     * @param header The RTP header
     * @param data The RTP data
     * @param offset The offset in the data
     * @param length The length of the data
     */
    public void handleRTPPacket(RTPHeader header, byte[] data, int offset,
            int length) {
        if (stream instanceof RTPDataStream) {
            ((RTPDataStream) stream).addPacket(header, data, offset, length);
        }
    }
    
    /**
     * Returns the jitter value
     */
    public long getJitter() {
        if (stream instanceof RTPDataStream) {
            return ((RTPDataStream) stream).getJitter();
        }
        return 0;
    }
    
    /**
     * Returns the reception statistics of the source
     */
    public ReceptionStats getSourceReceptionStats() {
        if (stream instanceof RTPDataStream) {
            return ((RTPDataStream) stream).getReceptionStats();
        }
        return null;
    }

}
