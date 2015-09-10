package io.block.api.utils;

public class BlockIOServerException extends BlockIOException {
    public BlockIOServerException () {}
    public BlockIOServerException (String message) {
        super(message);
    }
}
