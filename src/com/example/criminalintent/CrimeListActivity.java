package com.example.criminalintent;

import com.example.criminalintent.fragment.CrimeListfragment;

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeListfragment();
	}

}
