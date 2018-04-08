package com.moviebook.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoViews extends VideoView{
	
	public VideoViews(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VideoViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
