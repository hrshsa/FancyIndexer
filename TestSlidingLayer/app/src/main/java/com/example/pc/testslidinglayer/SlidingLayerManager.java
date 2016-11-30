package com.example.pc.testslidinglayer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;


/**
 * 可在页面内添加的SlidingLayer的管理类
 */
public class SlidingLayerManager {
	private static final int SHADOW_MAX_ALPHA = 150; // 蒙层透明度
	private static final float DEFAULT_WIDTH_RATE = 0.8f;
	private static final String FRAGMENT_TAG = "slidinglayer_fragment_tag";

	private FragmentActivity activity;
	private View slidingView;
	private SlidingLayer slidingLayer;
	private SlidingLayer.OnInteractListener interactListener;

	public SlidingLayerManager(FragmentActivity activity) {
		this.activity = activity;
	}
	/**
	 * 设置SlidingLayer布局
	 * 
	 * @param layout
	 */
	public void setSlidingView(View layout) {
		setSlidingView(layout, (int)(getScreenWidtd()*DEFAULT_WIDTH_RATE));
	}
	/**
	 * SlidingLayer内添加fragment
	 * 
	 * @param fragment
	 * @param fm
	 */
	public void setSlidingView(Fragment fragment, FragmentManager fm) {
		setSlidingView(fragment, fm, (int)(getScreenWidtd()*DEFAULT_WIDTH_RATE));
	}

	private int  getScreenWidtd() {
		return activity.getResources().getDisplayMetrics().widthPixels;
	}
	
	/**
	 * 设置SlidingLayer布局
	 * 
	 * @param layout
	 * @param width
	 */
	public void setSlidingView(View layout, int width) {
		if (activity != null) {
			initSlidingView(width);
			Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
			if (fragment!=null) {
				removeFragment(fragment, activity.getSupportFragmentManager());
			}
			slidingLayer.addView(layout);
		}
	}

	public void setStickTo(int side) {
		slidingLayer.setStickTo(side);
	}
	/**
	 * SlidingLayer内添加fragment
	 * 
	 * @param fragment
	 * @param fm
	 * @param width
	 */
	public void setSlidingView(Fragment fragment, FragmentManager fm, int width) {
		if (activity!=null && fm!=null && fragment!=null) {
			initSlidingView(width);
			FragmentTransaction fTransaction = fm.beginTransaction();
			fTransaction.replace(R.id.slidinglayer, fragment, FRAGMENT_TAG);
			fTransaction.commitAllowingStateLoss();
			if (fragment instanceof SlidingLayerContent) {
				((SlidingLayerContent)fragment).setSlidingLayerManager(this);
			}
		}
	}
	/**
	 * 添加fragment时，fragment实现次接口，可以控制SlidingLayer的开关
	 * @author lvyang
	 *
	 */
	public interface SlidingLayerContent{
		void setSlidingLayerManager(SlidingLayerManager slManager);
	}

	public void setShadeWidth(int width) {
		slidingLayer.setShadowWidth(width);
	}

	public void setShadeDrawable(Drawable width) {
		slidingLayer.setShadowDrawable(width);
	}
	/**
	 * 移除fragment
	 */
	public void removeFragment(Fragment fragment, FragmentManager fm) {
		if (fragment!=null && fm!=null) {
			FragmentTransaction fTransaction = fm.beginTransaction();
			fTransaction.remove(fragment);
			fTransaction.commitAllowingStateLoss();
		}
	}

	public boolean isFragmentRemoved(Fragment fragment){
		if (activity!=null && activity.getSupportFragmentManager()!=null) {
			Fragment addedFragment = activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
			if (addedFragment!=null && addedFragment.equals(fragment)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isViewRemoved(View view){
		if (activity!=null && view!=null && slidingLayer.getChildCount()!=0) {
			View addedView = slidingLayer.getChildAt(0);
			if (view.equals(addedView)) {
				return false;
			}
		}
		return true;
	}
	
	public void openLayer() {
		openLayerDelayed(10);
	}
	/**
	 * 延时打开SlidingLayer(第一次加载时若无滑动动画，可以使用此方法)
	 * @param delayMillis
	 */
	public void openLayerDelayed(long delayMillis) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (slidingLayer!=null && slidingView!=null) {
					slidingView.setVisibility(View.VISIBLE);
					slidingLayer.openLayer(true);
				}
				if (activity!=null && activity.getSupportFragmentManager()!=null) {
					Fragment addedFragment = activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
					if (addedFragment!=null) {
						addedFragment.setUserVisibleHint(true);
					}
				}
			}
		}, delayMillis);
	}
	
	public void closeLayer() {
		if (slidingLayer != null) {
			slidingLayer.closeLayer(true);
			if (activity!=null && activity.getSupportFragmentManager()!=null) {
				Fragment addedFragment = activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
				if (addedFragment!=null) {
					addedFragment.setUserVisibleHint(false);
				}
			}
		}
	}

	public boolean isLayerOpened() {
		if (slidingLayer != null) {
			return slidingLayer.isOpened();
		}
		return false;
	}
	/**
	 * 关闭activity中的SlidingLayer
	 * @param activity
	 * @return 如果SlidingLayer存在且处于打开状态，则关闭并返回true
	 */
	public static boolean closeSlidingLayer(Activity activity){
		View view = activity.findViewById(R.id.slidinglayer);
		if (view!=null && view instanceof SlidingLayer) {
			SlidingLayer slidingLayer = (SlidingLayer) view;
			if (slidingLayer.isOpened()) {
				slidingLayer.closeLayer(true);
				return true;
			}
		}
		return false;
	}

	private void initSlidingView(int width) {
		if (activity == null) {
			return;
		}
		if (slidingView == null) {
			slidingView = activity.findViewById(R.id.shadow_view);
			if (slidingView == null) {
				slidingView = activity.getLayoutInflater().inflate(R.layout.base_slidinglayer_layout, null);
				activity.addContentView(slidingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			slidingLayer = (SlidingLayer) slidingView.findViewById(R.id.slidinglayer);
			slidingLayer.setOnScrollListener(onScrollListener);
			slidingView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					slidingLayer.closeLayer(true);
				}
			});
		}
		slidingLayer.removeAllViews();
		LayoutParams params = slidingLayer.getLayoutParams();
		params.width = width;
		slidingLayer.setLayoutParams(params);
		if (interactListener!=null) {
			slidingLayer.setOnInteractListener(interactListener);
		}
	}

	private SlidingLayer.OnScrollListener onScrollListener = new SlidingLayer.OnScrollListener() {

		@Override
		public void OnScroll(float percent) {
			if (slidingView == null) {
				return;
			}
			if (percent == 0f) {
				slidingView.setVisibility(View.GONE);
				if (interactListener!=null) {
					interactListener.onClosed();
				}
			} else {
				slidingView.setVisibility(View.VISIBLE);
				int color = Color.argb((int) (SHADOW_MAX_ALPHA * percent), 0, 0, 0);
				slidingView.setBackgroundColor(color);
			}
		}
	};
	
	public void setOnInteractListener(SlidingLayer.OnInteractListener listener) {
		this.interactListener = listener;
		if (slidingLayer!=null) {
			slidingLayer.setOnInteractListener(interactListener);
		}
	}

}
