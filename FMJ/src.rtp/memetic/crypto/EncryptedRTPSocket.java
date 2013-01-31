/*
 * @(#)EncryptedRTPSocket.java
 * Created: 11-Aug-2005
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

package memetic.crypto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

/**
 * An Encrpyted RTP Datagram Socket
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class EncryptedRTPSocket extends MulticastSocket {

    // Encryption algorithm
    private RTPCrypt crypt = null;

    // True if this is an RTP socket
    private boolean isRTP = true;

    /**
     * Creates a new EncryptedRTPSocket bound to the specified port
     * 
     * @param crypt
     *            The encryption or null for none
     * @param port
     *            The port to bind to (RTP if even, RTCP if odd)
     * 
     * @throws IOException
     */
    public EncryptedRTPSocket(RTPCrypt crypt, int port) throws IOException {
        super(port);
        this.crypt = crypt;
        isRTP = ((port % 2) == 0);
    }

    /**
     * Creates a new EncryptedRTPSocket bound to the specified port
     * 
     * @param crypt
     *            The encryption or null for none
     * @param port
     *            The port to bind to (RTP if even, RTCP if odd)
     * @param isRTP
     *            True if the port is RTP, false if it is RTCP
     * 
     * @throws IOException
     */
    public EncryptedRTPSocket(RTPCrypt crypt, InetAddress addr, int port, 
            boolean isRTP)
            throws IOException {
        super(new InetSocketAddress(addr, port));
        this.crypt = crypt;
        this.isRTP = isRTP;
    }
    
    /**
     * Creates a new EncryptedRTPSocket bound to the specified port
     * 
     * @param crypt
     *            The encryption or null for none
     * @param port
     *            The port to bind to (RTP if even, RTCP if odd)
     * 
     * @throws IOException
     */
    public EncryptedRTPSocket(RTPCrypt crypt, InetAddress addr, int port) 
            throws IOException {
        super(new InetSocketAddress(addr, port));
        this.crypt = crypt;
        isRTP = ((port % 2) == 0);
    }

    /**
     * Creates a new EncryptedRTPSocket bound to the specified port
     * 
     * @param crypt
     *            The encryption or null for none
     * @param port
     *            The port to bind to (RTP if even, RTCP if odd)
     * @param isRTP
     *            True if the port is RTP, false if it is RTCP
     * 
     * @throws IOException
     */
    public EncryptedRTPSocket(RTPCrypt crypt, int port, boolean isRTP)
            throws IOException {
        super(port);
        this.crypt = crypt;
        this.isRTP = isRTP;
    }

    /**
     * @see java.net.DatagramSocket#send(java.net.DatagramPacket)
     */
    public void send(DatagramPacket p) throws IOException {
        if (crypt != null) {
            try {
                if (isRTP) {
                    byte[] out = new byte[crypt.getEncryptOutputSize(p
                            .getLength())];
                    int length = crypt.encryptData(p.getData(), p.getOffset(),
                            p.getLength(), out, 0);
                    p.setData(out, 0, length);
                    super.send(p);
                } else {
                    byte[] out = new byte[crypt.getEncryptOutputSize(p
                            .getLength())];
                    int length = crypt.encryptCtrl(p.getData(), p.getOffset(),
                            p.getLength(), out, 0);
                    p.setData(out, 0, length);
                    super.send(p);
                }
            } catch (InvalidKeyException e) {
                throw new IOException(e.getMessage());
            } catch (ShortBufferException e) {
                throw new IOException(e.getMessage());
            } catch (IllegalBlockSizeException e) {
                throw new IOException(e.getMessage());
            } catch (BadPaddingException e) {
                throw new IOException(e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
                throw new IOException(e.getMessage());
            }
        } else {
            super.send(p);
        }
    }

    /**
     * @see java.net.DatagramSocket#receive(java.net.DatagramPacket)
     */
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);

        if (crypt != null) {
            try {
                if ((p.getLength() % crypt.getBlockSize()) == 0) {
                    if (isRTP) {
                        byte[] out = new byte[crypt.getDecryptOutputSize(p
                                .getLength())];
                        int length = crypt.decryptData(p.getData(), 
                                p.getOffset(), p.getLength(), out, 0);
                        if (length > 0) {
                            int maxlen = p.getLength();
                            if (length < p.getLength()) {
                            	maxlen = length;
                            }
                            System.arraycopy(out, 0, p.getData(), p.getOffset(), 
                            		maxlen);
                            p.setLength(maxlen);
                        } else {
                            p.setLength(0);
                        }
                    } else {
                        byte[] out = new byte[crypt.getDecryptOutputSize(p
                                .getLength())];
                        int length = crypt.decryptCtrl(p.getData(), 
                                p.getOffset(), p.getLength(), out, 0);
                        if (length > 0) {
                            int maxlen = p.getLength();
                            if (length < p.getLength()) {
                            	maxlen = length;
                            }
                            System.arraycopy(out, 0, p.getData(), p.getOffset(), 
                            		maxlen);
                            p.setLength(maxlen);
                        } else {
                            p.setLength(0);
                        }
                    }
                }
            } catch (InvalidKeyException e) {
                throw new IOException(e.getMessage());
            } catch (ShortBufferException e) {
                throw new IOException(e.getMessage());
            } catch (IllegalBlockSizeException e) {
                throw new IOException(e.getMessage());
            } catch (BadPaddingException e) {
                throw new IOException(e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
                throw new IOException(e.getMessage());
            }
        }

    }
    
    /**
     * Sets the current encryption on the socket
     * @param crypt The encryption algorithm, or null for none
     */
    public void setEncryption(RTPCrypt crypt) {
        this.crypt = crypt;
    }
}
