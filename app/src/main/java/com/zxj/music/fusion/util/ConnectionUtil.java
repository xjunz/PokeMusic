package com.zxj.music.fusion.util;
import android.content.*;
import android.net.*;
import com.zxj.music.fusion.*;

public class ConnectionUtil
{
	
	public static boolean hasInternetConnection() {  

		ConnectivityManager manager = (ConnectivityManager) App.app_context  
			.getApplicationContext().getSystemService(  
			Context.CONNECTIVITY_SERVICE);  

		if (manager == null) {  
			return false;  
		}  

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();  

		if (networkinfo == null || !networkinfo.isAvailable()) {  
			return false;  
		}  

		return true;  
    }  
	
	public static boolean isMobileData(){
		ConnectivityManager manager = (ConnectivityManager) App.app_context  
			.getApplicationContext().getSystemService(  
			Context.CONNECTIVITY_SERVICE);  

		if (manager == null) {  
			return true;  
		}  
		
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();  

		if (networkinfo !=null&&networkinfo.getType()!=ConnectivityManager.TYPE_WIFI) {  
			return true;  
		}  

		return false;  
	}
}
