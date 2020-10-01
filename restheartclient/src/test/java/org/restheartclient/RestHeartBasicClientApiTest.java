package org.restheartclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
import org.restheartclient.data.RestHeartClientResponse;
import org.restheartclient.utils.GsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Alon Eirew on 7/12/2017.
 */
public class RestHeartBasicClientApiTest {

    private static final Logger LOGGER = Logger.getLogger(RestHeartBasicClientApiTest.class.getName());

    private static RestHeartClientApi api;

    private String dbName = "testDb";
    private String collName = "testColl";

    private RestHeartClientResponse creationResponseDB;

    @BeforeAll
    public static void initRestHeartClient() {
        api = new RestHeartClientApi("admin", "secret");
    }

    @AfterAll
    public static void releaseResources() {
        if (api != null) {
            try {
                api.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to release resources", e);
            }
        }
    }

    @BeforeEach
    public void beforeTest() {
        creationResponseDB = createDataBase();
    }

    @AfterEach
    public void afterTest() {
     //   dropDataBase(creationResponseDB);
    }

    @Test
    @Disabled
    public void testCreateAndDeleteCollection() {
        RestHeartClientResponse newCollection = createCollection();

        RestHeartClientResponse deleteCollection = api.deleteCollection(dbName, collName, newCollection.getEtag());
        assertEquals(204, deleteCollection.getStatusCode());
    }

    @Test
    @Disabled
    public void testDeleteDocById() throws MalformedURLException {
        createCollection();
        RestHeartClientResponse restHeartClientResponse = insertDocInDB();
        String documentUrlLocation = restHeartClientResponse.getDocumentUrlLocation();
        URL url = new URL(documentUrlLocation);
        String id = FilenameUtils.getName(url.getPath());

        RestHeartClientResponse deleteDocByIdResponse = api.deleteDocumentById(dbName, collName, id);
        assertEquals(
             204,
            deleteDocByIdResponse.getStatusCode());
    }

    @Test
    @Disabled
    public void testGetAllDocs() {
        createCollection();
        insertDocInDB();
        insertDocInDB();

        RestHeartClientResponse response = api.getAllDocumentsFromCollection(dbName, collName);
        assertNotNull(response);

        JsonObject responseObject = response.getResponseObject();
        assertNotNull(responseObject);

        JsonElement returned = responseObject.get("_returned");
        int numberOfElements = returned.getAsInt();
        assertEquals(2, numberOfElements);

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }

    @Test
    public void testGetDocById() throws MalformedURLException {
        createCollection();
        RestHeartClientResponse restHeartClientResponse = insertDocInDB();
//        String documentUrlLocation = restHeartClientResponse.getDocumentUrlLocation();
//        URL url = new URL(documentUrlLocation);
//        String idCreate = FilenameUtils.getName(url.getPath());
        String idCreate = restHeartClientResponse.getHeaderByName("Etag");
        System.out.println("idCreate = " + idCreate);
         
        assertNotNull(idCreate);

        RestHeartClientResponse response = api.getDocumentById(dbName, collName, idCreate);
        assertNotNull(response);

        JsonObject responseObject = response.getResponseObject();
        assertNotNull(responseObject);

        String idRes = responseObject.get("_etag").getAsJsonObject().get("$oid").getAsString();
        System.out.println("idRes = " + idRes);
        assertEquals( idCreate, idRes);

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }
    
    @Test
    public void testInsertDoc() {
    	
    }

    @Test
    @Disabled
    public void testGetDocQuery() throws MalformedURLException {
        createCollection();
        insertDocInDB();
        insertDocInDB();

        String query = "filter={'name':'John'}";

        RestHeartClientResponse response = api.getDocumentsQuery(dbName, collName, query);
        assertNotNull(response);

        JsonObject responseObject = response.getResponseObject();
        assertNotNull(responseObject);

        JsonArray jsonArray = responseObject
            .get("_embedded")
            .getAsJsonArray();

        assertEquals( 2, jsonArray.size());

        LOGGER.info(GsonUtils.toJson(response.getResponseObject()));
    }
    
    private void assertSuccess(int statusCode) {
    	assertTrue(statusCode == 200 || statusCode == 201);
    }

    private RestHeartClientResponse createDataBase() {
        RestHeartClientResponse creationResponse = api.createNewDataBase(dbName, "this is a test");
        assertSuccess(creationResponse.getStatusCode());
        assertNotNull("response eTag is null", creationResponse.getEtag());
        LOGGER.info("ETag=" + creationResponse.getEtag());
        return creationResponse;
    }

    private RestHeartClientResponse createCollection() {
        RestHeartClientResponse newCollection = api.createNewCollection(dbName, collName,
            "this is a test collection");

        assertSuccess(newCollection.getStatusCode());
        assertNotNull(newCollection.getEtag());
        return newCollection;
    }

    private RestHeartClientResponse insertDocInDB() {
        JsonObject jo = new JsonObject();
        jo.addProperty("Name", "John");
        jo.addProperty("Last", "Smith");

        RestHeartClientResponse restHeartClientResponse = api.insertDocumentInCollection(dbName, collName, jo);

        assertNotNull(restHeartClientResponse);
        assertNotNull(restHeartClientResponse.getHeaders());
        assertTrue(restHeartClientResponse.getHeaders().length > 0);
        assertNotNull(restHeartClientResponse.getEtag());

        return restHeartClientResponse;
    }

    private void dropDataBase(RestHeartClientResponse creationResponseSameName) {
        RestHeartClientResponse deleteResponse = api.deleteDataBase(dbName, creationResponseSameName.getEtag());

        assertEquals(
             204,
            deleteResponse.getStatusCode());
        LOGGER.info("ETag=" + deleteResponse.getEtag());
    }
}
