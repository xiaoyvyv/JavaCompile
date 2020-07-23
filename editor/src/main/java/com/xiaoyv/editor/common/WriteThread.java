package com.xiaoyv.editor.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Created by why on 2017/10/2.
 */
public class WriteThread extends Thread {
    public static final int MSG_WRITE_OK = 0x201;
    public static final int MSG_WRITE_FAIL = 0x202;
    private final Handler handler;
    private String outputPath;
    private String text;

    public WriteThread(String text, String outputPath, Handler handler) {
        this.outputPath = outputPath;
        this.text = text;
        this.handler = handler;
    }

    @Override
    public void run() {
        synchronized (handler) {
            writeFile(text, new File(outputPath));
        }
    }

    /**
     * 写文件
     */
    private void writeFile(final String text, File outputFile) {
        boolean isOk;
        File parentFile = outputFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.exists()) {
                boolean mkdir = parentFile.mkdir();
                Log.e("WriteThread", "创建：" + mkdir);
            }
        }
        FileOutputStream fileOutputStream;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(text);
            outputStreamWriter.flush();
            isOk = true;
        } catch (IOException e) {
            e.printStackTrace();
            isOk = false;

        } finally {
            if (outputStreamWriter != null)
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (isOk) {
            if (handler != null)
                handler.sendMessage(Message.obtain(handler, MSG_WRITE_OK));
        } else {
            if (handler != null)
                handler.sendMessage(Message.obtain(handler, MSG_WRITE_FAIL));
        }
    }
}
