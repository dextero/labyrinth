/*
 * @(#)Crypt.java
 * Created: 11-Jul-2005
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
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public interface Crypt {

    /**
     * Encrypts the input
     * 
     * @param input
     *            The input to be encrypted
     * @param offset
     *            The offset into the input buffer where the input starts
     * @param length
     *            The length of the input
     * @param output
     *            The output buffer
     * @param outOffset
     *            The offset into the output buffer where to store the output
     * 
     * @return The length of the output stored in the output buffer
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public int encrypt(byte[] input, int offset, int length, byte output[],
            int outOffset) throws ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException;

    /**
     * Decrypts the input
     * 
     * @param input
     *            The input to be decrypted
     * @param offset
     *            The offset into the input buffer where the input starts
     * @param length
     *            The length of the input
     * @param output
     *            The output buffer
     * @param outOffset
     *            The offset into the output buffer where to store the output
     * 
     * @return The length of the output stored in the output buffer
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public int decrypt(byte[] input, int offset, int length, byte output[],
            int outOffset) throws ShortBufferException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException;

    /**
     * Returns the maximum size the output buffer needs to be
     * 
     * @param inputSize
     *            The size of the input to be encrypted
     * @return The maximum size of the output buffer
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public int getEncryptOutputSize(int inputSize);

    /**
     * Returns the maximum size the output buffer needs to be
     * 
     * @param inputSize
     *            The size of the input to be decrypted
     * @return The maximum size of the output buffer
     */
    public int getDecryptOutputSize(int inputSize);

    /**
     * Returns the size of the block of the encryption or decryption
     * 
     * @return The block size (in bytes)
     */
    public int getBlockSize();
}