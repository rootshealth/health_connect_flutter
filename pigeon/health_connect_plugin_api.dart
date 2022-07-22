import 'package:pigeon/pigeon.dart';

class HealthConnectData {
  final double? weight;
  final double? height;

  HealthConnectData({
    required this.height,
    required this.weight,
  });
}

@HostApi()
abstract class HealthConnectPlugin {
  @async
  void requestPermission();

  void requestPermission2();

  void openSettings();

  @async
  HealthConnectData getHealthConnectData();

  bool hasPermission();
}
