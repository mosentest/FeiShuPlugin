package cn.zipper.feishu.plugin.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import cn.zipper.feishu.plugin.utils.SPUtil;

public class MyNotificationListenerService extends NotificationListenerService {

    private final static String pkg_feishu = "com.ss.android.lark";

    private long time;

    @Override
    public void onCreate() {
        super.onCreate();
        acquireCpuWakeLock(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onListenerConnected() {
        Log.i("AAA", "onListenerConnected:");

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i("AAA", "onNotificationPosted:");

        //当收到一条消息时回调，sbn里面带有这条消息的具体信息
        Bundle extras = null;
        extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE); //通知title
        String content = extras.getString(Notification.EXTRA_TEXT); //通知内容

        String packageName = sbn.getPackageName();

        Log.i("AAA", "packageName:" + packageName);
        Log.i("AAA", "title:" + title);
        Log.i("AAA", "content:" + content);

        if (pkg_feishu.equals(packageName)) {
            if (content != null && content.contains("[红包]")) {
                startApp(sbn);
                defaultMediaPlayer(this);
            }
        } else if ("com.tencent.mm".equals(packageName)) {
            //给自己手机做保活微信
            boolean isKeep = (boolean) SPUtil.get(this, SPUtil.FILE_NAME, "feishu_keep", false);
            if (isKeep) {
                startApp(sbn);
                defaultMediaPlayer(this);
            }
        } else {
            boolean isKeep = (boolean) SPUtil.get(this, SPUtil.FILE_NAME, "feishu_keep", false);
            if (isKeep) {
                if (System.currentTimeMillis() - time > 5 * 60 * 1000L) {
                    time = System.currentTimeMillis();
                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(pkg_feishu);
                    if (launchIntentForPackage != null) {
                        launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntentForPackage);
                    }
                }
            }
        }
    }

    private void startApp(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        PendingIntent contentIntent = notification.contentIntent;
        try {
            contentIntent.send(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //当移除一条消息的时候回调，sbn是被移除的消息
        Log.i("AAA", "onNotificationRemoved:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCpuLock();
    }

    private static PowerManager.WakeLock sCpuWakeLock;

    private static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        Log.v("AAA", "acquireCpuWakeLock");
        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    @SuppressLint("InvalidWakeLockTag")
    private static PowerManager.WakeLock createPartialWakeLock(Context context) {
        Log.v("AAA", "createPartialWakeLock");
        String flag = "log";
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, flag);
    }

    public void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            Log.v("AAA", "releaseCpuLock");
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }


    /**
     * 播放系统默认提示音
     *
     * @return MediaPlayer对象
     * @throws Exception
     */
    public void defaultMediaPlayer(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}