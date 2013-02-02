/*
 * @(#)RTPCrypt.java
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

/**
 * Performs RTP Encryption using a given encryption standard
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RTPCrypt {

    // The packet length offset from the start of an RTCP packet
    private static final int PACKET_LENGTH_START = 2;

    // The padding field in the RTCP packet
    private static final int PADDING = 0x20;

    // A crypt implementation
    private Crypt crypter = null;

    /**
     * Creates a new RTPCrypt
     * 
     * @param crypter
     *            The encryption standard to use
     */
    public RTPCrypt(Crypt crypter) {
        this.crypter = crypter;
    }

    /**
     * Encrypts the given RTCP data
     * 
     * @param input
     * @param offset
     * @param length
     * @param output
     * @param outOffset
     * @return The length of the output stored in the output buffer
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidKeyException
     */
    public int encryptCtrl(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws InvalidKeyException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        // Write four bytes of random data to the start of the packet
        int blockSize = crypter.getBlockSize();
        int padding = blockSize - ((length + 4) % blockSize);
        int sendLength = length + 4 + padding;
        byte[] sendData = new byte[sendLength];
        System.arraycopy(input, offset, sendData, 4, length);
        sendData[0] = (byte) (Math.random() * 255);
        sendData[1] = (byte) (Math.random() * 255);
        sendData[2] = (byte) (Math.random() * 255);
        sendData[3] = (byte) (Math.random() * 255);
        int sendOffset = 0;

        // Add padding to the correct block size
        if (padding != 0) {

            // Find the Last RTCP packet in the group (starting
            // after the 4-byte padding
            int position = 4;
            int lastPacket = 0;
            int packetLength = 0;
            while (position < (sendLength - padding)) {
                lastPacket = position;
                packetLength = 
                    ((sendData[position + PACKET_LENGTH_START] << 8) & 0xFF00)
                        | (sendData[position + PACKET_LENGTH_START + 1] & 0xFF);
                packetLength += 1;
                packetLength *= 4;
                position += packetLength;
            }
            
            // Add the padding
            boolean padded = (sendData[lastPacket] & PADDING) != 0;
            int paddingLength = 0;
            if (padded) {
                paddingLength = sendData[sendLength - 1] & 0xFF;
                sendData[sendLength - 1] = 0;
            }
            paddingLength += padding;
            packetLength += padding;
            packetLength /= 4;
            packetLength -= 1;

            // Set the parameters of the packet to reflect the
            // changes
            sendData[lastPacket] |= PADDING;
            for (int i = sendLength - paddingLength; i < sendLength; i++) {
                sendData[i] = (byte) (paddingLength & 0xFF);
            }
            sendData[lastPacket + PACKET_LENGTH_START] = 
                (byte) ((packetLength >> 8) & 0XFF);
            sendData[lastPacket + PACKET_LENGTH_START + 1] = 
                (byte) (packetLength & 0XFF);
        }

        // Encode the data
        return crypter.encrypt(sendData, sendOffset, sendLength, output,
                outOffset);
    }

    /**
     * Encrypts the given RTP data
     * 
     * @param input
     * @param offset
     * @param length
     * @param output
     * @param outOffset
     * @return The length of the output stored in the output buffer
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidKeyException
     */
    public int encryptData(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws InvalidKeyException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        byte[] sendData = input;
        int sendOffset = offset;
        int sendLength = length;

        // Add any padding
        int blockSize = crypter.getBlockSize();
        int padding = sendLength % blockSize;
        if (padding != 0) {

            // Create an array big enough for the padding
            padding = blockSize - padding;
            byte[] oldSendData = sendData;
            sendData = new byte[sendLength + padding];
            System.arraycopy(oldSendData, sendOffset, sendData, 0, sendLength);

            // Get the current padding stats
            boolean padded = (sendData[0] & PADDING) != 0;
            int paddingLength = 0;
            if (padded) {
                paddingLength = sendData[sendLength - 1] & 0xFF;
                sendData[sendLength - 1] = 0;
            }

            // Add the padding
            paddingLength += padding;
            sendLength += padding;

            // Alter the packet to reflect the changes
            sendData[0] |= PADDING;
            for (int i = sendLength - paddingLength; i < sendLength; i++) {
                sendData[i] = (byte) (paddingLength & 0xFF);
            }
        }

        // Encode the data
        return crypter.encrypt(sendData, sendOffset, sendLength, output,
                outOffset);
    }

    /**
     * Decrypts the given RTCP data
     * 
     * @param input
     * @param offset
     * @param length
     * @param output
     * @param outOffset
     * @return The length of the output stored in the output buffer
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidKeyException
     */
    public int decryptCtrl(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws InvalidKeyException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        int readLength = crypter.decrypt(input, offset, length, output,
                outOffset);

        // Remove the additional four bytes
        readLength -= 4;
        for (int i = outOffset; i < readLength; i++) {
            output[outOffset + i] = output[outOffset + i + 4];
        }

        // Find the last packet
        int position = outOffset;
        int lastPacket = 0;
        int packetLength = 0;
        while (position < (outOffset + readLength)) {
            lastPacket = position;
            packetLength = 
                ((output[position + PACKET_LENGTH_START] << 8) & 0xFF00)
                    | (output[position + PACKET_LENGTH_START] & 0xFF);
            packetLength += 1;
            packetLength *= 4;
            position += packetLength;
        }

        // Remove the padding from the packet
        boolean padded = (output[lastPacket] & PADDING) != 0;
        int paddingLength = 0;
        if (padded) {
            paddingLength = output[outOffset + readLength - 1] & 0xFF;
            output[lastPacket] = (byte) (output[lastPacket] & (~PADDING));
            packetLength -= paddingLength;
            packetLength /= 4;
            packetLength -= 1;
            output[lastPacket + 2] = (byte) ((packetLength >> 8) & 0XFF);
            output[lastPacket + 3] = (byte) (packetLength & 0XFF);
        }
        readLength -= paddingLength;
        return readLength;
    }

    /**
     * Decrypts the given RTP data
     * 
     * @param input
     * @param offset
     * @param length
     * @param output
     * @param outOffset
     * @return The length of the output stored in the output buffer
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidKeyException
     */
    public int decryptData(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws InvalidKeyException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        int readLength = crypter.decrypt(input, offset, length, output,
                outOffset);

        // Remove the padding
        boolean padded = (output[outOffset] & PADDING) != 0;
        int paddingLength = 0;
        if (padded) {
            paddingLength = output[outOffset + readLength - 1] & 0xFF;
            output[outOffset] = (byte) (output[outOffset] & (~PADDING));
        }
        readLength -= paddingLength;
        return readLength;
    }

    /**
     * @see memetic.crypto.Crypt#getEncryptOutputSize(int)
     */
    public int getEncryptOutputSize(int inputSize) {
        inputSize += 4;
        int padding = getBlockSize() - (inputSize % getBlockSize());
        return crypter.getEncryptOutputSize(inputSize + padding);
    }

    /**
     * @see memetic.crypto.Crypt#getDecryptOutputSize(int)
     */
    public int getDecryptOutputSize(int inputSize) {
        return crypter.getDecryptOutputSize(inputSize);
    }

    /**
     * @see memetic.crypto.Crypt#getBlockSize()
     */
    public int getBlockSize() {
        return crypter.getBlockSize();
    }
}
