package com.dexterous.flutterlocalnotifications;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterRunArguments;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationService extends JobIntentService implements MethodChannel.MethodCallHandler {
    public static final String TAG = "NotificationService";
    private static AtomicBoolean started = new AtomicBoolean(false);
    private static FlutterNativeView backgroundFlutterView;
    private static MethodChannel backgroundChannel;
    private static PluginRegistrantCallback pluginRegistrantCallback;

    private String appBundlePath;

    public static void onInitialized() {
        started.set(true);
    }

    public static void startNotificationService(Context context, long callbackHandle) {
        FlutterMain.ensureInitializationComplete(context, null);
        String mAppBundlePath = FlutterMain.findAppBundlePath(context);
        FlutterCallbackInformation cb =
                FlutterCallbackInformation.lookupCallbackInformation(callbackHandle);
        if (cb == null) {
            Log.e(TAG, "Fatal: failed to find callback");
            return;
        }

        // Note that we're passing `true` as the second argument to our
        // FlutterNativeView constructor. This specifies the FlutterNativeView
        // as a background view and does not create a drawing surface.
        backgroundFlutterView = new FlutterNativeView(context, true);
        if (mAppBundlePath != null && !started.get()) {
            Log.i(TAG, "Starting NotificationService...");
            FlutterRunArguments args = new FlutterRunArguments();
            args.bundlePath = mAppBundlePath;
            args.entrypoint = cb.callbackName;
            args.libraryPath = cb.callbackLibraryPath;
            backgroundFlutterView.runFromBundle(args);
            pluginRegistrantCallback.registerWith(backgroundFlutterView.getPluginRegistry());
        }
    }

    public static void setBackgroundChannel(MethodChannel channel) {
        backgroundChannel = channel;
    }


    public static boolean setBackgroundFlutterView(FlutterNativeView view) {
        if (backgroundFlutterView != null && backgroundFlutterView != view) {
            Log.i(TAG, "setBackgroundFlutterView tried to overwrite an existing FlutterNativeView");
            return false;
        }
        backgroundFlutterView = view;
        return true;
    }

    public static void setPluginRegistrant(PluginRegistrantCallback callback) {
        pluginRegistrantCallback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        FlutterMain.ensureInitializationComplete(context, null);
        appBundlePath = FlutterMain.findAppBundlePath(context);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }


    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {

    }
}
