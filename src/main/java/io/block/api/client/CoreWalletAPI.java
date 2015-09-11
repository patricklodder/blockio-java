package io.block.api.client;

import io.block.api.model.*;
import io.block.api.utils.BlockIOException;

/**
 * Generic members that are shared between all wallet implementations
 */
public interface CoreWalletAPI extends BasicAPIClient {

    /**
     * Different types of string parameters that are used to query transactions
     */
    enum ParamType {
        ADDRS,
        LABELS,
        USERIDS
    }

    NewAddress getNewAddress(String label) throws BlockIOException;

    AccountAddresses getAccountAddresses() throws BlockIOException;
    AddressBalances getAddressBalancesByAddress(String[] addresses) throws BlockIOException;
    AddressBalances getAddressBalancesByLabels(String[] labels) throws BlockIOException;
    AddressByLabel getAddressByLabel(String label) throws BlockIOException;

    TransactionsReceived getTransactionsReceived(String beforeTX) throws BlockIOException;
    TransactionsReceived getTransactionsReceivedByAddress(String[] addresses, String beforeTX) throws BlockIOException;
    TransactionsReceived getTransactionsReceivedByLabel(String[] labels, String beforeTX) throws BlockIOException;
    TransactionsReceived getTransactionsReceivedByUserID(String[] userIDs, String beforeTX) throws BlockIOException;

    TransactionsSent getTransactionsSent(String beforeTX) throws BlockIOException;
    TransactionsSent getTransactionsSentByAddress(String[] addresses, String beforeTX) throws BlockIOException;
    TransactionsSent getTransactionsSentByLabel(String[] labels, String beforeTX) throws BlockIOException;
    TransactionsSent getTransactionsSentByUserID(String[] userIDs, String beforeTX) throws BlockIOException;
}
