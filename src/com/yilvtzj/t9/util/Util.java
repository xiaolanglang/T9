package com.yilvtzj.t9.util;

import java.util.List;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class Util {
	/**
	 * 把字母根据字母在九宫格键盘上的顺序转换成数字，例如：nihao，转成：64426
	 * 
	 * @param pinyin
	 * @return
	 */
	public static String zimuToShuzi(String pinyin) {
		char[] chars = pinyin.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length);
		for (char str : chars) {
			switch (str) {
			case 'a':
			case 'b':
			case 'c':
				builder.append(2);
				break;
			case 'd':
			case 'e':
			case 'f':
				builder.append(3);
				break;
			case 'g':
			case 'h':
			case 'i':
				builder.append(4);
				break;
			case 'j':
			case 'k':
			case 'l':
				builder.append(5);
				break;
			case 'm':
			case 'n':
			case 'o':
				builder.append(6);
				break;
			case 'p':
			case 'q':
			case 'r':
			case 's':
				builder.append(7);
				break;
			case 't':
			case 'u':
			case 'v':
				builder.append(8);
				break;
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				builder.append(9);
				break;
			default:
				if (isNumeric(String.valueOf(str))) {
					builder.append(str);
				}
				break;
			}
		}
		return builder.toString();
	}

	public static void doStartAppWithPackageName(String packagename, Context context) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	public static Bitmap drawable2Bitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		System.out.println("Drawable转Bitmap");
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	public static boolean isNumeric(CharSequence str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
