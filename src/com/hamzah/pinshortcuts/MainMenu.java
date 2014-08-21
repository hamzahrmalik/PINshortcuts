package com.hamzah.pinshortcuts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		if(!new File(PatternShortcuts.lastWrongPattern).exists()){
			FileOutputStream fos = null;
			try {
				fos = openFileOutput("lastwrongpattern.txt", Context.MODE_WORLD_WRITEABLE);
				fos.write(Keys.NOT_SET.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!new File(PatternShortcuts.lastPattern).exists()){
			FileOutputStream fos = null;
			try {
				fos = openFileOutput("lastpattern.txt", Context.MODE_WORLD_WRITEABLE);
				fos.write(Keys.NOT_SET.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		SharedPreferences pref = getSharedPreferences(Keys.PREF, Context.MODE_WORLD_READABLE);
		Editor editor = pref.edit();
		editor.putBoolean(Keys.EDITING, false);
		editor.apply();
	}
	
	public void PIN(View v){
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
	
	public void pattern(View v){
		Intent i = new Intent(this, PatternShortcuts.class);
		startActivity(i);
	}
	
	public void XDA(View v){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/xposed/modules/mod-pinshortcuts-passwords-shortcuts-t2813945"));
		startActivity(browserIntent);
	}
}
