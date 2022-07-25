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

class HealthConnectWorkoutData {
  final List<String?> data;

  HealthConnectWorkoutData({
    required this.data,
  });
}

@HostApi()
abstract class HealthConnectPlugin {
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
  HealthConnectWorkoutData getHealthConnectWorkoutData();
}
