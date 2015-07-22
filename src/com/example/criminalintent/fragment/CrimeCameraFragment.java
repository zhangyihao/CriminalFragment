package com.example.criminalintent.fragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.example.criminalfragment.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private Camera mCamera;
	private SurfaceView mSufaceView;
	private Button mTakePictureButton;
	private View mProgressContainer;
	
	public static final String EXTRA_PHOTO_FILENAME = "com.zhangyihao.crimeintent.photo_filename"; 
	
	private String TAG = "CrimeCameraFragment";
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);
		
		mSufaceView = (SurfaceView)view.findViewById(R.id.crime_camera_sufaceView);
		mTakePictureButton = (Button)view.findViewById(R.id.crime_camera_takePictureButton);
		mProgressContainer = view.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);
		
		SurfaceHolder holder = mSufaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(mCamera!=null) {
					mCamera.stopPreview();
				}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if(mCamera!=null) {
					try {
						mCamera.setPreviewDisplay(holder);
					} catch (IOException e) {
						Log.e(TAG, "ERROR setting up preview display", e);
					}
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if(mCamera==null)
					return;
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);
				s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});
		
		mTakePictureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCamera!=null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		
		return view;
	}
	
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for(Size s : sizes) {
			int area = s.width * s.height;
			if(area>largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mCamera!=null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}
	
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String fileName = UUID.randomUUID().toString()+".jpg";
			FileOutputStream output = null;
			boolean success = true;
			try {
				output = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
				output.write(data);
			} catch (IOException e) {
				Log.e(TAG, "Error writing to file "+fileName, e);
				success = false;
			} finally {
				if(output!=null) {
					try {
						output.close();
					} catch (IOException e) {
						Log.e(TAG, "Error closing file "+fileName, e);
						success = false;
					} finally  {
						output = null;
					}
				}
			}
			
			if(success) {
				Log.i(TAG, "JPEG saved at "+fileName);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_PHOTO_FILENAME, fileName);
				getActivity().setResult(Activity.RESULT_OK, intent);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
	
}
