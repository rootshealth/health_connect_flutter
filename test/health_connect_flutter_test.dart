import 'package:flutter_test/flutter_test.dart';
import 'package:health_connect_flutter/health_connect_flutter.dart';
import 'package:health_connect_flutter/health_connect_flutter_platform_interface.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHealthConnectFlutterPlatform 
    with MockPlatformInterfaceMixin
    implements HealthConnectFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HealthConnectFlutterPlatform initialPlatform = HealthConnectFlutterPlatform.instance;

  test('$MethodChannelHealthConnectFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHealthConnectFlutter>());
  });

  test('getPlatformVersion', () async {
    HealthConnectFlutter healthConnectFlutterPlugin = HealthConnectFlutter();
    MockHealthConnectFlutterPlatform fakePlatform = MockHealthConnectFlutterPlatform();
    HealthConnectFlutterPlatform.instance = fakePlatform;
  
    expect(await healthConnectFlutterPlugin.getPlatformVersion(), '42');
  });
}
