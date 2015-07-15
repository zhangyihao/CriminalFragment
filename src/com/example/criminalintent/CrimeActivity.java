package com.example.criminalintent;

import com.example.criminalintent.fragment.CrimeFragment;

import android.support.v4.app.Fragment;

public class CrimeActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeFragment();
	}

}
