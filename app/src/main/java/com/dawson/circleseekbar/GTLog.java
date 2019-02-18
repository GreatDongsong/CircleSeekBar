package com.dawson.circleseekbar;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

public class GTLog {
    //add log define for ai service
    public static String AI_TAG = "AIINFO";
    private static StringBuilder gSBuilder = new StringBuilder();
    private static final boolean flag = BuildConfig.DEBUG;
    private static final int LOG_LEVEL_I = 1;
    private static final int LOG_LEVEL_W = 2;
    private static final int LOG_LEVEL_E = 3;
    private static LinkedBlockingQueue<Object[]> mMsg;
    private static Thread mSaveLogThread;
    private static boolean mLoop = true;

    public static void v(String tag, String msg) {
        if (flag) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (flag) {
            Log.i(tag, msg);
        }
        saveLog(LOG_LEVEL_I, tag, msg);
    }

    public static void d(String tag, String msg) {
        //add only for test audiogum
        if (flag || (tag != null && tag.equals("[AudioGumRequest]"))) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (flag) {
            Log.w(tag, msg);
        }
        saveLog(LOG_LEVEL_W, tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        saveLog(LOG_LEVEL_E, tag, msg);
    }

    private static void saveLog(int level, String tag, String msg) {
    }

    public static void stopSaveLogThread() {
        mLoop = false;
    }

    public static String caculateTimeDiffByNow(long startTime) {
        String retString = null;
        long lCurrTime = System.nanoTime();
        long lSpendTime = (lCurrTime - startTime) / 1000000;
        if (gSBuilder.length() > 0) {
            gSBuilder.delete(0, gSBuilder.length());
        }
        gSBuilder.append(lSpendTime).append(" ms[ ").append(lCurrTime)
                .append(" - ").append(startTime).append(" = ")
                .append(lCurrTime - startTime).append(" ns]");
        //i(tag, name + "take:" + gSBuilder.toString());
        retString = gSBuilder.toString();
        gSBuilder.delete(0, gSBuilder.length());
        return retString;
    }


    public static String caculateSecondTimeDiffByNow(long startTime) {
        String retString = null;
        long lCurrTime = System.nanoTime();
        long lSpendTime = (lCurrTime - startTime) / 1000000 / 1000;
        if (lSpendTime == 0) lSpendTime = 1;
        if (gSBuilder.length() > 0) {
            gSBuilder.delete(0, gSBuilder.length());
        }
        gSBuilder.append(lSpendTime).append(" S ");
        //i(tag, name + "take:" + gSBuilder.toString());
        retString = gSBuilder.toString();
        gSBuilder.delete(0, gSBuilder.length());
        return retString;
    }

    public static String caculateTimeDiffByNowSimple(long startTime) {
        String retString = null;
        long lCurrTime = System.nanoTime();
        long lSpendTime = (lCurrTime - startTime) / 1000000;
        if (gSBuilder.length() > 0) {
            gSBuilder.delete(0, gSBuilder.length());
        }
        if (lSpendTime > 0) {
            gSBuilder.append(String.valueOf(lSpendTime)).append("ms\n");
            //i(tag, name + "take:" + gSBuilder.toString());
            retString = gSBuilder.toString();
            gSBuilder.delete(0, gSBuilder.length());
        } else {
            retString = "";
        }
        return retString;
    }

    /**
     * use to return current time, nanosecond unit.
     *
     * @return nanosecond unit. get from System.nanoTime()
     */
    public static long getCurrentNanoTime() {
        return System.nanoTime();
    }

    public static long getLimitedTime(long timeLimited, String updateTime) {
        long result = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = sdf.parse(updateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.getTime() + timeLimited * 1000);
            cal.add(Calendar.HOUR, +8);
            result = cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}