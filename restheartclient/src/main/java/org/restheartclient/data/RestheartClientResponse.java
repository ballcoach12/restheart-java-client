package org.restheartclient.data;

import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.StatusLine;

/**
 * Created by Alon Eirew on 7/12/2017.
 */
public class RestheartClientResponse implements StorageClientResponse {

    private static final String ETAG_LABEL = "ETag";
    //private static final String LOCATION_LABEL = "Links";

    private final StatusLine statusLine;
   // private final JsonObject responseObject;
    private final JsonElement responseElement;
    private final Header[] headers;

    public RestheartClientResponse(final StatusLine statusLine, final Header[] headers,
       /* final JsonObject responseObject*/ final JsonElement responseElement) {
        this.statusLine = statusLine;
      //  this.responseObject = responseObject;
        this.responseElement = responseElement;
        this.headers = headers;
    }

    public RestheartClientResponse(final StatusLine statusLine, final Header[] headers) {
        this(statusLine, headers, null);
    }

    @Override
	public StatusLine getStatusLine() {
        return statusLine;
    }

    @Override
	public int getStatusCode() {
        if (statusLine != null) {
            return statusLine.getStatusCode();
        }

        return -1;
    }

    public String getEtag() {
        return getHeaderByName(ETAG_LABEL);
    }

//    public String getDocumentUrlLocation() {
//        return getHeaderByName(LOCATION_LABEL);
//    }

    @Override
	public String getHeaderByName(String headerName) {
        String value = null;
        if (this.headers != null) {
            for (Header header : this.headers) {
                if (header.getName().equalsIgnoreCase(headerName)) {
                    value = header.getValue();
                }
            }
        }

        return value;
    }

//    @Override
//	public JsonObject getResponseObject() {
//        return responseObject;
//    }
    
    @Override
	public JsonElement getResponseElement() {
        return responseElement;
    }

    @Override
	public Header[] getHeaders() {
        return headers;
    }
}
