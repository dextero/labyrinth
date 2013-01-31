/*
 * @(#)RTCPFeedback.java
 * Created: 02-Dec-2005
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.media.rtp.rtcp.Feedback;

/**
 * Represents an RTCP Feedback report
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTCPFeedback implements Feedback {
    
    /**
     * The size of a feedback report in bytes
     */
    public static final int SIZE = 24;
    
    // The SSRC that this is feedback for
    private long ssrc = 0;
    
    // The fraction of packets lost
    private int fractionLost = 0;
    
    // The number of packets lost
    private long numLost = 0;
    
    // The extended highest sequence number
    private long xtndSeqNum = 0;
    
    // The jitter
    private long jitter = 0;
    
    // The LSR
    private long lsr = 0;
    
    // The DLSR
    private long dlsr = 0;
    
    /**
     * Creates a new RTCP Feedback report
     * @param data The data to read from
     * @param offset The offset into the data where the report starts
     * @param length The length of the report
     * @throws IOException 
     */
    public RTCPFeedback(byte[] data, int offset, int length) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
                data, offset, length));
        ssrc = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        fractionLost = stream.readUnsignedByte();
        numLost = (stream.readUnsignedShort() << 8) | stream.readUnsignedByte();
        xtndSeqNum = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        jitter = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        lsr = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        dlsr = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
    }

    public long getSSRC() {
        return ssrc;
    }

    public int getFractionLost() {
        return fractionLost;
    }

    public long getNumLost() {
        return numLost;
    }

    public long getXtndSeqNum() {
        return xtndSeqNum;
    }

    public long getJitter() {
        return jitter;
    }

    public long getLSR() {
        return lsr;
    }

    public long getDLSR() {
        return dlsr;
    }

}
