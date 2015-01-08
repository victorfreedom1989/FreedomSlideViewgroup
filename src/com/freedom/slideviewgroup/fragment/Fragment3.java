package com.freedom.slideviewgroup.fragment;

import com.freedom.slideviewgroup.R;
import com.freedom.slideviewgroup.ui.SwipeDismissListView;
import com.freedom.slideviewgroup.ui.SlideMenu.RightListener;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment3 extends Fragment implements RightListener {
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
		text.setText(Fragment3.class.getSimpleName());
		return view;
	}

	@Override
	public void notifyDataChange() {
		Toast.makeText(getActivity(), "向右滑动了", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void postNotifyDataChange() {

	}

}
