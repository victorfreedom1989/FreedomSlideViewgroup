package com.freedom.slideviewgroup.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.freedom.slideviewgroup.R;
import com.freedom.slideviewgroup.utils.DptoPxUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * @blog http://blog.csdn.net/xiaanming
 * s
 * @author xiaanming
 * 
 */
@SuppressLint("NewApi")
public class SwipeDismissListView extends ListView {
	/** 向右 */
	private boolean toRight = false;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mSlop;
	/**
	 * 滑动的最小速度
	 */
	private int mMinFlingVelocity;
	/**
	 * 滑动的最大速度
	 */
	private int mMaxFlingVelocity;
	/**
	 * 执行动画的时间
	 */
	protected long mAnimationTime = 150;
	/**
	 * 用来标记用户是否正在滑动中
	 */
	private boolean mSwiping;
	/**
	 * 滑动速度检测类
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 手指按下的position
	 */
	private int mDownPosition;
	/**
	 * 按下的item对应的View
	 */
	private View mDownView;
	private float mDownX;
	private float mDownY;
	/**
	 * item的宽度
	 */
	private int mViewWidth;
	/**
	 * 当ListView的Item滑出界面回调的接口
	 */
	private OnDismissCallback onDismissCallback;

	/**
	 * 上下拖动 ，改变item位置
	 */
	private ImageView mDragView;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private int mDragPos;
	private int mFirstDragPos;
	private int mDragPoint;
	private int mCoordOffset;
	private DragListener mDragListener;
	private DropListener mDropListener;
	private RemoveListener mRemoveListener;
	private int mUpperBound;
	private int mLowerBound;
	private int mHeight;
	private GestureDetector mGestureDetector;
	public static final int FLING = 0;
	public static final int SLIDE_RIGHT = 1;
	public static final int SLIDE_LEFT = 2;
	private int mRemoveMode = -1;
	public void setmRemoveMode(int mRemoveMode) {
		this.mRemoveMode = mRemoveMode;
	}

	private Rect mTempRect = new Rect();
	private Bitmap mDragBitmap;
	private final int mTouchSlop;
	private int mItemHeightNormal = -1;
	private int mItemHeightExpanded = -1;
	private int grabberId = -1;
	private int dragndropBackgroundColor = 0x00000000;

	/**
	 * 设置动画时间
	 * 
	 * @param mAnimationTime
	 */
	public void setmAnimationTime(long mAnimationTime) {
		this.mAnimationTime = mAnimationTime;
	}

	/**
	 * 设置删除回调接口
	 * 
	 * @param onDismissCallback
	 */
	public void setOnDismissCallback(OnDismissCallback onDismissCallback) {
		this.onDismissCallback = onDismissCallback;
	}

	public SwipeDismissListView(Context context) {
		this(context, null);
	}

	public SwipeDismissListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeDismissListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		ViewConfiguration vc = ViewConfiguration.get(context);
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8; // 获取滑动的最小速度
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity(); // 获取滑动的最大速度

