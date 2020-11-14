package com.greglusk.restheart.validation;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.greglusk.restheart.data.RestheartClientResponse;
import com.greglusk.restheart.data.StorageClientResponse;
import com.greglusk.restheart.exceptions.ApiException;
import com.greglusk.restheart.exceptions.InvalidOperationException;
import com.greglusk.restheart.exceptions.UnauthorizedAccessException;
import com.greglusk.restheart.exceptions.UnknownResourceException;

public class DefaultResponseValidator implements ResponseValidator {

	private static final String IOE_DEFAULT_MESSAGE = null;

	@Override
	public StorageClientResponse validate(HttpResponse response) throws UnauthorizedAccessException, UnknownResourceException, InvalidOperationException, ApiException {
		// todo: we can probably refactor this to just thrown an ApiException with a reason.
		StorageClientResponse returnValue = null;
		System.out.println(response.toString());
		switch (response.getStatusLine().getStatusCode()) {
		case 200: // success - an existing resource was updated
		case 201: // success - a new resource was created
			returnValue = extractFromResponse(response);
			break;
		case 401: // unauthorized
			throw new UnauthorizedAccessException();
		case 404: // resource not found
			throw new UnknownResourceException();
		case 405: //method not allowed
			throw new InvalidOperationException(IOE_DEFAULT_MESSAGE);
			
		case 500: //server error
			throw new ApiException(response.getStatusLine().getReasonPhrase());
			
		}		

		return returnValue;
	}

	private StorageClientResponse extractFromResponse(final HttpResponse httpResponse) {
		StorageClientResponse response = null;
		JsonObject responseObj = null;
		if (httpResponse != null) {
			StatusLine statusLine = httpResponse.getStatusLine();
			Header[] allHeaders = httpResponse.getAllHeaders();
			HttpEntity resEntity = httpResponse.getEntity();
			if (resEntity != null) {
				try {
					String responseStr = IOUtils.toString(resEntity.getContent(), "UTF-8");
					if (responseStr != null && !responseStr.isEmpty()) {
						JsonParser parser = new JsonParser();
						responseObj = parser.parse(responseStr).getAsJsonObject();
					}
				} catch (IOException e) {
					// LOGGER.log(Level.SEVERE, "Was unable to extract response body", e);
				}
			}

			response = new RestheartClientResponse(statusLine, allHeaders, responseObj);
		}
		return response;
	}

}
