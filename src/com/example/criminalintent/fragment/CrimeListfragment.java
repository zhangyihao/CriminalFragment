package com.example.criminalintent.fragment;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.criminalfragment.R;
import com.example.criminalintent.CrimePageActivity;
import com.example.criminalintent.entity.Crime;
import com.example.criminalintent.entity.CrimeLab;

public class CrimeListfragment extends ListFragment {

	private List<Crime> mCrimes;	
	private String tag = "CrimeListFragment";
	private ListView mListView;
	private TextView mEmptyTextView;
	private boolean mSubtitleVisible;
	
	private CrimeAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		
		mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();
		
//		ArrayAdapter<Crime> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mCrimes);
		mAdapter = new CrimeAdapter(mCrimes);
		setListAdapter(mAdapter);
		setRetainInstance(true);
		mSubtitleVisible = false;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container!=null) {
			container.removeAllViews();
		}
		View v = inflater.inflate(R.layout.fragment_crime_list, container);
		if(mAdapter==null || mAdapter.getCount()==0) {
			mListView = (ListView)v.findViewById(R.id.crime_list);
			mEmptyTextView = new TextView(getActivity());  
			mEmptyTextView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mEmptyTextView.setText(R.string.crime_list_empty);  
			mEmptyTextView.setVisibility(View.GONE);
			((ViewGroup)mListView.getParent()).addView(mEmptyTextView);
			mListView.setEmptyView(mEmptyTextView);
		}
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			if(mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
//		Crime c = (Crime)(getListAdapter().getItem(position));
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
//		Intent intent = new Intent(getActivity(), CrimeActivity.class);
		Intent intent = new Intent(getActivity(), CrimePageActivity.class);
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(intent);
		Log.d(tag, c.getTitle()+" was clicked");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitl = menu.findItem(R.id.menu_item_show_subtitle);
		if(mSubtitleVisible && showSubtitl!=null) {
			showSubtitl.setTitle(R.string.hide_subtitle);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.getInstance(getActivity()).add(crime);
				Intent intent = new Intent(getActivity(), CrimePageActivity.class);
				intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(intent, 0);
				return true;
			case R.id.menu_item_show_subtitle:
				if(getActivity().getActionBar().getSubtitle() == null) {
					getActivity().getActionBar().setSubtitle(R.string.subtitle);
					mSubtitleVisible = true;
					item.setTitle(R.string.hide_subtitle);
				} else {
					getActivity().getActionBar().setSubtitle(null);
					mSubtitleVisible = false;
					item.setTitle(R.string.show_subtitle);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {

		public CrimeAdapter(List<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
			}
			
			Crime c = getItem(position);
			TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
			TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
			CheckBox solvedCheckbox = (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckbox);
			
			titleTextView.setText(c.getTitle());
			dateTextView.setText(DateFormat.format("yyyy/MM/dd HH:mm:ss", c.getDate()));
			solvedCheckbox.setChecked(c.isSolved());
			
			return convertView;
		}
		
		
	}
	
	
}
