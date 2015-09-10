package io.block.api.utils;

import sun.nio.ch.Net;

public enum Network {
    BTC,
    LTC,
    DOGE,
    BTCTEST,
    LTCTEST,
    DOGETEST;

    public static class NetworkParseException extends BlockIOException {
        public NetworkParseException() {
            super("Unable to parse network, please file an issue");
        }
    }

    public static Network getNetwork(String name) throws NetworkParseException
    {
        for (Network net : Network.values())
        {
            if (net.name().equals(name))
                return net;
        }

        throw new NetworkParseException();
    }
}
