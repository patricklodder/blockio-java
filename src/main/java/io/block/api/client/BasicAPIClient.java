package io.block.api.client;

import io.block.api.model.GreenAddresses;
import io.block.api.model.GreenTransactions;
import io.block.api.model.Prices;
import io.block.api.utils.BlockIOException;
import io.block.api.utils.Network;

/**
 * Defines basic operations against the Block.IO API that are not related to specific wallet implementations
 */
public interface BasicAPIClient {

    Prices getPrices(String baseCurrency) throws BlockIOException;

    GreenAddresses isGreenAddress(String[] addresses) throws BlockIOException;
    GreenTransactions isGreenTransaction(String[] txIDs) throws BlockIOException;

    Network getNetwork() throws BlockIOException;
    boolean validateKey() throws BlockIOException;

}
