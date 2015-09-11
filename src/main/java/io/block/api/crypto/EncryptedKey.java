package io.block.api.crypto;

import org.bouncycastle.util.encoders.Base64;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Handling of remotely stored encrypted privkeys
 */
public class EncryptedKey {

    private final byte[] cipherText;

    /**
     * @param cipherText A byte array containing the ciphertext
     */
    public EncryptedKey(byte[] cipherText)
    {
        this.cipherText = cipherText;
    }

    /**
     * @param base64CipherText A base64 encoded string containing the ciphertext
     */
    public EncryptedKey(String base64CipherText)
    {
        this.cipherText = Base64.decode(base64CipherText);
    }

    /**
     * Creates an EncryptedKey from a plaintext passphrase
     * @param plaintext The passphrase to encrypt
     * @param pin The pin to encrypt with
     * @return An {@link io.block.api.crypto.EncryptedKey}
     * @throws CryptoException
     */
    public static EncryptedKey fromPlainText(byte[] plaintext, String pin)
            throws CryptoException
    {
        byte[] aesKey = Pin.toKey(pin);
        byte[] cipherText = CryptoHelper.encrypt(plaintext, aesKey);
        return new EncryptedKey(cipherText);
    }

    /**
     * Decrypt into an ECKeyPair
     * @param pin The pin to decrypt with
     * @return An {@link io.block.api.crypto.ECKeyPair}
     * @throws CryptoException
     */
    public ECKeyPair toECKey(String pin)
            throws CryptoException
    {
        byte[] aesKey = Pin.toKey(pin);
        byte[] passphrase = CryptoHelper.decrypt(aesKey, cipherText);
        ECKeyPair keyPair;

        try {
           keyPair = ECKeyPair.fromPassphrase(CryptoHelper.fromHex(new String(passphrase, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new CryptoException("Your system does not seem to support UTF-8 encoding, cannot extract key", e);
        }

        return keyPair;
    }

    /**
     * Tests if a byte array matches our ciphertext
     * @param bytes The byte array to test against
     * @return boolean
     */
    public boolean equals(byte[] bytes)
    {
        return Arrays.equals(bytes, this.cipherText);
    }

    /**
     * Exposes the ciphertext as byte array
     * @return A byte array containing the ciphertext
     */
    public byte[] getCipherText()
    {
        return cipherText;
    }

    /**
     * Exposes the ciphertext as Base64
     * @return A {@link java.lang.String} containing the base64 encoded ciphertext
     */
    public String toBase64()
    {
        return Base64.toBase64String(cipherText);
    }

}

