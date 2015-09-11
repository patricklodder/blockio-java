package io.block.api.com;

import io.block.api.model.Response;
import io.block.api.utils.BlockIOClientException;
import io.block.api.utils.BlockIOException;
import io.block.api.utils.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;

/**
 * Communication protocol implementation using HTTP POST
 */
public class HttpPostCommunicator extends AbstractCommunicationProtocol implements CommunicationProtocol {

    /**
     * Execute an API call using HTTP POST
     * @param apiKey The API key to authenticate with.
     * @param method The method (endpoint) to call against.
     * @param params Map of parameters to urlencode and put in the POST body.
     * @param responseType The expected child class of {@link io.block.api.model.Response} to cast into
     * @return A child class of {@link io.block.api.model.Response} containing the response from the API service.
     * @throws BlockIOException
     */
    public Response doApiCall(String apiKey, String method, Map<String, String> params, Class<?> responseType) throws BlockIOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(Constants.buildUri(method, true));
        List<NameValuePair> postParams = new ArrayList<NameValuePair>(2);

        postParams.add(new BasicNameValuePair(Constants.Params.API_KEY, apiKey));

        if (params != null) {
            for (Map.Entry<String, String> entry: params.entrySet()) {
                postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        CloseableHttpResponse response;
        try {
            request.setEntity(new UrlEncodedFormEntity(postParams));
            response = client.execute(request);
            return getResponse(response, responseType);
        } catch (IOException e) {
            throw new BlockIOClientException("Network connectivity problem.", e);
        } finally {
            try {
                client.close();
            } catch (Exception ignore) {}
        }
    }
}
