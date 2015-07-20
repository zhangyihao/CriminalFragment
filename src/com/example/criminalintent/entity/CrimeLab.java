package com.example.criminalintent.entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class CrimeLab {
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crims.json";
	
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	private List<Crime> mCrimes;
	private CrimeIntentJSONSerializer mSerializer;
	
	private CrimeLab(Context appContext) {
		this.mAppContext = appContext;
		mSerializer = new CrimeIntentJSONSerializer(appContext, FILENAME);
		try {
			mCrimes = loadCrimes();
		} catch (IOException | JSONException e) {
			Log.e(TAG, "Error happing...", e);
		}
	}
	
	public static CrimeLab getInstance(Context c) {
		if(sCrimeLab==null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}

	public List<Crime> getCrimes() {
		return mCrimes;
	}
	
	public Crime getCrime(UUID uuid) {
		for(Crime crime : mCrimes) {
			if(crime.getId().equals(uuid)) {
				return crime;
			}
		}
		return null;
	}
	
	public void add(Crime crime) {
		if(crime!=null) {
			mCrimes.add(crime);
		}
	}
	
	public List<Crime> loadCrimes() throws IOException, JSONException {
		List<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mAppContext.openFileInput(FILENAME)));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine())!=null) {
				sb.append(line);
			}
			if(sb.length()>0) {
				JSONArray array = new JSONArray(sb.toString());
				for(int i=0; i<array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) {
				reader.close();
				reader = null;
			}
		}
		return crimes;
	}
	
	public boolean saveCrime() {
		Log.i(TAG, mAppContext.getFilesDir().getAbsolutePath());
		try {
			mSerializer.saveCrimes(mCrimes);
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Error saving crimes:", e);
			return false;
		}
	}
}
