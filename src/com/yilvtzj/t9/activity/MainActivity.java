package com.yilvtzj.t9.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.github.jpinyin.PinyinFormat;
import com.github.jpinyin.PinyinHelper;
import com.yilvtzj.t9.R;
import com.yilvtzj.t9.adapter.AppsGridAdapter;
import com.yilvtzj.t9.adapter.KeyboardGridAdapter;
import com.yilvtzj.t9.entity.PInfo;
import com.yilvtzj.t9.util.Util;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnTouchListener {

	private GridView keyboard, apps;
	private TextView content;

	private StringBuilder builder = new StringBuilder(20);
	private List<PInfo> resApps = new ArrayList<PInfo>(80);// 手机安装的所有非系统软件
	private List<String> quanpin = new ArrayList<String>(80);// 全拼数字
	private List<String> initials = new ArrayList<String>(80);// 首字母数字
	private List<PInfo> searchApps = new ArrayList<PInfo>(80);// 搜索到软件

	private AppsGridAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		new Thread(new Runnable() {

			@Override
			public void run() {
				getInstalledApps();
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position = position + 1;
		switch (position) {
		case 10:
			stayInBack();
			break;
		case 12:
			int l = builder.length();
			if (l > 0) {
				setData(-2);
			}
			break;
		default:
			if (position == 11) {
				position = 0;
			}
			setData(position);
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 11) {
			setData(-1);
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stayInBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		setData(-1);
	}

	private void initView() {
		keyboard = (GridView) findViewById(R.id.keyboard);
		content = (TextView) findViewById(R.id.content);
		apps = (GridView) findViewById(R.id.apps);

		keyboard.setAdapter(new KeyboardGridAdapter(this));
		keyboard.setOnItemClickListener(this);
		keyboard.setOnItemLongClickListener(this);

		gridAdapter = new AppsGridAdapter(this);
		gridAdapter.setList(searchApps);
		apps.setAdapter(gridAdapter);
		apps.setOnItemClickListener(new AppsOnItemClickListener());

		findViewById(R.id.scroll).setOnTouchListener(this);
		content.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stayInBack();
			}
		});
	}

	/**
	 * 设置并显示界面数据
	 * 
	 * @param msg
	 *            -1表示删除所有数据，-2表示删除末尾一个数据
	 */
	private void setData(int msg) {
		if (msg == -2) {// 删除末尾一个数据
			builder.deleteCharAt(builder.length() - 1);
		} else if (msg == -1) {
			// 删除所有数据
			builder.delete(0, builder.length());
		} else {
			// 添加数据
			builder.append(msg);
		}
		content.setText(builder.toString());
		showApps();
	}

	/**
	 * 显示检索到的app
	 */
	private void showApps() {
		String shuru = builder.toString();
		searchApps.clear();
		if (shuru.length() == 0) {
			gridAdapter.notifyDataSetChanged();
			return;
		}
		for (int i = 0, l = resApps.size(); i < l; i++) {
			String shou = initials.get(i);
			if (shou.indexOf(shuru) != -1) {
				searchApps.add(resApps.get(i));
				continue;
			}
			String quan = quanpin.get(i);
			if (quan.indexOf(shuru) != -1) {
				searchApps.add(resApps.get(i));
			}
		}
		gridAdapter.notifyDataSetChanged();
	}

	private void getInstalledApps() {
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
			boolean f = false;
			if ("相机".equals(appName) || "浏览器".equals(appName) || "音乐".equals(appName) || "设置".equals(appName)
					|| "计算器".equals(appName) || "日历".equals(appName) || "便签".equals(appName) || "时钟".equals(appName)
					|| "相册".equals(appName) || "天气".equals(appName) || "录音".equals(appName)) {
				f = true;

			}
			if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || f) {
				// 非系统app
				PInfo newInfo = new PInfo();
				newInfo.setAppname(p.applicationInfo.loadLabel(getPackageManager()).toString());
				newInfo.setPname(p.packageName);
				newInfo.setVersionName(p.versionName);
				newInfo.setVersionCode(p.versionCode);
				newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
				String quan = PinyinHelper.convertToPinyinString(newInfo.getAppname(), "", PinyinFormat.WITHOUT_TONE);
				String shou = PinyinHelper.getShortPinyin(newInfo.getAppname());
				quanpin.add(Util.zimuToShuzi(quan));
				initials.add(Util.zimuToShuzi(shou));
				resApps.add(newInfo);
			}
		}
	}

	private void stayInBack() {
		finish();
		MainActivity.this.overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out);
	}

	private class AppsOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			stayInBack();
			Util.doStartAppWithPackageName(searchApps.get(position).getPname(), MainActivity.this);
		}

	}

	private boolean close;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			close = true;
			break;
		case MotionEvent.ACTION_UP:
			if (close) {
				stayInBack();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			close = false;
			break;
		}
		return false;
	}

}
