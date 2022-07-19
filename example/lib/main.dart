import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:health_connect_flutter/health_connect_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _healthConnectPlugin = HealthConnectPlugin();

  bool? _hasPermission;
  HealthConnectData? _healthConnectData;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    HealthConnectData? healthConnectData;
    bool? hasPermission;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      //healthConnectData = await _healthConnectPlugin.getHealthConnectData();
      hasPermission = await _healthConnectPlugin.hasPermission();
    } on PlatformException {
      if (kDebugMode) {
        print("Failed to resolve platform method!");
      }
      //platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _healthConnectData = healthConnectData;
      _hasPermission = hasPermission;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Has permission: ${_hasPermission.toString()}'),
              TextButton(
                  onPressed: () async {
                    await _healthConnectPlugin.requestPermission();
                  },
                  child: const Text("Request permission"))
            ],
          ),
        ),
      ),
    );
  }
}
