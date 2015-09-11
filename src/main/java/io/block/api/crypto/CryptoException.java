package io.block.api.crypto;

import io.block.api.utils.BlockIOClientException;

public class CryptoException extends BlockIOClientException {
    public CryptoException() {}
    public CryptoException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
