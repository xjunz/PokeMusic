package com.zxj.music.fusion;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.transition.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.zxj.music.fusion.bean.*;
import com.zxj.music.fusion.task.*;
import com.zxj.music.fusion.util.*;
import java.util.*;

import android.support.v4.app.Fragment;

public class ResultFragment extends Fragment 
{

    public ArrayList<SongInfo> songInfoList;
	private static Transition transition=new ChangeBounds();
	private SparseIntArray expandItemArray=new SparseIntArray();
	
	private  SimultaneousAnimator animator=new SimultaneousAnimator();
	private static OnTouchListener touchEater=new OnTouchListener(){

		@Override
		public boolean onTouch(View p1, MotionEvent p2)
		{
			return true;
		}
	};

	public ResultFragment(ArrayList<SongInfo> songInfoList)
	{
		this.songInfoList = songInfoList;
	}

	public ResultFragment()
	{
		super();
	}


	private static MainActivity main;
	private RecyclerView rvResult;

	public void refreshData(ArrayList<SongInfo> songInfoList)
	{

		this.songInfoList = songInfoList;
		//有可能这个Fragment并没有被加载（PagerView的懒加载机制)，所以判断
		if (rvResult == null)
		{
			return;
		}
		expandItemArray.clear();
		this.rvResult.getAdapter().notifyDataSetChanged();

	}


