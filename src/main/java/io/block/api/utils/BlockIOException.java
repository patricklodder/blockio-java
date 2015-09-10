package io.block.api.utils;

public abstract class BlockIOException extends Exception {

    public BlockIOException() {

    }

    public BlockIOException(String message) {
        super(message);
    }
}
