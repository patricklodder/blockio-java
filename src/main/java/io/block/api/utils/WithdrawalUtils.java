package io.block.api.utils;

import io.block.api.crypto.CryptoException;
import io.block.api.crypto.CryptoHelper;
import io.block.api.crypto.ECKeyPair;
import io.block.api.crypto.EncryptedKey;
import io.block.api.model.Input;
import io.block.api.model.Signer;
import io.block.api.model.WithdrawSignRequest;
import org.bouncycastle.util.encoders.Base64;

import java.util.Arrays;

public class WithdrawalUtils {

    public static WithdrawSignRequest signWithdrawalRequest(WithdrawSignRequest request, String secretPin) throws BlockIOException {
        EncryptedKey encryptedKey = new EncryptedKey(Base64.decode(request.encryptedPassphrase.passphrase));
        ECKeyPair ECKeyPair = encryptedKey.toECKey(secretPin);
        return signWithdrawalRequest(request, ECKeyPair);
    }

    public static WithdrawSignRequest signWithdrawalRequest(WithdrawSignRequest request, ECKeyPair ECKeyPair) throws CryptoException
    {
        byte[] pubKey = ECKeyPair.getPubKey();

        for (Input input : request.inputs) {
            for (Signer signer : input.signers) {
                if (Arrays.equals(pubKey, CryptoHelper.fromHex(signer.signerPubKey))) {
                    signer.signedData = CryptoHelper.toHex(ECKeyPair.sign(CryptoHelper.fromHex(input.dataToSign)));
                }
            }
        }

        return request;
    }

}