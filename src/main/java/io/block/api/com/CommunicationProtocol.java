package io.block.api.com;

import io.block.api.model.Response;
import io.block.api.utils.BlockIOException;

import java.util.Map;

public interface CommunicationProtocol {
    Object abstractTransactionRequest(String apiKey, String[] whatFor, String typeOfParams, String beforeTx,
                                      String type) throws BlockIOException;

    Response doApiCall(String apiKey, String method, Map<String, String> params, Class<?> responseType)
            throws BlockIOException;
}
