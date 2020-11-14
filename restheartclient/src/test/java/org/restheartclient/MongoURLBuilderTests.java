/**
* Copyright 2020, Gregory A. Lusk
* Chattanooga, Tennessee USA
*/
package org.restheartclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.restheartclient.utils.MongoURLBuilder;

public class MongoURLBuilderTests {

	@Test
	public void testBuild() {
		MongoURLBuilder builder = new MongoURLBuilder();
		String url = builder.setBaseURL("http://127.0.0.1:8080").setCollectionName("TestColl").build();
		assertEquals("http://127.0.0.1:8080/TestColl", url);
	}
}
