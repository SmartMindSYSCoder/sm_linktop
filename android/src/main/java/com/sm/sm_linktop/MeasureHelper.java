package com.sm.sm_linktop;

import android.content.Context;
import android.widget.Toast;


import com.linktop.infs.OnBtResultListener;
import com.linktop.whealthService.task.BtTask;

import com.linktop.infs.OnSpO2ResultListener;
import com.linktop.whealthService.task.OxTask;

import com.linktop.infs.OnBpResultListener;
import com.linktop.whealthService.task.BpTask;

import android.util.Log;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import org.json.JSONObject;

public class MeasureHelper  implements OnBtResultListener,OnSpO2ResultListener,OnBpResultListener{

    private final   Context applicationContext;

    private HcService mHcService;

private MethodChannel.Result result;


    private OxTask mOxTask;

    private BtTask mBtTask;

    private BpTask mBpTask;




    private   int spo2Value=0,hrValue=0;

    MeasureHelper(Context context,HcService mHcService){
        this.mHcService=mHcService;
        this.applicationContext=context;


    }


    private void disconnect(){
//        if (mHcService.isConnected) {
//            mHcService.disConnect();
//        }
    }

    public void measureTemp(MethodChannel.Result result ){
this.result=result;
        if (mHcService != null) {
            mBtTask = mHcService.getBleDevManager().getBtTask();
            mBtTask.setOnBtResultListener(this);

            if (mBtTask != null) {
                mBtTask.start();
                mHcService.sendEvent(true);

            }
        }


    }
    public void measureSpo2(MethodChannel.Result result){

        this.result=result;
        if (mHcService != null) {
            mOxTask = mHcService.getBleDevManager().getOxTask();
            mOxTask.setOnSpO2ResultListener(this);

            if (mOxTask != null) {
                mOxTask.start();
                mHcService.sendEvent(true);

            }
        }


    }
    public void measureBP(MethodChannel.Result result){

        this.result=result;
        if (mHcService != null) {
            mBpTask = mHcService.getBleDevManager().getBpTask();
            mBpTask.setOnBpResultListener(this);

            boolean isMeasureWrist;

            if (mBpTask != null) {

                isMeasureWrist = mBpTask.isMeasureWrist();

                Log.d("isMeasureWrist","isMeasureWrist:"+isMeasureWrist);

                if (mHcService.getBleDevManager().getBatteryTask().getPower() < 20) {

                    toast("Low power.Please charge.\n"+
                            "Battery Level: "+mHcService.getBleDevManager().getBatteryTask().getPower()+"%"
                    );
                }

                else{
                mBpTask.start();
                    mHcService.sendEvent(true);

                }

            }
        }


    }

    @Override
    public void onBtResult(double tempValue) {


      //  Log.d("tempValue","temperature is :"+tempValue);

        mHcService.sendEvent(false);

        JSONObject inputObject = new JSONObject();
        try {

            inputObject.put("temperature", tempValue);
            result.success(inputObject.toString());

        } catch (Exception var5) {
            Log.i("erorr",var5.getMessage());

        }


//        result.success(tempValue);
    }




    /// spo2  impl
    @Override
    public void onSpO2Result(int spo2, int hr) {
        spo2Value=spo2;
        hrValue=hr;


        if(spo2>0){



            if (mOxTask != null) {
                mOxTask.stop();
                mHcService.sendEvent(false);
            }




            JSONObject inputObject = new JSONObject();
            try {

                inputObject.put("spo2", spo2);

                inputObject.put("heart_rate", hr);
                result.success(inputObject.toString());

            } catch (Exception var5) {
                Log.i("erorr",var5.getMessage());

            }

//            result.success(spo2Value);

        }

      //  Log.d("spo2Value","spo2Value:" +spo2);
     //   Log.d("hrValue","spo2Value:" +hr);

//        model.setValue(spo2);
//        model.setHr(hr);
    }

    @Override
    public void onSpO2Wave(int value) {

       // Log.d("waveValue","waveValue:" +value);
//        oxWave.addData(value);
    }

    @Override
    public void onSpO2End() {

//
//        Log.d("onSpO2End"," ***** onSpO2End  ************" );
//        Log.d("spo2Value","spo2Value:" +spo2Value);
//        Log.d("hrValue","spo2Value:" +hrValue);
        disconnect();
        mHcService.sendEvent(false);





        result.success(spo2Value);

    }


    /// bp impl


    @Override
    public void onBpResult(final int systolicPressure, final int diastolicPressure, final int heartRate) {


//        Log.d("systolicPressure","systolicPressure:"+systolicPressure);
//        Log.d("diastolicPressure","diastolicPressure:"+diastolicPressure);
//        Log.d("heartRate","heartRate:"+heartRate);

        disconnect();

        JSONObject inputObject = new JSONObject();
        try {

            inputObject.put("systolic", systolicPressure);
            inputObject.put("diastolic", diastolicPressure);

            inputObject.put("heart_rate", heartRate);
            mHcService.sendEvent(false);
            result.success(inputObject.toString());

        } catch (Exception var5) {
            Log.i("erorr",var5.getMessage());

        }

    }

    @Override
    public void onBpResultError() {
        toast("The blood result error, Please retry!");
    }

    @Override
    public void onLeakError(int errorType) {
        toast("The blood result error, Please retry!");

        Log.d("onLeakError","onLeakError  :"+errorType);
    }



    private void toast(String msg){

        Toast.makeText(applicationContext, msg,  Toast.LENGTH_LONG).show();

    }
}