package io.block.api.client;

import io.block.api.model.*;
import io.block.api.utils.BlockIOException;

import java.util.Map;

/**
 * Interface specific to the default 2-of-2 wallet API
 */
public interface WalletAPI extends CoreWalletAPI {
    AccountBalance getAccountBalance() throws BlockIOException;

    Withdrawal withdrawToAddress(String address, double amount, String secretPin) throws BlockIOException;
    Withdrawal withdraw(String[] sources, ParamType sourceType, Map<String, Double> targetsAndAmounts, ParamType targetType, String secretPin) throws BlockIOException;
}
