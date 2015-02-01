package com.droidroid.PM2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;

import java.text.DecimalFormat;
//import java.util.Calendar;

/**
 * Created by ed on 12/08/14.
 */
public class EquityBroadcastServiceWrapper extends Service {

    //binder
    private final IBinder mBinder = new LocalBinder();
    private final Handler handler = new Handler();


    private static final String TAG = "EquityBroadcastServiceWrapper";
    public static final String BROADCAST_ACTION = "com.droidroid.equitybroadcastservicewrapper.displayequities";

    Intent intent;
    int hero_array[]={0,0,0,0,0,0,0};
    int strict_equity_analysis=0;

    static{
        System.loadLibrary("equityAnalysis");
    }


    public native float[] equityAnalysisCore(int[] eh, int sea);



    public int onStartCommand (Intent sintent, int flags, int startId){

        //enqueue an action to be performed on a different thread than your own.
        //remove previous
        //handler.removeCallbacks(processEquities);
        // the Runnable r to be added to the message queue, to be run after the specified amount of time elapses.
        //handler.postDelayed(processEquities, 100); // .1 second
        //Log.d(TAG, "onStart()");

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        //Log.d(TAG, "onCreate()");

    }

    //deprecated
    @Override
    public void onStart(Intent intent, int startId) {
    }


    private void sendUpdatesToUI(String ehi, String emid, String vhi) {
        //Log.d(TAG,"processEquities() broadcast..");

        intent.putExtra("ehero", ehi);
        intent.putExtra("emid", emid);
        intent.putExtra("evillain", vhi);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent); //to activity listener

    }



    //Called by system when bound to pass a Binder back, the same binder always returned
    //When you implement your bound service, the most important part is defining the interface that your onBind() callback method returns.
    //There are a few different ways you can define your service's IBinder interface
    //If your service is private to your own application and runs in the same process as the client (which is common), you should create your interface
    //by extending the Binder class and returning an instance of it from onBind().
    //The client receives the Binder and can use it to directly access public methods available in either the Binder implementation or *even the Service*.

    //inner class
    public class LocalBinder extends Binder {
        EquityBroadcastServiceWrapper getService() {
            //Log.d(TAG,"s binder LocalBinder getService");
            // Return this instance of LocalService so clients can call public methods
            return EquityBroadcastServiceWrapper.this;
        }
    }

    /**
     * Called by system when the service is destroyed
     * Perform cleanup here
     */

    @Override
    public IBinder onBind(Intent intent) {
        //Log.d(TAG,"s binder onBind");
        return mBinder;
    }

    /** method for clients */
    public void be (int[] ha, int sea) {
        //Log.d(TAG,"s binder be");

        hero_array=ha;
        strict_equity_analysis=sea;

        handler.removeCallbacks(processEquities);
        handler.post(processEquities); // no delay

    }

    private Runnable processEquities = new Runnable() {
        public void run() {

            //Log.d(TAG," runnable started");

            //pass the hero preflop and the flop

            float[] eq = equityAnalysisCore(hero_array, strict_equity_analysis); //pass the hero

            //Log.d(TAG," eAc return");

            DecimalFormat df = new DecimalFormat("#.##");

            final String hero_result = df.format(eq[0]);
            final String equal_result = df.format(eq[1]);
            final String villain_result = df.format(eq[2]);

            sendUpdatesToUI(hero_result, equal_result, villain_result);


        }
    };

}
