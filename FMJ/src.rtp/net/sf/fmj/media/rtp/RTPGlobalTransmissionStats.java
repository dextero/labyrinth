/*
 * @(#)RTPGlobalTransmissionStats.java
 * Created: 26-Oct-2005
 * Version: TODO
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

import javax.media.rtp.GlobalTransmissionStats;

/**
 * Represents global transmission statistics
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPGlobalTransmissionStats implements GlobalTransmissionStats {
    
    // The number of RTP packets sent
    private int rtpSent = 0;
    
    // The number of bytes sent
    private int bytesSent = 0;
    
    // The number of RTCP packets sent
    private int rtcpSent = 0;
    
    // The number of local collisions
    private int localColls = 0;
    
    // The number of remote collisions
    private int remoteColls = 0;
    
    // The number of failed transmissions
    private int transmitFailed = 0;

    public int getRTPSent() {
        return rtpSent;
    }

    public int getBytesSent() {
        return bytesSent;
    }

    public int getRTCPSent() {
        return rtcpSent;
    }

    public int getLocalColls() {
        return localColls;
    }

    public int getRemoteColls() {
        return remoteColls;
    }

    public int getTransmitFailed() {
        return transmitFailed;
    }
    
    /**
     * Adds a packet to the sent packet count
     */
    public synchronized void addRTPSent() {
        rtpSent++;
    }
    
    /**
     * Adds bytes to the sent byte count
     */
    public synchronized void addBytesSent(int bytes) {
        bytesSent += bytes;
    }
    
    /**
     * Adds a packet to the sent rtcp packet count
     */
    public synchronized void addRTCPSent() {
        rtcpSent++;
    }
    
    /**
     * Adds a collision to the local collision count
     */
    public synchronized void addLocalColl() {
        localColls++;
    }
    
    /**
     * Adds a collision to the remote collision count
     */
    public synchronized void addRemoteColl() {
        remoteColls++;
    }
    
    /**
     * Adds a failure  to the transmit failed count
     */
    public synchronized void addTransmitFailed() {
        transmitFailed++;
    }

}
