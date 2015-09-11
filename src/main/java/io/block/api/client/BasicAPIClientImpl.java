package io.block.api.client;

import io.block.api.com.CommunicationProtocol;
import io.block.api.model.GreenAddresses;
import io.block.api.model.GreenTransactions;
import io.block.api.model.Prices;
import io.block.api.model.Response;
import io.block.api.utils.BlockIOException;
import io.block.api.utils.BlockIOServerException;
import io.block.api.utils.Constants;
import io.block.api.utils.Network;

import java.util.Arrays;
import java.util.HashMap;

public class BasicAPIClientImpl implements BasicAPIClient {

    protected final String apiKey;
    protected final CommunicationProtocol communicationProtocol;

    public BasicAPIClientImpl(String apiKey, CommunicationProtocol communicationProtocol) {
        this.apiKey = apiKey;
        this.communicationProtocol = communicationProtocol;
    }

    /**
     * Requests prices of the currency of the account associated with this clients' API key
     * @param baseCurrency Optional base currency to return prices in. null or "" to get prices in all available base currencies
     * @return A {@link io.block.api.model.Prices} object containing price information in one or more base currency from one or more exchange
     * @throws BlockIOException
     */
    public Prices getPrices(String baseCurrency) throws BlockIOException{
        HashMap<String, String> params = null;
        if (baseCurrency != null && !baseCurrency.equals("")) {
            params = new HashMap<String, String>(1);
            params.put(Constants.Params.PRICE_BASE, baseCurrency);
        }

        Response.ResponsePrices response = (Response.ResponsePrices) communicationProtocol.doApiCall(apiKey, Constants.Methods.GET_PRICES, params, Response.ResponsePrices.class);
        return response.prices;
    }

    /**
     * Checks the given address(es) for being Block.io Green Address(es)
     * @param addresses A String array containing the addresses to request status for
     * @return An {@link io.block.api.model.GreenAddresses} object containing the subset of the given addresses that are green
     * @throws BlockIOException
     */
    public GreenAddresses isGreenAddress(String[] addresses) throws BlockIOException {
        if (addresses.length == 0) {
            throw new IllegalArgumentException("You have to provide at least one address.");
        }

        String paramString = addresses.length == 1 ? addresses[0] : Arrays.asList(addresses).toString().replaceAll("^\\[|\\]$", "");
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(Constants.Params.ADDRS, paramString);

        Response.ResponseGreenAddresses response = (Response.ResponseGreenAddresses) communicationProtocol.doApiCall(apiKey, Constants.Methods.IS_GREEN_ADDR, params, Response.ResponseGreenAddresses.class);
        return response.greenAddresses;
    }

    /**
     * Checks the given transaction(s) for being sent by a Block.io Green Address
     * @return An {@link io.block.api.model.GreenTransactions} object containing the subset of the given transactions that are green
     * @throws BlockIOException
     */
    public GreenTransactions isGreenTransaction(String[] txIDs) throws BlockIOException {
        if (txIDs.length == 0) {
            throw new IllegalArgumentException("You have to provide at least one transaction ID.");
        }

        String paramString = txIDs.length == 1 ? txIDs[0] : Arrays.asList(txIDs).toString().replaceAll("^\\[|\\]$", "");
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(Constants.Params.TX_IDS, paramString);

        Response.ResponseGreenTransactions response = (Response.ResponseGreenTransactions) communicationProtocol.doApiCall(apiKey, Constants.Methods.IS_GREEN_TX, params, Response.ResponseGreenTransactions.class);
        return response.greenTransactions;
    }

    /**
     * Validates the API key and returns the Network the key is linked to
     * @return A {@link io.block.api.utils.Network} object
     * @throws BlockIOException
     */
    public Network getNetwork() throws BlockIOException {
        Response.ResponseNetworkDescriptor response = (Response.ResponseNetworkDescriptor) communicationProtocol.doApiCall(
                apiKey,
                Constants.Methods.VALIDATE_API_KEY,
                null,
                Response.ResponseNetworkDescriptor.class
        );
        return Network.getNetwork(response.network.network);
    }

    /**
     * Validates the API key
     * @return A boolean indicating the validity of the key
     * @throws BlockIOException
     */
    public boolean validateKey() throws BlockIOException {
        try {
            getNetwork();
        } catch (BlockIOServerException e) {
            return false;
        }

        return true;
    }
}
