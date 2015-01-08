package com.freedom.slideviewgroup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.freedom.slideviewgroup.R;
import com.freedom.slideviewgroup.ui.SlideMenu.LeftListener;

public class Fragment2 extends Fragment implements LeftListener {
	private TextView text;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_1, null);
		text = (TextView) view.findViewById(R.id.texts);
		text.setText(Fragment2.class.getSimpleName());
		// 该fragment里面的控件必须有响应点击事件（既需要消耗触摸事件，也可以是其他的触摸事件）的控件，不然所有触摸事件都不会响应
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return view;
	}

	@Override
	public void notifyDataChange() {
		Toast.makeText(getActivity(), "向左滑动了", Toast.LENGTH_SHORT).show();
	}

}
