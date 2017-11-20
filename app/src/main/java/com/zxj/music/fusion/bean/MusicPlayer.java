package com.zxj.music.fusion.bean;
import android.media.*;
import android.widget.*;
import java.util.*;
import android.os.*;
import java.io.*;
import com.zxj.music.fusion.util.*;
import com.zxj.music.fusion.*;



public class MusicPlayer implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener
,SeekBar.OnSeekBarChangeListener,AudioManager.OnAudioFocusChangeListener
{

    private Timer timer;
	private TimerTask task;
	private int var_update_period=1000;
    private boolean var_timer_enabled=false;
	private SeekBar seekbar;
	private MediaPlayer player;
	private OnProgressUpdateListener onProgressUpdateListener;


	public interface OnProgressUpdateListener
	{
		public void onProgress(MusicPlayer player, int progress);
	}


	public MusicPlayer(SeekBar seekbar)
	{
		this.seekbar = seekbar;
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnCompletionListener(this);
		player.setOnBufferingUpdateListener(this);
		player.setOnPreparedListener(this);
		audioManager = audioManager = (AudioManager) App.app_context. getSystemService(App.AUDIO_SERVICE);

        this.seekbar.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		if (p1.isPressed())
		{
			player.seekTo(p2 - p2 % 10);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{

	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{

	}

	public void play(String url)
	{
		player.reset();
		seekbar.setSecondaryProgress(0);
		seekbar.setProgress(0);
		try
		{
			player.setDataSource(url);
		}
		catch (IllegalStateException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (IOException e)
		{}
		player.prepareAsync();
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener listener)
	{
		this.onProgressUpdateListener = listener;
	}

	public void enableUpdateProgress()
	{ 
		timer = new Timer();
		task = new TimerTask(){
			@Override
			public void run()
			{
				if (!seekbar.isPressed())
				{
					seekbar.setProgress(player.getCurrentPosition());
				}
				onProgressUpdateListener.onProgress(MusicPlayer.this, player.getCurrentPosition());
			}
		};
		timer.schedule(task, 0, var_update_period);
		var_timer_enabled = true;
	}

	public void disableUpdateProgress()
	{ 
	    var_timer_enabled = false;
		if (task != null)
		{
			task.cancel();
		}
		if (timer != null)
		{
			timer.cancel();
		}
	}
	@Override
	public void onBufferingUpdate(MediaPlayer p1, int p2)
	{
		seekbar.setSecondaryProgress(seekbar.getMax() * p2);
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{

	}

	@Override
	public boolean onError(MediaPlayer p1, int p2, int p3)
	{

		seekbar.setEnabled(false);
		disableUpdateProgress();
		UiUtils.toast(App.app_context.getString(R.string.err_play));
		return false;
	}

	private static AudioManager audioManager;

	private boolean requestAudioFocus()
	{
		int result=audioManager.requestAudioFocus(this,
												  AudioManager.STREAM_MUSIC,
												  AudioManager.AUDIOFOCUS_GAIN); 
		if (result == audioManager.AUDIOFOCUS_REQUEST_GRANTED)
		{
			return true;
		}

		return false;
	}

	@Override
	public void onPrepared(MediaPlayer p1)
	{
		seekbar.setMax(p1.getDuration());
		requestAudioFocus();
		player.start();
        enableUpdateProgress();
	}

	public void auto(){
		if(player.isPlaying()){
			player.pause();
		}else{
			player.start();
		}
	}
	
	@Override
	public void onAudioFocusChange(int p1)
	{
		if (p1 == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT || p1 == AudioManager.AUDIOFOCUS_GAIN)
		{

			enableUpdateProgress();
			if (!player.isPlaying())
			{
				player.start();
				player.setVolume(1, 1);
			}

		}
		else if (p1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || p1 == AudioManager.AUDIOFOCUS_LOSS)
		{
			disableUpdateProgress();
			if (player.isPlaying())
			{

				player.pause();
			}
		}

		else if (p1 == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
		{
			player.setVolume(.2f, .2f);
		}
	}

	public void release()
	{
		disableUpdateProgress();
		player.release();
		audioManager.abandonAudioFocus(this);
	}
}
	



