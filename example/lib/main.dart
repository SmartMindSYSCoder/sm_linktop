import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:sm_linktop/sm_linktop.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _smLinktopPlugin = SMLinktop();

  String result="Data:";
  String connection="Disconnected";
  @override
  void initState() {
    super.initState();
  }


 // late Stream connectionStatus;
  StreamSubscription<dynamic>? _subscription;


  bool isConnected=false,isMeasuring=false;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('SM Linktop example app'),
        ),
        body: Center(
          child: Column(
            children: [


              if(isMeasuring)

              const Row(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [

                  SizedBox(
                      height: 40,
                      width: 40,
                      child: CircularProgressIndicator()),

                  SizedBox(width: 20,),

                  Text("Measuring...  please wait"),
                ],
              ),


              Text("connection status:  $connection"),


              // StreamBuilder<dynamic>(
              //   stream: _smLinktopPlugin.statusStream,
              //   builder: (context, snapshot) {
              //     if (snapshot.hasData) {
              //       return Text('connection: ${snapshot.data}');
              //     } else {
              //       return Text('Waiting for events...');
              //     }
              //   },
              // ),

              const SizedBox(height: 15,),

              Text(result),
              const SizedBox(height: 15,),


              TextButton(onPressed: (){

                _smLinktopPlugin.checkPermission();
              }, child: const Text("check Permission ")),

              const SizedBox(height: 15,),
              TextButton(onPressed: (){

                _smLinktopPlugin.init();
                startListen();


              }, child: const Text("init ")),
              const SizedBox(height: 15,),
              TextButton(onPressed: (){

                startListen();
                _smLinktopPlugin.connect();


              }, child: const Text("connect ")),

              const SizedBox(height: 15,),

              TextButton(onPressed: ()async{


             var disconnect=await   _smLinktopPlugin.disconnect();

             if(disconnect){

               stopListen();
             }

              }, child: const Text("disconnect")),

              const SizedBox(height: 15,),
              TextButton(onPressed: ()async{

           var temp=await     _smLinktopPlugin.measureTemp();



           setState(() {
             result="Temperature result:\n$temp";
           });
              }, child: const Text("Measure Temp ")),

              const SizedBox(height: 15,),
              TextButton(onPressed: () async {

             var spo2=await   _smLinktopPlugin.measureSpo2();
                setState(() {
                  result="Spo2 result:\n$spo2";
                });

              }, child: const Text("Measure Spo2 ")),

              const SizedBox(height: 15,),
              TextButton(onPressed: () async {

                var bp=await   _smLinktopPlugin.measureBP();
                setState(() {
                  result="BP result:\n$bp";
                });

              }, child: const Text("Measure BP ")),


            ],
          ),
        ),
      ),
    );
  }

  startListen(){

     _smLinktopPlugin.statusStream.listen((data) {
        print("*****************  on data  $data");
        try {
          var jsonParse = jsonDecode(data.toString());
          if (jsonParse['isConnected'] != null) {
            isConnected = jsonParse['isConnected'];
            isMeasuring = jsonParse['isMeasuring'];

            connection = jsonParse['connectStateMsg'].toString();
            setState(() {

            });
          }
        } catch (e) {
          print(e);
        }
      }, onError: (e) {
        print("on error :${e}");
      });
  }

  stopListen(){

    if(_subscription !=null) {
      _subscription!.cancel();
      _subscription = null;
    }
  }

}
