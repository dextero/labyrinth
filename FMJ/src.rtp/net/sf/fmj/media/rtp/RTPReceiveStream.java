/*
 * @(#)RTPReceiveStream.java
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

import java.util.HashMap;
import java.util.Vector;

import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceptionStats;
import javax.media.rtp.rtcp.Report;
import javax.media.rtp.rtcp.SenderReport;
import javax.media.rtp.rtcp.SourceDescription;

/**
 * Represents a stream received over RTP
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPReceiveStream implements ReceiveStream {
    
    // The data source
    private DataSource dataSource = null;
    
    // The ssrc
    private long ssrc = 0;
    
    // The last sender report
    private SenderReport lastSenderReport = null;
    
    // The participant
    private RTPRemoteParticipant participant = null;
    
    // The time at which the last SR report was received
    private long lastSRReportTime = 0;
    
    // The source descriptions for this stream
    private HashMap sourceDescriptions = new HashMap();
    
    /**
     * Creates a new RTPReceiveStream
     * @param dataSource The datasource of the stream
     * @param ssrc The ssrc of the stream
     */
    public RTPReceiveStream(DataSource dataSource, long ssrc) {
        this.dataSource = dataSource;
        this.ssrc = ssrc;
    }

    public ReceptionStats getSourceReceptionStats() {
        return ((RTPDataSource) dataSource).getSourceReceptionStats();
    }

    public Participant getParticipant() {
        return participant;
    }

    public SenderReport getSenderReport() {
        return lastSenderReport;
    }

    public long getSSRC() {
        return ssrc;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Sets the participant
     * @param participant The participant to set
     */
    protected void setParticipant(RTPRemoteParticipant participant) {
        this.participant = participant;
    }
    
    /**
     * Sets the last report received
     * @param lastReport The report
     */
    public void setReport(Report lastReport) {
        if (lastReport instanceof SenderReport) {
            lastSRReportTime = System.currentTimeMillis();
            this.lastSenderReport = (SenderReport) lastReport;
        }
        Vector sdes = lastReport.getSourceDescription();
        for (int i = 0; i < sdes.size(); i++) {
            SourceDescription sdesItem = (SourceDescription) sdes.get(i);
            sourceDescriptions.put(new Integer(sdesItem.getType()), sdesItem);
        }
        if (lastReport instanceof RTCPReport) {
            ((RTCPReport) lastReport).sourceDescriptions = 
                new Vector(sourceDescriptions.values());
        }
    }
    
    /**
     * Returns the time at which the last SR report was received
     */
    public long getLastSRReportTime() {
        return lastSRReportTime;
    }
    
    /**
     * Gets the timestamp of the last sr report
     */
    public long getLastSRReportTimestampLSW() {
        if (lastSenderReport == null) {
            return 0;
        }
        return lastSenderReport.getNTPTimeStampLSW();
    }
    
    /**
     * Gets the timestamp of the last sr report
     */
    public long getLastSRReportTimestampMSW() {
        if (lastSenderReport == null) {
            return 0;
        }
        return lastSenderReport.getNTPTimeStampMSW();
    }

}
