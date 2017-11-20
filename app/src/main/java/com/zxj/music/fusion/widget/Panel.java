package com.zxj.music.fusion.widget;
import android.view.*;
import android.support.v4.widget.ViewDragHelper;
import android.widget.RelativeLayout;
import android.support.v4.view.ViewCompat;
import com.zxj.music.fusion.*;


public class Panel extends RelativeLayout 
{

    private ViewDragHelper helper;
	private OnPanelSlideListener listener=new OnPanelSlideListener(){
		@Override
		public void onPanelClosed(View target){}

		@Override
		public void onPanelSlide(View target, float fraction){}

		@Override
		public void onPanelOpened(View target){}
	};
	
	public Panel(android.content.Context context)
	{
		super(context);
		init();
	}

    public Panel(android.content.Context context, android.util.AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

    public Panel(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

    public Panel(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init()
	{
		helper = ViewDragHelper.create(this, 1.0f, callback);
		
	}

	public void setOnPanelSlideListener(OnPanelSlideListener listener)
	{
		this.listener = listener;
	}

	private View target;
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		target = findViewById(R.id.playing_bar);
		}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		return helper.shouldInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		helper.processTouchEvent(event);
		return true;
	}

	private int minTop;
	private int maxTop;


	private int curTop;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		maxTop = target.getTop();
		minTop = maxTop - target.getHeight();
		target.layout(0, minTop+curTop, getRight(), maxTop +curTop);	
	}

  


	@Override
	public void computeScroll()
	{
		if (helper.continueSettling(true))
		{ViewCompat.postInvalidateOnAnimation(this);}
	}

	public void closePanel()
	{
		if (helper.smoothSlideViewTo(target, 0, minTop))
		{
	    	ViewCompat.postInvalidateOnAnimation(this);
		}}


	public void autoPanel()
	{
		if (target.getTop() == minTop)
		{
			openPanel();
		}
		else
		{
			closePanel();		
		}
	}
	public void openPanel()
	{
		if (helper.smoothSlideViewTo(target, 0, maxTop))
		{
			ViewCompat.postInvalidateOnAnimation(this);
		}

	}

	public boolean isPanelOpen()
	{
		return target.getTop() > minTop;
	}

	
	private ViewDragHelper.Callback callback=new ViewDragHelper.Callback(){

		@Override
		public boolean tryCaptureView(View p1, int p2)
		{
			return p1.getId() == R.id.playing_bar;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy)
		{   
			return (int) Math.min(maxTop,Math.max(top,minTop));
		}
		

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
		{  
		    curTop+=dy;
			listener.onPanelSlide(target, (float)(top - minTop) / (maxTop - minTop));
			if (top == minTop)
			{listener.onPanelClosed(Panel.this);
				
			}
			else if (top == maxTop)
			{
				listener.onPanelOpened(Panel.this);
			}
			
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{
		    if (yvel > 0)
			{
				openPanel();
			}
			else
			{
				closePanel();
			}
			super.onViewReleased(releasedChild, xvel, yvel);
		}


		@Override
		public int getViewVerticalDragRange(View child)
		{
			return getMeasuredHeight() - child.getMeasuredHeight();
		}

		@Override
		public int getViewHorizontalDragRange(View child)
		{
			return getMeasuredWidth() - child.getMeasuredWidth();
		}
		
	};

	
	public static interface OnPanelSlideListener
	{

		void onPanelClosed(View  target)
		void onPanelSlide(View target, float fraction)
		void onPanelOpened(View target)
	}
}
