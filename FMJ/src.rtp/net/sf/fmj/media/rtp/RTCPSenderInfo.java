/*
 * @(#)RTCPSenderInfo.java
 * Created: 2005-04-21
 * Version: 2-0-alpha
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

/**
 * Represents the sender info part of the RTCP SR Packet
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha
 */
public class RTCPSenderInfo {
    
    /**
     * The size of the sender info
     */
    public static final int SIZE = 20;

    // baseline NTP time if bit-0=0 -> 7-Feb-2036 @ 06:28:16 UTC
    private static final long MSB_0_BASE_TIME = 2085978496000L;

    // baseline NTP time if bit-0=1 -> 1-Jan-1900 @ 01:00:00 UTC
    private static final long MSB_1_BASE_TIME = -2208988800000L;

    // The Most Significant word of the timestamp
    private long ntpTimestampMSW = 0;

    // The Least Significant word of the timestamp
    private long ntpTimestampLSW = 0;

    // The RTP timestamp
    private long rtpTimestamp = 0;

    // The packet count
    private long packetCount = 0;

    // The octet count
    private long octetCount = 0;

    /**
     * Parses an RTCP SR packet
     * 
     * @param rtcpPacket
     *            The data of the RTCP packet
     * @throws IOException
     */
    public RTCPSenderInfo(byte[] rtcpPacket, int offset, int length) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
                rtcpPacket, offset, length));
        ntpTimestampMSW = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        ntpTimestampLSW = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        rtpTimestamp = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        packetCount = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
        octetCount = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String buf = "";
        buf += "ntp_ts=" + getNtpTimestampMSW();
        buf += " " + getNtpTimestampLSW();
        buf += " rtp_ts=" + getRtpTimestamp();
        buf += " packet_ct=" + getPacketCount();
        buf += " octect_ct=" + getOctetCount();
        return buf;
    }

    /**
     * Returns the timestamp of the information
     */
    public long getTimestamp() {
        long seconds = ntpTimestampMSW;
        long fraction = ntpTimestampLSW;

        // Use round-off on fractional part to preserve going to lower precision
        fraction = Math.round(1000D * fraction / 0x100000000L);

        /*
         * If the most significant bit (MSB) on the seconds field is set we use
         * a different time base. The following text is a quote from RFC-2030
         * (SNTP v4):
         * 
         * If bit 0 is set, the UTC time is in the range 1968-2036 and UTC time
         * is reckoned from 0h 0m 0s UTC on 1 January 1900. If bit 0 is not set,
         * the time is in the range 2036-2104 and UTC time is reckoned from 6h
         * 28m 16s UTC on 7 February 2036.
         */
        long msb = seconds & 0x80000000L;
        if (msb == 0) {
            // use base: 7-Feb-2036 @ 06:28:16 UTC
            return MSB_0_BASE_TIME + (seconds * 1000) + fraction;
        }
        // use base: 1-Jan-1900 @ 01:00:00 UTC
        return MSB_1_BASE_TIME + (seconds * 1000) + fraction;
    }

    /**
     * Returns the timestamp value in seconds
     */
    public double getNtpTimestampSecs() {
        return (double) getTimestamp() / 1000;
    }

    /**
     * Returns the timestamp most significant word
     */
    public long getNtpTimestampMSW() {
        return ntpTimestampMSW;
    }

    /**
     * Returns the timestamp least significant word
     */
    public long getNtpTimestampLSW() {
        return ntpTimestampLSW;
    }

    /**
     * Returns the rtp timestamp
     */
    public long getRtpTimestamp() {
        return rtpTimestamp;
    }

    /**
     * Returns the octet count
     */
    public long getOctetCount() {
        return octetCount;
    }

    /**
     * Returns the packet count
     */
    public long getPacketCount() {
        return packetCount;
    }
}