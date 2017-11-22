package com.zxj.music.fusion.task;
import android.os.*;
import com.zxj.music.fusion.bean.*;
import java.util.*;
import com.zxj.music.fusion.util.*;

public class LoadMoreTask extends AsyncTask<Object,Void,ArrayList<SongInfo>> 
{

	

	private LoadMoreListener listener;

	public LoadMoreTask setLoadMoreListener(LoadMoreListener listener)
	{
		this.listener = listener;
		return this;
	}
	public interface LoadMoreListener
	{
		void onPreLoad(); 
		void onPostLoad(ArrayList<SongInfo> result,boolean loadUp);
	}

	private ArrayList<SongInfo> list;
	

	public LoadMoreTask(ArrayList<SongInfo> list)
	{
		this.list = list;
		
	}


	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (listener != null)
		{
			listener.onPreLoad();
		}
	}


	private boolean loadUp;
	private int getNextPage(int size){
		if(size%20>0){
			return size/20+2;
		}else{
			return size/20+1;
		}
	}
	@Override
	protected ArrayList<SongInfo> doInBackground(Object...p1)
	{
		    int size=list.size();
			TaskUtil.loadData(TaskUtil.makeUrl(getNextPage(list.size()), TaskUtil.vendor), list);
			if(list.size()==size){
				loadUp=true;
			}
			return list;
	}

	@Override
	protected void onPostExecute(ArrayList<SongInfo> result)
	{

		
		super.onPostExecute(result);
		if (listener != null)
		{
			
			listener.onPostLoad(result,loadUp);
		}
	}


}
