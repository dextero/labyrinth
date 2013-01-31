/*
 * @(#)SocketInputStream.java
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;

/**
 * A DatagramSocket Input Stream Adapter
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class SimpleSocketInputStream extends Thread implements PushSourceStream {
    
    // The datagram socket
    private DatagramSocket socket = null;
    
    // True if the receiving is finished
    private boolean done = false;
    
    // The transfer handler
    private SourceTransferHandler handler = null;
    
    // True if data has been read
    private boolean dataRead = false;
    
    /**
     * Creates a new SocketInputStream
     * @param socket The socket to handle
     */
    public SimpleSocketInputStream(DatagramSocket socket) {
        this.socket = socket;
    }

    public synchronized int read(byte[] buffer, int offset, int length) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, offset, length);
            socket.receive(packet);
            synchronized (this) {
                dataRead = true;
                notify();
            }
            return packet.getLength();
        } catch (IOException e) {
            synchronized (this) {
                notify();
            }
            return 0;
        }
    }

    public int getMinimumTransferSize() {
        // There is currently no way to get the MTU in Java, so return a 
        // suitable large value
        return 2048;
    }

    public synchronized void setTransferHandler(SourceTransferHandler transferHandler) {
        this.handler = transferHandler;
        if (handler != null) {
            dataRead = true;
            notify();
        }
    }

    public ContentDescriptor getContentDescriptor() {
        return null;
    }

    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return done;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String controlType) {
        return null;
    }
    
    public void start() {
        super.start();
        if (handler != null) {
            dataRead = true;
            notify();
        }
    }
    
    public void run() {
        while (!done) {
            synchronized (this) {
                while (!dataRead && !done) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
                dataRead = false;
                if (!done && (handler != null)) {
                    handler.transferData(this);
                }
            }
        }
    }
    
    /**
     * Stops the socket
     */
    public void kill() {
        done = true;
        socket.close();
    }
}