	public RecyclerView getRecyclerView()
	{

		return rvResult;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.main = (MainActivity) getActivity();
		View layout=inflater.inflate(R.layout.layout_fragment, container, false);
		rvResult = (RecyclerView) layout.findViewById(R.id.rvResult);
		rvResult.setLayoutManager(new LinearLayoutManager(main));

		transition.addListener(new TransitionListenerAdapter(){

				public void onTransitionStart(Transition p1)
				{
					rvResult.setOnTouchListener(touchEater);
				}
				public void onTransitionEnd(Transition p1)
				{
					rvResult.setOnTouchListener(null);
					animator.setAnimateMoves(true);
				}

			}).setDuration(200).setInterpolator(App.fast_out_slow_in);

		rvResult.setItemAnimator(animator);

		rvResult.setAdapter(new ResultAdapter());

        rvResult.setOnScrollListener(new RecyclerView.OnScrollListener(){
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy)
				{
					if (dy < 0)
					{
						main.scaleFab(true);
						main.rotateFAB(true);
					}
					else if (dy > 0)
					{
						main.scaleFab(false);
					}
				}
				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState)
				{
					super.onScrollStateChanged(recyclerView, newState);
					if (newState == RecyclerView.SCROLL_STATE_IDLE)
					{
						if (!rvResult.canScrollVertically(1))
						{
							new LoadMoreTask(songInfoList)
								.setLoadMoreListener(main)
								.execute();
						}
						else if (!rvResult.canScrollVertically(-1))
						{
							main.rotateFAB(false);
						}
					}
				}
			});

		return layout;
	}



	class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.SongViewHolder>
	{

		@Override
		public ResultFragment.ResultAdapter.SongViewHolder onCreateViewHolder(ViewGroup p1, int p2)
		{
			return new SongViewHolder(LayoutInflater.from(main).inflate(R.layout.item_song, p1, false));
		}

		@Override
		public void onBindViewHolder(ResultFragment.ResultAdapter.SongViewHolder viewHolder, int position, List<Object> payloads)
		{
			if (payloads.size() == 0)
			{
				super.onBindViewHolder(viewHolder, position, payloads);
			}
			else
			{
				boolean shouldExpand=expandItemArray.get(position, -1) != -1;
				viewHolder.dloadContainer.setVisibility(shouldExpand ?0: 8);
				viewHolder.itemView.setActivated(shouldExpand);
			}

		}



        @Override
		public void onBindViewHolder(ResultFragment.ResultAdapter.SongViewHolder viewHolder, int p2)
		{
			SongInfo info=songInfoList.get(p2);
			viewHolder.tvTitle.setText(info.songname);
			viewHolder.tvSinger.setText(info.singer);
			viewHolder.tvInfo.setText(TextUtils.isEmpty(info.album) ?main.getString(R.string.unknown): info.album);
			boolean shouldExpand=expandItemArray.get(p2, -1) != -1;
			viewHolder.dloadContainer.setVisibility(shouldExpand ?0: 8);
			viewHolder.itemView.setActivated(shouldExpand);
			viewHolder.btnS128.setVisibility(info.id_s128.equals("0") ?8: 0);
			viewHolder.btnS320.setVisibility(info.id_s320.equals("0") ?8: 0);
			viewHolder.btnOgg.setVisibility(info.id_sogg.equals("0") ?8: 0);
			viewHolder.btnMV.setVisibility(info.id_mv.equals("0") ?8: 0);
		}

		@Override
		public int getItemCount()
		{
			return songInfoList.size();
		}


		
		class SongViewHolder extends RecyclerView.ViewHolder implements OnClickListener
		{

		    TextView tvTitle,tvInfo,tvSinger;
			View dloadContainer, ibPlay,btnS128,btnS320,btnOgg,btnMV;
			public SongViewHolder(final View item)
			{
				super(item);
				tvTitle = (TextView) item.findViewById(R.id.item_song_title);
				tvInfo = (TextView) item.findViewById(R.id.item_song_info);
				tvSinger = (TextView) item.findViewById(R.id.item_song_artist);
				ibPlay =  item.findViewById(R.id.ib_play);
				btnS128 = item.findViewById(R.id.btn_dload_s128);
				btnS128.setOnClickListener(this);
				btnS320 = item.findViewById(R.id.btn_dload_s320);
				btnS320.setOnClickListener(this);
				btnOgg = item.findViewById(R.id.btn_dload_ogg);
				btnOgg.setOnClickListener(this);
				btnMV = item.findViewById(R.id.btn_dload_mv);
				btnMV.setOnClickListener(this);
				dloadContainer = item.findViewById(R.id.rl_dload_container);
				ibPlay.setOnClickListener(this);
				item.setOnClickListener(this);

			}

			@Override
			public void onClick(View p1)
			{
				final int pos=getAdapterPosition();
				final SongInfo info=songInfoList.get(pos);
				switch (p1.getId())
				{
					case R.id.ib_play:
						if (ConnectionUtil.isMobileData())
						{
							new AlertDialog.Builder(main).setMessage(R.string.noti_use_mobile_data)
								.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										main.playMusic(songInfoList.get(pos));
									}
								}).show();
						}
						else
						{
							main.playMusic(songInfoList.get(getAdapterPosition()));
						}
						break;
					case R.id.item_song:
						if (expandItemArray.get(pos, -1) == -1)
						{
							expandItemArray.put(pos, pos);
						}
						else
						{
							expandItemArray.delete(pos);
						}
						TransitionManager.beginDelayedTransition(rvResult, transition);
						animator.setAnimateMoves(false);
						notifyItemChanged(pos, 0);
						break;
					case R.id.btn_dload_s320:
						DownloadUtils.download(info.getAudioSourceUrl(SongInfo.QUALITY_S320), DownloadUtils.PATH_NETEASE_MUSIC + info.songname
											   , info.songname + ".mp3", info.songname, info.songname+"-"+info.singer);
						break;
					case R.id.btn_dload_s128:
						DownloadUtils.download(info.getAudioSourceUrl(SongInfo.QUALITY_S128), DownloadUtils.PATH_NETEASE_MUSIC + info.songname
											   , info.songname + ".mp3", info.songname, info.songname+"-"+info.singer);
						break;
					case R.id.btn_dload_ogg:
						DownloadUtils.download(info.getAudioSourceUrl(SongInfo.QUALITY_SOGG), DownloadUtils.PATH_NETEASE_MUSIC + info.songname
											   , info.songname + ".ogg", info.songname, info.songname+"-"+info.singer);
					
					case R.id.btn_dload_mv:
						Intent it = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.parse(info.getMVUrl());
						it.setDataAndType(uri, "video/mp4");
						main.startActivity(it);
					
				}
			}
		}

	}


	static class SimultaneousAnimator extends DefaultItemAnimator
	{

		private boolean animateMoves = true;

		public SimultaneousAnimator()
		{
            super();
        }

        void setAnimateMoves(boolean animateMoves)
		{
            this.animateMoves = animateMoves;
        }

        @Override
        public boolean animateMove(
			RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY)
		{
            if (!animateMoves)
			{
                dispatchMoveFinished(holder);
                return false;
            }
            return super.animateMove(holder, fromX, fromY, toX, toY);
        }
    }

	public static class TransitionListenerAdapter implements Transition.TransitionListener
	{

		@Override
		public void onTransitionStart(Transition p1)
		{

		}

		@Override
		public void onTransitionEnd(Transition p1)
		{

		}

		@Override
		public void onTransitionCancel(Transition p1)
		{

		}

		@Override
		public void onTransitionPause(Transition p1)
		{

		}

		@Override
		public void onTransitionResume(Transition p1)
		{

		}
	}
}
