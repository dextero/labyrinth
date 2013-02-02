/*
 * @(#)RTPJitterBuffer.java
 * Created: 28-Oct-2005
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

import javax.media.Buffer;

/**
 * Implements a RTP Jitter Buffer
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPJitterBuffer {
    
    // The queue array
    private Buffer[] queue = null;
    
    // The next position array
    private int[] nextPos = null;
    
    // The previous position array
    private int[] prevPos = null;
    
    // The front of the queue
    private int firstElement = -1;
    
    // The back of the queue
    private int lastElement = 0;
    
    // The empty-position array
    private int[] emptyPos = null;
    
    // The maximum occupied position in the empty position array
    private int maxEmpty = 0;
    
    // Statistics to add to
    private RTPReceptionStats statistics = null;
    
    // The last sequence number sent
    private long lastSequenceSent = -1;
    
    /**
     * Creates a new RTPJitterBuffer
     * @param size The size of the buffer
     */
    public RTPJitterBuffer(int size) {
        queue = new Buffer[size];
        nextPos = new int[size];
        prevPos = new int[size];
        emptyPos = new int[size];
        for (int i = 1; i <= size; i++) {
            emptyPos[i - 1] = (size - i);
            nextPos[i - 1] = -1;
            prevPos[i - 1] = -1;
        }
        maxEmpty = size - 1;
    }
    
    /**
     * Sets the statistics to be added to by this buffer
     * @param statistics The statistics to add to
     */
    public void setStatistics(RTPReceptionStats statistics) {
        this.statistics = statistics;
    }
    
    /**
     * Adds a buffer of data to the buffer
     * @param data The data to add
     * @return true if the item was added, false if it was dropped
     */
    public synchronized boolean add(Buffer data) {
        
        // If the queue is empty, add the item to the start
        if (firstElement == -1) {
            int pos = emptyPos[maxEmpty];
            maxEmpty--;
            queue[pos] = data;
            nextPos[pos] = -1;
            prevPos[pos] = -1;
            firstElement = pos;
            lastElement = pos;
            return true;
        }
        
        // If there is no space on the buffer, drop the data
        if (maxEmpty < 0) {
            if (statistics != null) {
                statistics.addPDUInvalid();
            }
            return false;
        }
        
        // If this is less than the last position, and the last 
        // position is a high enough sequence number, add this element
        int pos = lastElement;
        long sequence = data.getSequenceNumber();
        if (((RTPHeader.MAX_SEQUENCE - queue[pos].getSequenceNumber()) < 100) 
                && sequence < queue[pos].getSequenceNumber()) {
            pos = lastElement;
        } else {
            // Search for the place in the queue where the packet goes
            while ((pos != -1) && 
                    ((queue[pos].getSequenceNumber() > sequence))) {
                int prev = prevPos[pos];
                
                // Don't bother looking at more nodes if the sequence gets 
                // higher
                if ((prev >= 0) && 
                        (queue[pos].getSequenceNumber() < 
                                queue[prev].getSequenceNumber())) {
                    pos = -1;
                } else {
                    pos = prev;
                }
            }
            if ((pos != lastElement) && (statistics != null)) {
                statistics.addPDUMisOrd();
            }

            // We have reached the front of the queue, 
            // so the packet should be dropped
            if (pos == -1) {
                if (statistics != null) {
                    statistics.addPDUInvalid();
                }
                return false;
            }
        
            // If the sequence number already exists, drop this repeated data
            if (queue[pos].getSequenceNumber() == sequence) {
                if (statistics != null) {
                    statistics.addPDUDuplicate();
                }
                return false;
            }
        }
        
        // Put the data in the next empty position
        int newPos = emptyPos[maxEmpty];
        int next = nextPos[pos];
        if (next != -1) {
            prevPos[next] = newPos;
        }
        nextPos[pos] = newPos;
        maxEmpty--;
        queue[newPos] = data;
        nextPos[newPos] = next;
        prevPos[newPos] = pos;
        if (next == -1) {
            lastElement = newPos;
        }
        return true;
    }
    
    /**
     * Removes the first buffer from the queue
     * @return the buffer removed
     */
    public synchronized Buffer remove() {
        if (firstElement != -1) {
            Buffer value = queue[firstElement];
            maxEmpty++;
            emptyPos[maxEmpty] = firstElement;
            firstElement = nextPos[firstElement];
            if (firstElement != -1) {
                prevPos[firstElement] = -1;
            }
            if (statistics != null) {
                statistics.addPDUProcessed();
            }
            long seq = value.getSequenceNumber();
            if (lastSequenceSent == -1) {
                lastSequenceSent = seq;
            } else if ((seq < lastSequenceSent) && (seq != 0) && 
                    (lastSequenceSent != RTPHeader.MAX_SEQUENCE)) {
                if (statistics != null) {
                    statistics.addPDULost((int) (seq + RTPHeader.MAX_SEQUENCE -
                            lastSequenceSent));
                }
            } else if (((lastSequenceSent + 1) != seq)) {
                if (statistics != null) {
                    statistics.addPDULost((int) (seq - lastSequenceSent));
                }
            }
            lastSequenceSent = seq;
            return value;
            
        }
        return null;
    }
    
    /**
     * Returns the first buffer sequence in the queue without removing it
     */
    public synchronized long peekTimeStamp() {
        if (firstElement != -1) {
            return queue[firstElement].getTimeStamp();
        }
        return -1;
    }
    
    /**
     * Returns the current number of elements on the queue
     */
    public synchronized int size() {
        return (queue.length - maxEmpty) - 1;
    }
    
}
