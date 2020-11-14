package org.restheartclient.data;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import com.google.gson.JsonElement;

public interface StorageClientResponse {

	StatusLine getStatusLine();

	int getStatusCode();

	String getHeaderByName(String headerName);

	//JsonObject getResponseObject();
	JsonElement getResponseElement();

	Header[] getHeaders();

}