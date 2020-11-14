/**
* Copyright 2020, Gregory A. Lusk
* Chattanooga, Tennessee USA
*/
package com.greglusk.restheart;

import static com.greglusk.utils.checks.Checks.ensure;
import static com.greglusk.utils.checks.Checks.invariant;
import static com.greglusk.utils.checks.Checks.require;

import java.lang.reflect.Type;
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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.greglusk.restheart.connection.HttpConnectionUtils;
import com.greglusk.restheart.data.RestheartClientResponse;
import com.greglusk.restheart.utils.MongoURLBuilder;

import lombok.SneakyThrows;

public class RestheartStorageClient implements StorageClient {

	private static final String DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8080";
	private static final Logger LOGGER = Logger.getLogger(RestheartStorageClient.class.getName());

	private String mongoUrl = DEFAULT_LOCALHOST_URL;
	private HttpConnectionUtils httpConnectionUtils;

	public RestheartStorageClient(String userName, String password) {
		this.httpConnectionUtils = new HttpConnectionUtils(userName, password);
	}

	@Override
	public void createCollection(String collectionName) {
		require(collectionName != null && "".equals(collectionName) == false,
				"collectionName cannot be null or empty.");

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).build();

		ensure(url.equals(mongoUrl + "/" + collectionName),
				"Invalid url: " + url + "; expected " + mongoUrl + "/" + collectionName);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, new JsonObject())) {
			response = parseResponse(httpResponse);
			// todo: handle the response with our default handler
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert collection named '" + collectionName + ".", e);
		}

	}

	private static final String ETAG_CONDITION = "If-Match";

	@Override
	public void deleteCollection(String collectionName) {
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

	}

	@Override
	@SneakyThrows
	public void insertValue(String collectionName, ValueWrapper value) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");
		require(value != null, "pair cannot be null.");
		require(StringUtils.isNotEmpty(value._id), "value._id cannot be null or empty.");

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName)
				.setDocumentId(value._id).build();

		ensure(url.equals("http://127.0.0.1:8080/" + collectionName + "/" + value._id), "Invalid URL. Expected "
				+ "http://127.0.0.1:8080/" + collectionName + "/" + value._id + ", but got " + url);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, value)) {
			response = parseResponse(httpResponse);
			int statusCode = response.getStatusCode();
			ensure(statusCode == 201 || statusCode == 200,
					"Error; expected 200 or 201 status code. Reason: " + response.getStatusLine().getReasonPhrase());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert KeyValuePair '" + value._id + "'.", e);
			throw (e);
		}

	}

	@Override
	@SneakyThrows
	public void insertValues(String collectionName, List<ValueWrapper> values) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");
		require(values != null, "pair cannot be null.");

		for (ValueWrapper value : values) {
			require(StringUtils.isNotEmpty(value._id), "value._id cannot be null or empty.");
		}

		RestheartClientResponse response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).build();

		ensure(url.equals("http://127.0.0.1:8080/" + collectionName),
				"Invalid URL. Expected " + "http://127.0.0.1:8080/" + collectionName + ", but got " + url);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPost(url, values)) {
			response = parseResponse(httpResponse);
			int statusCode = response.getStatusCode();
			ensure(statusCode == 201 || statusCode == 200,
					"Error; expected 200 or 201 status code. Reason: " + response.getStatusLine().getReasonPhrase());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert valuewrapper list.", e);
			throw (e);
		}

	}

	@Override
	@SneakyThrows
	public void deleteValue(String collectionName, String key) {
		// build a url
		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).setDocumentId(key)
				.build();
		ensure(url.equals("http://127.0.0.1:8080/" + collectionName + "/" + key),
				"Invalid URL. Expected " + "http://127.0.0.1:8080/" + collectionName + "/" + key + ", but got " + url);

		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpDelete(url,
				new ArrayList<Header>())) {
			var response = parseResponse(httpResponse);
			// String json =
			// parseResponse(httpResponse).getResponseElement().getAsJsonObject().toString();
			int statusCode = response.getStatusCode();
			ensure(statusCode == 200 || statusCode == 204, "Expected 200 or 204.");
		}

	}

	@Override
	public ValueWrapper getValue(String collectionName, String key) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");
		require(key != null, "key cannot be null.");
		require(StringUtils.isNotEmpty(key), "key cannot be empty.");

		ValueWrapper response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).setDocumentId(key)
				.build();

		ensure(url.equals("http://127.0.0.1:8080/" + collectionName + "/" + key),
				"Invalid URL. Expected " + "http://127.0.0.1:8080/" + collectionName + "/" + key + ", but got " + url);
		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpGet(url)) {
			response = new Gson().fromJson(
					parseResponse(httpResponse).getResponseElement().getAsJsonObject().toString(), ValueWrapper.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to insert KeyValuePair '" + key + "'.", e);
		}
		return response;
	}

	@Override
	public void updateValue(String collectionName, ValueWrapper value) {
		invariant(false, "Not implemented yet.");
		// this is just an insert, since Restheart automatically upserts;
		// this function needs testing, though

	}

	@SuppressWarnings("serial")
	@Override
	public List<ValueWrapper> getAllValues(String collectionName) {
		require(collectionName != null, "collectionName cannot be null.");
		require(StringUtils.isNotEmpty(collectionName), "collectionName cannot be empty.");

		List<ValueWrapper> response = null;

		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(collectionName).build();

		ensure(url.equals("http://127.0.0.1:8080/" + collectionName),
				"Invalid URL. Expected " + "http://127.0.0.1:8080/" + collectionName + "/" + ", but got " + url);
		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpGet(url)) {
			String json = parseResponse(httpResponse).getResponseElement().getAsJsonArray().toString();
			// String json =
			// parseResponse(httpResponse).getResponseElement().getAsJsonObject().toString();

			Type listOfValueWrapperObjects = new TypeToken<ArrayList<ValueWrapper>>() {
			}.getType();

			response = new Gson().fromJson(json, listOfValueWrapperObjects);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Was unable to get all values", e);
		}
		return response;
	}

	@Override
	@SneakyThrows
	public void deleteAllValues(String collectionName) {
		deleteCollection(collectionName);
		createCollection(collectionName);
	}

	private boolean isSuccess(int responseCode) {
		return responseCode == 200 || responseCode == 201 || responseCode == 204;
	}

	private RestheartClientResponse parseResponse(final CloseableHttpResponse httpResponse) {
		RestheartClientResponse response = null;
		// JsonObject responseObj = null;
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
