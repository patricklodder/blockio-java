package io.block.api.utils;

public class BlockIOClientException extends BlockIOException {
    public BlockIOClientException () {}
    public BlockIOClientException (String message) {
        super(message);
    }
    public BlockIOClientException (String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
