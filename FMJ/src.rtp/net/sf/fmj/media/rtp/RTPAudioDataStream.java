/*
 * @(#)RTPDataStream.java
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

import java.util.Timer;
import java.util.TimerTask;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.protocol.PushBufferStream;

/**
 * Represents a stream sent via RTP
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPAudioDataStream extends RTPDataStream implements PushBufferStream {
    
    // The maximum negative delay after which a packet is dropped
    private static final int MAX_NEG_DELAY = -100;
    
    // The length of the buffer
    private int bufferLength = 200;
    
    // The RTP buffer
    private RTPJitterBuffer buffer = null;
    
    // The Buffers to be used
    private Buffer[] bufferBuffer = null; 
    
    // The next position to use in the buffer array
    private int nextBuffer = 0;
    
    // The first timestamp seen
    private long firstTimestamp = -1;
    
    // The time at which the first packet was sent
    private long firstSendTime = -1;
    
    /**
     * Creates a new RTPDataStream
     * @param format The format of the data
     */
    public RTPAudioDataStream(long ssrc, Format format) {
        super(ssrc, format);
        if (format instanceof AudioFormat) {
            clockRate = ((AudioFormat) format).getSampleRate();
        }
        buffer = new RTPJitterBuffer(bufferLength);
        buffer.setStatistics(receptionStats);
        bufferBuffer = new Buffer[bufferLength + 1];
        }
    
    private long calculateDelay(long timestamp) {
        if (firstSendTime == -1) {
            firstSendTime = System.currentTimeMillis() + threshold;
            firstTimestamp = timestamp;
            return threshold;
        }
        
        // Timestamp wrap
        if (firstTimestamp > timestamp) {
            firstSendTime += (Integer.MAX_VALUE * 1000) / clockRate;
        }
        long delay = (long) (((timestamp - firstTimestamp) * 1000.0) /
                clockRate);
        delay -= System.currentTimeMillis() - firstSendTime;
        return delay;
    }
    
    /**
     * Adds a packet to the buffers
     * @param header The header of the packet
     * @param data The data of the packet
     * @param offset The offset of the packet
     * @param length The length of the packet
     */
    protected void addPacket(RTPHeader header, byte[] data, 
            int offset, int length) {
        
        // Setup the buffer
        int sequence = header.getSequence();
        long timestamp = header.getTimestamp();
        calculateJitter(timestamp);
        if (firstSequence == -1) {
            firstSequence = sequence;
        }
        if ((sequence < lastSequence) &&
                (RTPHeader.MAX_SEQUENCE - lastSequence < 100)) {
            receptionStats.addSequenceWrap();
        }
        lastSequence = sequence;
        if (bufferBuffer[nextBuffer] == null) {
            bufferBuffer[nextBuffer] = new Buffer();
        }
        Buffer buffer = bufferBuffer[nextBuffer];
        nextBuffer = (nextBuffer + 1) % bufferBuffer.length;
        buffer.setData(data);
        buffer.setOffset(offset);
        buffer.setLength(length);
        buffer.setTimeStamp(timestamp);
        buffer.setSequenceNumber(sequence);
        int flags = Buffer.FLAG_RTP_TIME | Buffer.FLAG_RELATIVE_TIME;
        if (header.getMarker() == 1) {
            flags |= Buffer.FLAG_RTP_MARKER;
        }
        buffer.setFlags(flags);
        if (this.buffer.add(buffer)) {
        
            // If this is the first packet in the buffer, setup the timer (in ms)
            if (this.buffer.size() == 1) {
                firstSendTime = System.currentTimeMillis() + threshold;
                firstTimestamp = timestamp;
                Timer timer = new Timer();
                timer.schedule(new RTPTimerTask(this), threshold);
            }
        } else {
            nextBuffer--;
            if (nextBuffer < 0) {
                 nextBuffer = bufferBuffer.length - 1;
            }
        }
    }
    
    public void read(Buffer buffer) {
        Buffer data = this.buffer.remove();
        if ((data != null) && (buffer != null)) {
            buffer.setData(data.getData());
            buffer.setOffset(data.getOffset());
            buffer.setLength(data.getLength());
            buffer.setTimeStamp(data.getTimeStamp());
            buffer.setSequenceNumber(data.getSequenceNumber());
            buffer.setFlags(data.getFlags());
            buffer.setFormat(format);
        }
    }
    
    private void dropPackets() {
        long nextTimestamp = buffer.peekTimeStamp();
        while ((buffer.peekTimeStamp() != -1) 
                && (nextTimestamp >= buffer.peekTimeStamp())) {
            buffer.remove();
        }
    }
    
    private void sendData() {
        long nextTimestamp = buffer.peekTimeStamp();
        while ((buffer.peekTimeStamp() != -1) 
                && (nextTimestamp >= buffer.peekTimeStamp())) {
            if (handler != null) {
                handler.transferData(this);
            } else {
                buffer.remove();
            }
        }
    }
    
    /**
     * Sends the first packet in the queue (and all packets with the same
     * timestamp)
     */
    public void run() {
        sendData();
        boolean scheduled = false;
        boolean endofdata = false;
        while (!scheduled && !endofdata) {
            long timestamp = buffer.peekTimeStamp();
            if (timestamp != -1) {
                long delay = calculateDelay(timestamp);
                if (delay < MAX_NEG_DELAY) {
                    dropPackets();
                } else {
                    sendData();
                }
            } else {
                endofdata = true;
            }
        }
    }
    
    private class RTPTimerTask extends TimerTask {
        
        // The data stream to run
        private RTPAudioDataStream stream;
               
        private RTPTimerTask(RTPAudioDataStream stream) {
            this.stream = stream;
        }
        
        public void run() {
            stream.run();
        }
    }
}
