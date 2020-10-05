package org.restheartclient;

import java.util.List;

import org.restheartclient.data.StorageClientResponse;

/** The interface for the Restheart API Client
/*  Works with v5 only
*/
interface StorageClient
{
	/** PUT http://localhost:8080/name 
	*   no JSON body 
	 * @return 
	*/
	StorageClientResponse createCollection(String collectionName); //<- we probably don't need this because insertKeyValuePair upserts the collection

	/** DELETE http://localhost:8080/collectionName*/
	StorageClientResponse deleteCollection(String collectionName); //<- same thing here -- delete all pairs kills the collection

	/** PUT http://localhost:8080/collectionName/pair.key
	
	*   JSON body: pair.value
	*/
	StorageClientResponse insertKeyValuePair(String collectionName, KeyValuePair pair);

	/** POST http://localhost:8080/collectionName
	 *  JSON body: pairs
	 * @param collectionName
	 * @param pairs
	 */
	void insertKeyValuePairList(String collectionName, List<KeyValuePair> pairs);

	/** GET http://localhost:8080/collectionName/key */
	KeyValuePair getKeyValuePair(String collectionName, String key);

	/** PATCH http://localhost:8080/collectionName/pair.key
	*   JSON body: pair.value
	*/
	void updateKeyValuePair(String collectionName, KeyValuePair pair);
	
	/** GET http://localhost:8080/collectionName */
	List<KeyValuePair> getAllKeyValuePairs(String collectionName);
	
	/** DELETE http://localhost:8080/collectionName/key */
	void deleteKeyValuePair(String collecollectionName, String key);

	/** DELETE http://localhost:8080/collectionName */
	void deleteAllKeyValuePairs(String collectionName);

}
