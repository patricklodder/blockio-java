package io.block.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.block.api.crypto.CryptoHelper;
import io.block.api.crypto.ECKeyPair;
import io.block.api.crypto.EncryptedKey;
import io.block.api.crypto.Pin;
import io.block.api.model.Response;
import io.block.api.model.WithdrawSignRequest;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Base64;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class WithdrawalUtilsTest extends TestCase {

    public void testPinToKey() throws Exception {
        String pin = "73a8c60ad5974b5830b3d6cf19adb567";
        byte[] key = CryptoHelper.fromHex("9a292db5193b18aee4fcff41025d2abecbff45288d09660befe25ef2edd53523");
        byte[] generatedKey = Pin.toKey(pin);

        assertTrue(Arrays.equals(key, generatedKey));
    }


    public void testDecrypt() throws Exception {
        String input = "I'm a little tea pot short and stout";
        byte[] key = Base64.decode("0EeMOVtm5YihUYzdCNgleqIUWkwgvNBcRmr7M0t9GOc=");
        String enc = "7HTfNBYJjq09+vi8hTQhy6lCp3IHv5rztNnKCJ5RB7cSL+NjHrFVv1jl7qkxJsOg";

        EncryptedKey generatedEnc = new EncryptedKey(CryptoHelper.encrypt(key, input.getBytes()));
        byte[] generatedInput = CryptoHelper.decrypt(key, generatedEnc.getCipherText());
        byte[] generatedInput2 = CryptoHelper.decrypt(key, Base64.decode(enc));

        assertEquals(enc, generatedEnc.toBase64());
        assertTrue(generatedEnc.equals(Base64.decode(enc)));
        assertEquals(input, new String(generatedInput));
        assertEquals(input, new String(generatedInput2));

    }

    public void testGetPrivKey() throws Exception {
        byte[] pass = "block.io".getBytes("UTF-8");
        byte[] privBytes = CryptoHelper.fromHex("7a01628988d23fae697fa05fcdae5a82fe4f749aa9f24d35d23f81bee917dfc3");

        ECKeyPair keyPair = ECKeyPair.fromPassphrase(pass);

        assertTrue(keyPair.equals(privBytes));
    }

    public void testDerivePubKey() throws Exception {
        ECKeyPair keyPair = ECKeyPair.fromPassphrase("block.io".getBytes("UTF-8"));
        byte[] pubKey = CryptoHelper.fromHex("03359ac0aa241b1a40fcab68486f8a4b546ad3301d201c3645487093578592ec8f");
        byte[] generatedPub = keyPair.getPubKey();

        assertTrue(Arrays.equals(pubKey, generatedPub));
    }

    public void testSignData() throws Exception {
        String input = "695369676e65645468697344617461546861744973323536426974734c6f6e67";
        byte[] privBytes = CryptoHelper.fromHex("7a01628988d23fae697fa05fcdae5a82fe4f749aa9f24d35d23f81bee917dfc3");
        String signature = "304402205587dfc87c3227ad37b021c08c873ca4b1faada1a83f666d483711edb2f4f743022004ee40d9fe8dd03e6d42bfc7d0e53f75286125a591ed14b39265978ebf3eea36";

        byte[] generatedSignature = new ECKeyPair(privBytes).sign(CryptoHelper.fromHex(input));

        assertEquals(signature, CryptoHelper.toHex(generatedSignature));
    }

    public void testComplete() throws Exception {
        String dataToSign = "695369676e65645468697344617461546861744973323536426974734c6f6e67";
        String pin = "bc4779ff545bc04a54e6c32b7609a91b";
        String pass = "x1pjDH1ptfB4uKRAF4k9HThMEckOA0loBOrhmXOLt51iSHZm5qS9cX8HqDm6dGliByLbcgT+kmGDuNcVhwP/S2pqQ2LXkV2iERRQmq4E5rY=";

        String signedData = "3045022100ed12a43f75e4df23f1c53a4a90b91d94251bce359c720c43b5d88bbdfc3f23240220563d2ff61f4dfdae708e193673c168bf3c4b76e9279977c8766f9b162338d7c0";

        ECKeyPair keyPair = new EncryptedKey(Base64.decode(pass)).toECKey(pin);
        byte[] generatedSignedData = keyPair.sign(CryptoHelper.fromHex(dataToSign));

        assertEquals(signedData, CryptoHelper.toHex(generatedSignedData));
    }

    public void testFromFile() throws Exception {
        String request = new String( Files.readAllBytes(Paths.get(WithdrawalUtilsTest.class.getResource("sample_signing_request.json").toURI())));
        String response = new String( Files.readAllBytes(Paths.get(WithdrawalUtilsTest.class.getResource("sample_signing_response.json").toURI())));
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Response.ResponseWithdrawSignRequest signResquestR = gson.fromJson(request, Response.ResponseWithdrawSignRequest.class);
        WithdrawSignRequest signResquest = signResquestR.withdrawSignRequest;

        signResquest = WithdrawalUtils.signWithdrawalRequest(signResquest, "bc4779ff545bc04a54e6c32b7609a91b");

        assertEquals(response, gson.toJson(signResquest, WithdrawSignRequest.class));
    }
}