		// 上下拖动
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.TouchListView, 0, 0);

			mItemHeightNormal = a.getDimensionPixelSize(
					R.styleable.TouchListView_normal_height, 0);
			mItemHeightExpanded = a.getDimensionPixelSize(
					R.styleable.TouchListView_expanded_height,
					mItemHeightNormal);
			grabberId = a.getResourceId(R.styleable.TouchListView_grabber, -1);
			dragndropBackgroundColor = a.getColor(
					R.styleable.TouchListView_dragndrop_background, 0x00000000);
			mRemoveMode = a.getInt(R.styleable.TouchListView_remove_mode, -1);

			a.recycle();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i(VIEW_LOG_TAG, "yy");
		if (mRemoveListener != null && mGestureDetector == null) {
			Log.i(VIEW_LOG_TAG, "xx");
			if (mRemoveMode == FLING) {
				mGestureDetector = new GestureDetector(getContext(),
						new SimpleOnGestureListener() {
							@Override
							public boolean onFling(MotionEvent e1,
									MotionEvent e2, float velocityX,
									float velocityY) {
								if (mDragView != null) {
									if (velocityX > 1000) {
										Rect r = mTempRect;
										mDragView.getDrawingRect(r);
										if (e2.getX() > r.right * 2 / 3) {
											stopDragging();
											mRemoveListener
													.remove(mFirstDragPos);
											unExpandViews(true);
										}
									}
									return true;
								}
								return false;
							}
						});
			}
		}
		Log.i(VIEW_LOG_TAG, "xxx");
		if (mDragListener != null || mDropListener != null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(true){
					break;
				}
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				int itemnum = pointToPosition(x, y);
				if (itemnum == AdapterView.INVALID_POSITION) {
					break;
				}
				ViewGroup item = (ViewGroup) getChildAt(itemnum
						- getFirstVisiblePosition());
				mDragPoint = y - item.getTop();
				mCoordOffset = ((int) ev.getRawY()) - y;
				View dragger = item.findViewById(R.id.text);
				// 点击有效的范围。。上下或者左右拖动的有效范围
				Rect r = mTempRect;
				r.left = dragger.getLeft();
				r.right = dragger.getRight();
//						+ ScreenUtil.dip2px(getContext(), 50);
				r.top = dragger.getTop();
				r.bottom = dragger.getBottom();
				Log.i(VIEW_LOG_TAG, "y");
				if ((r.left < x) && (x < r.right)) {
					Log.i(VIEW_LOG_TAG, "x");
					item.setDrawingCacheEnabled(true);
					Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					startDragging(bitmap, y);
					mDragPos = itemnum;
					mFirstDragPos = mDragPos;
					mHeight = getHeight();
					int touchSlop = mTouchSlop;
					mUpperBound = Math.min(y - touchSlop, mHeight / 3);
					mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
					return false;
				}
				mDragView = null;
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(ev);
		}
		if ((mDragListener != null || mDropListener != null)
				&& mDragView != null) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				handleActionUp(ev);
			case MotionEvent.ACTION_CANCEL:
				Rect r = mTempRect;
				mDragView.getDrawingRect(r);
				stopDragging();
				if (mDropListener != null && mDragPos >= 0
						&& mDragPos < getCount()) {
					mDropListener.drop(mFirstDragPos, mDragPos);
				}
				unExpandViews(false);
				break;
			case MotionEvent.ACTION_MOVE:
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				dragView(x, y);
				int itemnum = getItemForPosition(y);
				if (itemnum >= 0) {
					if (action == MotionEvent.ACTION_DOWN
							|| itemnum != mDragPos) {
						if (mDragListener != null) {
							mDragListener.drag(mDragPos, itemnum);
						}
						mDragPos = itemnum;
						doExpansion();
					}
					int speed = 0;
					adjustScrollBounds(y);
					if (y > mLowerBound) {
						// scroll the list up a bit
						speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
					} else if (y < mUpperBound) {
						// scroll the list down a bit
						speed = y < mUpperBound / 2 ? -16 : -4;
					}
					if (speed != 0) {
						int ref = pointToPosition(0, mHeight / 2);
						if (ref == AdapterView.INVALID_POSITION) {
							// we hit a divider or an invisible view, check
							// somewhere else
							ref = pointToPosition(0, mHeight / 2
									+ getDividerHeight() + 64);
						}
						View v = getChildAt(ref - getFirstVisiblePosition());
						if (v != null) {
							int pos = v.getTop();
							setSelectionFromTop(ref, pos - speed);
						}
					}
				}
				handleActionMove(ev);
				break;
			case MotionEvent.ACTION_DOWN:
				handleActionDown(ev);
				unExpandViews(true);
				break;
			}
		}
		if (mDragView == null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				handleActionDown(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				handleActionMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				handleActionUp(ev);
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	/*
	 * pointToPosition() doesn't consider invisible views, but we need to, so
	 * implement a slightly different version.
	 */
	private int myPointToPosition(int x, int y) {
		Rect frame = mTempRect;
		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	private int getItemForPosition(int y) {
		int adjustedy = y - mDragPoint - 32;
		int pos = myPointToPosition(0, adjustedy);
		if (pos >= 0) {
			if (pos <= mFirstDragPos) {
				pos += 1;
			}
		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
	}

	private void adjustScrollBounds(int y) {
		if (y >= mHeight / 3) {
			mUpperBound = mHeight / 3;
		}
		if (y <= mHeight * 2 / 3) {
			mLowerBound = mHeight * 2 / 3;
		}
	}

	/*
	 * Restore size and visibility for all listitems
	 */
	private void unExpandViews(boolean deletion) {
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = getFirstVisiblePosition();
					int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					setSelectionFromTop(position, y);
					// end hack
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = mItemHeightNormal;
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * Adjust visibility and size to make it appear as though an item is being
	 * dragged around and other items are making room for it: If dropping the
	 * item would result in it still being in the same place, then make the
	 * dragged listitem's size normal, but make the item invisible. Otherwise,
	 * if the dragged listitem is still on screen, make it as small as possible
	 * and expand the item below the insert point. If the dragged item is not on
	 * screen, only expand the item below the current insertpoint.
	 */
	private void doExpansion() {
		int childnum = mDragPos - getFirstVisiblePosition();
		if (mDragPos > mFirstDragPos) {
			childnum++;
		}

		View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());

		for (int i = 0;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int height = mItemHeightNormal;
			int visibility = View.VISIBLE;
			if (vv.equals(first)) {
				// processing the item that is being dragged
				if (mDragPos == mFirstDragPos) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (mDragPos < getCount() - 1) {
					height = mItemHeightExpanded;
				}
			}
			ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
		}
	}

	/**
	 * 按下事件处理
	 * 
	 * @param ev
	 * @return
	 */
	private void handleActionDown(MotionEvent ev) {
		mDownX = ev.getX();
		mDownY = ev.getY();
		mDownPosition = pointToPosition((int) mDownX, (int) mDownY);
		if (mDownPosition == AdapterView.INVALID_POSITION) {
			return;
		}

		mDownView = getChildAt(mDownPosition - getFirstVisiblePosition());

		if (mDownView != null) {
			mViewWidth = mDownView.getWidth();
		}

		// 加入速度检测
		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
	}

	/**
	 * 处理手指滑动的方法
	 * 
	 * @param ev
	 * @return
	 */
	@SuppressLint("NewApi")
	private boolean handleActionMove(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null) {
			return super.onTouchEvent(ev);
		}

		float deltaX = ev.getX() - mDownX;
		float deltaY = ev.getY() - mDownY;
		// X方向滑动的距离大于mSlop并且Y方向滑动的距离小于mSlop，表示可以滑动
		if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < mSlop) {
			mSwiping = true;

			// 当手指滑动item,取消item的点击事件，不然我们滑动Item也伴随着item点击事件的发生
			MotionEvent cancelEvent = MotionEvent.obtain(ev);
			cancelEvent
					.setAction(MotionEvent.ACTION_CANCEL
							| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			onTouchEvent(cancelEvent);
		}

		if (mSwiping) {
			// 跟谁手指移动item
			ViewHelper.setTranslationX(mDownView, deltaX);
			// 透明度渐变
			ViewHelper.setAlpha(
					mDownView,
					Math.max(
							0f,
							Math.min(1f, 1f - 2f * Math.abs(deltaX)
									/ mViewWidth)));

			// 手指滑动的时候,返回true，表示SwipeDismissListView自己处理onTouchEvent,其他的就交给父类来处理
			return true;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * 手指抬起的事件处理
	 * 
	 * @param ev
	 */
	private void handleActionUp(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null || !mSwiping) {
			return;
		}

		float deltaX = ev.getX() - mDownX;

		// 通过滑动的距离计算出X,Y方向的速度
		mVelocityTracker.computeCurrentVelocity(1000);
		float velocityX = Math.abs(mVelocityTracker.getXVelocity());
		float velocityY = Math.abs(mVelocityTracker.getYVelocity());

		boolean dismiss = false; // item是否要滑出屏幕
		boolean dismissRight = false;// 是否往右边删除

		// 当拖动item的距离大于item的 1/6，item滑出屏幕
		if (Math.abs(deltaX) > mViewWidth / 6) {
			dismiss = true;
			dismissRight = deltaX > 0;

			// 手指在屏幕滑动的速度在某个范围内，也使得item滑出屏幕
		} else if (mMinFlingVelocity <= velocityX
				&& velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
			dismiss = true;
			dismissRight = mVelocityTracker.getXVelocity() > 0;

		}
		toRight = dismissRight;
		if (dismiss) {

			ViewPropertyAnimator.animate(mDownView)
					.translationX(dismissRight ? mViewWidth : -mViewWidth)
					// X轴方向的移动距离
					.alpha(0).setDuration(mAnimationTime)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Item滑出界面之后执行删除
							performDismiss(mDownView, mDownPosition);
						}
					});
		} else {
			// 将item滑动至开始位置
			ViewPropertyAnimator.animate(mDownView).translationX(0).alpha(1)
					.setDuration(mAnimationTime).setListener(null);
		}

		// 移除速度检测
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		mSwiping = false;
	}

	/**
	 * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
	 * 
	 * @param dismissView
	 * @param dismissPosition
	 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(mAnimationTime);
		animator.start();
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (onDismissCallback != null) {
					onDismissCallback.onDismiss(dismissPosition, toRight);
				}
				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
				// 所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);

			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}

	private void startDragging(Bitmap bm, int y) {
		stopDragging();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP;
		mWindowParams.x = -DptoPxUtil.dip2px(getContext(), 300);
		mWindowParams.y = y - mDragPoint + mCoordOffset;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		ImageView v = new ImageView(getContext());
		// int backGroundColor =
		// getContext().getResources().getColor(R.color.dragndrop_background);
		v.setBackgroundColor(dragndropBackgroundColor);
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		mWindowManager = (WindowManager) getContext()
				.getSystemService("window");
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	private void dragView(int x, int y) {
		float alpha = 1.0f;
		int width = mDragView.getWidth();

		if (mRemoveMode == SLIDE_RIGHT) {
			if (x > width / 2) {
				alpha = ((float) (width - x)) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		} else if (mRemoveMode == SLIDE_LEFT) {
			if (x < width / 2) {
				alpha = ((float) x) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		}
		mWindowParams.y = y - mDragPoint + mCoordOffset;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	private void stopDragging() {
		if (mDragView != null) {
			WindowManager wm = (WindowManager) getContext().getSystemService(
					"window");
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	/**
	 * 删除的回调接口
	 * 
	 * @author xiaanming
	 * 
	 */
	public interface OnDismissCallback {
		/**
		 * 
		 * @param dismissPosition
		 * @param direction
		 *            0左滑，1右滑
		 */
		public void onDismiss(int dismissPosition, boolean direction);
	}


	public void setDragListener(DragListener l) {
		mDragListener = l;
	}

	public void setDropListener(DropListener l) {
		mDropListener = l;
	}

	public void setRemoveListener(RemoveListener l) {
		mRemoveListener = l;
	}

	public interface DragListener {
		void drag(int from, int to);
	}

	public interface DropListener {
		void drop(int from, int to);
	}

	public interface RemoveListener {
		void remove(int which);
	}

}
