package com.example.criminalintent;

import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

import com.example.criminalfragment.R;
import com.example.criminalintent.entity.Crime;
import com.example.criminalintent.entity.CrimeLab;
import com.example.criminalintent.fragment.CrimeFragment;

public class CrimePageActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private List<Crime> mCrimes;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.ViewPager);
		setContentView(mViewPager);
		
		mCrimes = CrimeLab.getInstance(this).getCrimes();
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getId());
			}
		});
		
		UUID crimeID = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for(int i=0; i<mCrimes.size(); i++) {
			if(mCrimes.get(i).getId().equals(crimeID)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				Crime crime = mCrimes.get(pos);
				if(crime.getTitle()!=null) {
					setTitle(crime.getTitle());
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public View onCreateView(String name, @NonNull Context context,
			@NonNull AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}
	
	
	
}
