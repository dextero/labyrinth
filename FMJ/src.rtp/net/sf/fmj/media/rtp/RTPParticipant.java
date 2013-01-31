/*
 * @(#)RTPParticipant.java
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

import java.util.HashMap;
import java.util.Vector;

import javax.media.rtp.Participant;
import javax.media.rtp.RTPStream;
import javax.media.rtp.rtcp.Report;
import javax.media.rtp.rtcp.SourceDescription;

/**
 * Represents an RTP participant
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPParticipant implements Participant {
    
    // The streams of the participant
    private Vector streams = new Vector();
    
    // The RTCP reports of the participant
    private HashMap rtcpReports = new HashMap();
    
    // The CNAME of the particpant
    private String cName = "";
    
    // A vector of source description objects
    protected HashMap sourceDescriptions = new HashMap();
    
    // True if the participant is active
    private boolean active = false;
    
    // The size of the sdes elements combined in SDES format
    private int sdesSize = 0;
    
    /**
     * Creates a new RTPParticipant
     */
    public RTPParticipant(String cName) {
        this.cName = cName;
        addSourceDescription(
                new SourceDescription(SourceDescription.SOURCE_DESC_CNAME, 
                        cName, 1, false));
        addSourceDescription(
                new SourceDescription(SourceDescription.SOURCE_DESC_NAME, 
                        cName, 1, false));
    }

    public Vector getStreams() {
        return streams;
    }

    public Vector getReports() {
        return new Vector(rtcpReports.values());
    }

    public String getCNAME() {
        return cName;
    }

    public Vector getSourceDescription() {
        return new Vector(sourceDescriptions.values());
    }
    
    /**
     * Returns true if the participant is active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the participant active or inactive
     */
    protected void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Adds a stream to the participant
     */
    protected void addStream(RTPStream stream) {
        streams.add(stream);
    }
    
    protected void removeStream(RTPStream stream) {
        streams.remove(stream);
    }
    
    /**
     * Adds a source description item to the participant
     * @param sdes The SDES item to add
     */
    protected void addSourceDescription(SourceDescription sdes) {
        SourceDescription oldSdes = 
            (SourceDescription) sourceDescriptions.get(
                    new Integer(sdes.getType()));
        if (oldSdes != null) {
            sdesSize -= oldSdes.getDescription().length();
            sdesSize -= 2;
        }
        sourceDescriptions.put(new Integer(sdes.getType()), sdes);
        sdesSize += 2;
        sdesSize += sdes.getDescription().length();
    }
    
    /**
     * Returns the number of bytes of sdes that this participant requires
     */
    public int getSdesSize() {
        return sdesSize;
    }
    
    /**
     * Adds an RTCP Report for this participant
     * @param report The report to add
     */
    public void addReport(Report report) {
        rtcpReports.put(new Long(report.getSSRC()), report);
        Vector sdes = report.getSourceDescription();
        for (int i = 0; i < sdes.size(); i++) {
            addSourceDescription((SourceDescription) sdes.get(i));
        }
        
        if ((streams.size() == 0) && (report instanceof RTCPReport)) {
            ((RTCPReport) report).sourceDescriptions = 
                new Vector(sourceDescriptions.values());
        }
    }

}
