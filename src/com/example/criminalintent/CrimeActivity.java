package com.example.criminalintent;

import java.util.UUID;

import com.example.criminalintent.fragment.CrimeFragment;
import com.example.criminalintent.fragment.CrimeFragmentConst;

import android.support.v4.app.Fragment;

public class CrimeActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragmentConst.EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeId, CrimeFragmentConst.CRIME_OPERATOR_ADD);
	}

}
