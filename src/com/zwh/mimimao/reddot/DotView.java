package com.zwh.mimimao.reddot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class DotView extends View{
	private Paint paint;

	private float mRadius;
	private float mx;
	private float my;

	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	private void initPaint(){
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setColor(0xfff50d0d);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(mx - mStepx * mCount, my -  mStepy * mCount, mRadius, paint);
	}

	public void setPosition(float x, float y, float radius){
		mRadius = radius;
		mx = x;
		my = y;
		this.invalidate();
	}

	private float mStepx = 0;
	private float mStepy = 0;
	private int mCount = 0;

	void catMove(float x, float y, float radius){
		mStepx = ( x - mx ) / 8;
		mStepy = ( y - my ) / 8;

		mx = x;
		my = y;
		mRadius = radius;


		final Handler handler=new Handler();

		Runnable runnable = new Runnable()
		{
			int count = 8;
			public void run() 
			{
				count--;

				if( count >= 0 ){
					test(count);
					handler.postDelayed(this, 10);
				}
				else{
					handler.removeCallbacks(this);
				}
			}
		};

		handler.postDelayed(runnable, 10);
	}

	private void test(int count){
		mCount = count;
		invalidate();
	}
}