package com.sm.sm_linktop;


import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.os.Build;

import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class PermissionHelper{
  private final   Activity activity;
  private final   Context applicationContext;

    PermissionHelper( Activity activity, Context applicationContext){

        this.activity=activity;
        this.applicationContext =applicationContext;
    }

    public  final int REQUEST_ENABLE_BT = 1;

    public void checkPermissions() {

       final int PERMISSIONS_REQUEST_CODE = 1;
        if(Build.VERSION.SDK_INT < 31){
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        PERMISSIONS_REQUEST_CODE);
            }
        }


    }
    public boolean isPermissionsGranted() {

       final int PERMISSIONS_REQUEST_CODE = 1;
        if(Build.VERSION.SDK_INT < 31){
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return  false;
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
              return  false;
            }
        }
return  true;

    }

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public void enableBluetooth() {

        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported
            Log.d("Bluetooth","Bluetooth is not supported on this device.");
            return;
        }

        // Check if Bluetooth is already enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // Start an activity to request Bluetooth enabling (requires activity context)
           activity. startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("Bluetooth","Bluetooth is already enabled.");
        }
    }
    public boolean isBluetoothEnabled() {

        // Check if Bluetooth is supported
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported
            return false;
        }

        // Return whether Bluetooth is enabled
        return bluetoothAdapter.isEnabled();
    }
}