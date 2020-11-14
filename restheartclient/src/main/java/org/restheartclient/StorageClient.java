package org.restheartclient;

import java.util.List;

import org.restheartclient.data.RestheartClientResponse;
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
	void createCollection(String collectionName); //<- we probably don't need this because insertKeyValuePair upserts the collection

	/** DELETE http://localhost:8080/collectionName*/
	void deleteCollection(String collectionName); //<- same thing here -- delete all pairs kills the collection

	/** PUT http://localhost:8080/collectionName/pair.key
	
	*   JSON body: pair.value
	*/
	void insertValue(String collectionName, ValueWrapper value);

	/** POST http://localhost:8080/collectionName
	 *  JSON body: pairs
	 * @param collectionName
	 * @param values
	 */
	void insertValues(String collectionName, List<ValueWrapper> values);
	
	//RestheartClientResponse insertValue(String collectionName, ValueWrapper wrapper);

	/** GET http://localhost:8080/collectionName/key */
	ValueWrapper getValue(String collectionName, String key);

	/** PATCH http://localhost:8080/collectionName/pair.key
	*   JSON body: pair.value
	*/
	void updateValue(String collectionName, ValueWrapper value);
	
	/** GET http://localhost:8080/collectionName */
	List<ValueWrapper> getAllValues(String collectionName);
	
	/** DELETE http://localhost:8080/collectionName/key */
	void deleteValue(String collectionName, String key);

	/** DELETE http://localhost:8080/collectionName */
	void deleteAllValues(String collectionName);

}
