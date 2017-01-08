package com.yilvtzj.t9.adapter;

import java.util.List;

import com.yilvtzj.t9.R;
import com.yilvtzj.t9.activity.MainActivity;
import com.yilvtzj.t9.entity.PInfo;
import com.yilvtzj.t9.util.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AppsGridAdapter extends BaseAdapter {

	private Context mContext;
	private List<PInfo> list;

	public AppsGridAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public List<PInfo> getList() {
		return list;
	}

	public void setList(List<PInfo> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null && list.size() > 0) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_t9_apps, null);
		}
		PInfo pInfo = list.get(position);
		final TextView appBtn = ViewHolder.get(convertView, R.id.appBtn);
		// LinearLayout mark = ViewHolder.get(convertView, R.id.mark);
		final Drawable drawable = pInfo.getIcon();
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		appBtn.setCompoundDrawables(null, drawable, null, null);
		appBtn.setText(pInfo.getAppname());
		appBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					drawable.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
					break;
				case MotionEvent.ACTION_UP:
					Util.doStartAppWithPackageName(list.get(position).getPname(), mContext);
					((MainActivity) mContext).close();
				case MotionEvent.ACTION_CANCEL:
					drawable.mutate().clearColorFilter();
					break;
				}
				return true;
			}
		});
		return convertView;
	}

}