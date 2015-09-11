package io.block.api.client;

import com.google.gson.Gson;
import io.block.api.com.CommunicationProtocol;
import io.block.api.model.*;
import io.block.api.utils.*;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default 2-of-2 wallet API implementation
 */
public class WalletClient extends BasicAPIClientImpl implements WalletAPI {

    public WalletClient(String apiKey, CommunicationProtocol communicationProtocol) {
        super(apiKey, communicationProtocol);
    }

    /**
     * Requests the balance of the account associated with this clients' API key
     * @return An {@link io.block.api.model.AccountBalance} object containing the balances
     * @throws BlockIOException
     */
    public AccountBalance getAccountBalance() throws BlockIOException {
        Response.ResponseAccountBalance response = (Response.ResponseAccountBalance) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_ACCOUNT_BALANCE, null, Response.ResponseAccountBalance.class);
        return response.accountBalance;
    }

    /**
     * Requests the creation of a new address for the account associated with the clients' API key
     * @param label Optional label for the new address. null or "" for random label
     * @return A {@link io.block.api.model.NewAddress} object containing information about the new address
     * @throws BlockIOException
     */
    public NewAddress getNewAddress(String label) throws BlockIOException {
        HashMap<String, String> params = null;
        if (label != null && !label.equals("")) {
            params = new HashMap<String, String>(1);
            params.put(Constants.Params.LABEL, label);
        }

        Response.ResponseNewAddress response = (Response.ResponseNewAddress) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_NEW_ADDRESS, params, Response.ResponseNewAddress.class);
        return response.newAddress;
    }

    /**
     * Requests a list of addresses in the account associated with this clients' API key
     * @return An {@link io.block.api.model.AccountAddresses} object containing the addresses
     * @throws BlockIOException
     */
    public AccountAddresses getAccountAddresses() throws BlockIOException {
        Response.ResponseAccountAddresses response = (Response.ResponseAccountAddresses) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_MY_ADDRESSES, null, Response.ResponseAccountAddresses.class);
        return response.accountAddresses;
    }

    /**
     * Requests balance(s) of given address(es) in the account associated with this clients' API key <br>
     * Make sure that the addresses actually exist in the account or the whole call will fail
     * @param addresses A String array containing the addresses to request balances for
     * @return An {@link io.block.api.model.AddressBalances} object containing the balances
     * @throws BlockIOException
     */
    public AddressBalances getAddressBalancesByAddress(String[] addresses) throws BlockIOException {
        if (addresses.length == 0) {
            throw new IllegalArgumentException("You have to provide at least one address.");
        }

        String paramString = addresses.length == 1 ? addresses[0] : Arrays.asList(addresses).toString().replaceAll("^\\[|\\]$", "");
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(Constants.Params.ADDRS, paramString);

        Response.ResponseAddressBalances response = (Response.ResponseAddressBalances) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_ADDR_BALANCE, params, Response.ResponseAddressBalances.class);
        return response.addressBalances;
    }

    /**
     * Requests balance(s) of given label(s) in the account associated with this clients' API key <br>
     * Make sure that the labels actually exist in the account or the whole call will fail
     * @param labels A String array containing the labels to request balances for
     * @return An {@link io.block.api.model.AddressBalances} object containing the balances
     * @throws BlockIOException
     */
    public AddressBalances getAddressBalancesByLabels(String[] labels) throws BlockIOException {
        if (labels.length == 0) {
            throw new IllegalArgumentException("You have to provide at least one label.");
        }

        String paramString = labels.length == 1 ? labels[0] : Arrays.asList(labels).toString().replaceAll("^\\[|\\]$", "");
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(Constants.Params.LABELS, paramString);

        Response.ResponseAddressBalances response = (Response.ResponseAddressBalances) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_ADDR_BALANCE, params, Response.ResponseAddressBalances.class);
        return response.addressBalances;
    }

    /**
     * Requests the address with the given label from the account associated with this clients' API key
     * @param label The label for which to request the address for
     * @return An {@link io.block.api.model.AddressByLabel} object containing the address and additional info about it
     * @throws BlockIOException
     */
    public AddressByLabel getAddressByLabel(String label) throws BlockIOException {
        if (label == null || label.equals("")) {
            throw new IllegalArgumentException("You have to provide a valid label.");
        }

        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(Constants.Params.LABEL, label);

        Response.ResponseAddressByLabel response = (Response.ResponseAddressByLabel) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_ADDR_BY_LABEL, params, Response.ResponseAddressByLabel.class);
        return response.addressByLabel;
    }

    /**
     * Convenience method for a simple withdrawal from the account associated with this clients' API key to the specified address
     * @param address Target address
     * @param amount Amount to withdraw
     * @param secretPin The secret PIN you set at block.io to authorize and sign the withdrawal
     * @return A {@link io.block.api.model.Withdrawal} object containing information about the sent transaction.
     * @throws BlockIOException
     */
    public Withdrawal withdrawToAddress(String address, double amount, String secretPin) throws BlockIOException {
        HashMap<String, Double> target = new HashMap<String, Double>(1);
        target.put(address, amount);
        return withdraw(null, null, target, ParamType.ADDRS, secretPin);
    }

    /**
     * Withdraw from the account associated with this clients' API key.
     * @param sources Supply an array of sources for this withdrawal. If you set this to null, then block.io will select the sources automatically.
     *                You must not mix source types!
     * @param sourceType If you supplied a sources array, this is mandatory. Must be one of {@link ParamType}.
     * @param targetsAndAmounts A {@link java.util.Map} with target as key ({@link java.lang.String}) and amount as value ({@link java.lang.Double}).
     *                          Each entry will be one target of the withdrawal. Limit is 100 per withdrawal.
     *                          You must not mix target types!
     * @param targetType This is mandatory and defines what type of targets this withdrawal goes to. One of {@link ParamType}.
     * @param secretPin The secret PIN you set at block.io to authorize and sign the withdrawal
     * @return A {@link io.block.api.model.Withdrawal} object containing information about the sent transaction.
     * @throws BlockIOException
     */
    public Withdrawal withdraw(String[] sources, ParamType sourceType, Map<String, Double> targetsAndAmounts, ParamType targetType, String secretPin) throws BlockIOException {
        if (targetsAndAmounts == null || targetsAndAmounts.size() == 0) {
            throw new IllegalArgumentException("You have to provide between one and 100 pair(s) of targets and amounts to withdraw to");
        }

        if (secretPin == null || secretPin.equals("")) {
            throw new IllegalArgumentException("You have to provide your secret pin with withdrawals");
        }

        HashMap<String, String> params = setupWithdrawalParams(targetsAndAmounts, targetType);

        String method = Constants.Methods.WITHDRAW_FROM_ANY;
        if (sources != null && sources.length > 0) {
            String sourcesString = sources.length == 1 ? sources[0] : Arrays.asList(sources).toString().replaceAll("^\\[|\\]$", "");
            switch (sourceType) {
                case ADDRS:
                    params.put(Constants.Params.FROM_ADDRS, sourcesString);
                    method = Constants.Methods.WITHDRAW_FROM_ADDRS;
                    break;
                case LABELS:
                    params.put(Constants.Params.FROM_LABELS, sourcesString);
                    method = Constants.Methods.WITHDRAW_FROM_LABELS;
                    break;
                case USERIDS:
                    params.put(Constants.Params.FROM_USERIDS, sourcesString);
                    method = Constants.Methods.WITHDRAW_FROM_USERIDS;
                    break;
                default:
                    throw new BlockIOClientException("You requested a withdrawal from specific sources but did not set the source type.");
            }
        }

        Response.ResponseWithdrawSignRequest signRequestResponse = (Response.ResponseWithdrawSignRequest) communicationProtocol.doApiCall(apiKey, method, params, Response.ResponseWithdrawSignRequest.class);
        WithdrawSignRequest signRequest = signRequestResponse.withdrawSignRequest;

        return finalizeWithdrawal(signRequest, secretPin);
    }

    private HashMap<String, String> setupWithdrawalParams(Map<String, Double> addrsAndAmounts, ParamType targetType) throws BlockIOClientException {
        String addrsParamString = "";
        String amountsParamString = "";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US); // This will force '.' as decimal separator
        for (Map.Entry<String, Double> entry: addrsAndAmounts.entrySet()) {
            addrsParamString += entry.getKey() + ",";
            amountsParamString +=  nf.format(entry.getValue()) + ",";
        }

        // Now remove the trailing ','
        addrsParamString = addrsParamString.replaceAll(",$", "");
        amountsParamString = amountsParamString.replaceAll(",$", "");

        // And put it where it belongs
        HashMap<String, String> params = new HashMap<String, String>(2);
        switch (targetType) {
            case ADDRS:
                params.put(Constants.Params.TO_ADDRS, addrsParamString);
                break;
            case LABELS:
                params.put(Constants.Params.TO_LABELS, addrsParamString);
                break;
            case USERIDS:
                params.put(Constants.Params.TO_USERIDS, addrsParamString);
                break;
            default:
                throw new BlockIOClientException("You did not set the target type.");
        }
        params.put(Constants.Params.AMOUNTS, amountsParamString);

        return params;
    }

    private Withdrawal finalizeWithdrawal(WithdrawSignRequest signRequest, String secretPin) throws BlockIOException {
        // Now lets' do the clientside magic
        signRequest = SigningUtils.signWithdrawalRequest(signRequest, secretPin);

        // And do the actual withdrawal
        HashMap<String, String> params = new HashMap<String, String>(1);
        Gson gson = new Gson();
        params.put(Constants.Params.SIG_DATA, gson.toJson(signRequest, WithdrawSignRequest.class));
        Response.ResponseWithdrawal withdrawalResponse = (Response.ResponseWithdrawal) communicationProtocol.doApiCall(apiKey, Constants.Methods.WITHDRAW_DO_FINAL, params, Response.ResponseWithdrawal.class);

        return withdrawalResponse.withdrawal;
    }

    /**
     * Lists up to 100 of the last transactions received by the account associated with this clients' API key
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsReceived} object containing the list of received transactions
     * @throws BlockIOException
     */
    public TransactionsReceived getTransactionsReceived(String beforeTX) throws BlockIOException {
        return (TransactionsReceived) communicationProtocol.abstractTransactionRequest(apiKey, null, null, beforeTX, Constants.Values.TYPE_RECEIVED);
    }

    /**
     * Lists up to 100 of the last transactions received by the provided addresses of the account associated with this clients' API key
     * @param addresses A String array containing the addresses to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsReceived} object containing the list of received transactions
     * @throws BlockIOException
     */
    public TransactionsReceived getTransactionsReceivedByAddress(String[] addresses, String beforeTX) throws BlockIOException {
        return (TransactionsReceived) communicationProtocol.abstractTransactionRequest(apiKey, addresses, Constants.Params.ADDRS, beforeTX, Constants.Values.TYPE_RECEIVED);
    }

    /**
     * Lists up to 100 of the last transactions received by the provided labels of the account associated with this clients' API key
     * @param labels A String array containing the labels to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsReceived} object containing the list of received transactions
     * @throws BlockIOException
     */
    public TransactionsReceived getTransactionsReceivedByLabel(String[] labels, String beforeTX) throws BlockIOException {
        return (TransactionsReceived) communicationProtocol.abstractTransactionRequest(apiKey, labels, Constants.Params.LABELS, beforeTX, Constants.Values.TYPE_RECEIVED);
    }

    /**
     * Lists up to 100 of the last transactions received by the provided user IDs of the account associated with this clients' API key
     * @param userIDs A String array containing the user IDs to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsReceived} object containing the list of received transactions
     * @throws BlockIOException
     */
    public TransactionsReceived getTransactionsReceivedByUserID(String[] userIDs, String beforeTX) throws BlockIOException {
        return (TransactionsReceived) communicationProtocol.abstractTransactionRequest(apiKey, userIDs, Constants.Params.USER_IDS, beforeTX, Constants.Values.TYPE_RECEIVED);
    }

    /**
     * Lists up to 100 of the last transactions sent by the account associated with this clients' API key
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsSent} object containing the list of sent transactions
     * @throws BlockIOException
     */
    public TransactionsSent getTransactionsSent(String beforeTX) throws BlockIOException {
        return (TransactionsSent) communicationProtocol.abstractTransactionRequest(apiKey, null, null, beforeTX, Constants.Values.TYPE_SENT);
    }

    /**
     * Lists up to 100 of the last transactions sent by the provided addresses of the account associated with this clients' API key
     * @param addresses A String array containing the addresses to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsSent} object containing the list of sent transactions
     * @throws BlockIOException
     */
    public TransactionsSent getTransactionsSentByAddress(String[] addresses, String beforeTX) throws BlockIOException {
        return (TransactionsSent) communicationProtocol.abstractTransactionRequest(apiKey, addresses, Constants.Params.ADDRS, beforeTX, Constants.Values.TYPE_SENT);
    }

    /**
     * Lists up to 100 of the last transactions sent by the provided labels of the account associated with this clients' API key
     * @param labels A String array containing the labels to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsSent} object containing the list of sent transactions
     * @throws BlockIOException
     */
    public TransactionsSent getTransactionsSentByLabel(String[] labels, String beforeTX) throws BlockIOException {
        return (TransactionsSent) communicationProtocol.abstractTransactionRequest(apiKey, labels, Constants.Params.LABELS, beforeTX, Constants.Values.TYPE_SENT);
    }

    /**
     * Lists up to 100 of the last transactions sent by the provided user IDs of the account associated with this clients' API key
     * @param userIDs A String array containing the user IDs to request transactions for
     * @param beforeTX An optional transaction ID used as upper bound of the requested transactions. Use this to request more than 100 of the last transactions
     * @return A {@link io.block.api.model.TransactionsSent} object containing the list of sent transactions
     * @throws BlockIOException
     */
    public TransactionsSent getTransactionsSentByUserID(String[] userIDs, String beforeTX) throws BlockIOException {
        return (TransactionsSent) communicationProtocol.abstractTransactionRequest(apiKey, userIDs, Constants.Params.USER_IDS, beforeTX, Constants.Values.TYPE_SENT);
    }

}
