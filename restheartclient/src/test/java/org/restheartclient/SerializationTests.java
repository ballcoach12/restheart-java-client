package org.restheartclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SerializationTests {
	
	String expectedJson = "{\r\n"
			+ "  \"_id\": \"newItem\",\r\n"
			+ "  \"_etag\": {\r\n"
			+ "    \"$oid\": \"5f76822d794dbe66cb89763c\"\r\n"
			+ "  },\r\n"
			+ "  \"value\": {\r\n"
			+ "    \"item\": \"notebook\",\r\n"
			+ "    \"qty\": 50,\r\n"
			+ "    \"size\": {\r\n"
			+ "      \"h\": 8.5,\r\n"
			+ "      \"w\": 11,\r\n"
			+ "      \"uom\": \"in\"\r\n"
			+ "    },\r\n"
			+ "    \"status\": \"A\",\r\n"
			+ "    \"suppliers\": [\r\n"
			+ "      \"brand_1\",\r\n"
			+ "      \"brand_2\",\r\n"
			+ "      \"brand_3\"\r\n"
			+ "    ]\r\n"
			+ "  }\r\n"
			+ "}";
	
//	@Test
//	public void testSerializeDeserializeObjectIdentifier() {
//		ObjectIdentifier oid = new ObjectIdentifier("abcd");
//		assertNotEquals(0, oid.get$OID());
//		System.out.println(oid.get$OID());
//		
//		assertEquals("abcd", oid.get$OID());
//	}
	
	private <REQ> String serialize(REQ request){
		String json = new Gson().toJson(request);
		return json;
	}

	@Test
	public void testSerializeDeserializeValueWrapper() {
		Item item = new Item();
	
		item.qty = 50;
		item.item = "notebook";
		item.size.h = 8.5d;
		item.size.w = 11.0d;
		item.size.uom = "in";

		item.status = "A";
		
		item.suppliers.add("brand_1");
		item.suppliers.add("brand_2");
		item.suppliers.add("brand_3");
		

		ValueWrapper wrapper = new ValueWrapper("newItem", item);
	//	wrapper._etag.set$OID("5f76822d794dbe66cb89763c");
		Gson gson = new Gson();
//		String json = gson.toJson(wrapper);
		String json = serialize(wrapper);
		
		assertNotEquals(0, json.length());
		System.out.println(json);
		try {
			JSONAssert.assertEquals(expectedJson, json, false);
		} catch (JSONException e) {
			fail(e);
		}
		
		//System.out.println(json);
		
		ValueWrapper result = gson.fromJson(json, ValueWrapper.class);
		assertNotNull(result);
		Item resultItem = gson.fromJson(result.value.toString(), Item.class);
		assertEquals(item.qty, resultItem.qty);
		assertEquals(item.size.h, resultItem.size.h);
		assertEquals(item.item, resultItem.item);
		assertEquals(item.size.w, resultItem.size.w);
		assertEquals(item.status, resultItem.status);

	}

	@Test
	public void deserializeTest() {
		// String json =
		// "{\"_id\":\"a\",\"key\":\"a\",\"_etag\":{\"$oid\":\"5f70d92c794dbe66cb8975e3\"}}";
		String json = "[{\"_id\":\"a\",\"key\":\"a\",\"_etag\":{\"$oid\":\"5f70db72794dbe66cb8975e7\"}}]";
		Object jo = new Gson().fromJson(json, Object.class);
		assertNotNull(jo);

		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(json);
		JsonObject je2 = je.getAsJsonObject();
		assertNotNull(je2);
	}

	@Test
	public void testDeserializeArray() {
		String json = "[\r\n"
				+ "   { \"item\": \"journal\", \"qty\": 25, \"size\": { \"h\": 14, \"w\": 21, \"uom\": \"cm\" }, \"status\": \"A\" },\r\n"
				+ "   { \"item\": \"notebook\", \"qty\": 50, \"size\": { \"h\": 8.5, \"w\": 11, \"uom\": \"in\" }, \"status\": \"A\" },\r\n"
				+ "   { \"item\": \"paper\", \"qty\": 100, \"size\": { \"h\": 8.5, \"w\": 11, \"uom\": \"in\" }, \"status\": \"D\" },\r\n"
				+ "   { \"item\": \"planner\", \"qty\": 75, \"size\": { \"h\": 22.85, \"w\": 30, \"uom\": \"cm\" }, \"status\": \"D\" },\r\n"
				+ "   { \"item\": \"postcard\", \"qty\": 45, \"size\": { \"h\": 10, \"w\": 15.25, \"uom\": \"cm\" }, \"status\": \"A\" }\r\n"
				+ "]";
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(json);
		assertNotNull(je);
		assertNotNull(je.getAsJsonObject());
	}

}
