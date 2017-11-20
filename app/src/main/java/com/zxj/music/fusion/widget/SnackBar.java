package com.zxj.music.fusion.widget;

import android.animation.*;
import android.view.*;
import android.widget.*;

import android.app.Activity;
import android.content.Context;
import android.view.View.OnClickListener;
import static java.lang.Math.abs;
import android.view.animation.*;
import com.zxj.music.fusion.*;


//一个非常简单的SnackBar实现
public class SnackBar
{
	private Activity context;
	private ViewGroup  container;
	private ViewGroup  parent;
	private Interpolator fosi;
	private ViewGroup decor;
	private TextView text;
	private Button action;
	private int w,h;
	private boolean shouldDismiss;
	private long formerMillis;
	private float formerRawX,v,formerX;
	public int var_feedback_duration=500;
	public int var_show_duration=2000;
	private boolean cancelable=true;
    //private int navigationBarHeight;
	
	private Callback callback=callback = new Callback(){
		@Override
		public void onAnimate(SnackBar snackbar, float transY)
		{}
		@Override
		public void onShow(SnackBar snackbar)
		{}
		@Override
		public void onDismiss(SnackBar snackbar)
		{}
	};


	
	
	public SnackBar(Activity context)
	{   
	    this.fosi = App.fast_out_slow_in;
	    this.w=App.app_width;
		this.h=App.app_height;
	    this.context = context;
		this.decor = (ViewGroup) context.getWindow().getDecorView();
		this.parent=(ViewGroup) LayoutInflater.from(context).inflate
		(R.layout.layout_snack_bar, decor, false);
		this.container = (ViewGroup) parent.getChildAt(0);
		this.text = (TextView) container.findViewById(R.id.layout_snack_bar_text);
		this.action = (Button) container.findViewById(R.id.layout_snack_bar_action);
		this.shouldDismiss = true;

		this.container.setOnTouchListener(new View.OnTouchListener(){
				public boolean onTouch(View view, MotionEvent event)
				{
					if (cancelable)
					{
						if (event.getAction() == event.ACTION_DOWN)
						{
							shouldDismiss = false;
							formerMillis = System.currentTimeMillis();
							formerRawX = event.getX();
							formerX = formerRawX;}
						else if (event.getAction() == event.ACTION_MOVE)
						{
							//简单的手指速度测量
							if (abs(event.getRawX() - formerRawX) >= 5)
							{
								v = (event.getRawX() - formerRawX) / (System.currentTimeMillis() - formerMillis);
							}
							formerMillis = System.currentTimeMillis();
							formerRawX = event.getRawX();
							view.setTranslationX(event.getRawX() - formerX);
							view.setAlpha(1 - abs(view.getTranslationX()) / w);
						}
						else if (event.getAction() == event.ACTION_UP)
						{
							shouldDismiss = true;
							int sign=(int) Math.signum(v);
							v = sign * v < w / 250 ?sign * w / 250: v;
							float tx=view.getTranslationX();

							if (v > 0)
							{
								if (tx < 0)
								{
									animate(0, -tx / v);
								}
								else
								{
									animate(w, (w - tx) / v);
								}
							}
							else if (v < 0)
							{
								if (tx > 0)
								{
									animate(0, tx / -v);
								}
								else
								{
									animate(-w, (w + tx) / -v);
								}
							}
							formerX = 0;
						}
					}
					return true;
				}

			});

	}
	
