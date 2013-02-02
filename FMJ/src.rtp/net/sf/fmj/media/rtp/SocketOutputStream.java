/*
 * @(#)SocketOutputStream.java
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.media.rtp.OutputDataStream;

/**
 * An output Datagram Socket for JMF
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class SocketOutputStream implements OutputDataStream {
    
    // The datagram socket of the stream
    private DatagramSocket socket = null;
    
    // The address to send items to
    private InetAddress sendAddress = null;
    
    // The port to send items to
    private int sendPort = 0;
    
    /**
     * Creates a new SocketOutputStream
     * @param socket The datagram socket to adapt
     * @param sendAddress The address to send packets to
     * @param sendPort The port to send packets to
     */
    public SocketOutputStream(DatagramSocket socket, InetAddress sendAddress,
            int sendPort) {
        this.socket = socket;
        this.sendAddress = sendAddress;
        this.sendPort = sendPort;
    }

    public int write(byte[] buffer, int offset, int length) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, offset, length,
                    sendAddress, sendPort);
            socket.send(packet);
        } catch (Exception e) {
            return -1;
        }
        return length;
    }

}
