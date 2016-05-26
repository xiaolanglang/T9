package com.yilvtzj.t9.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yilvtzj.t9.R;

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_t9_btn, null);
		}
		if (position == 2 || position == 5 || position == 8 || position == 11) {
			// 屏幕最右侧图片不显示
			ViewHolder.get(convertView, R.id.content_height).setVisibility(View.GONE);
		}
		ImageView image = ViewHolder.get(convertView, R.id.img);
		image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), iamges[position]));
		return convertView;
	}

	int[] iamges = { R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6,
			R.drawable.c7, R.drawable.c8, R.drawable.c9, R.drawable.c10, R.drawable.c0, R.drawable.c11 };

}