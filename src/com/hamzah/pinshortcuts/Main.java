package com.hamzah.pinshortcuts;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	String pname = "com.hamzah.pinshortcuts";
	XSharedPreferences pref;
	Context c = null;

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		  XposedHelpers.findAndHookMethod(
		  "com.android.internal.widget.LockPatternUtils", lpparam.classLoader,
		  "checkPassword", String.class, new XC_MethodHook() {
		  
		  @Override
		  protected void beforeHookedMethod(MethodHookParam param)
		  throws Throwable {
		  
		  String pass = (String) param.args[0];
		  log("pin entered: " + pass);
		  
		  c = (Context) XposedHelpers.getObjectField( param.thisObject,
		  "mContext");
		  
		  XSharedPreferences sp = new XSharedPreferences(pname, pass);
		  String pname_to_launch = sp.getString(Keys.PNAME, Keys.NOT_SET);
		  
		  if (!pname_to_launch.equals(Keys.NOT_SET)) {
			  Intent intent = getIntent(pname_to_launch, c);
			  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  c.startActivity(intent);
			  param.args[0]=pref.getString(Keys.NORMAL_PASSWORD, "1234");
		  }
		  }
		  });
		 
		XposedHelpers.findAndHookMethod(
				"com.android.internal.widget.LockPatternUtils",
				lpparam.classLoader, "checkPattern", List.class,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						pref.reload();
						List res = (List) param.args[0];
						String patternEntered = ".";
						int i = 0;
						if(res!=null)
						while(i<res.size()){
							patternEntered = patternEntered + res.get(i);
							i++;
						}
						log("PATTERN ENTERED " + patternEntered);
						
						File lastPattern = new File(PatternShortcuts.lastPattern);
						FileWriter lastPatternWriter = new FileWriter(lastPattern);
						lastPatternWriter.write(patternEntered);
						lastPatternWriter.close();
						
						if(!pref.getString(Keys.NORMAL_PATTERN, "").equals(patternEntered)){
							log("Pattern is wrong!" + pref.getString(Keys.NORMAL_PATTERN, ""));
							File lastWrongPattern = new File(PatternShortcuts.lastWrongPattern);
							FileWriter lastWrongPatternWriter = new FileWriter(lastWrongPattern);
							lastWrongPatternWriter.write(patternEntered);
							lastWrongPatternWriter.close();
						}
						
						if(pref.getBoolean(Keys.EDITING, false)){
							param.setResult(true);
						}
						else{
							c = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
							
							XSharedPreferences pref = new XSharedPreferences(pname, patternEntered);
							String pname_to_launch = pref.getString(Keys.PNAME, Keys.NOT_SET);
							log(pname_to_launch);
							
							if (!pname_to_launch.equals(Keys.NOT_SET)) {
								if (c == null)
									log("NO CONTEXT");
								else {
									Intent intent = getIntent(pname_to_launch, c);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									c.startActivity(intent);
									param.setResult(true);
								}
							}
						}
					}
				});
	}

	public Intent getIntent(String pname, Context c) {
		Intent i = null;
		PackageManager manager = c.getPackageManager();
		try {
			i = manager.getLaunchIntentForPackage(pname);
			if (i == null)
				throw new PackageManager.NameNotFoundException();
			i.addCategory(Intent.CATEGORY_LAUNCHER);
		} catch (PackageManager.NameNotFoundException e) {

		}
		return i;
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		pref = new XSharedPreferences(pname, Keys.PREF);
	}
	
	public void log(String string) {
		XposedBridge.log(string);
	}
}