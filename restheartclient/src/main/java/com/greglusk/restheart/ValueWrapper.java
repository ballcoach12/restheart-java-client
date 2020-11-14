package com.greglusk.restheart;

import com.google.gson.Gson;
import java.util.UUID;

/**
 * A class that wraps objects with the identifiers that are expected by Restheart.
 * @author Greg Lusk
 *
 */
public class ValueWrapper {
	
	transient Gson gson;
	String _id = "";
//	ObjectIdentifier _etag = new ObjectIdentifier();
	Object value = null; // could we use generics here to vary the type?
	
	public ValueWrapper() {
		gson = new Gson();
		_id = gson.toJson(UUID.randomUUID().toString());
	}
	public ValueWrapper(String id) {
		this._id = id;
	}
	
	public ValueWrapper(String id, Object value) {
		this(id);
		this.value = value;
	}
	
	public ValueWrapper(Object value) {
		this.value = value;
	}
	
	public Object getId() {
		return this._id;		
	}
	
	public void setId(String value) {
		this._id = value;
	}
	
	
	
	public Object getValue() {
		return this.value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
