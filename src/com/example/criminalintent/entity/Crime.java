package com.example.criminalintent.entity;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	
	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_SOLVED = "solved";
	public static final String JSON_DATE = "date";
	public static final String JSON_PHOTO = "photo";
	
	public Crime() {
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	public Crime(JSONObject json) throws JSONException {
		this.mId = UUID.fromString(json.getString(JSON_ID));
		this.mTitle = json.getString(JSON_TITLE);
		this.mDate = new Date(json.getLong(JSON_DATE));
		this.mSolved = json.getBoolean(JSON_SOLVED);
		if(json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public UUID getId() {
		return mId;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public Date getDate() {
		return mDate;
	}
	
	public String toString() {
		return mTitle;
	}
	
	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, this.mTitle);
		json.put(JSON_SOLVED, this.mSolved);
		json.put(JSON_DATE, this.mDate.getTime());
		if(mPhoto!=null) {
			json.put(JSON_SOLVED, this.mPhoto.toJson());
		}
		return json;
	}
	
	
}
