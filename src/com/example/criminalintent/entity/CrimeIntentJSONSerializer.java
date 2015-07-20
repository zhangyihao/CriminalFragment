package com.example.criminalintent.entity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

public class CrimeIntentJSONSerializer {

	private Context mContext;
	private String mFileName;
	
	public CrimeIntentJSONSerializer(Context context, String fileName) {
		mContext = context;
		mFileName = fileName;
	}
	
	public void saveCrimes(List<Crime> crimes) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for(Crime crime : crimes) {
			array.put(crime.toJSONObject());
		}
		Writer writer = null;
		try {
			OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput(this.mFileName, Context.MODE_PRIVATE+Context.MODE_APPEND));
			out.write(array.toString());
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					writer = null;
				}
			}
		}
	}
}
