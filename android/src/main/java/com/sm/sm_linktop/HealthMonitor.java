package com.sm.sm_linktop;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.os.Build;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import androidx.annotation.Nullable;
import com.linktop.constant.BluetoothState;
import com.linktop.constant.DeviceInfo;
import com.linktop.constant.WareType;

import com.linktop.DeviceType;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.infs.AuthCheckResult;

import com.linktop.infs.OnBatteryListener;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnDeviceInfoListener;
import com.linktop.infs.OnDeviceVersionListener;
import com.linktop.whealthService.BleDevManager;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.EventChannel;
import org.json.JSONObject;

public class HealthMonitor implements  ServiceConnection, MonitorDataTransmissionManager.OnServiceBindListener,OnBleConnectListener, OnBatteryListener,OnDeviceInfoListener,OnDeviceVersionListener,EventChannel.StreamHandler {

    private final   Activity activity;
    private final   Context applicationContext;

    public HcService mHcService;

    private MeasureHelper measureHelper;

    private EventChannel.EventSink events;

    private MethodChannel.Result result;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HcService.BLE_STATE) {
                final int state = (int) msg.obj;
                Log.e("Message", "receive state:" + state);



                if (state == BluetoothState.BLE_NOTIFICATION_ENABLED) {
                    //TODO
                    mHcService.dataQuery(HcService.DATA_QUERY_SOFTWARE_VER);
                } else {
                 //   mMonitorInfoFragment.onBleState(state);
                }
            } else if (msg.what == HcService.SDK_ERROR && msg.obj instanceof Throwable) {
                onSDKThrowable((Throwable) msg.obj);
            }
        }
    };

    HealthMonitor( Activity activity, Context applicationContext){

        this.activity=activity;
        this.applicationContext =applicationContext;
    }


   public void init(){
       MonitorDataTransmissionManager.isDebug(true);

       Intent serviceIntent = new Intent(activity, HcService.class);
      activity.  bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);




   }



   public void connect(MethodChannel.Result result){



        if(mHcService !=null) {
            if (mHcService.isConnected) {
                result.success(true);


                Log.d("connection","already connected");
//                mHcService.disConnect();
            } else {
                final int bluetoothEnable = mHcService.isBluetoothEnable();
                if (bluetoothEnable == -1) {
                    Log.d("Bluetooth", "Bluetooth not supported");
                    //   onBLENoSupported();
                } else if (bluetoothEnable == 0) {
                    Log.d("Bluetooth", "Bluetooth onOpenBLE");

//               onOpenBLE();
                } else {
                    mHcService.quicklyConnect(result,events,activity);
                }
            }
        }
        else{

            init();

            Log.d("not_init","the service not init yet");
        }

   }

   public void disconnect(MethodChannel.Result result){

        if(mHcService !=null && mHcService.isConnected){
    mHcService.disConnect();
    Log.d("connection","disConnect");
    sendEvent();

        result.success(true);
        }
}
   public void isConnected(MethodChannel.Result result){

        if(mHcService !=null && mHcService.isConnected){


        result.success(true);
        }
        else{
            result.success(false);

        }
}



    public void sendEvent(){
        JSONObject inputObject = new JSONObject();

        if(activity != null) {
            activity.runOnUiThread(() -> {
                if (events != null) {

                    try {

                        inputObject.put("isConnected", false);
                        inputObject.put("connectStateMsg", "Disconnected");
                        inputObject.put("isMeasuring", false);

                        events.success(inputObject.toString());

                    } catch (Exception var5) {
                        Log.i("events_erorr", "From events : *************   " + var5.getMessage());

                    }

                } else {
                    Log.i("events", " ***************************   events is null   ******************");

                }

            });
        }



    }


   public void measureTemp(MethodChannel.Result result){


        measureHelper.measureTemp(result);
   }

   public void measureSpo2(MethodChannel.Result result){



        measureHelper.measureSpo2(result);
   }
   public void measureBP(MethodChannel.Result result){



        measureHelper.measureBP(result);
   }

   ///  implements


    @Override
    public void onListen(Object args, EventChannel.EventSink events) {


        this.events = events;

//        Log.i("onListen","this is from onListen  ******************************"+events);
//
//        if (events != null) {
//            events.success(connectState);
//        }



    }

    @Override
    public void onCancel(Object args) {
    }




    @Override
    public void onServiceBind() {

        MonitorDataTransmissionManager.getInstance().needDeviceAuthCheck(new AuthCheckResult() {
            @Override
            public void onAuthCheckResult(boolean success) {
                Log.d("onAuthCheckResult","onAuthCheckResult: "+success);

                // toast("Auth check result: " + success);
            }

            @Override
            public void onAuthCheckResultTimeout() {
                Log.d("onAuthCheckResultTimeout","onAuthCheckResultTimeout: timeout");

//                toast("Auth check timeout!");
            }
        });

    }



    @Override
    public void onServiceUnbind() {

    }

    @Override
    public void onSDKThrowable(Throwable e) {
        e.printStackTrace();
    }


    public void onDestroy(){

        activity.  unbindService(this);

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        Log.d("service_connected","service is connected ****************");

        mHcService = ((HcService.LocalBinder) service).getService();
        mHcService.setHandler(mHandler);
        mHcService.initBluetooth();
        measureHelper=new MeasureHelper(applicationContext,mHcService);

        BleDevManager bleDevManager = mHcService.getBleDevManager();
        mHcService.setOnDeviceVersionListener(this);
        bleDevManager.getBatteryTask().setBatteryStateListener(this);
        bleDevManager.getDeviceTask().setOnDeviceInfoListener(this);


//        connect();

//        MonitorDataTransmissionManager.getInstance().setOnBleConnectListener(this);


        /*
         * If you know that your devices are not specific custom device,
         * you can skip to integrate this step.
         * 如果你知道设备不是需要进行认证检查的特定设备，可以跳过集成这一步。
         */
        if (mHcService != null)
            mHcService.getBleDevManager().getCommunicate().needDeviceAuthCheck(new AuthCheckResult() {
                @Override
                public void onAuthCheckResult(boolean success) {
                    Log.d("connection","service connection is:"+success);



                   // toast("Auth check result: " + success);
                }

                @Override
                public void onAuthCheckResultTimeout() {

                    Log.d("connection","service connection is:  timeout");

//                    toast("Auth check timeout!");
                }
            });

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mHcService = null;
    }








    /******
     * 以上两个回调值，可以根据设备ID保存在SP里，
     * 这样可以在某些未连接设备但已知设备ID的情况下，
     * 直接获取并显示设备的软硬件版本号
     * 但是切记，设备升级软硬件，会更新版本号，所以每次连接蓝牙设备都应该读取软硬件版本号，
     * 若有做本地保存，及时更新本地保存，才能保证任何情况下显示版本号都是最新的。
     **************************************************************/

    @Override
    public void onBLENoSupported() {
       // Toast.makeText(getContext(), "Your Android device does not support BLE feature！", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onOpenBLE() {
       // startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_OPEN_BT);
    }

    @Override
    public void onBleState(int bleState) {
        JSONObject inputObject = new JSONObject();

        switch (bleState) {
            case BluetoothState.BLE_CLOSED: {
                Log.d("bleState", "Turn on Bluetooth");
//                btnText.set(getString(R.string.turn_on_bluetooth));
                // reset();


                if (events != null) {

                    try {

                        inputObject.put("isConnected",false);
                        inputObject.put("connectStateMsg","Turn on Bluetooth");

                        events.success(inputObject.toString());

                    } catch (Exception var5) {
                        Log.i("erorr",var5.getMessage());

                    }

                }


            }

                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                try {
                   // Log.d("bleState","****************    connect  ********************");

                    if (events != null) {

                        try {

                            inputObject.put("isConnected",false);
                            inputObject.put("connectStateMsg","Disconnected");

                            events.success(inputObject.toString());

                        } catch (Exception var5) {
                            Log.i("erorr",var5.getMessage());

                        }

                    }


//                    btnText.set(getString(R.string.connect));
                   // reset();
                } catch (Exception ignored) {
                }
                break;
            case BluetoothState.BLE_CONNECTING_DEVICE:
//                try {
////                    btnText.set(getString(R.string.connecting));
//                    Log.d("bleState","connecting");
//
//                } catch (Exception ignored) {
//                }

                if (events != null) {

                    try {

                        inputObject.put("isConnected",false);
                        inputObject.put("connectStateMsg","Connecting");

                        events.success(inputObject.toString());

                    } catch (Exception var5) {
                        Log.i("erorr",var5.getMessage());

                    }

                }

                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                Log.d("bleState","disconnect");

                if (events != null) {

                    try {

                        inputObject.put("isConnected",true);
                        inputObject.put("connectStateMsg","connected");

                        events.success(inputObject.toString());

                    } catch (Exception var5) {
                        Log.i("erorr",var5.getMessage());

                    }

                }

//                btnText.set(getString(R.string.disconnect));
                break;
        }
    }


    @Override
    public void onUpdateDialogBleList() {}


    @Override
    public void onBatteryCharging() {


        Log.d("battery"," Battery Charging");

//        power.set("充电中...");
    }

    /*
     * 设备拔掉USB充电线，正常使用
     * */
    @Override
    public void onBatteryQuery(int batteryValue) {

        Log.d("battery","Battery level "+batteryValue+"%");

//        power.set(batteryValue + "%");
    }

    /*
     * 设备插着USB充电线，已充满电的状态
     * */
    @Override
    public void onBatteryFull() {

//        power.set("已充满");
        Log.d("battery"," Battery full");


    }

    @Override
    public void onDeviceInfo(DeviceInfo device) {
        Log.e("onDeviceInfo", device.toString());

        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }

    @Override
    public void onReadDeviceInfoFailed() {
//        id.set("Unable to read ID.");
//        key.set("Unable to read key.");
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }

    @Override
    public void onDeviceVersion(@WareType int wareType, String version) {
        switch (wareType) {
            case WareType.VER_SOFTWARE:
                Log.d("version"," VER_SOFTWARE :"+version);

//                softVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_HARDWARE_VER);
                }
                break;
            case WareType.VER_HARDWARE:
                Log.d("version"," VER_HARDWARE :"+version);

//                hardVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_FIRMWARE_VER);
                }
                break;
            case WareType.VER_FIRMWARE:
//                firmVer.set(version);
                Log.d("version"," VER_FIRMWARE :"+version);

                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_CONFIRM_ECG_MODULE_EXIST);
                }
                break;
        }

    }
}