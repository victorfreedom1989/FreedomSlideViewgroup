package com.freedom.slideviewgroup.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.freedom.slideviewgroup.FreedomApplication;
import com.freedom.slideviewgroup.utils.DptoPxUtil;

/**
 * @ClassName: SlideMenu
 * @author victor_freedom (x_freedom_reddevil@126.com)
 * @createddate 2015-1-5 下午8:00:36
 * @Description: TODO
 */

public class SlideMenu extends ViewGroup {

	private Context mContext;

	// 默认第一个
	private int currentScreen = 0; // 当前屏
	// 移动控制者
	private Scroller mScroller = null;
	// 判断是否可以移动
	private boolean canScroll = false;
	// 是否拦截点击事件，false表示拦截，true表示将点击事件传递给子控件
	private boolean toChild = false;
	// 处理触摸事件的移动速率标准
	public static int SNAP_VELOCITY = 600;
	// 触发move的最小滑动距离
	private int mTouchSlop = 0;
	// 最后一点的X坐标
	private float mLastionMotionX = 0;
	// 处理触摸的速率
	private VelocityTracker mVelocityTracker = null;
	// 左右子控件的监听器
	private LeftListener leftListener;
	private RightListener rightListener;
	// 触摸状态
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	// 响应触摸事件的边距判定距离（这个根据自定义响应）
	public static int TOHCH_LEFT = 140;
	public static int TOHCH_RIGHT = FreedomApplication.mScreenWidth;
	public static final String TAG = "SlideMenu";

	public SlideMenu(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	/**
	 * @Title: init
	 * @Description: 初始化滑动相关的东西
	 * @throws
	 */
	private void init() {
		mScroller = new Scroller(mContext, new AccelerateInterpolator());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	/**
	 * @Title: onMeasure
	 * @Description: 设定viewGroup大小
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 * @throws
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * @Title: onLayout
	 * @Description: 设置子控件的分布位置
	 * @param changed
	 * @param l
	 *            left
	 * @param t
	 *            top
	 * @param r
	 *            right
	 * @param b
	 *            bottom
	 * @throws
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int startLeft = 0; // 每个子视图的起始布局坐标
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(startLeft, 0, startLeft + getWidth(), getHeight());
			startLeft = startLeft + getWidth(); // 校准每个子View的起始布局位置
		}
	}

	/**
	 * @Title: onInterceptTouchEvent
	 * @Description:触摸事件拦截判定
	 * @param ev
	 * @return
	 * @throws
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		// 如果当前正在滑动状态，则拦截事件
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastionMotionX = x;
			// 判断当前状态
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			// 判断是否能够响应滑动事件
			if ((ev.getX() < DptoPxUtil.dip2px(mContext, TOHCH_LEFT) && currentScreen == 1)
					|| (ev.getX() > TOHCH_RIGHT
							- DptoPxUtil.dip2px(mContext, 200) && currentScreen == 0)) {
				canScroll = true;
				toChild = false;
				return super.onInterceptTouchEvent(ev);
			} else {
				// 如果不能则不拦截事件
				canScroll = false;
				toChild = true;
				return false;
			}
		case MotionEvent.ACTION_MOVE:
			if (toChild) {
				return false;
			}
			final int differentX = (int) Math.abs(mLastionMotionX - x);
			// 超过了最小滑动距离，并且没有传递事件给子控件，则更改状态
			if (differentX > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (toChild) {
				return false;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * @Title: onTouchEvent
	 * @Description: 触摸事件响应
	 * @param event
	 * @return
	 * @throws
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		// 获取移动的速率
		mVelocityTracker.addMovement(event);
		super.onTouchEvent(event);

		// 手指位置地点
		float x = event.getX();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 如果屏幕的动画还没结束，你就按下了，我们就结束该动画
			if (mScroller != null) {
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
			}
			mLastionMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (canScroll) {
				int detaX = (int) (mLastionMotionX - x);
				mLastionMotionX = x;
				// 移动距离
				scrollBy(detaX, 0);

			}
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			// 滑动速率达到了一个标准(快速向右滑屏，返回上一个屏幕) 马上进行切屏处理
			if (velocityX > SNAP_VELOCITY && currentScreen > 0 && canScroll) {
				changedScreen(currentScreen - 1);
			}
			// 快速向左滑屏，返回下一个屏幕)
			else if (velocityX < -SNAP_VELOCITY
					&& currentScreen < (getChildCount() - 1) && canScroll) {
				changedScreen(currentScreen + 1);
			}
			// 以上为快速移动的 ，强制切换屏幕
			else {
				// 如果移动缓慢，那么先判断是保留在本屏幕还是到下一屏幕
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			mTouchState = TOUCH_STATE_REST;

			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return super.onInterceptTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {// 如果返回true，则代表正在模拟数据，false表示已经停止模拟数据
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());// 更新偏移量
			postInvalidate();
		}
	}

	/**
	 * @Title: startMove
	 * @Description: 这是从第一个屏幕跳转到第二个屏幕的快捷方法
	 * @throws
	 */
	public void startMove() {
		if (currentScreen == 1) {
			return;
		}
		if (currentScreen == 0 && rightListener != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					rightListener.postNotifyDataChange();
				}
			}).start();

		}
		currentScreen++;
		mScroller.startScroll((currentScreen - 1) * getWidth(), 0, getWidth(),
				0, 600);
		// 刷新界面
		invalidate();// invalidate -> drawChild -> child.draw -> computeScroll

	}

	/**
	 * @Title: startMoves
	 * @Description: 跳转到第一个屏幕
	 * @throws
	 */
	public void startMoves() {
		changedScreen(0);
	}

	/**
	 * @Title: snapToDestination
	 * @Description: 当缓慢移动的时候，判断跳转屏幕
	 * @throws
	 */
	private void snapToDestination() {
		int destScreen = (getScrollX() + getWidth() / 3) / getWidth();
		changedScreen(destScreen);
	}

	/**
	 * @Title: changedScreen
	 * @Description: 跳转屏幕
	 * @param whichScreen
	 * @throws
	 */
	private void changedScreen(int whichScreen) {
		currentScreen = whichScreen;
		if (currentScreen > getChildCount() - 1) {
			currentScreen = getChildCount() - 1;
		}
		if (currentScreen == 0 && leftListener != null) {
			leftListener.notifyDataChange();
		}
		if (currentScreen == 1 && rightListener != null) {
			rightListener.notifyDataChange();
		}
		// getScrollX得到的是当前视图相对于父控件的偏移量。初始值是0，
		int dx = currentScreen * getWidth() - getScrollX();
		// dx为正值时，屏幕向右滑动，dx为负值时，屏幕向左滑动
		mScroller.startScroll(getScrollX(), 0, dx, 0, 600);
		postInvalidate();

	}

	public interface LeftListener {
		public void notifyDataChange();
	}

	public interface RightListener {
		public void notifyDataChange();

		public void postNotifyDataChange();
	}

	public void setLeftListener(LeftListener leftListener) {
		this.leftListener = leftListener;
	}

	public void setRightListener(RightListener rightListener) {
		this.rightListener = rightListener;
	}

}
