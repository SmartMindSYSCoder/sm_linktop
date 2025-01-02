# sm_linktop

Smartmind Linktop flutter plugin.
This plugin to connect and communicate with Linktop Health Monitor

## Getting Started

add this to pubspec:

     sm_linktop:
       git: https://github.com/SmartMindSYSCoder/sm_linktop.git

To start use this api must be declare instance of plugin :

      final _smLinktopPlugin = SMLinktop();

Then must be check permission to give app needed permissions:

    _smLinktopPlugin.checkPermission();

Now you can start init the plugin and listen to connection status :

    _smLinktopPlugin.init();
     startListen();

The method _smLinktopPlugin.init()  will init and connect automatically.
You have another method called **connect()**   ,enable you reconnect when the device not connect or 
if called method **disconnect()** to disconnect the device

When  the device is connected you will be able to measure as you want:

    var temp = await  _smLinktopPlugin.measureTemp();
    var spo2 = await  _smLinktopPlugin.measureSpo2();
    var bp   = await  _smLinktopPlugin.measureBP();




Note: It is important to understand how the device work before using this plugin 

you can see this demo  video https://www.youtube.com/watch?v=CVZkU5JT8ss
I hope this clear.