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
public class SocketInputStream extends Thread implements PushSourceStream {
    
    // The default receive buffer size
    private static final int BUFFER_SIZE = 2048;
    
    // The default number of buffers
    private static final int NO_BUFFERS = 8;
    
    // The datagram socket
    private DatagramSocket socket = null;
    
    // The buffer for the received data
    private byte [][] buffer = new byte[NO_BUFFERS][BUFFER_SIZE];
    
    // The size of the buffers
    private int [] bufferSize = new int[NO_BUFFERS];
    
    // The number of buffers used
    private int buffersUsed = 0;
    
    // The current receive buffer position
    private int currentReceive = 0;
    
    // The current send buffer position
    private int currentSend = 0;
    
    // True if the receiving is finished
    private boolean done = false;
    
    // The transfer handler
    private Handler handler = null;
    
    /**
     * Creates a new SocketInputStream
     * @param socket The socket to handle
     */
    public SocketInputStream(DatagramSocket socket) {
        this.socket = socket;
    }

    public synchronized int read(byte[] buffer, int offset, int length) {
        if (buffersUsed > 0) {
            int maxSize = length;
            if (bufferSize[currentSend] < length) {
                maxSize = bufferSize[currentSend];
            }
            System.arraycopy(this.buffer[currentSend], 0, buffer, offset, 
                    maxSize);
            currentSend = (currentSend + 1) % this.buffer.length;
            buffersUsed--;
            return maxSize;
        }
        return 0;
    }

    public int getMinimumTransferSize() {
        if (buffersUsed > 0) {
            return bufferSize[currentSend];
        }
        return 0;
    }

    public void setTransferHandler(SourceTransferHandler transferHandler) {
        this.handler = new Handler(transferHandler, this);
        handler.start();
    }

    public ContentDescriptor getContentDescriptor() {
        return null;
    }

    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return done && (buffersUsed == 0);
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String controlType) {
        return null;
    }
    
    private int getQueueLength() {
        return buffersUsed;
    }
    
    public void run() {
        while (!done) {
            synchronized (this) {
                try {
                    if (buffersUsed < buffer.length) {
                        DatagramPacket packet = 
                            new DatagramPacket(buffer[currentReceive],
                                    0, buffer[currentReceive].length);
                        socket.receive(packet);
                        bufferSize[currentReceive] = packet.getLength();
                        currentReceive = (currentReceive + 1) % buffer.length;
                        buffersUsed++;
                        if (handler != null) {
                            handler.dataReady();
                        }
                    }
                } catch (IOException e) {
                    // Do Nothing
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
    
    private class Handler extends Thread {
        private SourceTransferHandler handle = null;
        
        private SocketInputStream stream = null;
        
        private boolean dataReady = false;
        
        /**
         * Creates a new Handler
         * @param handler The handler of data transfer
         * @param stream The stream
         */
        public Handler(SourceTransferHandler handler, 
                SocketInputStream stream) {
            this.handle = handler;
            this.stream = stream;
        }
        
        /**
         * Notifys the handler that data is ready
         */
        public synchronized void dataReady() {
            dataReady = true;
            notify();
        }
        
        public void run() {
            while (!stream.endOfStream()) {
                while (!dataReady && (stream.getQueueLength() == 0)) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                }
                dataReady = false;
                if (!stream.endOfStream()) {
                    handle.transferData(stream);
                }
            }
        }
    }
}
