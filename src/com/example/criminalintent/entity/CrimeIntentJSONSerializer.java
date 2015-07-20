package com.example.criminalintent.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;

public class CrimeIntentJSONSerializer {

	private Context mContext;
	private String mFileName;
	
	public CrimeIntentJSONSerializer(Context context, String fileName) {
		mContext = context;
		mFileName = fileName;
	}
	
	public List<Crime> loadCrimes() throws IOException, JSONException {
		List<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.openFileInput(mFileName)));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine())!=null) {
				sb.append(line);
			}
			if(sb.length()>0) {
				JSONArray array = (JSONArray) new JSONTokener(sb.toString()).nextValue();
				for(int i=0; i<array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) {
				reader.close();
				reader = null;
			}
		}
		return crimes;
	}
	
	public void saveCrimes(List<Crime> crimes) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for(Crime crime : crimes) {
			array.put(crime.toJSONObject());
		}
		Writer writer = null;
		try {
			OutputStream output = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(output);
			writer.write(array.toString());
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
				} finally {
					writer = null;
				}
			}
		}
	}
	
	public List<Crime> loadCrimesWithSDCard() throws IOException, JSONException {
		List<Crime> crimes = new ArrayList<Crime>();
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return crimes;
		}
		File sdCardDir = Environment.getExternalStorageDirectory();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(sdCardDir.getCanonicalFile()+mFileName)));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine())!=null) {
				sb.append(line);
			}
			if(sb.length()>0) {
				JSONArray array = (JSONArray) new JSONTokener(sb.toString()).nextValue();
				for(int i=0; i<array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) {
				reader.close();
				reader = null;
			}
		}
		return crimes;
	}
	
	public void saveCrimesWithSDCard(List<Crime> crimes) throws JSONException, IOException {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			JSONArray array = new JSONArray();
			for(Crime crime : crimes) {
				array.put(crime.toJSONObject());
			}
			File sdCardDir = Environment.getExternalStorageDirectory();
			File file = new File(sdCardDir.getCanonicalPath()+mFileName);
			RandomAccessFile raf= new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(array.toString().getBytes());
			raf.close();
		}
	}
}
