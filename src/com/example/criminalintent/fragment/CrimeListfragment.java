package com.example.criminalintent.fragment;

import java.util.List;

import com.example.criminalfragment.R;
import com.example.criminalintent.CrimeActivity;
import com.example.criminalintent.entity.Crime;
import com.example.criminalintent.entity.CrimeLab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListfragment extends ListFragment {

	private List<Crime> mCrimes;
	
	private String tag = "CrimeListFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.crimes_title);
		
		mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();
		
//		ArrayAdapter<Crime> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mCrimes);
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
//		Crime c = (Crime)(getListAdapter().getItem(position));
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		Intent intent = new Intent(getActivity(), CrimeActivity.class);
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(intent);
//		Log.d(tag, c.getTitle()+" was clicked");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
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
