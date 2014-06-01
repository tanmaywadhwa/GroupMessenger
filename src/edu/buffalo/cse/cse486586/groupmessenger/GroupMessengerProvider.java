package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.*;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider{

	//public static final String Filename = null;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// You do not need to implement this.
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// You do not need to implement this.
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		/*
		 * TODO: You need to implement this method. Note that values will have two columns (a key
		 * column and a value column) and one row that contains the actual (key, value) pair to be
		 * inserted.
		 * 
		 * For actual storage, you can use any option. If you know how to use SQL, then you can use
		 * SQLite. But this is not a requirement. You can use other storage options, such as the
		 * internal storage option that I used in PA1. If you want to use that option, please
		 * take a look at the code for PA1.
		 */

		// Using a file storage option as used by steveko in Assignment 1.

		//start
		String Filename = ""+values.get("key");
		String string = values.get("value").toString();
		File Location = new File(getContext().getFilesDir().getAbsolutePath());

		try {
			File fout=new File(Location+"/"+Filename);
			FileOutputStream outputStream = new FileOutputStream(fout);
			//Log.v(Filename+" Write", string);
			outputStream.write(string.getBytes());
			outputStream.close();

		} catch (Exception e) {
			Log.e("File not written", Filename);
			Log.e("Stacktrace",e.getMessage());
		}
		//end

		return uri;
	}

	@Override
	public boolean onCreate() {
		// If you need to perform any one-time initialization task, please do it here.
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		/*
		 * TODO: You need to implement this method. Note that you need to return a Cursor object
		 * with the right format. If the formatting is not correct, then it is not going to work.
		 * 
		 * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
		 * still need to be careful because the formatting might still be incorrect.
		 * 
		 * If you use a file storage option, then it is your job to build a Cursor * object. I
		 * recommend building a MatrixCursor described at:
		 * http://developer.android.com/reference/android/database/MatrixCursor.html
		 */
		//Log.v("Value of key", selection);
		MatrixCursor mc = new MatrixCursor(new String[]{"key","value"});
		File Location = new File(getContext().getFilesDir().getAbsolutePath());
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String column[] = new String[2];
		String s;
		column[0] = selection;
		try{
			File fin=new File(Location+"/"+selection);
			FileInputStream inputStream = new FileInputStream(fin);
			br=new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		catch(IOException e){
			Log.e("error while reading file",e.getMessage());
		}
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		s=sb.toString();
		column[1] = s;

		mc.addRow(column);
		//Log.v("Read "+ selection,s);


		return mc;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// You do not need to implement this.
		return 0;
	}
}
