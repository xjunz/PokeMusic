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
   public static Interpolator fast_out_slow_in;
   public static int navigation_bar_height;
	@Override
	public void onCreate()
	{
		super.onCreate();
		app_context=getApplicationContext();
		app_width=getResources().getDisplayMetrics().widthPixels;
		app_height=getResources().getDisplayMetrics().heightPixels;
        fast_out_slow_in=AnimationUtils.loadInterpolator(app_context,android.R.interpolator.fast_out_slow_in);
		cm=(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	}
	
	private static  ClipboardManager cm;
	public static void copyToClipborad(String str){
		cm.setText(str);
	}
	
	
}
