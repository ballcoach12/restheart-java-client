package org.restheartclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.greglusk.utils.checks.PreConditionViolationException;

public class StorageClientTests {

	private final String COLLECTION_NAME = "myCollection";
	private static String USER_NAME = "admin";
	private static String PASSWORD = "secret";
	private static StorageClient client;

	@BeforeAll
	public static void setup() {
		client = new RestheartStorageClient(USER_NAME, PASSWORD);
	}

	@BeforeEach
	public void beforeEach() {
		try {
			createCollection();
		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
	}

	@AfterEach
	public void afterEach() {
		try {
	//		deleteCollection();
		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
	}

	@Test
	public void testInsertNullValue() {
		assertThrows(PreConditionViolationException.class, () -> {
			client.insertValue("bob", null);
		});

//		assertThrows(PreConditionViolationException.class, () -> {
//			client.insertValue("bob", new ValueWrapper());
//		});

	}

	@Test
	public void testCreateCollectionNoName() {
		assertThrows(PreConditionViolationException.class, () -> {
			client.createCollection(null);
		});
	}

	@Test
	@Disabled
	public void testDeleteCollection() {
		assertNotNull(client);
		try {
			deleteCollection();
		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
	}

	@Test
	public void testCreateCollection() {
		assertNotNull(client);
		try {
			createCollection();
		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
	}
	
	@Test
	public void testGetAllValues() {
		deleteCollection();
		createCollection();
		insertValues();
		
		List<ValueWrapper> values = client.getAllValues(COLLECTION_NAME);
		assertNotNull(values);
		assertEquals(10, values.size());
	}
	
	@Test
	public void testInsertList() {
		try {
			deleteCollection();
			createCollection();
			insertValues();
			var zero = client.getValue(COLLECTION_NAME, "0");
			assertNotNull(zero);
			var one = client.getValue(COLLECTION_NAME, "1");
			assertNotNull(one);
			var two = client.getValue(COLLECTION_NAME, "2");
			assertNotNull(two);
			var three = client.getValue(COLLECTION_NAME, "3");
			assertNotNull(three);
			var four = client.getValue(COLLECTION_NAME, "4");
			assertNotNull(four);
			var five = client.getValue(COLLECTION_NAME, "5");
			assertNotNull(five);
			var six = client.getValue(COLLECTION_NAME, "6");
			assertNotNull(six);
			var seven = client.getValue(COLLECTION_NAME, "7");
			assertNotNull(seven);
			var eight = client.getValue(COLLECTION_NAME, "8");
			assertNotNull(eight);
			var nine = client.getValue(COLLECTION_NAME, "9");
			assertNotNull(nine);
		}
		catch(Exception e) {
			System.out.println(e);
			fail(e);
		}
	}
	
	@Test
	public void testGetValue() {
		// insert a value
		try {
			insertValue();
		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}

		ValueWrapper response = client.getValue(COLLECTION_NAME, "a");
		assertNotNull(response);
		assertEquals("a", response._id);
	}

	@Test
	public void testDeleteAllValues() {
		deleteCollection();
		createCollection();
		insertValues();
		
		client.deleteAllValues(COLLECTION_NAME);
		List<ValueWrapper> values = client.getAllValues(COLLECTION_NAME);
		assertEquals(0, values.size());
		
	}
	@Test
	public void testDeleteValue() {
		try {
			insertValue();

		} catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
		ValueWrapper response = client.getValue(COLLECTION_NAME, "a");
		assertNotNull(response);
		assertEquals("a", response._id);
		
		try {
			client.deleteValue(COLLECTION_NAME, "a");
		}
		catch (Exception e) {
			System.out.println(e);
			fail(e);
		}
		
		assertNull(client.getValue(COLLECTION_NAME, "a").value);
	}

	@Test
	public void testInsertValue() {
		try {
			insertValue();
		} catch (Exception e) {
			fail(e);
		}
	}

	private void insertValues() {
		ArrayList<ValueWrapper> values = new ArrayList<ValueWrapper>();
		for(int i=0; i < 10; i++) {
			ValueWrapper w = new ValueWrapper();
			w.setId(new Integer(i).toString());
			w.setValue(new Item());
			values.add(w);
		}
		client.insertValues(COLLECTION_NAME, values);
		
	}

	
	private void insertValue() {
		ValueWrapper wrapper = new ValueWrapper();
		wrapper.value = new Item();
		wrapper._id = "a";
		client.insertValue(COLLECTION_NAME, wrapper);
	}

	private void createCollection() {
		client.createCollection(COLLECTION_NAME);
	}

	private void deleteCollection() {
		client.deleteCollection(COLLECTION_NAME);
	}
}
