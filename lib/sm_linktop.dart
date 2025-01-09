
import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';


class SMLinktop {

  final methodChannel = const MethodChannel('sm_linktop');
  final _eventChannel = const EventChannel("sm_linktop_status");


  Future checkPermission() async{
   await methodChannel.invokeMethod('checkPermission');
  }



  Future init() async{
   return await  methodChannel.invokeMethod('init');
  }
  Future connect() async{
  return await   methodChannel.invokeMethod('connect');
  }
  Future<bool> disconnect() async{
    var disconnected=await methodChannel.invokeMethod('disconnect');
    return disconnected ?? true;
  }
  Future<bool> isConnected() async{
    var connected=await methodChannel.invokeMethod('isConnected');
    return connected ?? false;
  }


  Future<dynamic> measureTemp() async{

  var temp=  await methodChannel.invokeMethod('measureTemp');


    return temp;
  }
  Future<dynamic> measureSpo2() async{


   var spo2= await methodChannel.invokeMethod('measureSpo2');

   // print("*********************   spo2     $spo2");

   return  spo2;
  }
  Future<dynamic> measureBP() async{


   return await methodChannel.invokeMethod('measureBP');


  }


   Stream<dynamic>? _statusStream;

   Stream<dynamic> get statusStream {
     _statusStream = _eventChannel.receiveBroadcastStream();
    return _statusStream!;
  }

}
