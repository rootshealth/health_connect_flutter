import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:health_connect_flutter/health_connect_plugin.dart';
import 'package:package_info_plus/package_info_plus.dart';

class HealthConnectFlutterApiImpl implements HealthConnectFlutterApi {
  HealthConnectFlutterApiImpl() {
    HealthConnectFlutterApi.setup(this);
  }

  @override
  void onWorkoutDataUpdated(HealthConnectWorkoutData healthConnectWorkoutData) {
    final info = '''HealthConnectWorkoutData
    Identifier: ${healthConnectWorkoutData.identifier}
    Name: ${healthConnectWorkoutData.name}
    Description: ${healthConnectWorkoutData.description}
    Activity: ${healthConnectWorkoutData.activityType}
    StartTimestamp: ${healthConnectWorkoutData.startTimestamp}
    EndTimestamp: ${healthConnectWorkoutData.endTimestamp}''';
    print(info);
  }
}

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HealthConnectFlutterApiImpl();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _healthConnectHostApi = HealthConnectHostApi();

  String? _packageName;
  PermissionResult? _permissionsResult;
  PermissionResult? _activityRecognitionPermissionResult;
  PermissionResult? _bodySensorsPermissionResult;
  PermissionResult? _oAuthPermissionResult;

  HealthConnectData? _healthConnectData;
  List<HealthConnectWorkoutData?>? _healthConnectWorkoutsData;

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
      //hasPermission = await _healthConnectPlugin.hasPermission();
      _healthConnectHostApi.subscribeToHealthConnectWorkoutsData();

      final endDateTime = DateTime.now();
      final startDateTime = endDateTime.subtract(const Duration(days: 30));
      final predicate = Predicate(
          startDateInMsSinceEpoch: startDateTime.millisecondsSinceEpoch,
          endDateInMsSinceEpoch: endDateTime.millisecondsSinceEpoch);
      //_healthConnectHostApi.getHealthConnectWorkoutsData(predicate);
      _healthConnectHostApi.getHealthConnectData();
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
      //_hasPermission = hasPermission;
      _healthConnectData = healthConnectData;
    });
  }

  @override
  Widget build(BuildContext context) {
    final buttonStyle = TextButton.styleFrom(
      primary: Colors.white,
      backgroundColor: Colors.teal,
      onSurface: Colors.grey,
    );
    const titleStyle = TextStyle(fontWeight: FontWeight.bold, fontSize: 14);
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text('Package name', style: titleStyle),
                Text('$_packageName', textAlign: TextAlign.center),
                const Padding(
                  padding: EdgeInsets.only(top: 30),
                  child: Text("Permissions", style: titleStyle),
                ),
                Text('Permission status: ${_permissionsResult?.permissionStatus.toString()}'),
                Text(
                    'Activity Recognition status: ${_activityRecognitionPermissionResult?.permissionStatus.toString()}'),
                Text(
                    'Body sensors status: ${_bodySensorsPermissionResult?.permissionStatus.toString()}'),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final permissionResult = await _healthConnectHostApi.requestPermissions();
                      setState(() {
                        _permissionsResult = permissionResult;
                      });
                    },
                    child: const Text("Request permissions")),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final result = await _healthConnectHostApi.hasActivityRecognitionPermission();
                      final permissionStatus =
                          result ? PermissionStatus.granted : PermissionStatus.denied;
                      final permissionResult = PermissionResult(
                          permissionType: PermissionType.activityRecognition,
                          permissionStatus: permissionStatus);
                      setState(() {
                        _activityRecognitionPermissionResult = permissionResult;
                      });
                    },
                    child: const Text("Check Activity Recognition permission")),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final result = await _healthConnectHostApi.hasBodySensorsPermission();
                      final permissionStatus =
                          result ? PermissionStatus.granted : PermissionStatus.denied;
                      final permissionResult = PermissionResult(
                          permissionType: PermissionType.bodySensors,
                          permissionStatus: permissionStatus);
                      setState(() {
                        _bodySensorsPermissionResult = permissionResult;
                      });
                    },
                    child: const Text("Check Body sensors permission")),
                const SizedBox(
                  height: 30,
                ),
                Text(
                    'OAuth permission status: ${_oAuthPermissionResult?.permissionStatus.toString()}'),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final permissionResult = await _healthConnectHostApi.requestOAuthPermission();
                      setState(() {
                        _oAuthPermissionResult = permissionResult;
                      });
                    },
                    child: const Text("Request Google Fit OAuth permission")),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final result = await _healthConnectHostApi.hasOAuthPermission();
                      final permissionStatus =
                          result ? PermissionStatus.granted : PermissionStatus.denied;
                      final permissionResult = PermissionResult(
                          permissionType: PermissionType.oAuth, permissionStatus: permissionStatus);
                      setState(() {
                        _oAuthPermissionResult = permissionResult;
                      });
                    },
                    child: const Text("Check OAuth permission")),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async => await _healthConnectHostApi.disconnect(),
                    child: const Text("Disconnect from Google Fit")),
                const SizedBox(
                  height: 30,
                ),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async => await _healthConnectHostApi.openSettings(),
                    child: const Text("Open settings")),
                const Padding(
                  padding: EdgeInsets.only(top: 30),
                  child: Text("Data", style: titleStyle),
                ),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final healthConnectData = await _healthConnectHostApi.getHealthConnectData();
                      setState(() {
                        _healthConnectData = healthConnectData;
                      });
                    },
                    child: const Text("Request Health Data")),
                TextButton(
                    style: buttonStyle,
                    onPressed: () async {
                      final endDateTime = DateTime.now();
                      final startDateTime = endDateTime.subtract(const Duration(days: 30));

                      final predicate = Predicate(
                          startDateInMsSinceEpoch: startDateTime.millisecondsSinceEpoch,
                          endDateInMsSinceEpoch: endDateTime.millisecondsSinceEpoch);
                      final healthConnectWorkoutsData =
                          await _healthConnectHostApi.getHealthConnectWorkoutsData(predicate);
                      setState(() {
                        _healthConnectWorkoutsData = healthConnectWorkoutsData;
                      });
                      //final a = _healthConnectPlugin.testStream();
                    },
                    child: const Text("Request Workout Data")),
                Text("Height: ${_healthConnectData?.height.toString()}"),
                Text("Weight: ${_healthConnectData?.weight.toString()}"),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
