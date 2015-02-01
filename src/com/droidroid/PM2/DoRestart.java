package com.droidroid.PM2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.util.Log;

/**
 * Created by ed on 7/19/14.
 * http://stackoverflow.com/questions/6609414/howto-programatically-restart-android-app
 */

public class DoRestart {

    //This will also reinitialize jni classes and all static instances.

    static String TAG = "DoRestart";
    static int DELAY = 75; //100

    public static void doRestart(Context c) {
        try {       PackageManager pm = c.getPackageManager();
                    Intent mStartActivity = pm.getLaunchIntentForPackage(c.getPackageName());

                    c.stopService(new Intent(c,EquityBroadcastServiceWrapper.class));

                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(c, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + DELAY, mPendingIntent);
                        //kill the application
                        System.exit(0);

        } catch (Exception ex) {
            //Log.e(TAG, "Was not able to restart application "+ex);
        }
    }



}