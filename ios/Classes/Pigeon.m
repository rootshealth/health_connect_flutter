// Autogenerated from Pigeon (v3.2.5), do not edit directly.
// See also: https://pub.dev/packages/pigeon
#import "Pigeon.h"
#import <Flutter/Flutter.h>

#if !__has_feature(objc_arc)
#error File requires ARC to be enabled.
#endif

static NSDictionary<NSString *, id> *wrapResult(id result, FlutterError *error) {
  NSDictionary *errorDict = (NSDictionary *)[NSNull null];
  if (error) {
    errorDict = @{
        @"code": (error.code ?: [NSNull null]),
        @"message": (error.message ?: [NSNull null]),
        @"details": (error.details ?: [NSNull null]),
        };
  }
  return @{
      @"result": (result ?: [NSNull null]),
      @"error": errorDict,
      };
}
static id GetNullableObject(NSDictionary* dict, id key) {
  id result = dict[key];
  return (result == [NSNull null]) ? nil : result;
}
static id GetNullableObjectAtIndex(NSArray* array, NSInteger key) {
  id result = array[key];
  return (result == [NSNull null]) ? nil : result;
}


@interface PermissionResult ()
+ (PermissionResult *)fromMap:(NSDictionary *)dict;
+ (nullable PermissionResult *)nullableFromMap:(NSDictionary *)dict;
- (NSDictionary *)toMap;
@end
@interface HealthConnectData ()
+ (HealthConnectData *)fromMap:(NSDictionary *)dict;
+ (nullable HealthConnectData *)nullableFromMap:(NSDictionary *)dict;
- (NSDictionary *)toMap;
@end
@interface HealthConnectWorkoutData ()
+ (HealthConnectWorkoutData *)fromMap:(NSDictionary *)dict;
+ (nullable HealthConnectWorkoutData *)nullableFromMap:(NSDictionary *)dict;
- (NSDictionary *)toMap;
@end

@implementation PermissionResult
+ (instancetype)makeWithPermissionStatus:(PermissionStatus)permissionStatus {
  PermissionResult* pigeonResult = [[PermissionResult alloc] init];
  pigeonResult.permissionStatus = permissionStatus;
  return pigeonResult;
}
+ (PermissionResult *)fromMap:(NSDictionary *)dict {
  PermissionResult *pigeonResult = [[PermissionResult alloc] init];
  pigeonResult.permissionStatus = [GetNullableObject(dict, @"permissionStatus") integerValue];
  return pigeonResult;
}
+ (nullable PermissionResult *)nullableFromMap:(NSDictionary *)dict { return (dict) ? [PermissionResult fromMap:dict] : nil; }
- (NSDictionary *)toMap {
  return @{
    @"permissionStatus" : @(self.permissionStatus),
  };
}
@end

@implementation HealthConnectData
+ (instancetype)makeWithWeight:(nullable NSNumber *)weight
    height:(nullable NSNumber *)height {
  HealthConnectData* pigeonResult = [[HealthConnectData alloc] init];
  pigeonResult.weight = weight;
  pigeonResult.height = height;
  return pigeonResult;
}
+ (HealthConnectData *)fromMap:(NSDictionary *)dict {
  HealthConnectData *pigeonResult = [[HealthConnectData alloc] init];
  pigeonResult.weight = GetNullableObject(dict, @"weight");
  pigeonResult.height = GetNullableObject(dict, @"height");
  return pigeonResult;
}
+ (nullable HealthConnectData *)nullableFromMap:(NSDictionary *)dict { return (dict) ? [HealthConnectData fromMap:dict] : nil; }
- (NSDictionary *)toMap {
  return @{
    @"weight" : (self.weight ?: [NSNull null]),
    @"height" : (self.height ?: [NSNull null]),
  };
}
@end

@implementation HealthConnectWorkoutData
+ (instancetype)makeWithData:(NSArray<NSString *> *)data {
  HealthConnectWorkoutData* pigeonResult = [[HealthConnectWorkoutData alloc] init];
  pigeonResult.data = data;
  return pigeonResult;
}
+ (HealthConnectWorkoutData *)fromMap:(NSDictionary *)dict {
  HealthConnectWorkoutData *pigeonResult = [[HealthConnectWorkoutData alloc] init];
  pigeonResult.data = GetNullableObject(dict, @"data");
  NSAssert(pigeonResult.data != nil, @"");
  return pigeonResult;
}
+ (nullable HealthConnectWorkoutData *)nullableFromMap:(NSDictionary *)dict { return (dict) ? [HealthConnectWorkoutData fromMap:dict] : nil; }
- (NSDictionary *)toMap {
  return @{
    @"data" : (self.data ?: [NSNull null]),
  };
}
@end

