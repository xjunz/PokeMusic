package com.zxj.music.fusion.util;

import com.zxj.music.fusion.bean.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class TaskUtil
{
	public  static final String task_base_url="https://www.tikitiki.cn";
	public  static final String task_result_json_url=task_base_url + "/searchjson.do?keyword=%1$s&page=%2$s&type=%3$s";

	public  static  int vendor=1;
	
	public  static String keyword="";


	public static String  makeUrl(int page,int vendor)
	{
		String url="";
		try
		{
		url= String.format(task_result_json_url, URLEncoder.encode(keyword, "UTF-8"), page, vendor);
		}
		catch (UnsupportedEncodingException e)
		{}
		return url;
	}
	
	public static String formatSongDuration(long duration)
	{
        int min=(int) duration / 60000;
		int sec=(int) ((duration - min * 60000) / 1000);
		return min + ":" + (sec < 10 ?"0" + sec: sec);
	}

	public static ArrayList<SongInfo> loadData(String strUrl, ArrayList<SongInfo> songInfoList)
	{
		try
		{
			JSONObject jobj=null;
			JSONArray jarray=null;
			byte[] data = new byte[1024];  
			int len = 0; 
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
			InputStream inStream = conn.getInputStream();  
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();  

			while ((len = inStream.read(data)) != -1)
			{  
				outStream.write(data, 0, len);  
			}  
			inStream.close();  

			JSONTokener parser=new JSONTokener(new String(outStream.toByteArray()));
			jobj = (JSONObject) parser.nextValue();
			jarray = jobj.getJSONArray("data");
			for (int a=0;a < jarray.length();a++)
			{ 
				songInfoList.add(SongInfo.parseFromJSONObject(jarray.getJSONObject(a)));
			}
		}
		catch (Exception e)
		{

		}

		return songInfoList;
	}




}
