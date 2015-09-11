package io.block.api.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import java.math.BigInteger;

public class CryptoHelper {

    /**
     * Convert byte array to a hex string representation
     * @param array input bytes
     * @return hex string
     */
    public static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * Converts a hex string representation of bytes into a byte array
     * @param s hex string
     * @return byte array
     */
    public static byte[] fromHex(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * AES-256 encrypt template
     * @param key The key to encrypt with
     * @param plain Plaintext to encrypt
     * @return A byte array with ciphertext
     * @throws CryptoException
     */
    public static byte[] encrypt(byte[] key, byte[] plain) throws CryptoException {
        try {
            return cipher(true, key, plain);
        } catch (InvalidCipherTextException e) {
            throw new CryptoException("Unexpected error while encrypting. Please file an issue report.", e);
        }
    }

    /**
     * AES-256 decrypt shorthand
     * @param key The key to decrypt with
     * @param cipherText The ciphertext to decrypt
     * @return A byte array with plaintext
     * @throws CryptoException
     */
    public static byte[] decrypt(byte[] key, byte[] cipherText) throws CryptoException {
        try {
            return cipher(false, key, cipherText);
        } catch (InvalidCipherTextException e) {
            throw new CryptoException("Unexpected error while decrypting. Please file an issue report.", e);
        }
    }

    /**
     * AES-256 encrypt/decrypt implementation
     * @param encrypt Boolean indicating whether to encrypt or decrypt
     * @param key The key to encrypt/decrypt with
     * @param data The plaintext to encrypt or ciphertext to decrypt
     * @return A byte array with plaintext (decrypt) or ciphertext (encrypt)
     * @throws InvalidCipherTextException
     */
    private static byte[] cipher(boolean encrypt, byte[] key, byte[] data)
            throws InvalidCipherTextException
    {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());
        CipherParameters aesKey = new KeyParameter(key);
        cipher.init(encrypt, aesKey);

        int minSize = cipher.getOutputSize(data.length);
        byte[] outBuf = new byte[minSize];

        int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
        int length2 = cipher.doFinal(outBuf, length1);
        int actualLength = length1 + length2;

        byte[] result = new byte[actualLength];
        System.arraycopy(outBuf, 0, result, 0, result.length);
        return result;
    }

}
