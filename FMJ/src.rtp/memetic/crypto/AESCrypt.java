/*
 * @(#)AESCrypt.java
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
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * Encrypts and decrypts data using AES algorithm
 * 
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class AESCrypt implements Crypt {

	private static final Logger logger = LoggerSingleton.logger;

    /**
     * The AES encryption type
     */
    public static final String TYPE = "AES";

    // The object to decrypt using
    private Cipher decrypter = null;

    // The object to encrypt using
    private Cipher encrypter = null;

    /**
     * Creates a new AESCrypt
     * 
     * @param password
     *            The encryption password
     */
    public AESCrypt(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Key = md5.digest(password.getBytes("UTF-8"));

            SecretKeySpec secretKey = new SecretKeySpec(md5Key, "AES");

            decrypter = Cipher.getInstance("AES/ECB/NoPadding");
            encrypter = Cipher.getInstance("AES/ECB/NoPadding");

            decrypter.init(Cipher.DECRYPT_MODE, secretKey);
            encrypter.init(Cipher.ENCRYPT_MODE, secretKey);

        } catch (Exception e) {
        	logger.log(Level.WARNING, "" + e, e);
        }
    }

    /**
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @see memetic.crypto.Crypt#encrypt(byte[], int, int, byte[], int)
     */
    public int encrypt(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws ShortBufferException,
            IllegalBlockSizeException, BadPaddingException {
        return encrypter.doFinal(input, offset, length, output, outOffset);
    }

    /**
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws ShortBufferException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @see memetic.crypto.Crypt#decrypt(byte[], int, int, byte[], int)
     */
    public int decrypt(byte[] input, int offset, int length, byte[] output,
            int outOffset) throws ShortBufferException,
            IllegalBlockSizeException, BadPaddingException {
        return decrypter.doFinal(input, offset, length, output, outOffset);
    }

    /**
     * @see memetic.crypto.Crypt#getEncryptOutputSize(int)
     */
    public int getEncryptOutputSize(int inputSize) {
        return encrypter.getOutputSize(inputSize);
    }

    /**
     * @see memetic.crypto.Crypt#getDecryptOutputSize(int)
     */
    public int getDecryptOutputSize(int inputSize) {
        return decrypter.getOutputSize(inputSize);
    }

    /**
     * @see memetic.crypto.Crypt#getBlockSize()
     */
    public int getBlockSize() {
        return encrypter.getBlockSize();
    }
}