///**
//* Copyright 2020, Gregory A. Lusk
//* Chattanooga, Tennessee USA
//*/
//
//package com.greglusk.restheart;
//
//import java.io.IOException;
//import java.util.logging.Level;
//
//import org.apache.http.client.methods.CloseableHttpResponse;
//
//import com.google.gson.JsonObject;
//import com.greglusk.restheart.connection.HttpConnectionUtils;
//import com.greglusk.restheart.data.StorageClientResponse;
//import com.greglusk.restheart.exceptions.ApiException;
//import com.greglusk.restheart.exceptions.InvalidOperationException;
//import com.greglusk.restheart.exceptions.UnauthorizedAccessException;
//import com.greglusk.restheart.exceptions.UnknownResourceException;
//import com.greglusk.restheart.utils.MongoURLBuilder;
//import com.greglusk.restheart.validation.DefaultResponseValidator;
//import com.greglusk.restheart.validation.ResponseValidator;
//
//public class RestHeartClient {
////todo: add logging
//	private ResponseValidator validator = new DefaultResponseValidator();
//	private static final String DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8080";
//	private static final String DB_DESCRIPTION_PROPERTY_NAME = "descr";
//
//	private String mongoUrl = DEFAULT_LOCALHOST_URL;
//	private HttpConnectionUtils httpConnectionUtils;
//
//	public RestHeartClient(String userName, String password) {
//		this.httpConnectionUtils = new HttpConnectionUtils(userName, password);
//	}
//
//	public StorageClientResponse createDatabase(String dbName, String dbDescription) throws UnauthorizedAccessException, UnknownResourceException, InvalidOperationException, ApiException {
//		StorageClientResponse response = null;
//		JsonObject jo = new JsonObject();
//		jo.addProperty(DB_DESCRIPTION_PROPERTY_NAME, dbDescription);
//		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
//		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setDatabaseName(dbName).build();
//
//		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
//			response = validator.validate(httpResponse);
//		} catch (IOException e) {
////			LOGGER.log(Level.SEVERE, "was unable to create new Mongo DB collection with name-" + collectionName
////			+ "in DB-" + databaseName, e);
//			System.out.println("Error");
//		}
//		return response;
//	}
//
//	public StorageClientResponse createCollection(String name) throws UnauthorizedAccessException, UnknownResourceException, InvalidOperationException, ApiException
//			 {
//		StorageClientResponse response = null;
//		JsonObject jo = new JsonObject();
//		MongoURLBuilder mongoURLBuilder = new MongoURLBuilder();
//		String url = mongoURLBuilder.setBaseURL(this.mongoUrl).setCollectionName(name).build();
//
//		try (CloseableHttpResponse httpResponse = this.httpConnectionUtils.sendHttpPut(url, jo)) {
//			response = validator.validate(httpResponse);
//		} catch (IOException e) {
////			LOGGER.log(Level.SEVERE, "was unable to create new Mongo DB collection with name-" + collectionName
////					+ "in DB-" + databaseName, e);
//			System.out.println("Error");
//		}
//		return response;
//	}
//}
