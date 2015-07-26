package com.example.criminalintent.entity;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Crime {
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;
	
	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_SOLVED = "solved";
	public static final String JSON_DATE = "date";
	public static final String JSON_PHOTO = "photo";
	public static final String JSON_SUSPECT = "suspect";
	
	public Crime() {
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	public Crime(JSONObject json) throws JSONException {
		Log.v("CrimeFragment", json.toString());
		this.mId = UUID.fromString(json.getString(JSON_ID));
		this.mTitle = json.getString(JSON_TITLE);
		this.mDate = new Date(json.getLong(JSON_DATE));
		this.mSolved = json.getBoolean(JSON_SOLVED);
		if(json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		if(json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
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
	
	public String getSuspect() {
		return mSuspect;
	}

	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, this.mTitle);
		json.put(JSON_SOLVED, this.mSolved);
		json.put(JSON_DATE, this.mDate.getTime());
		if(mPhoto!=null) {
			json.put(JSON_PHOTO, this.mPhoto.toJson());
		}
		if(mSuspect!=null && !"".equals(mSuspect)) {
			json.put(JSON_SUSPECT, mSuspect);
		}
		return json;
	}
	
	
}
