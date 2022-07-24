import 'package:pigeon/pigeon.dart';

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
  void requestPermission();

  bool hasPermission();

  void openSettings();

  @async
  void disconnect();

  @async
  HealthConnectData getHealthConnectData();

  @async
  HealthConnectWorkoutData getHealthConnectWorkoutData();
}