	private void animate(final float transX, float dur)
	{

		ValueAnimator animator= new ValueAnimator().ofFloat(new float[]{(float)container.getTranslationX(),transX})
			.setDuration((int)(dur + .5));
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float f=p1.getAnimatedValue();
					container.setTranslationX(f);
					container.setAlpha(1 - abs(container.getTranslationX()) / w);
				}
			});
		animator.addListener(new AnimatorListenerAdapter(){
				@Override
				public void onAnimationEnd(Animator p1)
				{
					if (abs(transX) >= w)
					{
						decor.removeView(parent);	
						callback.onDismiss(SnackBar.this);
					}
				}
			});
		animator.start();
	}

	public static SnackBar build(Activity context, int resText, int resActionText)
	{   
		SnackBar sb=new SnackBar(context);
		sb.setMessage(resText);
	    sb.setActionText(resActionText);
		return sb;
	}
	

	public SnackBar setMessage(int res)
	{
		text.setText(res);
		return this;
	}

	

	public SnackBar setActionText(int res)
	{   action.setVisibility(0);
		action.setText(res);
		action.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					action.setEnabled(false);				
					dismiss();
				}
			});
		return this;
	}
	



	public SnackBar setAction(final View.OnClickListener listener)
	{   action.setVisibility(0);
		action.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					action.setEnabled(false);
					listener.onClick(p1);
					dismiss();

				}
			});
		return this;
	}

	public SnackBar setCancelable(boolean cancelable)
	{
		this.cancelable = cancelable;
		return this;
	}

	
    private void rawShow()
	{
		decor.addView(parent,-1,h);
		container.post(new Runnable(){
				@Override
				public void run()
				{    
				   final int height=container.getHeight();
					container.setVisibility(0);
					ValueAnimator va=new ValueAnimator().ofFloat(height, 0);
					va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

							@Override
							public void onAnimationUpdate(ValueAnimator p1)
							{
								float value=p1.getAnimatedValue();
	
								container.setTranslationY(value);
								
								callback.onAnimate(SnackBar.this, value - height);
								
							}
						});
					va.addListener(new AnimatorListenerAdapter(){
							@Override
							public void onAnimationEnd(Animator p1)
							{
								callback.onShow(SnackBar.this);		
							}
						});
					va.setInterpolator(fosi);
					va.setDuration(200);
					va.start();

				}
			});
	}
	public void show()
	{   
	    rawShow();
		indefiniteDismiss(var_show_duration);
	}

	public static int DURATION_INFINATE=-1;
	public void show(int duration)
	{
		rawShow();
		if (duration == DURATION_INFINATE)
		{return;}
		indefiniteDismiss(duration);
	}

	//无期限关闭（当用户手指按住SnackBar时(不包括按钮)，不关闭)
	private void indefiniteDismiss(final int duration)
	{
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(duration);
					}
					catch (InterruptedException e)
					{}
					context.runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								if (shouldDismiss)
								{
									if (parent.getParent() != null)
									{   
										dismiss();
									}
								}
								else
								{ 
									indefiniteDismiss(duration);
								}
							}
						});
				}
			}).start();

	}

    private boolean dismissing;

	public void dismiss()
	{         
		//保证即使连续调用dismiss(),动画也能完整执行
		if (!dismissing)
		{
			dismissing = true;

			final int height=container.getHeight();
			ValueAnimator va=new ValueAnimator().ofFloat(0, height);
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

					@Override
					public void onAnimationUpdate(ValueAnimator p1)
					{
						float value=p1.getAnimatedValue();
						container.setTranslationY(value);
						callback.onAnimate(SnackBar.this, value - height);
					}
				});
			va.addListener(new AnimatorListenerAdapter(){
					@Override
					public void onAnimationEnd(Animator p1)
					{
						if(parent.getParent()!=null){
						decor.removeView(parent);
						callback.onDismiss(SnackBar.this);
						dismissing = false;
}
					}
				});
			va.setInterpolator(fosi);
			va.setDuration(200);
			va.start();


		}
	}



	public boolean isShown()
	{
		if (this.container.isShown())
		{
			return true;
		}
		return false;
	}

	public interface Callback
	{
		void onShow(SnackBar snackbar);
		void onAnimate(SnackBar snackbar, float transY);
		void onDismiss(SnackBar snackbar);
	}

	public SnackBar setCallback(Callback cb)
	{
		this.callback = cb;
		return this;
	}

}
