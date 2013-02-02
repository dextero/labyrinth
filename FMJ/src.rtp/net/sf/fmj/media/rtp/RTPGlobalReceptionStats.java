/*
 * @(#)RTPGlobalReceptionStats.java
 * Created: 26-Oct-2005
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

import javax.media.rtp.GlobalReceptionStats;

/**
 * Represents reception statistics
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPGlobalReceptionStats implements GlobalReceptionStats {
    
    // The number of packets received
    private int packetsRecd = 0;
    
    // The number of bytes received
    private int bytesRecd = 0;
    
    // The number of bad rtp packets
    private int badRTPkts = 0;
    
    // The number of local collisions
    private int localColls = 0;
    
    // The number of remote collisions
    private int remoteColls = 0;
    
    // The number of packets looped
    private int packetsLooped = 0;
    
    // The number of failed trans packets
    private int transmitFailed = 0;
    
    // The number of RTCP packets received
    private int rtcpRecd = 0;
    
    // The number of SR packets received
    private int srRecd = 0;
    
    // The number of bad RTCP packets
    private int badRTCPPkts = 0;
    
    // The number of unknown types
    private int unknownTypes = 0;
    
    // The number of malformed RR packets
    private int malformedRR = 0;
    
    // The number of malformed SDES packets
    private int malformedSDES = 0;
    
    // The number of malformed BYE packets
    private int malformedBYE = 0;
    
    // The number of malformed SR packets
    private int malformedSR = 0;

    public int getPacketsRecd() {
        return packetsRecd;
    }

    public int getBytesRecd() {
        return bytesRecd;
    }
    
    /**
     * Resets the number of bytes read
     */
    public void resetBytesRecd() {
        bytesRecd = 0;
    }

    public int getBadRTPkts() {
        return badRTPkts;
    }

    public int getLocalColls() {
        return localColls;
    }

    public int getRemoteColls() {
        return remoteColls;
    }

    public int getPacketsLooped() {
        return packetsLooped;
    }

    public int getTransmitFailed() {
        return transmitFailed;
    }

    public int getRTCPRecd() {
        return rtcpRecd;
    }

    public int getSRRecd() {
        return srRecd;
    }

    public int getBadRTCPPkts() {
        return badRTCPPkts;
    }

    public int getUnknownTypes() {
        return unknownTypes;
    }

    public int getMalformedRR() {
        return malformedRR;
    }

    public int getMalformedSDES() {
        return malformedSDES;
    }

    public int getMalformedBye() {
        return malformedBYE;
    }

    public int getMalformedSR() {
        return malformedSR;
    }
    
    /**
     * Adds a packet to the received packet count
     */
    public synchronized void addPacketRecd() {
        packetsRecd++;
    }
    
    /**
     * Adds bytes to the received byte count
     */
    public synchronized void addBytesRecd(int bytes) {
        bytesRecd += bytes;
    }
    
    /**
     * Adds a packet to the bad packet count
     */
    public synchronized void addBadRTPkt() {
        badRTPkts++;
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
     * Adds a packet to the looped packet count
     */
    public synchronized void addLoopedPacket() {
        packetsLooped++;
    }
    
    /**
     * Adds a failure  to the transmit failed count
     */
    public synchronized void addTransmitFailed() {
        transmitFailed++;
    }
    
    /**
     * Adds a rtcp pakcet to the received rtcp packet count
     */
    public synchronized void addRTCPRecd() {
        rtcpRecd++;
    }
    
    /**
     * Adds a sr packet to the received sr packet count
     */
    public synchronized void addSRRecd() {
        srRecd++;
    }
    
    /**
     * Adds a bad rtcp packet to the bad rtcp packet count
     */
    public synchronized void addBadRTCPPkt() {
        badRTCPPkts++;
    }
    
    /**
     * Adds a unknown type to the unknown type count
     */
    public synchronized void addUnknownType() {
        unknownTypes++;
    }
    
    /**
     * Adds a malformed RR to the malformed RR count
     */
    public synchronized void addMalformedRR() {
        malformedRR++;
    }
    
    /**
     * Adds a malformed SDES to the malformed SDES count
     */
    public synchronized void addMalformedSDES() {
        malformedSDES++;
    }
    
    /**
     * Adds a malformed BYE to the malformed BYE count
     */
    public synchronized void addMalformedBYE() {
        malformedBYE++;
    }
    
    /**
     * Adds a malformed SR to the malformed SR count
     */
    public synchronized void addMalformedSR() {
        malformedSR++;
    }
}
