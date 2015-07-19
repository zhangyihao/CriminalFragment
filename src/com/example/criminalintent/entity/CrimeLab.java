package com.example.criminalintent.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	private List<Crime> mCrimes;
	
	private CrimeLab(Context appContext) {
		this.mAppContext = appContext;
		mCrimes = new ArrayList<Crime>();
//		for(int i=0; i<100; i++) {
//			Crime crime = new Crime();
//			crime.setTitle("Crime #"+i);
//			crime.setSolved((i%3)==0);
//			mCrimes.add(crime);
//		}
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
}
