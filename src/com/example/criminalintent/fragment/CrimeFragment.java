package com.example.criminalintent.fragment;

import java.util.Date;
import java.util.UUID;

import com.example.criminalfragment.R;
import com.example.criminalintent.CrimeListActivity;
import com.example.criminalintent.entity.Crime;
import com.example.criminalintent.entity.CrimeLab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class CrimeFragment extends Fragment {

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private Button mAddButton;
	private int operator;
	
	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;

	public static CrimeFragment newInstance(UUID crimeId, int operator) {
		Bundle bundle = new Bundle();
		if(crimeId!=null) {
			bundle.putSerializable(CrimeFragmentConst.EXTRA_CRIME_ID, crimeId);
		}
		bundle.putSerializable(CrimeFragmentConst.EXTRA_CRIME_OPERATOR, operator);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID)getArguments().getSerializable(CrimeFragmentConst.EXTRA_CRIME_ID);
		operator = (Integer)getArguments().getSerializable(CrimeFragmentConst.EXTRA_CRIME_OPERATOR);
		if(crimeId!=null) {
			this.mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//生成View，第三个参数，通知布局管理器是否将生成的视图添加给父视图，此处为false，将通过代码方式添加视图
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mDateButton = (Button)v.findViewById(R.id.crime_date);
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
		mAddButton = (Button)v.findViewById(R.id.crime_add);
		
		switch (this.operator) {
			case CrimeFragmentConst.CRIME_OPERATOR_ADD:
				this.mCrime = new Crime();
				mTitleField.setText("");
				mAddButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mCrime.getTitle()==null || "".equals(mCrime.getTitle())) {
							Toast.makeText(getActivity(), "请填写名称", Toast.LENGTH_SHORT).show();
							return;
						}
						CrimeLab.getInstance(getActivity()).add(mCrime);
						Intent intent = new Intent(getActivity(), CrimeListActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
				});
				break;
			default:
				if(this.mCrime!=null) {
					mTitleField.setText(mCrime.getTitle());
					mDateButton = (Button)v.findViewById(R.id.crime_date);
					mSolvedCheckBox.setChecked(mCrime.isSolved());
					mAddButton.setVisibility(View.GONE);
				}
				break;
		}
		updateDate();
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mTitleField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment datepicker = DatePickerFragment.newInstance(mCrime.getDate());
				datepicker.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				datepicker.show(fm, DIALOG_DATE);
			}
		});
		
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});
		
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode == REQUEST_DATE) {
			Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		}
	}
	
	public void updateDate() {
//		mDateButton.setText(DateFormat.format("yyyy/MM/dd HH:mm:ss", mCrime.getDate()));
		mDateButton.setText(DateFormat.format("yyyy/MM/dd", mCrime.getDate()));
	}
}
