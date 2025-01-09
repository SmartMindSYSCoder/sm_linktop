package com.sm.sm_linktop;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** SmLinktopPlugin */
public class SmLinktopPlugin implements FlutterPlugin, MethodCallHandler,ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private EventChannel eventChannel;


  public Context applicationContext;
  public Activity activity;

  public PermissionHelper permissionHelper;
  public HealthMonitor healthMonitor;

private   MethodChannel.Result result;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "sm_linktop");
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "sm_linktop_status");


    channel.setMethodCallHandler(this);

    this.applicationContext = flutterPluginBinding.getApplicationContext();


  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    this.result=result;


    switch (call.method){


      case "checkPermission":{


        permissionHelper.checkPermissions();

      }
      break;
      case "init":{
        


        if(permissionHelper.isBluetoothEnabled()) {

          if (permissionHelper.isPermissionsGranted()) {
            eventChannel.setStreamHandler(healthMonitor);

            healthMonitor.init();

          } else {
            android.widget.Toast.makeText(applicationContext, "Please check bluetooth permission", Toast.LENGTH_SHORT).show();

            permissionHelper.checkPermissions();
          }
        }
        else{
          permissionHelper.enableBluetooth();
        }


      }

      break;

      case "connect":{





        if(permissionHelper.isBluetoothEnabled()) {

          if (permissionHelper.isPermissionsGranted()) {
            healthMonitor.connect();

          } else {
            android.widget.Toast.makeText(applicationContext, "Please check bluetooth permission", Toast.LENGTH_SHORT).show();

            permissionHelper.checkPermissions();
          }
        }
        else{
          permissionHelper.enableBluetooth();
        }

      }

      break;



      case "disconnect":{
        healthMonitor.disconnect(result);
      }
break;

      case "isConnected":{
        healthMonitor.isConnected(result);
      }
      break;

      case "measureTemp":{
        healthMonitor.measureTemp(result);
      }

      break;

      case "measureSpo2":{
        healthMonitor.measureSpo2(result);
      }

      break;

      case "measureBP":{
        healthMonitor.measureBP(result);
      }

      break;



      default:{
        result.notImplemented();
      }




    }

//    if (call.method.equals("getPlatformVersion")) {
//      result.success("Android " + android.os.Build.VERSION.RELEASE);
//    } else {
//      result.notImplemented();
//    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }




  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
//    this.activity = activityPluginBinding.getActivity();
    this.activity = activityPluginBinding.getActivity();
//    this.applicationContext = activityPluginBinding.getApplicationContext();
    permissionHelper = new PermissionHelper(activity, applicationContext);
    healthMonitor = new HealthMonitor(activity, applicationContext);


  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
  }

  @Override
  public void onDetachedFromActivity() {


    eventChannel.setStreamHandler(null);


  }


}
