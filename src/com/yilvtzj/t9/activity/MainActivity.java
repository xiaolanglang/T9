package com.yilvtzj.t9.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.github.jpinyin.PinyinFormat;
import com.github.jpinyin.PinyinHelper;
import com.yilvtzj.t9.R;
import com.yilvtzj.t9.adapter.AppsGridAdapter;
import com.yilvtzj.t9.adapter.KeyboardGridAdapter;
import com.yilvtzj.t9.entity.PInfo;
import com.yilvtzj.t9.util.Util;

public class MainActivity extends Activity implements OnTouchListener {

	private GridView keyboard, apps;
	private TextView content, msgTv;

	private AppsGridAdapter gridAdapter;

	private StringBuilder builder = new StringBuilder(20);
	private List<PInfo> resApps = new ArrayList<PInfo>(80);// 手机安装的所有非系统软件
	private List<String> quanpin = new ArrayList<String>(80);// 全拼数字
	private List<String> initials = new ArrayList<String>(80);// 首字母数字
	private List<PInfo> searchApps = new ArrayList<PInfo>(80);// 搜索到软件

	private boolean canInput = false;// 初始化app是异步操作，为了防止用户在没有初始化好就开始输入，这里加个标记
	private boolean close;// 是否退出app
	private int times = 0;// 次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initApp();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 按下去后，如果过程中没有移动，那么在抬起的时候就退出，否则不退出
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

	@Override
	protected void onPause() {
		super.onPause();
		setData(-1);
	}

	private void initView() {
		keyboard = (GridView) findViewById(R.id.keyboard);
		content = (TextView) findViewById(R.id.content);
		apps = (GridView) findViewById(R.id.apps);
		msgTv = (TextView) findViewById(R.id.msg);

		SpannableString styledText = new SpannableString(getString(R.string.msg));
		styledText.setSpan(new TextAppearanceSpan(this, R.style.msg0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(this, R.style.msg1), 6, styledText.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msgTv.setText(styledText, TextView.BufferType.SPANNABLE);

		keyboard.setAdapter(new KeyboardGridAdapter(this));

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
	 * 键盘按钮点击事件
	 * 
	 * @param position
	 */
	public void onBtnClick(int position) {
		// 当app刚打开的时候，可能还没有初始化好，这里最长等待1秒钟，实际运行中手机不卡的话，等待时间大约400m
		// 该等待只在打开app的时候会遇到，后面的查找过程中不会遇到
		while (!canInput) {
			times++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (times == 10) {
				times = 0;
				return;
			}
		}
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

	/**
	 * 删除按钮长按事件
	 * 
	 * @param position
	 * @return
	 */
	public void onBtnLongClick(int position) {
		if (position == 11) {
			setData(-1);
		}
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
		if ("".equals(builder.toString())) {
			msgTv.setVisibility(View.VISIBLE);
		} else {
			msgTv.setVisibility(View.GONE);
		}
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

	/**
	 * 获得已经安装的app，并提取出app的拼音等信息
	 */
	private void initApp() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				getInstalledApps();
			}
		}).start();
	}

	private void getInstalledApps() {
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
			boolean f = false;
			if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				f = true;
			} else if ("相机".equals(appName) || "浏览器".equals(appName) || "音乐".equals(appName) || "设置".equals(appName)
					|| "计算器".equals(appName) || "日历".equals(appName) || "便签".equals(appName) || "时钟".equals(appName)
					|| "相册".equals(appName) || "天气".equals(appName) || "录音".equals(appName)) {
				f = true;
			}

			if (f) {
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
		canInput = true;
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

}
