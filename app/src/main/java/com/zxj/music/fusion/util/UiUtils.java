package com.zxj.music.fusion.util;

import android.view.*;
import android.widget.*;
import android.content.Context;
import com.zxj.music.fusion.*;
import android.app.*;

public class UiUtils
{

	private static final Context context=App.app_context;

	
	public static int dip2px(int dipValue)
	{
		return (int)(dipValue * context.getResources().getDisplayMetrics().density + .5);
	}

	
	public static void visible(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.VISIBLE);
		}
	}
	public static void invisible(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.INVISIBLE);
		}
	}
	public static void disable(View...views)
	{
		for (View v:views)
		{
			v.setEnabled(false);
		}
	}
	
	public static void enable(View...views)
	{
		for (View v:views)
		{
			v.setEnabled(true);
		}
	}
	
	public static void gone(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.GONE);
		}
	}

	
	public static  AlertDialog.Builder autoLinkDialog(Context context,int resTitle,int resMsg){
		TextView tv=(TextView) LayoutInflater.from(context).inflate(R.layout.layout_dialog_textview,null,false);
		tv.setText(resMsg);
	return	
	    new AlertDialog.Builder(context)
		.setTitle(resTitle)
		.setView(tv);
	}
	

	public static void toast(String str)
	{
		Toast.makeText(context, str, 0).show();
	}
	
	private static Toast mToast=Toast.makeText(context, "", 0);

	public static void rawToast(String str)
	{
		TextView tv=(TextView)((ViewGroup)mToast.getView()).getChildAt(0);
		if (mToast.getView().isShown())
		{
			mToast.setText(tv.getText() + "\n" + str);
		}
		else
		{
			mToast.setText(str);
		}
		 mToast.show();
	}

	public static void instantToast(Object...objs)
	{
		
			for (Object obj:objs)
			{
				if (obj == null)
				{
					rawToast("null");
				}
				else if (obj instanceof Object[])
				{
					instantToast(obj);
				}
				else
				{
					rawToast(obj.toString());
				}
			}
		}
	
	
	
}