@interface HealthConnectPluginCodecReader : FlutterStandardReader
@end
@implementation HealthConnectPluginCodecReader
- (nullable id)readValueOfType:(UInt8)type 
{
  switch (type) {
    case 128:     
      return [HealthConnectData fromMap:[self readValue]];
    
    case 129:     
      return [HealthConnectWorkoutData fromMap:[self readValue]];
    
    case 130:     
      return [PermissionResult fromMap:[self readValue]];
    
    default:    
      return [super readValueOfType:type];
    
  }
}
@end

@interface HealthConnectPluginCodecWriter : FlutterStandardWriter
@end
@implementation HealthConnectPluginCodecWriter
- (void)writeValue:(id)value 
{
  if ([value isKindOfClass:[HealthConnectData class]]) {
    [self writeByte:128];
    [self writeValue:[value toMap]];
  } else 
  if ([value isKindOfClass:[HealthConnectWorkoutData class]]) {
    [self writeByte:129];
    [self writeValue:[value toMap]];
  } else 
  if ([value isKindOfClass:[PermissionResult class]]) {
    [self writeByte:130];
    [self writeValue:[value toMap]];
  } else 
{
    [super writeValue:value];
  }
}
@end

@interface HealthConnectPluginCodecReaderWriter : FlutterStandardReaderWriter
@end
@implementation HealthConnectPluginCodecReaderWriter
- (FlutterStandardWriter *)writerWithData:(NSMutableData *)data {
  return [[HealthConnectPluginCodecWriter alloc] initWithData:data];
}
- (FlutterStandardReader *)readerWithData:(NSData *)data {
  return [[HealthConnectPluginCodecReader alloc] initWithData:data];
}
@end

NSObject<FlutterMessageCodec> *HealthConnectPluginGetCodec() {
  static dispatch_once_t sPred = 0;
  static FlutterStandardMessageCodec *sSharedObject = nil;
  dispatch_once(&sPred, ^{
    HealthConnectPluginCodecReaderWriter *readerWriter = [[HealthConnectPluginCodecReaderWriter alloc] init];
    sSharedObject = [FlutterStandardMessageCodec codecWithReaderWriter:readerWriter];
  });
  return sSharedObject;
}


void HealthConnectPluginSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<HealthConnectPlugin> *api) {
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.requestActivityRecognitionPermission"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(requestActivityRecognitionPermissionWithCompletion:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(requestActivityRecognitionPermissionWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api requestActivityRecognitionPermissionWithCompletion:^(PermissionResult *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.hasActivityRecognitionPermission"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(hasActivityRecognitionPermissionWithError:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(hasActivityRecognitionPermissionWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        NSNumber *output = [api hasActivityRecognitionPermissionWithError:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.requestOAuthPermission"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(requestOAuthPermissionWithCompletion:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(requestOAuthPermissionWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api requestOAuthPermissionWithCompletion:^(PermissionResult *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.hasOAuthPermission"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(hasOAuthPermissionWithError:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(hasOAuthPermissionWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        NSNumber *output = [api hasOAuthPermissionWithError:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.openSettings"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(openSettingsWithError:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(openSettingsWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        [api openSettingsWithError:&error];
        callback(wrapResult(nil, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.disconnect"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(disconnectWithCompletion:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(disconnectWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api disconnectWithCompletion:^(FlutterError *_Nullable error) {
          callback(wrapResult(nil, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.getHealthConnectData"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getHealthConnectDataWithCompletion:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(getHealthConnectDataWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getHealthConnectDataWithCompletion:^(HealthConnectData *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.HealthConnectPlugin.getHealthConnectWorkoutData"
        binaryMessenger:binaryMessenger
        codec:HealthConnectPluginGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getHealthConnectWorkoutDataWithCompletion:)], @"HealthConnectPlugin api (%@) doesn't respond to @selector(getHealthConnectWorkoutDataWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api getHealthConnectWorkoutDataWithCompletion:^(HealthConnectWorkoutData *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
}
