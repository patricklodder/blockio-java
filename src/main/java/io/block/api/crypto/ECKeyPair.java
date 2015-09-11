package io.block.api.crypto;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * An ECDSA private/public keypair using secp256k1
 */
public class ECKeyPair {

    private static final X9ECParameters CURVE = SECNamedCurves.getByName("secp256k1");
    private static final ECDomainParameters EC_PARAMS = new ECDomainParameters(CURVE.getCurve(), CURVE.getG(), CURVE.getN(), CURVE.getH());

    private final byte[] privKey;

    public ECKeyPair(byte[] privKey)
    {
        this.privKey = privKey;
    }

    /**
     * Creates an ECKeyPair from a passphrase
     * @param passphrase The passphrase to generate the keypair from
     * @return An {@link io.block.api.crypto.ECKeyPair}
     */
    public static ECKeyPair fromPassphrase(byte[] passphrase)
    {
        SHA256Digest digest = new SHA256Digest();
        byte [] privBytes = new byte[digest.getDigestSize()];
        digest.update(passphrase, 0, passphrase.length);
        digest.doFinal(privBytes, 0);
        return new ECKeyPair(privBytes);
    }

    /**
     * Calculates the public key from the private key
     * @return A byte array containing the public key
     */
    public byte[] getPubKey()
    {
        BigInteger priv = new BigInteger(1, privKey);
        return EC_PARAMS.getG().multiply(priv).getEncoded(true);
    }

    /**
     * Sign data with this keypair
     * @param data A byte array with data to sign
     * @return A DER encoded, BIP62-compatible signature
     * @throws CryptoException
     */
    public byte[] sign(byte[] data) throws CryptoException
    {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        BigInteger d = new BigInteger(1, privKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(d, EC_PARAMS);

        signer.init(true, privateKeyParameters);
        BigInteger[] sigs = signer.generateSignature(data);

        BigInteger r = sigs[0];
        BigInteger s = sigs[1];

        // BIP62: "S must be less than or equal to half of the Group Order N"
        BigInteger n = CURVE.getN();
        BigInteger overTwo = n.shiftRight(1);
        while (s.compareTo(overTwo) == 1)
            s = n.subtract(s);

        return encodeDERSig(r, s);
    }

    /**
     * Tests if a byte array matches the privkey we have in memory
     * @param bytes bytes to check against
     * @return boolean
     */
    public boolean equals(byte[] bytes)
    {
        return Arrays.equals(bytes, privKey);
    }

    /**
     * Encode signature components R and S as DER
     * @param r The r component
     * @param s The s component
     * @return A byte array containing the DER encoded signature
     * @throws CryptoException
     */
    private byte[] encodeDERSig(BigInteger r, BigInteger s) throws CryptoException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(r));
            seq.addObject(new ASN1Integer(s));
            seq.close();
        } catch (IOException e) {
            // Cannot happen, according to @langerhans ;)
            throw new CryptoException("Error occurred while encoding signature, please file an issue report", e);
        }

        return bos.toByteArray();
    }

}
