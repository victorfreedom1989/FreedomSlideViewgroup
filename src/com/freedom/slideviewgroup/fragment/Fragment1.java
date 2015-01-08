package com.freedom.slideviewgroup.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.freedom.slideviewgroup.R;
import com.freedom.slideviewgroup.ui.SlideMenu.RightListener;
import com.freedom.slideviewgroup.ui.SwipeDismissListView;
import com.freedom.slideviewgroup.ui.SwipeDismissListView.DragListener;
import com.freedom.slideviewgroup.ui.SwipeDismissListView.DropListener;
import com.freedom.slideviewgroup.ui.SwipeDismissListView.OnDismissCallback;

@SuppressLint("NewApi")
public class Fragment1 extends Fragment implements RightListener {

	private SwipeDismissListView listView;

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
		View view = View.inflate(getActivity(), R.layout.fragment_main, null);
		listView = (SwipeDismissListView) view.findViewById(R.id.display);
		listView.setAdapter(new TestAdpter());
		listView.setOnDismissCallback(new OnDismissCallback() {

			@Override
			public void onDismiss(int dismissPosition, boolean direction) {

			}
		});
		return view;
	}

	private class TestAdpter extends BaseAdapter {

		@Override
		public int getCount() {
			return 30;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText("第" + position + "个");
			return convertView;
		}

		private class ViewHolder {

			TextView tv;
		}
	}

	@Override
	public void notifyDataChange() {
		Toast.makeText(getActivity(), "向右滑动了", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void postNotifyDataChange() {
		
	}
}
