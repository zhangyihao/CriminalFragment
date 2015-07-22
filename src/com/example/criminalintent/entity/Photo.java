package com.example.criminalintent.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
	private static final String JSON_FILENAME = "filename";
	
	private String mFilename;

	public Photo(String filename) {
		super();
		mFilename = filename;
	}
	
	public Photo(JSONObject json) throws JSONException {
		mFilename = json.getString(JSON_FILENAME);
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, mFilename);
		return json;
	}
	
	public String getFileName() {
		return mFilename;
	}
}
