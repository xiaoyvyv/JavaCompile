package com.lib.utils;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.smtt.sdk.CookieManager;
import com.xiaoyv.http.OkHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/29.
 *
 * @author 王怀玉
 * @explain DownloadUtils
 */
public class DownloadUtils {
    private static DownloadUtils downloadUtils;
    private OkHttpClient okHttpClient = OkHttp.getInstance();

    public static DownloadUtils getInstance() {
        if (downloadUtils == null) {
            synchronized (DownloadUtils.class) {
                if (downloadUtils == null) {
                    downloadUtils = new DownloadUtils();
                }
            }
        }
        return downloadUtils;
    }


    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final String filename, final OnDownloadListener listener) {
        CookieManager cookieManager = CookieManager.getInstance();
        String CookieStr = cookieManager.getCookie(MyUtils.getHost(url));
        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isEmpty(CookieStr)) {
            builder.addHeader("Cookie", CookieStr);
        }
        LogUtils.i("下载链接：" + url);
        Request request = builder.url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, final IOException e) {
                Utils.runOnUiThread(() -> {
                    // 下载失败
                    listener.onDownloadFailed(e.toString());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Utils.runOnUiThread(() -> {
                        // 下载失败
                        listener.onDownloadFailed("下载出错：" + response.code());
                    });
                    return;
                }
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                FileUtils.createOrExistsDir(saveDir);
                try {
                    is = Objects.requireNonNull(response.body()).byteStream();
                    long total = Objects.requireNonNull(response.body()).contentLength();
                    File file = new File(saveDir, filename);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);

                        Utils.runOnUiThread(() -> {
                            // 下载中
                            listener.onDownloading(progress);
                        });
                    }
                    fos.flush();
                    // 下载完成
                    Utils.runOnUiThread(listener::onDownloadSuccess);
                } catch (final Exception e) {
                    Utils.runOnUiThread(() -> {
                        // 下载失败
                        listener.onDownloadFailed(e.toString());
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException ignored) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    /**
     * @param request  下载请求头
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void downloadWithRequest(final Request request, final String saveDir, final String filename, final OnDownloadListener listener) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Utils.runOnUiThread(() -> {
                    // 下载失败
                    listener.onDownloadFailed(e.toString());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = Objects.requireNonNull(response.body()).byteStream();
                    long total = Objects.requireNonNull(response.body()).contentLength();
                    File file = new File(savePath, filename);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);

                        Utils.runOnUiThread(() -> {
                            // 下载中
                            listener.onDownloading(progress);
                        });
                    }
                    fos.flush();

                    // 下载完成
                    Utils.runOnUiThread(listener::onDownloadSuccess);
                } catch (final Exception e) {
                    Utils.runOnUiThread(() -> {
                        // 下载失败
                        listener.onDownloadFailed(e.toString());
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException ignored) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            boolean newFile = downloadFile.createNewFile();
            LogUtils.i("下载目录是否存在：" + newFile);
        }
        return downloadFile.getAbsolutePath();
    }


    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(String error);
    }
}