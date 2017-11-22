package com.zxj.music.fusion.util;

import android.view.*;
import android.widget.*;
import android.content.Context;
import com.zxj.music.fusion.*;
import android.app.*;
import android.view.View.*;

public class UiUtils
{

	private static final Context context=App.app_context;

	//风车戏法：通过在图片旋转过程中切换图片营造过渡效果
	public static void windmillTrick(final ImageView v,final int imageRes,int rotation){
		v.animate().rotation(rotation).setDuration(250)
			.setInterpolator(App.linear_out_slow_in)
			.start();
		v.postDelayed(new Runnable(){
				@Override
				public void run()
				{
					v.setImageResource(imageRes);
				}
			}, 50);
	}
	
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
	    new AlertDialog.Builder(context,R.style.StyleDialog)
		.setTitle(resTitle)
		.setView(tv);
	}
	
	
	public static AlertDialog.Builder dialog(Context context,int resTitle,int resMsg){
		return new AlertDialog.Builder(context,R.style.StyleDialog).setTitle(resTitle).setMessage(resMsg);
	}
	
	public static AlertDialog.Builder checkableDialog(Context context,String resTitle,String resMsg,final String key){
		ViewGroup vg=(ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_dialog_checkable,null,false);
		TextView tvMsg=(TextView) vg.getChildAt(0);
		final CheckedTextView checkBox=(CheckedTextView) vg.getChildAt(1);
		tvMsg.setText(resMsg);
		checkBox.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
				   checkBox.setChecked(!checkBox.isChecked());
				   App.app_pref_editor.putBoolean(key,checkBox.isChecked()).commit();
				}
			});
			
	   return new AlertDialog.Builder(context,R.style.StyleDialog).setTitle(resTitle).setView(vg);
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
