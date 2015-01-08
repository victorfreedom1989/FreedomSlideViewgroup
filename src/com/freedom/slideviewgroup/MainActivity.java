package com.freedom.slideviewgroup;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.freedom.slideviewgroup.fragment.Fragment1;
import com.freedom.slideviewgroup.fragment.Fragment2;
import com.freedom.slideviewgroup.ui.SlideMenu;

public class MainActivity extends FragmentActivity {
	private SlideMenu sm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sm = (SlideMenu) findViewById(R.id.slide);
		Fragment2 f2 =  new Fragment2();
		Fragment1 f1 =  new Fragment1();
		sm.setLeftListener(f2);
		sm.setRightListener(f1);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.main,f2, "main").commit();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.list,f1, "list").commit();
	}

}
