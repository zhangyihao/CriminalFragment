package com.example.criminalintent.fragment;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.criminalfragment.R;
import com.example.criminalintent.CrimeCameraActivity;
import com.example.criminalintent.entity.Crime;
import com.example.criminalintent.entity.CrimeLab;
import com.example.criminalintent.entity.Photo;
import com.example.criminalintent.entity.PictureUtils;

public class CrimeFragment extends Fragment {

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private Button mAddButton;
	private ImageButton mTakePictureButton;
	private ImageView mPhotoView;
	
	private int operator;
	
	private ActionMode mActionMode = null;
	
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;
	
	private final String TAG = "CrimeFragment";

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
		setHasOptionsMenu(true);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//生成View，第三个参数，通知布局管理器是否将生成的视图添加给父视图，此处为false，将通过代码方式添加视图
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		
		switch (operator) {
		case CrimeFragmentConst.CRIM_OPERATOR_SHOW:
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB && NavUtils.getParentActivityName(getActivity())!=null) {
				v.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						if(mActionMode!=null) {
							return false;
						}
						mActionMode = getActivity().startActionMode(new ActionMode.Callback() {
							
							@Override
							public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
								return false;
							}
							
							@Override
							public void onDestroyActionMode(ActionMode mode) {
							}
							
							@Override
							public boolean onCreateActionMode(ActionMode mode, Menu menu) {
								mode.getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
								return true;
							}
							
							@Override
							public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
								switch(item.getItemId()) {
								case R.id.menu_item_delete_crime:
									CrimeLab.getInstance(getActivity()).delete(mCrime);
									mode.finish();
									NavUtils.navigateUpFromSameTask(getActivity());
									return true;
								default:
									return false;
								}
							}
						});
						return false;
					}
				});
			} else {
				registerForContextMenu(v);
			}
			break;
		default:
			break;
		}
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB && NavUtils.getParentActivityName(getActivity())!=null) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mDateButton = (Button)v.findViewById(R.id.crime_date);
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
		mAddButton = (Button)v.findViewById(R.id.crime_add);
		mTakePictureButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
		mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
		
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || //前置摄像头
				pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)|| //后置摄像头
				Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD || Camera.getNumberOfCameras()>0;
		if(!hasACamera) {
			mTakePictureButton.setEnabled(false);
		}
		mTakePictureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(intent, REQUEST_PHOTO);
			}
		});
		
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
						NavUtils.navigateUpFromSameTask(getActivity());
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
		
		mPhotoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if(p==null) {
					return;
				}
				
				FragmentManager fm = getActivity().getSupportFragmentManager(); 
				String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_DATE);
				
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
		} else if(requestCode == REQUEST_PHOTO) {
			String fileName = (String)data.getSerializableExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if(fileName!=null) {
				Photo photo = new Photo(fileName);
				mCrime.setPhoto(photo);
				showPhoto();
				Log.i(TAG, "Crime: "+mCrime.getTitle()+"has a photo, fileName:"+fileName);
			}
		}
	}
	
	public void updateDate() {
//		mDateButton.setText(DateFormat.format("yyyy/MM/dd HH:mm:ss", mCrime.getDate()));
		mDateButton.setText(DateFormat.format("yyyy/MM/dd", mCrime.getDate()));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}

	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.getInstance(getActivity()).saveCrime();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.getInstance(getActivity()).delete(mCrime);
			NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
	
	private void showPhoto() {
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if(p!=null) {
			String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
			b = PictureUtils.getScaleDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
}
