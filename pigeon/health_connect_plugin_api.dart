import 'package:pigeon/pigeon.dart';

/// code used to generate the pigeon api
/// see README for details
class HealthConnectInitializationParams {
  final String apiKey;

  HealthConnectInitializationParams({
    required this.apiKey,
  });
}

class HealthConnectData {
  final String id;
  final String? data;

  HealthConnectData({
    required this.id,
    this.data,
  });
}

@HostApi()
abstract class HealthConnectPlugin {
  /// Initializes the HelpCrunch plugin.
  /// This method must be called before any other method.
  @async
  void initialize(HealthConnectInitializationParams params);

  @async
  void requestPermission();

  @async
  HealthConnectData getHealthConnectData();

  bool hasPermission();
}
