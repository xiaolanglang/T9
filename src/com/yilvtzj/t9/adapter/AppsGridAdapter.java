package com.yilvtzj.t9.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yilvtzj.t9.R;
import com.yilvtzj.t9.entity.PInfo;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_t9_apps, null);
		}
		PInfo pInfo = list.get(position);
		final TextView appBtn = ViewHolder.get(convertView, R.id.appBtn);
		final LinearLayout mark = ViewHolder.get(convertView, R.id.mark);
		Drawable drawable = pInfo.getIcon();
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		appBtn.setCompoundDrawables(null, drawable, null, null);
		appBtn.setText(pInfo.getAppname());
		appBtn.post(new Runnable() {
			@Override
			public void run() {
				int height = appBtn.getHeight();
				ViewGroup.LayoutParams layoutParams = mark.getLayoutParams();
				layoutParams.height = height;
				mark.setLayoutParams(layoutParams);
			}
		});
		return convertView;
	}

}