package com.zxj.music.fusion.task;
import android.os.*;
import com.zxj.music.fusion.bean.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import com.zxj.music.fusion.util.*;

public class SearchTask extends AsyncTask<Void,Void,ArrayList<SongInfo>[]>
{

	private SearchListener listener;

	public interface SearchListener
	{
		void onPreSearch();
		void onPostSearch(ArrayList<SongInfo>[] result);
	}


	public SearchTask setSearchListener(SearchListener listener)
	{
		this.listener = listener;
		return this;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (listener != null)
		{
			listener.onPreSearch();
		}
	}


	
	@Override
	protected ArrayList<SongInfo>[] doInBackground(Void...voids)
	{
		ArrayList<SongInfo>[] infoList=new ArrayList[3];
		for (int i=1;i <=3;i++)
		{
			infoList[i-1]=TaskUtil.loadData(TaskUtil.makeUrl(1, i), new ArrayList<SongInfo>());
		}
		return infoList;
	}


	@Override
	protected void onPostExecute(ArrayList<SongInfo>[] result)
	{
		super.onPostExecute(result);
		if (listener != null)
		{
			listener.onPostSearch(result);
		}
	}


}

