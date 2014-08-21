package com.hamzah.pinshortcuts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity {
	
	SharedPreferences pref;
	
	EditText normal_password_input;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		pref = this.getSharedPreferences(Keys.PREF, Context.MODE_WORLD_READABLE);
		
		normal_password_input = (EditText) findViewById(R.id.normal_password_input);
		
		load();
	}
	
	public void save(View v){
		Editor editor = pref.edit();
		editor.putString(Keys.NORMAL_PASSWORD, normal_password_input.getText().toString());
		editor.apply();
		
		Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
		
		finish();
	}
	
	public void load(){
		normal_password_input.setText(pref.getString(Keys.NORMAL_PASSWORD, ""));
	}
}
