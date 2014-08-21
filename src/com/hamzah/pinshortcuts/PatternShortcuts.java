package com.hamzah.pinshortcuts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PatternShortcuts extends Activity {
	
	SharedPreferences pref;
	
	public static final String lastWrongPattern = "/data/data/com.hamzah.pinshortcuts/files/lastwrongpattern.txt";
	public static final String lastPattern = "/data/data/com.hamzah.pinshortcuts/files/lastpattern.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pattern_shortcuts);
	}
	
	@Override
	protected void onResume(){
		checkNormalPassword();
		super.onResume();
	}
	
	@SuppressWarnings("deprecation")
	public void checkNormalPassword() {
		pref = getSharedPreferences(Keys.PREF, Context.MODE_WORLD_READABLE);
		if(pref.getString(Keys.NORMAL_PATTERN, "").equals(""))
			setNormalPassword(null);
	}
	
	public void setNormalPassword(View v){
		final Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Pattern Shortcuts");
		builder.setMessage("The mod needs to know your normal pattern, so it can auto-unlock the device after opening a shortcut.\nPlease lock your device then unlock it. The pattern you enter to unlock will be recorded and saved. Press OK AFTER you have locked and unlocked your device");
		builder.setPositiveButton("OK", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File lastPatternFile = new File(lastPattern);
				String lastPattern = "";
				try {
					lastPattern = new Scanner(lastPatternFile).useDelimiter("\\A").next();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Editor editor = pref.edit();
				editor.putString(Keys.NORMAL_PATTERN, lastPattern);
				editor.apply();
			}
		});
		builder.show();
	}
	
	public void addShortcut(View v){
		Intent i = new Intent(this, ShortcutCreatorPattern.class);
		startActivity(i);
	}
	
	public void removeShortcut(View v){
		Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Remove shortcut");
		builder.setMessage("Lock your phone and draw a shortcut. The shortcut you draw will be deleted when you press 'Done!'");
		Editor editor = pref.edit();
		editor.putBoolean(Keys.EDITING, true);
		editor.apply();
		builder.setPositiveButton("Done!", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = pref.edit();
				editor.putBoolean(Keys.EDITING, false);
				editor.apply();
				File lastWrongPatternFile = new File(lastWrongPattern);
				String lastPattern = "";
				try {
					lastPattern = new Scanner(lastWrongPatternFile).useDelimiter("\\A").next();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				delete(lastPattern);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	public void delete(String pattern){
		@SuppressWarnings("deprecation")
		final SharedPreferences pref = getSharedPreferences(pattern, Context.MODE_WORLD_READABLE);
		final Context c = this;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete shortcut");
		builder.setMessage("Do you want to delete this shortcut? (" + pref.getString(Keys.PNAME, "") + ")");
		builder.setPositiveButton("Delete", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = pref.edit();
				editor.putString(Keys.PNAME, Keys.NOT_SET);
				editor.apply();
				Toast.makeText(c, "Shortcut deleted", Toast.LENGTH_SHORT).show();
			}
		});
		builder.show();
	}
}
