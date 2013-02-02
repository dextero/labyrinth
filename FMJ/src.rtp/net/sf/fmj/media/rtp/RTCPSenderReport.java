/*
 * @(#)RTCPSenderReport.java
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

import java.io.IOException;

import javax.media.rtp.RTPStream;
import javax.media.rtp.rtcp.Feedback;
import javax.media.rtp.rtcp.SenderReport;

/**
 * Represents an RTCP Sender Report
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTCPSenderReport extends RTCPReport implements SenderReport {
    
    // The sender information
    RTCPSenderInfo senderInformation = null;
    
    // The RTPStream associated with the sender
    private RTPStream stream = null;
    
    /**
     * Creates a new RTCPSenderReport
     * @param data The data of the report
     * @param offset The offset of the report in the data
     * @param length The length of the data
     * @throws IOException
     */
    public RTCPSenderReport(byte data[], int offset, int length) 
            throws IOException {
        super(data, offset, length);
        senderInformation = new RTCPSenderInfo(data, offset + RTCPHeader.SIZE, 
                length - RTCPHeader.SIZE);
        readFeedbackReports(data, 
                offset + RTCPHeader.SIZE + RTCPSenderInfo.SIZE,
                length - RTCPHeader.SIZE - RTCPSenderInfo.SIZE);
        
        // Read any source descriptions
        offset += (header.getLength() + 1) * 4;
        length -= (header.getLength() + 1) * 4;
        readSourceDescription(data, offset, length);
        offset += sdesBytes;
        length -= sdesBytes;
        readBye(data, offset, length);
    }

    protected void setStream(RTPStream stream) {
        this.stream = stream;
    }
    
    public RTPStream getStream() {
        return stream;
    }

    public long getSenderPacketCount() {
        return senderInformation.getPacketCount();
    }

    public long getSenderByteCount() {
        return senderInformation.getOctetCount();
    }

    public long getNTPTimeStampMSW() {
        return senderInformation.getNtpTimestampMSW();
    }

    public long getNTPTimeStampLSW() {
        return senderInformation.getNtpTimestampLSW();
    }

    public long getRTPTimeStamp() {
        return senderInformation.getTimestamp();
    }

    public Feedback getSenderFeedback() {
        // TODO Auto-generated method stub
        return null;
    }

}
