package com.yilvtzj.t9.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.yilvtzj.t9.R;
import com.yilvtzj.t9.activity.MainActivity;

public class KeyboardGridAdapter extends BaseAdapter {

	private Context mContext;

	public KeyboardGridAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return 12;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_t9_btn, null);
		}
		if (position == 2 || position == 5 || position == 8 || position == 11) {
			// 屏幕最右侧图片不显示
			ViewHolder.get(convertView, R.id.content_height).setVisibility(View.GONE);
		}
		Button btn = ViewHolder.get(convertView, R.id.btn);
		final RelativeLayout content = ViewHolder.get(convertView, R.id.content);
		btn.setBackgroundResource(images[position]);
		final MainActivity ac = (MainActivity) mContext;
		btn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					content.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_light));
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					content.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
					break;
				}
				return false;
			}
		});
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ac.onBtnClick(position);
			}
		});
		if (position == 11) {
			btn.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					ac.onBtnLongClick(position);
					return false;
				}
			});
		}
		return convertView;
	}

	int[] images = { R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6,
			R.drawable.c7, R.drawable.c8, R.drawable.c9, R.drawable.c10, R.drawable.c0, R.drawable.c11 };

}