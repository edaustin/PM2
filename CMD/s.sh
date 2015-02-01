adb shell pm install -r "/data/local/tmp/com.droidroid.PM2"
adb shell am start -n "com.droidroid.PM2/com.droidroid.PM2.SplashActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
