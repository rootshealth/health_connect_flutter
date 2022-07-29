import 'package:pigeon/pigeon.dart';

enum PermissionStatus { granted, denied }

enum PermissionType { activityRecognition, oAuth }

class PermissionResult {
  final PermissionType permissionType;

  final PermissionStatus permissionStatus;

  PermissionResult(this.permissionType, this.permissionStatus);
}

class HealthConnectData {
  final double? weight;
  final double? height;

  HealthConnectData({
    required this.height,
    required this.weight,
  });
}

/// startTimestamp, endTimestamp end duration are represented in seconds
class HealthConnectWorkoutData {
  final String? uuid;
  final String? identifier;
  final String? name;
  final String? description;
  final String? activity;
  final int? startTimestamp;
  final int? endTimestamp;
  final int? duration;

  HealthConnectWorkoutData({
    required this.uuid,
    required this.identifier,
    required this.name,
    required this.description,
    required this.activity,
    required this.startTimestamp,
    required this.endTimestamp,
    required this.duration,
  });
}

/// flutter call native
@HostApi()
abstract class HealthConnectHostApi {
  @async
  PermissionResult requestActivityRecognitionPermission();

  bool hasActivityRecognitionPermission();

  @async
  PermissionResult requestOAuthPermission();

  bool hasOAuthPermission();

  void openSettings();

  @async
  void disconnect();

  @async
  HealthConnectData getHealthConnectData();

  @async
  List<HealthConnectWorkoutData> getHealthConnectWorkoutsData();

  void subscribeToHealthConnectWorkoutsData();
}

/// native call flutter
@FlutterApi()
abstract class HealthConnectFlutterApi {
  void onWorkoutDataUpdated(HealthConnectWorkoutData healthConnectWorkoutData);
}
