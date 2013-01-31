/*
 * @(#)RTPSendStream.java
 * Created: 29-Oct-2005
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
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.PushDataSource;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.Participant;
import javax.media.rtp.SendStream;
import javax.media.rtp.TransmissionStats;
import javax.media.rtp.rtcp.SenderReport;
import javax.media.rtp.rtcp.SourceDescription;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * Represnts an RTP sending stream
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPSendStream implements SendStream, 
        SourceTransferHandler, BufferTransferHandler {

	private static final Logger logger = LoggerSingleton.logger;

    // The ssrc of the stream
    private long ssrc = 0;
    
    // The data source being sent
    private DataSource dataSource = null;
    
    // The rtp output stream
    private OutputDataStream rtpDataStream = null;
    
    // The source description objects
    private HashMap sourceDescriptions = new HashMap();
    
    // The size of the sdes items
    private int sdesSize = 0;
    
    // The index of the stream to use from the data source
    private int index = 0;
    
    // The rtp format being sent
    private int format = 0;
    
    // The clock rate of the format
    private double clockRate = 90000;
    
    // True if the source has been started
    private boolean started = false;
    
    // The local participant
    private RTPLocalParticipant participant = null;
    
    // The buffer used to send the data
    private byte[] buffer = new byte[0];
    
    // The last sequence number
    private int lastSequence = (int) (Math.random() * RTPHeader.MAX_SEQUENCE);
    
    // The last time at which a value was sent
    private long lastSendTime = -1;
    
    // The last timestamp sent
    private long lastTimestamp = (long) (Math.random() * Integer.MAX_VALUE);
    
    // The first byte of the rtp header
    private byte header0 = (byte) 0x80;
    
    // The transmission statistics
    private RTPTransmissionStats stats = new RTPTransmissionStats();
    
    
    /**
     * Creates a new RTPSendStream
     * @param ssrc The ssrc of the stream
     * @param dataSource The datasource of the stream
     * @param rtpDataStream The rtp output
     */
    public RTPSendStream(long ssrc, DataSource dataSource, 
            OutputDataStream rtpDataStream, int index, 
            RTPLocalParticipant participant, int format, double clockRate) {
        this.ssrc = ssrc;
        this.dataSource = dataSource;
        this.rtpDataStream = rtpDataStream;
        this.index = index;
        this.participant = participant;
        this.format = format;
        this.clockRate = clockRate;
        addSourceDescription(
                new SourceDescription(SourceDescription.SOURCE_DESC_CNAME, 
                        participant.getCNAME(), 1, false));
        addSourceDescription(
                new SourceDescription(SourceDescription.SOURCE_DESC_NAME, 
                        participant.getCNAME(), 1, false));
    }
    
    /**
     * Adds a source description to this send stream
     * @param sdes The description to add
     */
    public void addSourceDescription(SourceDescription sdes) {
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

    public void setSourceDescription(SourceDescription[] sourceDesc) {
        for (int i = 0; i < sourceDesc.length; i++) {
            addSourceDescription(sourceDesc[i]);
        }
    }

    public void close() {
        if (started) {
            try
			{
				stop();
			} catch (IOException e)
			{
				logger.log(Level.WARNING, "" + e, e);
			}
        }
    }

    public void stop() throws IOException {
    	if (started)
    	{
            // we must call stop on the datasource, see http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/SendStream.html#start()
            dataSource.stop();	
    	}
        started = false;
    }

    public void start() throws IOException {
        if (!started) {
            if (dataSource instanceof PushBufferDataSource) {
                PushBufferStream[] streams = 
                    ((PushBufferDataSource) dataSource).getStreams();
                streams[index].setTransferHandler(this);
                
            } else if (dataSource instanceof PushDataSource) {
                PushSourceStream[] streams = 
                    ((PushDataSource) dataSource).getStreams();
                streams[index].setTransferHandler(this);
            }
            
            // we must call start on the datasource, see http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/SendStream.html#start()
            dataSource.start();	
			
        }
    }

    public int setBitRate(int bitRate) {
        return -1;
    }

    public TransmissionStats getSourceTransmissionStats() {
        return stats;
    }

    public Participant getParticipant() {
        return participant;
    }

    public SenderReport getSenderReport() {
        return null;
    }

    public long getSSRC() {
        return ssrc;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
    private void writeHeaderToBuffer(boolean marker, long timestamp) {
        // Write the marker bit and packet type
        buffer[0] = header0;
        buffer[1] = (byte) (format & 0xFF);
        if (marker) {
            buffer[1] |= 0x80;
        }
        lastSequence++;
        if (lastSequence > RTPHeader.MAX_SEQUENCE) {
            lastSequence = 0;
        }
        buffer[2] = (byte) ((lastSequence >> 8) & 0xFF);
        buffer[3] = (byte) (lastSequence & 0xFF);
        buffer[4] = (byte) ((timestamp >> 24) & 0xFF);
        buffer[5] = (byte) ((timestamp >> 16) & 0xFF);
        buffer[6] = (byte) ((timestamp >> 8) & 0xFF);
        buffer[7] = (byte) (timestamp & 0xFF);
        buffer[8] = (byte) ((ssrc >> 24) & 0xFF);
        buffer[9] = (byte) ((ssrc >> 16) & 0xFF);
        buffer[10] = (byte) ((ssrc >> 8) & 0xFF);
        buffer[11] = (byte) (ssrc & 0xFF);
    }

    public void transferData(PushSourceStream stream) {
        if (!stream.endOfStream()) {
            int size = stream.getMinimumTransferSize();
            if (buffer.length < size + RTPHeader.SIZE) {
                buffer = new byte[size + RTPHeader.SIZE];
            }
            try {
                int length = stream.read(buffer, RTPHeader.SIZE, 
                        buffer.length - RTPHeader.SIZE);
                if (length > 0) {
                    long time = System.currentTimeMillis();
                    if (lastSendTime != -1) {
                        lastTimestamp += 
                            ((time - lastSendTime) * clockRate) / 1000;
                    }
                    lastSendTime = time;
                    // TODO: this timestamp calculation will be wrong for video, where all packets of a single frame should have
                    // the same timestamp.  Also, the RTP marker flag will never be set, and for video, it needs to be set for the
                    // last packet of a frame.
                    // See the other call to writeHeaderToBuffer, in the transferDatat(PushBufferStream).
                    // really, we should probably not accept a PushSourceStream, as it does not give us access to the
                    // timestamps, or the RTP marker flag.
                    // kenlars99 8/21/07
                    writeHeaderToBuffer(false, lastTimestamp);
                    rtpDataStream.write(buffer, 0, length + RTPHeader.SIZE);
                }
            } catch (IOException e) {
            	logger.log(Level.WARNING, "" + e, e);
            }
        }
    }

    private static final int MAX_PUSHBUFFER_DATA_SIZE = 2048;	// TODO: this is just an arbitrary number.  Ideally, we can just 
    															// get the data directly from the stream without using our buffer,
    															// or somehow find out how much space we need.  kenlars99  6/3/07
    public void transferData(PushBufferStream stream) {

        if (!stream.endOfStream()) {
            try {
                Buffer recvBuffer = new Buffer();
                // Make sure that buffer is big enough.  kenlars99 6/3/07.
                if (buffer.length < MAX_PUSHBUFFER_DATA_SIZE + RTPHeader.SIZE) {
                    buffer = new byte[MAX_PUSHBUFFER_DATA_SIZE + RTPHeader.SIZE];
                }
                
                // According to the API, if the caller sets the
				// data in the buffer, the stream should not allocate it.
				// See http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PushBufferStream.html
				// The original implementation here assumed this.  But in JMF there are some badly behaving PushBufferStreams,
                // namely those in RTPSyncBufferMux, which give us a new buffer.  So we'll check for that below, and
                // copy it back to buffer.  kenlars99 7/13/07.
                recvBuffer.setData(buffer);
                recvBuffer.setOffset(RTPHeader.SIZE);
                recvBuffer.setLength(buffer.length - RTPHeader.SIZE);
                stream.read(recvBuffer);
                if (recvBuffer.getLength() > 0) {
                    long time = System.currentTimeMillis();
                    if (lastSendTime != -1) {
                        lastTimestamp += 
                            ((time - lastSendTime) * clockRate) / 1000;
                    }
                    lastSendTime = time;
                    // the calculation is still here to calculate our own timestamp in lastTimestamp.  However, this does not work properly for
                    // video, since all packets for a given video frame should have the same timestamp.
                    // the FMJ/JMF infrastructure generally assigns these timestamps correctly, so we will simply use that.
                    // TODO: verify that JMF also just passes the timestamp through.
                    // kenlars99 8/21/07
                    writeHeaderToBuffer((recvBuffer.getFlags() & Buffer.FLAG_RTP_MARKER) != 0, /*lastTimestamp*/ recvBuffer.getTimeStamp());
                    
                    // copy the data back into buffer, if the stream put it in a different byte array. kenlars99 7/13/07.
                    if (recvBuffer.getData() != buffer)
                    	System.arraycopy(recvBuffer.getData(), recvBuffer.getOffset(), buffer, RTPHeader.SIZE, recvBuffer.getLength());
                    
                    rtpDataStream.write(buffer, 0, 
                            recvBuffer.getLength() + RTPHeader.SIZE);
                }
            } catch (IOException e) {
            	logger.log(Level.WARNING, "" + e, e);
            }
        }
    }
    
    /**
     * Returns the source description for this source
     * @return The source description objects
     */
    public Vector getSourceDescription() {
        return new Vector(sourceDescriptions.values());
    }
    
    /**
     * Returns the number of bytes of sdes that this participant requires
     */
    public int getSdesSize() {
        return sdesSize;
    }
    
    /**
     * Returns the last time a packet was sent
     */
    public long getLastSendTime() {
        return lastSendTime;
    }
    
    /**
     * Returns the last timestamp of a packet sent
     */
    public long getLastTimestamp() {
        return lastTimestamp;
    }

}
