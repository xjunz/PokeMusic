package com.zxj.music.fusion;


import android.*;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import com.zxj.music.fusion.*;
import com.zxj.music.fusion.bean.*;
import com.zxj.music.fusion.task.*;
import com.zxj.music.fusion.util.*;
import com.zxj.music.fusion.widget.*;
import java.util.*;

import com.zxj.music.fusion.R;

public class MainActivity extends FragmentActivity implements SearchTask.SearchListener,LoadMoreTask.LoadMoreListener,
SnackBar.Callback,Panel.OnPanelSlideListener,MusicPlayer.OnProgressUpdateListener,MediaPlayer.OnCompletionListener,MusicPlayer.OnAudioFocusChangeListener

{

	

	

	private Toolbar toolbar;
	private ViewPager pager;
	private View[] tabs;
	private ImageButton fabGotoTop;
	private ProgressBar progressSearch,progressLoad;
	private ResultFragment[]  fragmentList;
    private Panel panel;
	private static MusicPlayer player;
	private ImageButton ibBarPlay;
	

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		initViews();
		player = new MusicPlayer(sbPlayProgress);
		player.setOnProgressUpdateListener(this);
		player.setOnCompletionListener(this);
		player.setOnAudioFocusChangeListener(this);
		//判断应用是否拥有读取内置储存的权限
		if (ContextCompat.checkSelfPermission(App.app_context, Manifest.permission.READ_EXTERNAL_STORAGE)
			== PackageManager.PERMISSION_DENIED)
		{   
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
		}
		
	}
	
	
	
	private boolean reenter_from_settings;
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
				UiUtils.toast(getString(R.string.noti_permission_granted));
            }
			else
            {
                //用户拒绝授权，弹窗提示
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setTitle(R.string.noti_permission_not_granted)
					.setMessage(R.string.msg_permission_not_granted)
					.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							//打开设置，让用户手动允许授权
							Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
							String pkg = "com.android.settings";
							String cls = "com.android.settings.applications.InstalledAppDetails";
							i.setComponent(new ComponentName(pkg, cls));
							i.setData(Uri.parse("package:" + getPackageName()));
							startActivity(i);
							reenter_from_settings = true;
			
						}
					})
					.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
						    android.os.Process.killProcess(android.os.Process.myPid());
							System.exit(0);
						}
					})
					.setCancelable(false)
					.show();
			}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
            return;
        
	}

	@Override
	protected void onResume()
	{
		if (reenter_from_settings)
		{
			//保证这个方法不被反复误调用
			reenter_from_settings = false;
			//用户要是还是拒绝授权
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_DENIED)
			{
				//直接退出应用
				UiUtils.toast(getString(R.string.err_permission));
				finish();
			}
			
		}
		super.onResume();
	}
		 
	@Override
	protected void onDestroy()
	{
		player.release();
		super.onDestroy();
	}


    private void initViews()
	{
		toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setActionBar(toolbar);
		indicator = findViewById(R.id.indicator);
		panel = (Panel) findViewById(R.id.panel);
		panel.setOnPanelSlideListener(this);
		progressSearch = (ProgressBar) findViewById(R.id.progressbar_top);
		progressLoad = (ProgressBar) findViewById(R.id.progressbar_bottom);
		tvCurTime = (TextView) findViewById(R.id.tv_bar_curtime);
		tvDuration = (TextView) findViewById(R.id.tv_bar_duration);
		sbPlayProgress = (SeekBar) findViewById(R.id.seekbar);
		fabGotoTop = (ImageButton) findViewById(R.id.fab_goto_top);
		ibBarPlay=(ImageButton) findViewById(R.id.ib_play);
		tabs = new View[]{findViewById(R.id.tv_vendor_tencent),findViewById(R.id.tv_vendor_netease),findViewById(R.id.tv_vendor_kugou)};
		indicator.post(new Runnable(){
				@Override
				public void run()
				{
					indicator.setX(App.app_width / 6 - indicator.getWidth() / 2);
				}
			});
		pager = (ViewPager) findViewById(R.id.viewpager);
        ibChevron = (ImageButton) findViewById(R.id.ib_chevron);
		setUpViewPager();
	}


	
	private boolean isScaling;
	protected void scaleFab(boolean toBeVisible)
	{
		if (toBeVisible)
		{
			if (fabGotoTop.getScaleX() == 0)
			{
				fabGotoTop.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).start();
			}
		}
		else
		{
			if (fabGotoTop.getScaleX() == 1 && !isScaling)
			{

				fabGotoTop.animate().scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator())
					.setListener(new AnimatorListenerAdapter(){
						@Override
						public void onAnimationEnd(Animator anim)
						{
							isScaling = false;
						}
					}).start();
				isScaling = true;
			}
		}
	}

	
	protected void rotateFAB(boolean toBeUp){
		if(toBeUp){
			if(fabGotoTop.getRotation()==90){
			fabGotoTop.animate().rotation(-90).setInterpolator(new OvershootInterpolator()).start();
			}
		}else{
			 if(fabGotoTop.getRotation()==-90){
			fabGotoTop.animate().rotation(90).setInterpolator(new OvershootInterpolator()).start();
			}
		}
	}
	

	private void setUpViewPager()
	{

		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
				{

				}

				@Override
				public void onPageSelected(int position)
				{

					indicator.animate().x((App.app_width / 6 - indicator.getWidth() / 2) + position * App.app_width / 3)
						.setDuration(200)
						.setInterpolator(App.fast_out_slow_in)
						.start();
					TaskUtil.vendor = position + 1;
				}

				@Override
				public void onPageScrollStateChanged(int state)
				{

				}
			});
	}



	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
	    return true;
	}

	private View searchMenuView;
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.item_search:
				searchMenuView = toolbar.findViewById(R.id.item_search);
				gotoSearch();
				break;
			case R.id.item_about:
				UiUtils.autoLinkDialog(this,R.string.about,R.string.msg_about).show();
				break;
			case R.id.item_about_os:
				UiUtils.autoLinkDialog(this,R.string.about_os,R.string.msg_about_os).show();
				break;
			case R.id.item_exit:
				this.finish();
			    System.exit(0);
				break;
			case R.id.item_feedback:
				try{
					startActivity(new Intent().setData(Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=561721325&card_type=group&source=qrcode")));
				}catch(Exception e){
				UiUtils.toast(getString(R.string.operation_failed));
				}
				break;
			case R.id.item_donate:
				try{
					startActivity(new Intent().setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=HTTPS://QR.ALIPAY.COM/FKX09948AIIIZRJRKVBD01")));
				}catch(Exception e){
					UiUtils.toast(getString(R.string.operation_failed));
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void gotoSearch()
	{
		Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView, "transition_search_back").toBundle();
		startActivityForResult(new Intent(this, SearchActivity.class), 0, options);
	}
	@Override
	public void onCompletion(MediaPlayer p1)
	{
		UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_play,0);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.ib_play:
				
				if(player!=null){
			if(view.getRotation()==0){
				UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_pause,360);
				}else{
				UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_play,0);
				}
				player.auto();
			}
				break;
		}
	}

	private View indicator;
	public void onTabClick(View view)
	{
		int indx=pager.getCurrentItem();
		int tag=Integer.parseInt((String)view.getTag());
		if (indx != tag)
		{
			indicator.animate().x((view.getWidth() / 2 - indicator.getWidth() / 2) + tag * view.getWidth())
				.setDuration(Math.abs((tag - indx)) * 50)
				.setInterpolator(App.fast_out_slow_in).start();
			indx = tag;
            pager.setCurrentItem(indx);
		}
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
			case 1:

				final String curkeyword=data.getStringExtra("keyword");
				toolbar.setTitle(getString(R.string.result_for, curkeyword));
				if (ConnectionUtil.hasInternetConnection())
				{
				    if (!curkeyword.equals(TaskUtil.keyword))
					{
						TaskUtil.keyword = curkeyword;
						new SearchTask().setSearchListener(this).execute();
					}
					else
					{
						SnackBar.build(MainActivity.this, R.string.noti_keyword_not_change, R.string.still_try)
							.setAction(new View.OnClickListener(){
								@Override
								public void onClick(View p1)
								{
									TaskUtil.keyword = curkeyword;
									new SearchTask().setSearchListener(MainActivity.this).execute();

								}
							}).setCallback(this).show(5000);
					}
				}
				else
				{
					SnackBar.build(this, R.string.noti_no_internet_detected, R.string.retry)
						.setAction(new OnClickListener(){

							@Override
							public void onClick(View p1)
							{

							}
						}).setCallback(this).show(SnackBar.DURATION_INFINATE);
				}
				break;
		}
	}



	class ResultFragmentAdapter extends FragmentPagerAdapter
	{
		private ResultFragment[] fragments;
		public ResultFragmentAdapter(android.support.v4.app.FragmentManager fm, ResultFragment... fragments)
		{
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public int getCount()
		{

			return fragments.length;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int p1)
		{
			return fragments[p1];
		}
	}


	@Override
	public void onPreSearch()
	{
		progressSearch.animate().alpha(1).setDuration(1000).start();
	}


	@Override
	public void onPostSearch(ArrayList<SongInfo>[] result)
	{
		progressSearch.animate().alpha(0).setDuration(1000).start();

		if (fragmentList == null)
		{
			fragmentList = new ResultFragment[3];
			for (int i=0;i < fragmentList.length;i++)
			{
				fragmentList[i] = new ResultFragment(result[i]);
			}
			pager.setAdapter(new ResultFragmentAdapter(getSupportFragmentManager(), fragmentList));
			UiUtils.enable(tabs);
			UiUtils.visible(indicator, pager,fabGotoTop,ibChevron);
		}
		else
		{
			for (int i=0;i < fragmentList.length;i++)
			{
				fragmentList[i].refreshData(result[i]);
				//有可能某个Fragment并没有被加载（PagerView的懒加载机制)，所以判断
				if (fragmentList[i].getRecyclerView() != null)
				{
					fragmentList[i].getRecyclerView().smoothScrollToPosition(0);
				}
			}
		} 

		if (panel.isPanelOpen())
		{
			panel.closePanel();
		}
	}

	public void autoPanel(View view)
	{
		panel.autoPanel();
	}

	private ImageButton ibChevron;
	@Override
	public void onPanelClosed(View target)
	{
		findViewById(R.id.ib_chevron).animate().rotation(0).setInterpolator(App.fast_out_slow_in).start();

	}



	@Override
	public void onPanelSlide(View target, float fraction)
	{
		target.setElevation(fraction * getResources().getDimension(R.dimen.z_toolbar));
		progressSearch.setTranslationY(target.getHeight() * fraction);
		ibChevron.setTranslationY(target.getHeight() * fraction);
	}



	@Override
	public void onPanelOpened(View target)
	{
		findViewById(R.id.ib_chevron).animate().rotation(180).setInterpolator(App.fast_out_slow_in).start();
	}


	@Override
	public void onPreLoad()
	{
		progressLoad.animate().alpha(1).setDuration(500).start();

	}

	private SnackBar snackbarLoadUp ;

	@Override
	public void onPostLoad(ArrayList<SongInfo> result, boolean loadUp)
	{

		progressLoad.animate().alpha(0).setDuration(1000).start();
		if (loadUp)
		{
			if (snackbarLoadUp != null && snackbarLoadUp.isShown())
			{
				snackbarLoadUp.dismiss();
			}
			else
			{

				snackbarLoadUp = new SnackBar(this).setMessage(R.string.noti_load_up)
					.setCallback(this);
				snackbarLoadUp.show(1000);}
			return;
		}
		fragmentList[pager.getCurrentItem()].refreshData(result);
		fragmentList[pager.getCurrentItem()].getRecyclerView().smoothScrollBy(0, 100, new AccelerateDecelerateInterpolator());
        
	}

	@Override
	public void onShow(SnackBar snackbar)
	{

	}

	@Override
	public void onAnimate(SnackBar snackbar, float transY)
	{
		if (fabGotoTop.isShown())
		{
			fabGotoTop.setTranslationY(transY);
		}
	}

	@Override
	public void onDismiss(SnackBar snackbar)
	{
		if (fabGotoTop.isShown())
		{
			fabGotoTop.animate().translationY(0)
				.setDuration(200)
				.setInterpolator(new BounceInterpolator()).start();
		}
	}


	public void scrollToTop(View v)
	{
		if (pager.isShown())
		{
			RecyclerView rv=fragmentList[pager.getCurrentItem()].getRecyclerView();
			if(v.getRotation()==-90){
			rv.smoothScrollToPosition(0);}
			else if(v.getRotation()>=90){
				rv.smoothScrollToPosition(rv.getAdapter().getItemCount());
			}
		}
	}

	private TextView tvCurTime,tvDuration;
	private SeekBar sbPlayProgress;

	protected void playMusic(SongInfo info)
	{ 
	    String quality=info.QUALITY_S128;
		if(info.id_s128.equals("0")){
			if(info.id_sogg.equals("0")){
				quality=info.QUALITY_S320;
			}else{
				quality=info.QUALITY_SOGG;
			}
		}
		App.copyToClipborad(info.getAudioSourceUrl(quality));
		String url=info.getAudioSourceUrl(quality);
		tvDuration.setText(info.formated_duration);
		player.play(url);
		panel.openPanel();
		UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_pause,360);
		
        ((TextView)findViewById(R.id.player_artist)).setText(info.singer);
		((TextView)findViewById(R.id.player_info)).setText(info.album);
		((TextView)findViewById(R.id.player_title)).setText(info.songname);
	}

	@Override
	public void onProgress(MusicPlayer player, final int progress)
	{
		runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					tvCurTime.setText(TaskUtil.formatSongDuration(progress));
				}

			});
	}
	
	@Override
	public void onGainFocus()
	{
		UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_pause,360);
	}

	@Override
	public void onLoseFocus()
	{
		UiUtils.windmillTrick(ibBarPlay,R.drawable.ic_play,0);
	}
	
}
	

