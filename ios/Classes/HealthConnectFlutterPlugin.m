#import "HealthConnectFlutterPlugin.h"
#if __has_include(<health_connect_flutter/health_connect_flutter-Swift.h>)
#import <health_connect_flutter/health_connect_flutter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "health_connect_flutter-Swift.h"
#endif

@implementation HealthConnectFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftHealthConnectFlutterPlugin registerWithRegistrar:registrar];
}
@end
