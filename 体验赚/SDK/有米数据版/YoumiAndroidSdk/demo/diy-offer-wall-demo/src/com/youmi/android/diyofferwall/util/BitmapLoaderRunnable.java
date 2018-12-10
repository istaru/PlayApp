package com.youmi.android.diyofferwall.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.youmi.android.diyofferwall.R;
import com.youmi.android.diyofferwall.listener.BitmapDownloadListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 线程池 异步加载图片示例（仅供参考，实际使用时，建议开发者自行处理图片加载部分）
 */
public class BitmapLoaderRunnable implements Runnable {

	private Context mContext;

	private String mBmUrl;

	private BitmapDownloadListener mListener;

	public BitmapLoaderRunnable(Context context, BitmapDownloadListener listener, String bmUrl) {
		this.mContext = context;
		this.mListener = listener;
		this.mBmUrl = bmUrl;
	}

	/**
	 * 本地中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
	 */
	@Override
	public void run() {
		if (mBmUrl == null) {
			return;
		}

		try {
			String storeFileName = Util.md5(mBmUrl);
			if (TextUtils.isEmpty(storeFileName)) {
				return;
			}
			File file = mContext.getFileStreamPath(storeFileName);
			if (file.exists()) {

				// 如果本地已经有图片的话
				mListener.onLoadBitmap(mBmUrl, BitmapFactory.decodeFile(file.getPath()));

			} else {

				// 如果本地没有图片就上网下载
				BufferedOutputStream bos = null;
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) ((new URL(mBmUrl)).openConnection());
					conn.setDoInput(true);
					conn.connect();

					if (conn.getResponseCode() != 200) {

						// 网络加载失败,则显示加载失败的图片
						mListener.onLoadBitmap(mBmUrl, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon));
						Log.e("Youmi", mBmUrl + " failed to load icon");

					} else {

						// 下载图片到本地
						bos = new BufferedOutputStream(new FileOutputStream(file), 5 * 1024 * 1024);
						int len;
						byte[] buff = new byte[10240];
						while ((len = conn.getInputStream().read(buff)) > 0) {
							bos.write(buff, 0, len);
						}
						bos.flush();
						bos.close();
						bos = null;
						mListener.onLoadBitmap(mBmUrl, BitmapFactory.decodeFile(file.getPath()));
					}
					conn.disconnect();
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					try {
						if (conn != null) {
							conn.disconnect();
						}
					} catch (Throwable e) {
						e.printStackTrace();

					}

				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
