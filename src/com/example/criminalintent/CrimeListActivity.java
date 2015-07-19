package com.example.criminalintent;

import android.support.v4.app.Fragment;

import com.example.criminalintent.fragment.CrimeListfragment;

public class CrimeListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeListfragment();
	}

}
