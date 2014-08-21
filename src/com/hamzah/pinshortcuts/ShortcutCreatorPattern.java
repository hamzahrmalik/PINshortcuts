package com.hamzah.pinshortcuts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShortcutCreatorPattern extends Activity {
	
	SharedPreferences pref;
	
	ListView app_list;
	
	ArrayList<String> installed_apps;
	ArrayList<PInfo> pinfos;
	ArrayAdapter<String> adapter;
	AlertDialog alert;
	
	class PInfo {
	    private String appname = "";
	    private String pname = "";
	}
	
	PInfo selected = null;
	String pattern = "";

	TextView chosen_app;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shortcut_creator_pattern);
		pref = getSharedPreferences(Keys.PREF, Context.MODE_WORLD_READABLE);
		
		chosen_app = (TextView) findViewById(R.id.chosen_app_pattern);
		app_list = new ListView(this);
	}
	
	public void selected(final int pos){
		selected = pinfos.get(pos);
		if(selected!=null){
			chosen_app.setText("You have chosen " + selected.appname);
			alert.cancel();
		}
	}
	
	public ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
	    ArrayList<PInfo> res = new ArrayList<PInfo>();        
	    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
	    for(int i=0;i<packs.size();i++) {
	        PackageInfo p = packs.get(i);
	        if ((!getSysPackages) && (p.versionName == null)) {
	            continue ;
	        }
	        PInfo newInfo = new PInfo();
	        newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
	        newInfo.pname = p.packageName;
	        res.add(newInfo);
	    }
	    return res;
	}
	
	public void save(View v){
		@SuppressWarnings("deprecation")
		SharedPreferences pref = getSharedPreferences(pattern, Context.MODE_WORLD_READABLE);
		Editor editor = pref.edit();
		editor.putString(Keys.PNAME, selected.pname);
		editor.apply();
		
		Toast.makeText(this, "Shortcut saved", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	public void chooseApp(View v){
		pinfos = getInstalledApps(true);
		
		Collections.sort(pinfos, new Comparator<PInfo>() {

            @Override
            public int compare(PInfo lhs, PInfo rhs) {
                return lhs.appname.compareTo(rhs.appname);
            }
        });
		
		installed_apps = new ArrayList<String>();
		int i = 0;
		while(i<pinfos.size()){
			installed_apps.add(pinfos.get(i).appname);
			i++;
		}
		
		adapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                installed_apps);
		app_list.setAdapter(adapter);
		app_list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selected(installed_apps.indexOf(app_list.getItemAtPosition(position)));
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Create Shortcut");
		builder.setMessage("Choose an app to launch");
		builder.setView(app_list);
		builder.setCancelable(false);
		
		alert = builder.create();
		alert.show();	
	}
	
	public void record(View v){
		final Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Record pattern");
		builder.setMessage("Please lock your phone, draw the pattern you want to associate with the shortcut, then draw your real pattern (to unlock). Then press OK");
		builder.setPositiveButton("OK", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File lastWrongPatternFile = new File(PatternShortcuts.lastWrongPattern);
				String lastWrongPattern = "";
				try {
					lastWrongPattern = new Scanner(lastWrongPatternFile).useDelimiter("\\A").next();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				pattern = lastWrongPattern;
			}
		});
		builder.show();
	}
}
