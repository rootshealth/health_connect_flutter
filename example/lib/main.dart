import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:health_connect_flutter/health_connect_plugin.dart';
import 'package:package_info_plus/package_info_plus.dart';

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

  String? _packageName;
  bool? _hasPermission;
  HealthConnectData? _healthConnectData;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String? packageName;
    bool? hasPermission;
    HealthConnectData? healthConnectData;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      packageName = (await PackageInfo.fromPlatform()).packageName;
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
      _packageName = packageName;
      _hasPermission = hasPermission;
      _healthConnectData = healthConnectData;
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
              Text('Package name:\n $_packageName', textAlign: TextAlign.center),
              const SizedBox(height: 10),
              Text('Has permission: ${_hasPermission.toString()}'),
              TextButton(
                  onPressed: () async {
                    if (_hasPermission == true) {
                      await _healthConnectPlugin.openSettings();
                    } else {
                      await _healthConnectPlugin.requestPermission2();
                    }
                  },
                  child: const Text("Request permission")),
              TextButton(
                  onPressed: () async {
                    if (_hasPermission == true) {
                      final healthConnectData = await _healthConnectPlugin.getHealthConnectData();
                      setState(() {
                        _healthConnectData = healthConnectData;
                      });
                      return;
                    }
                  },
                  child: const Text("Request Health Data"))
            ],
          ),
        ),
      ),
    );
  }
}
