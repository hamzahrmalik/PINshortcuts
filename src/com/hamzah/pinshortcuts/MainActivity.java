package com.hamzah.pinshortcuts;

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
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume(){
		checkNormalPassword();
		super.onResume();
	}
	
	@SuppressWarnings("deprecation")
	public void checkNormalPassword() {
		pref = getSharedPreferences(Keys.PREF, Context.MODE_WORLD_READABLE);
		if(pref.getString(Keys.NORMAL_PASSWORD, "").equals(""))
			setNormalPassword(null);
	}
	
	public void setNormalPassword(View v){
		final Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("PIN Shortcuts");
		builder.setMessage(R.string.normal_password);
		final EditText input = new EditText(c);
		builder.setView(input);
		input.setHint(R.string.normal_password_eg);
		builder.setPositiveButton("Save", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = pref.edit();
				editor.putString(Keys.NORMAL_PASSWORD, input.getText().toString());
				editor.apply();
				Toast.makeText(c, "Password saved. You can change it from the Settings page", Toast.LENGTH_LONG).show();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	public void addShortcut(View v){
		Intent i = new Intent(this, ShortcutCreator.class);
		startActivity(i);
	}
	
	public void removeShortcut(View v){
		final Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(R.string.remove_shortcut);
		builder.setMessage("Type a password to remove the shortcut associated with it");
		final EditText input = new EditText(c);
		builder.setView(input);
		builder.setPositiveButton("Ok", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				@SuppressWarnings("deprecation")
				SharedPreferences sp = getSharedPreferences(input.getText().toString(), Context.MODE_WORLD_READABLE);
				Editor editor = sp.edit();
				editor.putString(Keys.PNAME, Keys.NOT_SET);
				editor.apply();
			}
		});
		builder.show();
	}
}
