package org.restheartclient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.restheartclient.data.StorageClientResponse;
import org.restheartclient.exceptions.ApiException;
import org.restheartclient.exceptions.InvalidOperationException;
import org.restheartclient.exceptions.UnauthorizedAccessException;
import org.restheartclient.exceptions.UnknownResourceException;

public class RestHeartClientTests {
	
//	@Test
//	public void testGetDatabaseInfo() {
//		RestHeartClient client = new RestHeartClient("admin", "secret");
//		DatabaseInfo dbInfo = client.getDatabaseInfo("MyTestDatabase");
//		assertNotNull(DatabaseInfo);
//		assertNotNull(dbInfo.
//	}
	
	@Test
	public void testDeleteCollection() {
		
	}

	@Test
	public void testCreateDatabase() {
		RestHeartClient client = new RestHeartClient("admin", "secret");
		try {
			StorageClientResponse response = client.createDatabase("MyTestDatabase", "Delete this");
			assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201);
			
			
		}
		catch (UnauthorizedAccessException | UnknownResourceException | InvalidOperationException | ApiException e) {
			fail(e);
		}
	}
	@Test
	public void testCreateCollection() {
		RestHeartClient client = new RestHeartClient("admin", "secret");
		assertNotNull(client);
		
		// add a new collection called myTest
		try {
			int statusCode = client.createCollection("myTest").getStatusCode();
			assertTrue(statusCode == 200 || statusCode == 201);
		} catch (UnauthorizedAccessException | UnknownResourceException | InvalidOperationException | ApiException e) {
			fail(e);
		}
		
	}
}
