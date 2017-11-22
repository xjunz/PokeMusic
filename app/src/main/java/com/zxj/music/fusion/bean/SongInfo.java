package com.zxj.music.fusion.bean;
import org.json.*;
import android.text.*;
import com.zxj.music.fusion.util.*;

public class SongInfo
{
	public String songname;
	public String singer;
	public String album;
	public String vendor_type;
	public String id_s128;
	public String id_s320;
	public String id_SQ;
	public String id_mv;
	public String id_sogg;
	public String formated_duration;

	public static final String QUALITY_S128="s128";
	public static final String QUALITY_S320="s320";
	public static final String QUALITY_SOGG="sogg";
	public static final String QUALITY_SQ="SQ";
	

	public static final int VENDOR_TENCENT=1;
	public static final int VENDOR_NETEASE=2;
	public static final int VENDOR_KUGOU=3;

	public SongInfo()
	{}


	public static  SongInfo parseFromJSONObject(JSONObject jobj)
	{
		SongInfo info=new SongInfo();
		try
		{
			info.album = jobj.getString("album");
			info.formated_duration = jobj.getString("time");
			info.id_mv = jobj.getString("mv");
			info.id_s128 = jobj.getString("s128");
			info.id_s320 = jobj.getString("s320");
			info.id_sogg = jobj.getString("sogg");
			info.id_SQ=jobj.getString("SQ");
			info.singer = jobj.getString("singer");
			info.songname = jobj.getString("name");
		}
		catch (JSONException e)
		{   

		}
		return info;
	}

	private String getId(String quality)
	{
		switch (quality)
		{
			case QUALITY_S128:return id_s128;
			case QUALITY_S320:return id_s320;
			case QUALITY_SOGG:return id_sogg;
			case QUALITY_SQ:return id_SQ;
		}
		return null;
	}

	public String getAudioSourceUrl(String quality)
	{
		
		return String.format(TaskUtil.task_download_url,
							 quality,getId(quality), TaskUtil.vendor);
	}
	
	public String getMVUrl(){
		return String.format(TaskUtil.task_mv_url,id_mv,TaskUtil.vendor);
	}
}

