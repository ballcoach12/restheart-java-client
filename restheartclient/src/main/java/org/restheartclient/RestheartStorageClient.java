package org.restheartclient;

import static com.greglusk.utils.checks.Checks.ensure;
import static com.greglusk.utils.checks.Checks.invariant;
import static com.greglusk.utils.checks.Checks.require;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.restheartclient.connection.HttpConnectionUtils;
import org.restheartclient.data.RestheartClientResponse;
import org.restheartclient.utils.MongoURLBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RestheartStorageClient implements StorageClient {

	private static final String DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8080";
	private static final Logger LOGGER = Logger.getLogger(RestheartStorageClient.class.getName());

	private String mongoUrl = DEFAULT_LOCALHOST_URL;
	private HttpConnectionUtils httpConnectionUtils;

	public RestheartStorageClient(String userName, String password) {
		this.httpConnectionUtils = new HttpConnectionUtils(userName, password);
	}

	@Override
	public RestheartClientResponse createCollection(String collectionName) {
		require(collectionName != null && "".equals(collectionName) == false,
				"collectionName cannot be null or empty.");

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).build();

		ensure(url.equals(mongoUrl + "/" + collectionName),
				"Invalid url: " + url + "; expected " + mongoUrl + "/" + collectionName);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, new JsonObject())) {
			response = parseResponse(httpResponse);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert collection named '" + collectionName + ".", e);
		}

		return response;

	}

	private static final String ETAG_CONDITION = "If-Match";

	@Override
	public RestheartClientResponse deleteCollection(String collectionName) {
		require(collectionName != null && !collectionName.equals(""), "collectionName cannot be null or empty.");

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).build();
		List<Header> headers = new ArrayList<Header>();

		ensure(url.equals(mongoUrl + "/" + collectionName),
				"Invalid url: " + url + "; expected " + mongoUrl + "/" + collectionName);

		// get the collection so that you can get itsID
		String collectionID = null;
		// todo: refactor to call getCollection()
		try (CloseableHttpResponse getResp = this.httpConnectionUtils.sendHttpGet(url)) {
			response = parseResponse(getResp);
			if (isSuccess(response.getStatusCode())) {
				collectionID = response.getHeaderByName("ETag");
				headers.add(new BasicHeader(ETAG_CONDITION, collectionID));
			}
		} catch (Exception e) {
			System.out.println(e);
			LOGGER.log(Level.SEVERE, "Was unable to retrieve collection '" + collectionName + "'.", e);
		}

		ensure(!headers.isEmpty(), "Could not locate collection ID for collection " + collectionName);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url, headers)) {
			response = parseResponse(httpResponse);
		} catch (Exception e) {
			System.out.println(e);
			LOGGER.log(Level.SEVERE, "Was unable to delete collection '" + collectionName + "'.", e);
		}

		return response;

	}

	

	@Override
	public RestheartClientResponse insertKeyValuePair(String collectionName, KeyValuePair pair) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");
		require(pair != null, "pair cannot be null.");
		require(StringUtils.isNotEmpty(pair.key), "pair.key cannot be null or empty.");

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).setDocumentId(pair.key)
				.build();
		
		ensure(url.equals("http://127.0.0.1:8080/" + collectionName + "/" + pair.key), "Invalid URL. Expected " + 
				"http://127.0.0.1:8080/" + collectionName + "/" + pair.key + ", but got " + url);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, pair)) {
			response = parseResponse(httpResponse);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert KeyValuePair '" + pair.key + "'.", e);
		}
		return response;

	}

	@Override
	public void insertKeyValuePairList(String collectionName, List<KeyValuePair> pairs) {
		// TODO Auto-generated method stub

	}

	@Override
	public KeyValuePair getKeyValuePair(String collectionName, String key) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");
		require(key != null, "key cannot be null.");
		require(StringUtils.isNotEmpty(key), "key cannot be empty.");
		
		KeyValuePair response = null;
		
		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).setDocumentId(key)
				.build();
		
		ensure(url.equals("http://127.0.0.1:8080/" + collectionName + "/" + key), "Invalid URL. Expected " + 
				"http://127.0.0.1:8080/" + collectionName + "/" + key + ", but got " + url);
		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpGet(url)) {
			response = new Gson().fromJson(parseResponse(httpResponse).getResponseElement().getAsJsonObject().toString(), KeyValuePair.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert KeyValuePair '" + key + "'.", e);
		}
		return response;
	}

	@Override
	public void updateKeyValuePair(String collectionName, KeyValuePair pair) {
		invariant(false, "Not implemented yet.");

	}

	@Override
	public List<KeyValuePair> getAllKeyValuePairs(String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteKeyValuePair(String collecollectionName, String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllKeyValuePairs(String collectionName) {
		// TODO Auto-generated method stub

	}
	
	private boolean isSuccess(int responseCode) {
		return responseCode == 200 || responseCode == 201 || responseCode == 204;
	}

	private RestheartClientResponse parseResponse(final CloseableHttpResponse httpResponse) {
		RestheartClientResponse response = null;
		//JsonObject responseObj = null;
		JsonElement responseElement = null;
		if (httpResponse != null) {
			StatusLine statusLine = httpResponse.getStatusLine();
			Header[] allHeaders = httpResponse.getAllHeaders();
			HttpEntity resEntity = httpResponse.getEntity();
			if (resEntity != null) {
				try {
					String responseStr = IOUtils.toString(resEntity.getContent(), "UTF-8");
					if (responseStr != null && !responseStr.isEmpty()) {
						JsonParser parser = new JsonParser();
						responseElement = parser.parse(responseStr);
					}
				} catch (/* IOException */ Exception e) {
					LOGGER.log(Level.SEVERE, "Was unable to extract response body", e);
				}
			}

			response = new RestheartClientResponse(statusLine, allHeaders, responseElement);
		}
		return response;
	}

}
