import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';

void main() {
  MethodChannelHealthConnectFlutter platform = MethodChannelHealthConnectFlutter();
  const MethodChannel channel = MethodChannel('health_connect_flutter');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
