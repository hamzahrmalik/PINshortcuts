package com.hamzah.pinshortcuts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShortcutCreator extends Activity {
	
	ListView app_list;
	
	ArrayList<String> installed_apps;
	ArrayList<PInfo> pinfos;
	ArrayAdapter<String> adapter;
	AlertDialog alert;
	
	EditText password_input;
	TextView chosen_app;
	
	class PInfo {
	    private String appname = "";
	    private String pname = "";
	}
	
	PInfo selected = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shortcut_creator);
		
		load();
		
		app_list = new ListView(this);
	}
	
	public void selected(final int pos){
		selected = pinfos.get(pos);
		alert.cancel();
		if(selected!=null)
			chosen_app.setText("You have chosen " + selected.appname);
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
	
	public void load(){
		chosen_app = (TextView) findViewById(R.id.chosen_app);
		password_input = (EditText) findViewById(R.id.password_to_trigger);
	}
	
	public void save(View v){
		String password = password_input.getText().toString();
		@SuppressWarnings("deprecation")
		SharedPreferences pref = getSharedPreferences(password, Context.MODE_WORLD_READABLE);
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
}