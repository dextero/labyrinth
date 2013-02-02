/*
 * @(#)RTPReceptionStats.java
 * Created: 06-Dec-2005
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

import javax.media.rtp.ReceptionStats;

/**
 * Represents receptions statistics for a given stream
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPReceptionStats implements ReceptionStats {
    
    // The number of packets lost
    private int pduLost = 0;
    
    // The number of packets processed
    private int pduProcessed = 0;
    
    // The number of packets misordered
    private int pduMisOrd = 0;
    
    // The number of packets outwith an acceptable range
    private int pduInvalid = 0;
    
    // The number of packets with duplicate numbers
    private int pduDuplicate = 0;
    
    // The number of times the sequence number has wrapped
    private int sequenceWrap = 0;

    public int getPDUlost() {
        return pduLost;
    }

    public int getPDUProcessed() {
        return pduProcessed;
    }

    public int getPDUMisOrd() {
        return pduMisOrd;
    }

    public int getPDUInvalid() {
        return pduInvalid;
    }

    public int getPDUDuplicate() {
        return pduDuplicate;
    }
    
    /**
     * The number of times the sequence number has wrapped for this source
     */
    public int getSequenceWrap() {
        return sequenceWrap;
    }
    
    /**
     * Adds a lost packet to the count
     */
    public void addPDULost(int lost) {
        pduLost += lost;
    }
    
    /**
     * Adds a processed packet to the count
     */
    public void addPDUProcessed() {
        pduProcessed++;
    }
    
    /**
     * Adds a misordered packet to the count
     */
    public void addPDUMisOrd() {
        pduMisOrd++;
    }
    
    /**
     * Adds an invalid packet to the count
     */
    public void addPDUInvalid() {
        pduInvalid++;
    }
    
    /**
     * Adds a duplicate packet to the count
     */
    public void addPDUDuplicate() {
        pduDuplicate++;
    }
    
    /**
     * Adds a sequence number wrap
     */
    public void addSequenceWrap() {
        sequenceWrap++;
    }
}
