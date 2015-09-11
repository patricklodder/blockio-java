package io.block.api.com;

import com.google.gson.Gson;
import io.block.api.model.Response;
import io.block.api.utils.BlockIOClientException;
import io.block.api.utils.BlockIOException;
import io.block.api.utils.BlockIOServerException;
import io.block.api.utils.Constants;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements common communication protocol functionality.
 */
public abstract class AbstractCommunicationProtocol implements CommunicationProtocol {

    public abstract Response doApiCall(String apiKey, String method, Map<String, String> params, Class<?> responseType) throws BlockIOException;

    /**
     * Implements an abstract way to query transactions.
     * @param apiKey The api key to query against.
     * @param whatFor The addresses/labels/user ids to query against.
     * @param typeOfParams Either one of addresses, labels or user_ids.
     * @param beforeTx An optional transaction ID used as upper bound of the requested transactions.
     * @param type The type of transactions (spent/received) to query.
     * @return Either a {@link io.block.api.model.TransactionsReceived} or a {@link io.block.api.model.TransactionsSent} object depending on the type parameter.
     * @throws BlockIOException
     */
    public Object abstractTransactionRequest(String apiKey, String[] whatFor, String typeOfParams, String beforeTx, String type) throws BlockIOException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.Params.TYPE, type);

        if (whatFor != null && typeOfParams != null) {
            if (whatFor.length == 0) {
                throw new IllegalArgumentException("You have to provide at least one address/label/user ID.");
            }
            String paramString = whatFor.length == 1 ? whatFor[0] : Arrays.asList(whatFor).toString().replaceAll("^\\[|\\]$", "");
            params.put(typeOfParams, paramString);
        }

        if (beforeTx != null && !beforeTx.equals("")) {
            params.put(Constants.Params.BEFORE_TX, beforeTx);
        }

        if (type.equals(Constants.Values.TYPE_RECEIVED)) {
            Response.ResponseTransactionsReceived response = (Response.ResponseTransactionsReceived) doApiCall(apiKey, Constants.Methods.GET_TXNS, params, Response.ResponseTransactionsReceived.class);
            return response.transactionsReceived;
        } else if (type.equals(Constants.Values.TYPE_SENT)) {
            Response.ResponseTransactionsSent response = (Response.ResponseTransactionsSent) doApiCall(apiKey, Constants.Methods.GET_TXNS, params, Response.ResponseTransactionsSent.class);
            return response.transactionsSent;
        } else {
            throw new IllegalArgumentException("Internal error. Please file an issue report");
        }
    }

    protected Response getResponse(CloseableHttpResponse response, Class<?> responseType) throws BlockIOClientException, BlockIOServerException {
        Gson gson = new Gson();
        String responseString;
        try {
            responseString = EntityUtils.toString(response.getEntity());
            response.close();
        } catch (IOException e) {
            throw new BlockIOClientException("Received invalid data from API.", e);
        }

        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                return (Response) gson.fromJson(responseString, responseType);
            case HttpStatus.SC_NOT_FOUND:
                Response.ResponseError error = gson.fromJson(responseString, Response.ResponseError.class);
                throw new BlockIOServerException("API returned error: " + error.error.message);
            default:
                throw new BlockIOClientException("Unknown API response.");
        }
    }

}
