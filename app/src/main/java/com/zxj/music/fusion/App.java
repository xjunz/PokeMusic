package com.zxj.music.fusion;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.view.animation.*;
import java.lang.reflect.*;

public class App extends Application
{
   public static Context app_context;
   public static int app_width,app_height;
   public static Interpolator fast_out_slow_in,linear_out_slow_in;
   public static int navigation_bar_height;
   public static SharedPreferences app_pref;
   public static SharedPreferences.Editor app_pref_editor;
	public static String var_not_show_download_confirmation_dialog="var_show_download_confirmation_dialog";
  
	@Override
	public void onCreate()
	{
		super.onCreate();
		app_context=getApplicationContext();
		app_width=getResources().getDisplayMetrics().widthPixels;
		app_height=getResources().getDisplayMetrics().heightPixels;
		app_pref=getSharedPreferences("data",MODE_PRIVATE);
		app_pref_editor=app_pref.edit();
		 fast_out_slow_in=AnimationUtils.loadInterpolator(app_context,android.R.interpolator.fast_out_slow_in);
		linear_out_slow_in=AnimationUtils.loadInterpolator(app_context,android.R.interpolator.linear_out_slow_in);
		cm=(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	}
	
	private static  ClipboardManager cm;
	public static void copyToClipborad(String str){
		cm.setText(str);
	}
	
	
}
