/*
 * @(#)RTPManager.java
 * Created: 25-Oct-2005
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.EncryptionInfo;
import javax.media.rtp.GlobalReceptionStats;
import javax.media.rtp.GlobalTransmissionStats;
import javax.media.rtp.LocalParticipant;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;
import javax.media.rtp.RTPStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.RemoteListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.SendStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.SessionManager;
import javax.media.rtp.TransmissionStats;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.ReceiverReportEvent;
import javax.media.rtp.event.RemoteEvent;
import javax.media.rtp.event.SenderReportEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;
import javax.media.rtp.rtcp.SourceDescription;

import net.sf.fmj.utility.LoggerSingleton;

/**
 *
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPSessionMgr extends javax.media.rtp.RTPManager implements
        SessionManager {

    private static final Logger logger = LoggerSingleton.logger;

    // The minimum RTCP interval in ms
    private static final int MIN_RTCP_INTERVAL = 5000;

    // The map of Integer -> format recognised
    private HashMap formatMap = new HashMap();

    // A vector of receive stream listeners
    private Vector receiveStreamListeners = new Vector();

    // A vector of remote listeners
    private Vector remoteListeners = new Vector();

    // A vector of send stream listeners
    private Vector sendStreamListeners = new Vector();

    // A vector of session listeners
    private Vector sessionListeners = new Vector();

    // The local participant
    private RTPLocalParticipant localParticipant = null;

    // A map of active participants (cname -> participant)
    private HashMap activeParticipants = new HashMap();

    // A map of inactive participants (cname -> participant)
    private HashMap inactiveParticipants = new HashMap();

    // The global reception statistics
    private RTPGlobalReceptionStats globalReceptionStats =
        new RTPGlobalReceptionStats();

    // The global transmission statistics
    private RTPGlobalTransmissionStats globalTransmissionStats =
        new RTPGlobalTransmissionStats();

    // A map of receive streams (ssrc -> stream)
    private HashMap receiveStreams = new HashMap();

    // A map of send streams (ssrc -> stream)
    private HashMap sendStreams = new HashMap();

    // A map of streams that are ignored as they cannot be recognised
    private HashMap ignoredStreams = new HashMap();

    // A map of ssrcs to cnames
    private HashMap senders = new HashMap();

    // The RTCP Receiver Bandwidth fraction
    private double rtcpReceiverBandwidthFraction = 0.0375;

    // The RTCP Sender Bandwidth fraction
    private double rtcpSenderBandwidthFraction = 0.0125;

    // The local address to use to bind sockets to
    private SessionAddress localAddress = null;

    // The RTP Connectors for targets that have been set up
    // (address -> connector)
    private HashMap targets = new HashMap();

    // The RTP Handler
    private RTPHandler rtpHandler = null;

    // The RTCP Handler
    private RTCPHandler rtcpHandler = null;

    // The lock for sending events (so events are received in order)
    private Integer eventLock = new Integer(0);

    // True if the event lock has been obtained
    private boolean eventLocked = false;

    // True when the session is finished with
    private boolean done = false;

    // An RTCP timer
    private Timer rtcpTimer = new Timer();

    // The time at which the last rtcpPacket was sent
    private long lastRTCPSendTime = -1;

    // The average size of an RTCP packet received
    private int averageRTCPSize = 0;

    // The ssrc if we are not sending
    protected static long ssrc = (long) (Math.random() * Integer.MAX_VALUE);

    private void getEventLock() {
        synchronized (eventLock) {
            while (eventLocked) {
                try {
                    eventLock.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            eventLocked = true;
        }
    }

    private void releaseEventLock() {
        synchronized (eventLock) {
            eventLocked = false;
            eventLock.notifyAll();
        }
    }

    /**
     * Creates a new RTPManager
     */
    public RTPSessionMgr() {
        String user = System.getProperty("user.name");
        String host = "localhost";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Do Nothing
        }
        this.localParticipant = new RTPLocalParticipant(user + "@" + host);
        addFormat(new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1), 0);
        addFormat(new AudioFormat(AudioFormat.GSM_RTP, 8000,
                Format.NOT_SPECIFIED, 1), 3);
        addFormat(new AudioFormat(AudioFormat.G723_RTP, 8000,
                Format.NOT_SPECIFIED, 1), 4);
        addFormat(new AudioFormat(AudioFormat.DVI_RTP, 8000, 4, 1), 5);
        addFormat(new AudioFormat(AudioFormat.MPEG_RTP), 14);
        addFormat(new AudioFormat(AudioFormat.G728_RTP, 8000.0, Format.NOT_SPECIFIED, 1), 15);
        addFormat(new AudioFormat(AudioFormat.DVI_RTP, 11025, 4, 1), 16);
        addFormat(new AudioFormat(AudioFormat.DVI_RTP, 22050, 4, 1), 17);
		addFormat(new AudioFormat(AudioFormat.G729_RTP, 8000.0, Format.NOT_SPECIFIED, 1), 18);
        addFormat(new VideoFormat(VideoFormat.JPEG_RTP), 26);
        addFormat(new VideoFormat(VideoFormat.H261_RTP), 31);
        addFormat(new VideoFormat(VideoFormat.MPEG_RTP), 32);
        addFormat(new VideoFormat(VideoFormat.H263_RTP), 34);
		addFormat(new VideoFormat("h263-1998/rtp"), 42);

        // ??? dex
        addFormat(new VideoFormat("rgb"), 44);
    }
    
    /** 
     * This is a function provided by Sun's implementation, which is not in the API as AFAIK - kenlars99.
     * This does not include formats in the RTP bonus formats mgr.
     * This is a somewhat dubious function, since the actual RTPSessionMgr used is determined by RTPManager.newInstance().
     * To the extend that Sun's code calls this function, it is assuming that its own RTPSessionMgr will be used, which
     * may be the source of some problems.
     * 
     * Note: the format list in JMF is static, and in FMJ it is not.  So you can add a format once with
     * JMF, but you have to add it every time you instantiate the manager with FMJ.
     * 
     */
	public static boolean formatSupported(Format f) {
		final RTPSessionMgr mgr = new RTPSessionMgr();
		
        final Iterator iter = mgr.formatMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = (Integer) iter.next();
            Format testFormat = (Format) mgr.formatMap.get(id);
            if (testFormat.matches(f)) {
               return true;
            }
        }
        
		return false;
	}
	
	/** Another function provided by Sun's implementation, not in the spec/API. */
	public Format getFormat(int payload) {
		return (Format) formatMap.get(new Integer(payload));
	}

    public void addFormat(Format format, int payload) {
        formatMap.put(new Integer(payload), format);
    }

    public void addReceiveStreamListener(ReceiveStreamListener listener) {
        receiveStreamListeners.add(listener);
    }

    public void addRemoteListener(RemoteListener listener) {
        remoteListeners.add(listener);
    }

    public void addSendStreamListener(SendStreamListener listener) {
        sendStreamListeners.add(listener);
    }

    public void addSessionListener(SessionListener listener) {
        sessionListeners.add(listener);
    }

    public void removeReceiveStreamListener(ReceiveStreamListener listener) {
        receiveStreamListeners.remove(listener);
    }

    public void removeRemoteListener(RemoteListener listener) {
        remoteListeners.remove(listener);
    }

    public void removeSendStreamListener(SendStreamListener listener) {
        sendStreamListeners.remove(listener);
    }

    public void removeSessionListener(SessionListener listener) {
        sessionListeners.remove(listener);
    }

    public Vector getActiveParticipants() {
        Vector participants = new Vector(activeParticipants.values());
        if (localParticipant.isActive()) {
            participants.add(localParticipant);
        }
        return participants;
    }

    public Vector getAllParticipants() {
        Vector participants = new Vector();
        participants.addAll(activeParticipants.values());
        participants.addAll(inactiveParticipants.values());
        participants.add(localParticipant);
        return participants;
    }

    public LocalParticipant getLocalParticipant() {
        return localParticipant;
    }

    public Vector getPassiveParticipants() {
        Vector participants = new Vector(inactiveParticipants.values());
        if (!localParticipant.isActive()) {
            participants.add(localParticipant);
        }
        return participants;
    }

    public Vector getRemoteParticipants() {
        Vector participants = new Vector();
        participants.addAll(activeParticipants.values());
        participants.addAll(inactiveParticipants.values());
        return participants;
    }

    public GlobalReceptionStats getGlobalReceptionStats() {
        return globalReceptionStats;
    }

    public GlobalTransmissionStats getGlobalTransmissionStats() {
        return globalTransmissionStats;
    }

    public Vector getReceiveStreams() {
        return new Vector(receiveStreams.values());
    }

    public Vector getSendStreams() {
        return new Vector(sendStreams.values());
    }

    public void initialize(SessionAddress localAddress)
            throws IOException {
        String user = System.getProperty("user.name");
        initialize(new SessionAddress[] { localAddress },
                new SourceDescription[] {
                        new SourceDescription(
                                SourceDescription.SOURCE_DESC_CNAME, user
                                        + "@"
                                        + InetAddress.getLocalHost()
                                                .getHostName(), 1, false),
                        new SourceDescription(
                                SourceDescription.SOURCE_DESC_NAME, user
                                        + "@"
                                        + InetAddress.getLocalHost()
                                                .getHostName(), 3, false) },
                0.05, 0.25, null);
    }

    public void initialize(SessionAddress[] localAddresses,
            SourceDescription[] sourceDescription,
            double rtcpBandwidthFraction, double rtcpSenderBandwidthFraction,
            EncryptionInfo encryptionInfo) {
        this.rtcpSenderBandwidthFraction = rtcpBandwidthFraction
                * rtcpBandwidthFraction;
        this.rtcpReceiverBandwidthFraction = rtcpBandwidthFraction
                - this.rtcpSenderBandwidthFraction;
        this.localParticipant = new RTPLocalParticipant("");
        localParticipant.setSourceDescription(sourceDescription);
        localAddress = localAddresses[0];
        start();
    }

    public void addTarget(SessionAddress remoteAddress)
            throws IOException {
        RTPSocketAdapter socket = new RTPSocketAdapter(
                localAddress.getDataAddress(),
                remoteAddress.getDataAddress(), remoteAddress.getDataPort(),
                remoteAddress.getTimeToLive());
        rtpHandler = new RTPHandler(this);
        rtcpHandler = new RTCPHandler(this);
        socket.getControlInputStream().setTransferHandler(rtcpHandler);
        socket.getDataInputStream().setTransferHandler(rtpHandler);
        targets.put(remoteAddress, socket);
    }

    public void initialize(RTPConnector connector) {
        try {
            rtpHandler = new RTPHandler(this);
            rtcpHandler = new RTCPHandler(this);
            connector.getControlInputStream().setTransferHandler(rtcpHandler);
            connector.getDataInputStream().setTransferHandler(rtpHandler);
            targets.put(null, connector);
            start();
        } catch (IOException e) {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    public void removeTarget(SessionAddress remoteAddress, String reason) {
        RTPConnector connector = (RTPConnector) targets.get(remoteAddress);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);

        try {
            Vector sendStreams = localParticipant.getStreams();

            // Send a bye packet
            output.writeByte(0x80 | 0 | (0 & 0x1F));
            output.writeByte(RTCPPacket.PT_RR & 0xFF);
            output.writeShort(1);
            output.writeInt((int) (ssrc & 0xFFFFFFFF));
            output.writeByte(0x80 | 0 | ((sendStreams.size() + 1) & 0x1F));
            output.writeByte(RTCPPacket.PT_BYE & 0xFF);
            output.writeShort(sendStreams.size() + 1);
            output.writeInt((int) (ssrc & 0xFFFFFFFF));
            for (int i = 0; i < sendStreams.size(); i++) {
                output.writeInt((int) (((RTPSendStream)
                        sendStreams.get(i)).getSSRC() & 0xFFFFFFFF));
            }
            output.close();
            bytes.close();
            byte[] data = bytes.toByteArray();
            OutputDataStream out = connector.getControlOutputStream();
            out.write(data, 0, data.length);
        } catch (IOException e) {
            logger.log(Level.WARNING, "" + e, e);
        }
        if (connector != null) {
            targets.remove(remoteAddress);
        }
    }

    public void removeTargets(String reason) {
        Iterator iter = targets.keySet().iterator();
        while (iter.hasNext()) {
            SessionAddress addr = (SessionAddress) iter.next();
            removeTarget(addr, reason);
        }
    }

    public SendStream createSendStream(DataSource dataSource, int streamIndex)
            throws UnsupportedFormatException, IOException {
        int format = -1;
        Format fmt = null;
        double clockRate = 90000;
        if (dataSource instanceof PushBufferDataSource) {
            PushBufferStream stream =
                ((PushBufferDataSource) dataSource).getStreams()[streamIndex];
            fmt = stream.getFormat();
        } else if (dataSource instanceof PullBufferDataSource) {
            PullBufferStream stream =
                ((PullBufferDataSource) dataSource).getStreams()[streamIndex];
            fmt = stream.getFormat();
        } else {
            throw new IOException("Cannot use stream sources");
        }
        Iterator iter = formatMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = (Integer) iter.next();
            Format testFormat = (Format) formatMap.get(id);
            if (testFormat.matches(fmt)) {
                format = id.intValue();
            }
        }
        if (format == -1) {
            throw new UnsupportedFormatException(fmt);
        }
        if (fmt instanceof AudioFormat) {
            clockRate = ((AudioFormat) fmt).getSampleRate();
        }
        Iterator iterator = targets.values().iterator();
        RTPConnector connector = (RTPConnector) iterator.next();
        OutputDataStream stream = connector.getDataOutputStream();
        return new RTPSendStream((long) (Math.random() * Integer.MAX_VALUE),
                dataSource, stream, streamIndex, localParticipant, format,
                clockRate);
    }

    public void dispose() {
        removeTargets("Quitting");
        done = true;
    }

    public Object getControl(String controlClass) {
        return null;
    }

    public Object[] getControls() {
        return new Object[] {};
    }

    /**
     * Handles an incoming RTP packet
     * @param data The packet data
     * @param offset The packet offset
     * @param length The packet length
     * @throws IOException
     */
    protected void handleRTPPacket(byte[] data, int offset, int length) {
        try {
            globalReceptionStats.addPacketRecd();
            globalReceptionStats.addBytesRecd(length);
            RTPHeader header = new RTPHeader(data, offset, length);
            long ssrc = header.getSsrc();
            Integer packetType = (Integer) ignoredStreams.get(new Long(ssrc));
            if (packetType != null) {
                if (packetType.intValue() != header.getPacketType()) {
                    ignoredStreams.remove(new Long(ssrc));
                    packetType = null;
                }
            }
            if (packetType == null) {
                RTPReceiveStream stream =
                    (RTPReceiveStream) receiveStreams.get(new Long(ssrc));
                if (stream == null) {
                    int type = header.getPacketType();
                    Format format = (Format) formatMap.get(new Integer(type));
                    if (format == null) {
                        globalReceptionStats.addUnknownType();
                        logger.warning("Unknown format identifier: "
                                + type);
                        ignoredStreams.put(new Long(ssrc), new Integer(type));
                    } else {
                        RTPDataSource dataSource =
                            new RTPDataSource(ssrc, format);
                        stream = new RTPReceiveStream(dataSource, ssrc);
                        receiveStreams.put(new Long(ssrc), stream);
                        ReceiveStreamEvent event = new NewReceiveStreamEvent(
                                this, stream);
                        new ReceiveStreamNotifier(receiveStreamListeners,
                                event);
                    }
                }
                if (stream != null) {
                    RTPDataSource dataSource =
                        (RTPDataSource) stream.getDataSource();
                    dataSource.handleRTPPacket(header,
                            data, offset + header.getSize(),
                            length - header.getSize());
                }
            }
        } catch (IOException e) {
            globalReceptionStats.addBadRTPkt();
        }
    }

    /**
     * Handles an incoming RTCP packet
     * @param data The packet data
     * @param offset The packet offset
     * @param length The packet length
     */
    protected void handleRTCPPacket(byte[] data, int offset, int length) {
        try {
            int avgeRTCPSize =
                averageRTCPSize * globalReceptionStats.getRTCPRecd();
            globalReceptionStats.addRTCPRecd();
            globalReceptionStats.addBytesRecd(length);
            averageRTCPSize =
                (avgeRTCPSize + length + 28) /
                globalReceptionStats.getRTCPRecd();
            RTCPHeader header = new RTCPHeader(data, offset, length);

            // Get the stream of the participant, if available
            long ssrc = header.getSsrc();
            RTPReceiveStream stream =
                (RTPReceiveStream) receiveStreams.get(new Long(ssrc));

            RTCPReport report = null;
            RemoteEvent remoteEvent = null;

            // If the packet is SR, read the sender info
            if (header.getPacketType() == RTCPPacket.PT_SR) {
                report = new RTCPSenderReport(data, offset, length);
                ((RTCPSenderReport) report).setStream(stream);
                remoteEvent = new SenderReportEvent(this,
                        (RTCPSenderReport) report);
                globalReceptionStats.addSRRecd();
            }

            // If the packet is RR, read the receiver info
            if (header.getPacketType() == RTCPPacket.PT_RR) {
                report = new RTCPReceiverReport(data, offset, length);
                remoteEvent = new ReceiverReportEvent(this,
                        (RTCPReceiverReport) report);
            }

            // If the report is not null
            if (report != null) {
                String cname = report.getCName();
                if (cname == null) {
                    cname = (String) senders.get(new Long(ssrc));
                }

                if (stream != null) {
                    stream.setReport(report);
                }

                // If the cname is in the report
                if (cname != null) {

                    // Store the cname for later
                    senders.put(new Long(ssrc), cname);

                    // Get the participant
                    RTPRemoteParticipant participant =
                        (RTPRemoteParticipant) activeParticipants.get(cname);
                    if (participant == null) {
                        participant = (RTPRemoteParticipant)
                                          inactiveParticipants.get(cname);
                    }

                    // If there is no participant, create one
                    if (participant == null) {
                        participant = new RTPRemoteParticipant(cname);
                        getEventLock();
                        SessionEvent event =
                            new NewParticipantEvent(this, participant);
                        new SessionNotifier(sessionListeners, event);
                        inactiveParticipants.put(cname, participant);
                    }

                    // Set the participant of the report
                    report.setParticipant(participant);
                    participant.addReport(report);

                    // If this is a bye packet, remove the stream
                    if (report.isByePacket()) {
                        participant.removeStream(stream);
                        getEventLock();
                        new ReceiveStreamNotifier(receiveStreamListeners,
                                new ByeEvent(this, participant, stream,
                                        report.getByeReason(),
                                        participant.getStreams().size() == 0));
                        if (participant.getStreams().size() == 0) {
                            activeParticipants.remove(cname);
                            inactiveParticipants.put(cname, participant);
                        }
                    } else {

                        // If the stream is not null, map the stream
                        if (stream != null) {
                            if (!activeParticipants.containsKey(cname)) {
                                inactiveParticipants.remove(cname);
                                activeParticipants.put(cname, participant);
                            }

                            if (stream.getParticipant() == null) {
                                participant.addStream(stream);
                                stream.setParticipant(participant);
                                getEventLock();
                                ReceiveStreamEvent event =
                                    new StreamMappedEvent(this, stream,
                                            participant);
                                new ReceiveStreamNotifier(
                                        receiveStreamListeners, event);
                            }
                        }
                    }
                }

                // Notify listeners of this packet
                getEventLock();
                new RemoteNotifier(remoteListeners, remoteEvent);
            } else {
                throw new IOException("Unknown report type: " +
                        header.getPacketType());
            }

        } catch (IOException e) {
            globalReceptionStats.addBadRTCPPkt();
        }
    }

    // A notifier of receive stream events
    private class ReceiveStreamNotifier extends Thread {

        // The receive stream listener
        private Vector listeners = null;

        // The event
        private ReceiveStreamEvent event = null;

        private ReceiveStreamNotifier(Vector listeners,
                ReceiveStreamEvent event) {
            this.listeners = listeners;
            this.event = event;
            start();
        }

        public void run() {
            for (int i = 0; i < listeners.size(); i++) {
                ReceiveStreamListener listener =
                    (ReceiveStreamListener) listeners.get(i);
                listener.update(event);
            }
            releaseEventLock();
        }
    }

    // A notifier of receive session events
    private class SessionNotifier extends Thread {

        // The session listener
        private Vector listeners = null;

        // The event
        private SessionEvent event = null;

        private SessionNotifier(Vector listeners,
                SessionEvent event) {
            this.listeners = listeners;
            this.event = event;
            start();
        }

        public void run() {
            for (int i = 0; i < listeners.size(); i++) {
                SessionListener listener =
                    (SessionListener) listeners.get(i);
                listener.update(event);
            }
            releaseEventLock();
        }
    }

    // A notifier of remote events
    private class RemoteNotifier extends Thread {

        // The remote listeners
        private Vector listeners = null;

        // The event
        private RemoteEvent event = null;

        private RemoteNotifier(Vector listeners,
                RemoteEvent event) {
            this.listeners = listeners;
            this.event = event;
            start();
        }

        public void run() {
            for (int i = 0; i < listeners.size(); i++) {
                RemoteListener listener =
                    (RemoteListener) listeners.get(i);
                listener.update(event);
            }
            releaseEventLock();
        }
    }

    public int initSession(SessionAddress localAddress, long defaultSSRC,
            SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
            double rtcp_sender_bw_fraction) {
        initialize(new SessionAddress[]{localAddress}, defaultUserDesc,
                rtcp_bw_fraction,
                rtcp_sender_bw_fraction, null);
        return 0;
    }

    public int initSession(SessionAddress localAddress,
            SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
            double rtcp_sender_bw_fraction) {
        initialize(new SessionAddress[]{localAddress}, defaultUserDesc,
                rtcp_bw_fraction,
                rtcp_sender_bw_fraction, null);
        return 0;
    }

    public int startSession(SessionAddress destAddress, int mcastScope,
            EncryptionInfo encryptionInfo) throws IOException {
        addTarget(destAddress);
        return 0;
    }

    public int startSession(SessionAddress localReceiverAddress,
            SessionAddress localSenderAddress,
            SessionAddress remoteReceiverAddress,
            EncryptionInfo encryptionInfo) throws IOException {
        addTarget(remoteReceiverAddress);
        return 0;
    }

    public long getDefaultSSRC() {
        return 0;
    }

    public RTPStream getStream(long filterssrc) {
        return null;
    }

    public int getMulticastScope() {
        return 127;
    }

    public void setMulticastScope(int multicastScope) {
        // Does Nothing
    }

    public void closeSession(String reason) {
        removeTargets(reason);
    }

    public String generateCNAME() {
        return localParticipant.getCNAME();
    }

    public long generateSSRC() {
        return (long) (Math.random() * Integer.MAX_VALUE);
    }

    public SessionAddress getSessionAddress() {
        return null;
    }
    
    public SessionAddress getRemoteSessionAddress() {
        return null;	// TODO: added only for JMF Compatibility.  Called, for example in AVReceiver example.
    }

    public SessionAddress getLocalSessionAddress() {
        return localAddress;
    }

    public SendStream createSendStream(int ssrc, DataSource ds,
            int streamindex) throws UnsupportedFormatException,
            IOException {
        return createSendStream(ds, streamindex);
    }

    public int startSession(int mcastScope, EncryptionInfo encryptionInfo) {
        return -1;
    }

    public void addPeer(SessionAddress peerAddress) throws IOException {
        addTarget(peerAddress);
    }

    public void removePeer(SessionAddress peerAddress) {
        removeTarget(peerAddress, "Leaving");
    }

    public void removeAllPeers() {
        removeTargets("Leaving");
    }

    public Vector getPeers() {
        return getAllParticipants();
    }

    /**
     * Starts the sending of RTCP packets
     */
    public void start() {
        // Send the first RTCP packet
        long delay = (long) (Math.random() * 1000) + 500;
        rtcpTimer.schedule(new RTCPTimerTask(this), delay);
        globalReceptionStats.resetBytesRecd();
        lastRTCPSendTime = System.currentTimeMillis();
    }

    private long calculateRTCPDelay() {
        long delay = MIN_RTCP_INTERVAL;
        double bandwidth = ((double) globalReceptionStats.getBytesRecd() /
                (System.currentTimeMillis() - lastRTCPSendTime));
        if (bandwidth < 0.1) {
            delay = MIN_RTCP_INTERVAL;
        } else {
            double senderFraction = 0;
            if ((activeParticipants.size() > 0) ||
                    (inactiveParticipants.size() > 0) ) {
                senderFraction =
                    activeParticipants.size() /
                    (inactiveParticipants.size() + activeParticipants.size());
            }
            if ((activeParticipants.size() > 0) && (senderFraction < 0.25)) {
                if (localParticipant.getStreams().size() > 0) {
                    delay = (long) ((averageRTCPSize *
                            activeParticipants.size()) /
                            (bandwidth * rtcpSenderBandwidthFraction));
                } else {
                    delay = (long) ((averageRTCPSize *
                                     inactiveParticipants.size()) /
                            (bandwidth * rtcpReceiverBandwidthFraction));
                }
            } else {
                delay = (long) ((averageRTCPSize * (activeParticipants.size() +
                        inactiveParticipants.size())) /
                        (bandwidth * (rtcpSenderBandwidthFraction +
                                rtcpReceiverBandwidthFraction)));
            }
            if (delay < MIN_RTCP_INTERVAL) {
                delay = MIN_RTCP_INTERVAL;
            }
        }
        return delay;
    }

    private int writeSDESHeader(DataOutputStream output, int ssrcs, int size)
            throws IOException {
        int packetSize = size + 5 + (4 * ssrcs);
        int padding = 4 - (packetSize % 4);
        if (padding == 4) {
           padding = 0;
        }
        packetSize += padding;
        int pBit = 0;
        if (padding > 0) {
            pBit = 0x20;
        }
        // Add a RTCP header
        output.writeByte(0x80 | pBit | (ssrcs & 0x1F));
        output.writeByte(RTCPPacket.PT_SDES & 0xFF);
        output.writeShort((packetSize / 4) - 1);

        return padding;
    }

    private void writeSDES(DataOutputStream output, Vector sdesItems,
            long ssrc)
            throws IOException {
        output.writeInt((int) (ssrc & 0xFFFFFFFF));
        for (int i = 0; i < sdesItems.size(); i++) {
            SourceDescription sdes =
                (SourceDescription) sdesItems.get(i);
            int type = sdes.getType();
            String description = sdes.getDescription();
            byte[] desc = description.getBytes("UTF-8");
            output.writeByte(type & 0xFF);
            output.writeByte(desc.length & 0xFF);
            output.write(desc);
        }
        output.writeByte(0);
    }

    /**
     * Sends an RTCP packet, and schedules the next one
     */
    public void sendRTCPPacket() {
        int rc = receiveStreams.size();
        if (rc > 31) {
            rc = 31;
        }
        long delay = calculateRTCPDelay();
        long now = System.currentTimeMillis();

        // If now is too early to send a packet, wait until later
        if (now < (lastRTCPSendTime + delay)) {
            rtcpTimer.schedule(new RTCPTimerTask(this),
                    (lastRTCPSendTime + delay) - now);
        } else {

            // Reset the stats
            lastRTCPSendTime = System.currentTimeMillis();
            globalReceptionStats.resetBytesRecd();

            // Get the packet details
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(bytes);

            try {

                // Determine the packet type
                int packetType = RTCPPacket.PT_RR;
                int packetSize = (rc * RTCPFeedback.SIZE) + 8;
                if (localParticipant.getStreams().size() > 0) {
                    packetType = RTCPPacket.PT_SR;
                    packetSize += RTCPSenderInfo.SIZE;
                }

                // Add a RTCP header
                output.writeByte(0x80 | 0 | (rc & 0x1F));
                output.writeByte(packetType & 0xFF);
                output.writeShort(((packetSize) / 4) - 1);
                output.writeInt((int) (ssrc & 0xFFFFFFFF));

                // If we are a sender, add sender stats
                if (localParticipant.getStreams().size() > 0) {
                    packetType = RTCPPacket.PT_SR;
                    int senderIndex =
                        (int) (Math.random() *
                                localParticipant.getStreams().size());
                    RTPSendStream sendStream =
                        (RTPSendStream) localParticipant.getStreams().get(
                                senderIndex);
                    TransmissionStats stats =
                        sendStream.getSourceTransmissionStats();
                    long sendtime = sendStream.getLastSendTime();
                    sendtime += 22089888000000l;
                    long sendTimeSeconds = sendtime / 1000;
                    long sendTimeFractions =
                        ((sendtime - (sendTimeSeconds * 1000)) / 1000) *
                        (Integer.MAX_VALUE * 2);
                    long timestamp = sendStream.getLastTimestamp();
                    output.writeInt((int) (sendTimeSeconds & 0xFFFFFFFF));
                    output.writeInt((int) (sendTimeFractions & 0xFFFFFFFF));
                    output.writeInt((int) (timestamp & 0xFFFFFFFF));
                    output.writeInt(stats.getPDUTransmitted());
                    output.writeInt(stats.getBytesTransmitted());
                }

                // Add the receiver reports
                Vector streams = new Vector(receiveStreams.values());
                now = System.currentTimeMillis();
                for (int i = 0; i < rc; i++) {
                    int pos = (int) (Math.random() * streams.size());
                    RTPReceiveStream stream =
                        (RTPReceiveStream) streams.get(pos);
                    RTPReceptionStats stats =
                        (RTPReceptionStats) stream.getSourceReceptionStats();
                    RTPDataSource dataSource =
                        (RTPDataSource) stream.getDataSource();
                    RTPDataStream dataStream =
                        (RTPDataStream) dataSource.getStreams()[0];
                    long streamSSRC = stream.getSSRC();
                    int lossFraction = 0;
                    if (stats.getPDUProcessed() > 0) {
                        lossFraction = (256 * stats.getPDUlost()) /
                                        stats.getPDUProcessed();
                    }
                    long lastESequence =
                        (stats.getSequenceWrap() * RTPHeader.MAX_SEQUENCE) +
                        dataStream.getLastSequence();
                    long packetsExpected =
                        lastESequence - dataStream.getFirstSequence();
                    int cumulativePacketLoss = (int) (packetsExpected -
                        (stats.getPDUProcessed() + stats.getPDUDuplicate()));
                    long jitter =
                        ((RTPDataSource) stream.getDataSource()).getJitter();
                    long lsrMSW = stream.getLastSRReportTimestampMSW();
                    long lsrLSW = stream.getLastSRReportTimestampLSW();
                    long DLSR = ((now - stream.getLastSRReportTime()) *
                                    1000) / 65536;
                    if (stream.getLastSRReportTime() == 0) {
                        DLSR = 0;
                    }
                    output.writeInt((int) (streamSSRC & 0xFFFFFFFF));
                    output.writeByte(lossFraction & 0xFF);
                    output.writeByte((cumulativePacketLoss >> 16) & 0xFF);
                    output.writeShort((cumulativePacketLoss & 0xFFFF));
                    output.writeInt((int) (lastESequence & 0xFFFFFFFF));
                    output.writeInt((int) (jitter & 0xFFFFFFFF));
                    output.writeShort((int) (lsrMSW & 0xFFFF));
                    output.writeShort((int) ((lsrLSW >> 16) & 0xFFFF));
                    output.writeInt((int) (DLSR & 0xFFFFFFFF));
                    streams.remove(pos);
                }

                // Add the SDES items
                if (localParticipant.getStreams().size() == 0) {
                    Vector sdesItems =
                        localParticipant.getSourceDescription();
                    if (sdesItems.size() > 0) {
                        int padding = writeSDESHeader(output, 1,
                                localParticipant.getSdesSize());
                        writeSDES(output, sdesItems, ssrc);

                        // Add the sdes padding
                        for (int i = 0; i < padding; i++) {
                            output.writeByte(padding);
                        }
                    }
                } else {
                    Vector sendStreams = localParticipant.getStreams();
                    int totalSDES = 0;
                    for (int i = 0; i < sendStreams.size(); i++) {
                        totalSDES +=
                            ((RTPSendStream)
                                    sendStreams.get(i)).getSdesSize();
                    }
                    int padding = writeSDESHeader(output, sendStreams.size(),
                            totalSDES);
                    for (int i = 0; i < sendStreams.size(); i++) {
                        RTPSendStream sendStream =
                            (RTPSendStream) sendStreams.get(i);
                        writeSDES(output, sendStream.getSourceDescription(),
                                sendStream.getSSRC());
                    }

                    // Add the sdes padding
                    for (int i = 0; i < padding; i++) {
                        output.writeByte(padding);
                    }
                }


            } catch (IOException e) {
                logger.log(Level.WARNING, "" + e, e);
            }

            Iterator iterator = targets.values().iterator();
            while (iterator.hasNext()) {
                RTPConnector connector = (RTPConnector) iterator.next();
                try {
                    OutputDataStream outputStream =
                        connector.getControlOutputStream();
                    output.close();
                    bytes.close();
                    byte[] data = bytes.toByteArray();
                    outputStream.write(data, 0, data.length);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "" + e, e);
                }
            }

            // Prepare to send the next packet
            if (!done) {
                rtcpTimer.schedule(new RTCPTimerTask(this), delay);
            }
        }
    }

    // A timer task for sending RTCP packets
    private class RTCPTimerTask extends TimerTask {

        private RTPSessionMgr rtpSessionManager = null;

        private RTCPTimerTask(RTPSessionMgr rtpSessionManager) {
            this.rtpSessionManager = rtpSessionManager;
        }

        public void run() {
            rtpSessionManager.sendRTCPPacket();
        }
    }
}
