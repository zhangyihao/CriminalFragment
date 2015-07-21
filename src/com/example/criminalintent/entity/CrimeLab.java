package com.example.criminalintent.entity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
//			mCrimes = mSerializer.loadCrimes();
			mCrimes = mSerializer.loadCrimesWithSDCard();
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
	
	public void delete(Crime c) {
		mCrimes.remove(c);
	}
	
	public boolean saveCrime() {
		try {
//			mSerializer.saveCrimes(mCrimes);
			mSerializer.saveCrimesWithSDCard(mCrimes);
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Error saving crimes:", e);
			return false;
		}
	}
}
