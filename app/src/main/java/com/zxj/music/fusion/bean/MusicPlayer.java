package com.zxj.music.fusion.bean;
import android.media.*;
import android.widget.*;
import java.util.*;
import android.os.*;
import java.io.*;
import com.zxj.music.fusion.util.*;
import com.zxj.music.fusion.*;



public class MusicPlayer extends MediaPlayer implements 
MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener
,SeekBar.OnSeekBarChangeListener,AudioManager.OnAudioFocusChangeListener
{

    private Timer timer;
	private TimerTask task;
	private int var_update_period=1000;
    private boolean var_timer_enabled=false;
	private SeekBar seekbar;
	private OnAudioFocusChangeListener listener;
	private OnProgressUpdateListener onProgressUpdateListener;

	public interface OnAudioFocusChangeListener
	{
		void onGainFocus();
		void onLoseFocus();
	}

	public void setOnAudioFocusChangeListener(OnAudioFocusChangeListener listenr)
	{
		this.listener = listenr;
	}

	public interface OnProgressUpdateListener
	{
		public void onProgress(MusicPlayer player, int progress);
	}


	public MusicPlayer(SeekBar seekbar)
	{
		this.seekbar = seekbar;

		this.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.setOnBufferingUpdateListener(this);
		this.setOnPreparedListener(this);
		audioManager = audioManager = (AudioManager) App.app_context. getSystemService(App.AUDIO_SERVICE);
        this.seekbar.setOnSeekBarChangeListener(this);
	}


	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		if (p1.isPressed())
		{
			this.seekTo(p2 - p2 % 10);
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
		reset();
		seekbar.setSecondaryProgress(0);
		seekbar.setProgress(0);
		try
		{
			this.setDataSource(url);
		}
		catch (IllegalStateException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (IOException e)
		{}
		prepareAsync();
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
                    if (isPlaying())
					{
						seekbar.setProgress(getCurrentPosition());
					}
				}
				onProgressUpdateListener.onProgress(MusicPlayer.this, getCurrentPosition());
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
		this.start();
        enableUpdateProgress();
	}

	public void auto()
	{
		if (isPlaying())
		{
			pause();
		}
		else
		{
			this.start();
		}
	}

	@Override
	public void onAudioFocusChange(int p1)
	{
		if (p1 == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT || p1 == AudioManager.AUDIOFOCUS_GAIN)
		{

			enableUpdateProgress();
			if (!isPlaying())
			{

				this.start();
				this.setVolume(1, 1);
				if (listener != null)
				{
					listener.onGainFocus();
				}
			}

		}
		else if (p1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || p1 == AudioManager.AUDIOFOCUS_LOSS)
		{
			disableUpdateProgress();
			if (isPlaying())
			{
				pause();
			}
			if (!isPlaying())
			{
				if (listener != null)
				{
					listener.onLoseFocus();
				}
			}

			else if (p1 == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
			{
				this.setVolume(.2f, .2f);
			}
		}
	}

	public void release()
	{
		disableUpdateProgress();
		super.release();
		audioManager.abandonAudioFocus(this);
	}
}
	



