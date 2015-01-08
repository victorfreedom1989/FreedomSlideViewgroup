/**   
 * @Company: Batways
 * @Project:Lottey 
 * @Title: Lottey.java
 * @Package com.freedom.lottey
 * @Description: TODO
 * @author victor_freedom (x_freedom_reddevil@126.com)
 * @date 2014��8��20�� ����2:10:22
 * @version V1.0   
 */

package com.freedom.slideviewgroup;


import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * @ClassName: FreedomApplication
 * @author victor_freedom (x_freedom_reddevil@126.com)
 * @createddate 2015-1-5 上午11:00:26
 * @Description: TODO
 */
public class FreedomApplication extends Application {
	public static SharedPreferences sp;
	private FreedomApplication instance;

	/**
	 * 屏幕宽高
	 */
	public static int mScreenWidth;
	public static int mScreenHeight;

	@Override
	public void onCreate() {
		super.onCreate();
		if (instance == null) {
			instance = this;
		}
		sp = getSharedPreferences("share", MODE_PRIVATE);
		DisplayMetrics metric = getApplicationContext().getResources()
				.getDisplayMetrics();
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

}
