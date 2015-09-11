package io.block.api.crypto;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

public class Pin {

    private static final int DEFAULT_ITERATIONS_PER_ROUND = 1024;
    private static final int ROUND1_SIZE = 128;
    private static final int ROUND2_SIZE = 256;
    private static final char[] DEFAULT_SALT = "".toCharArray();

    public static byte[] toKey(String pin)
    {
        return toKey(pin, DEFAULT_ITERATIONS_PER_ROUND, DEFAULT_SALT);
    }

    public static byte[] toKey(String pin, Integer iterations, char[] salt) {
        if (iterations == null)
            iterations = DEFAULT_ITERATIONS_PER_ROUND;

        if (salt == null)
            salt = DEFAULT_SALT;

        byte[] pinBytes = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pin.toCharArray());
        byte[] pbeSalt = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(salt);

        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(pinBytes, pbeSalt, iterations);
        KeyParameter params = (KeyParameter) generator.generateDerivedParameters(ROUND1_SIZE);

        byte[] intResult = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(CryptoHelper.toHex(params.getKey()).toCharArray());

        generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(intResult, pbeSalt, iterations);
        params = (KeyParameter) generator.generateDerivedParameters(ROUND2_SIZE);

        return params.getKey();
    }
}
