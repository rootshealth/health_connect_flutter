// Autogenerated from Pigeon (v3.2.5), do not edit directly.
// See also: https://pub.dev/packages/pigeon
#import <Foundation/Foundation.h>
@protocol FlutterBinaryMessenger;
@protocol FlutterMessageCodec;
@class FlutterError;
@class FlutterStandardTypedData;

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, PermissionStatus) {
  PermissionStatusGranted = 0,
  PermissionStatusDenied = 1,
};

@class PermissionResult;
@class HealthConnectData;
@class HealthConnectWorkoutData;

@interface PermissionResult : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithPermissionStatus:(PermissionStatus)permissionStatus;
@property(nonatomic, assign) PermissionStatus permissionStatus;
@end

@interface HealthConnectData : NSObject
+ (instancetype)makeWithWeight:(nullable NSNumber *)weight
    height:(nullable NSNumber *)height;
@property(nonatomic, strong, nullable) NSNumber * weight;
@property(nonatomic, strong, nullable) NSNumber * height;
@end

@interface HealthConnectWorkoutData : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithData:(NSArray<NSString *> *)data;
@property(nonatomic, strong) NSArray<NSString *> * data;
@end

/// The codec used by HealthConnectPlugin.
NSObject<FlutterMessageCodec> *HealthConnectPluginGetCodec(void);

@protocol HealthConnectPlugin
- (void)requestActivityRecognitionPermissionWithCompletion:(void(^)(PermissionResult *_Nullable, FlutterError *_Nullable))completion;
/// @return `nil` only when `error != nil`.
- (nullable NSNumber *)hasActivityRecognitionPermissionWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)requestOAuthPermissionWithCompletion:(void(^)(PermissionResult *_Nullable, FlutterError *_Nullable))completion;
/// @return `nil` only when `error != nil`.
- (nullable NSNumber *)hasOAuthPermissionWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)openSettingsWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)disconnectWithCompletion:(void(^)(FlutterError *_Nullable))completion;
- (void)getHealthConnectDataWithCompletion:(void(^)(HealthConnectData *_Nullable, FlutterError *_Nullable))completion;
- (void)getHealthConnectWorkoutDataWithCompletion:(void(^)(HealthConnectWorkoutData *_Nullable, FlutterError *_Nullable))completion;
@end

extern void HealthConnectPluginSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<HealthConnectPlugin> *_Nullable api);

NS_ASSUME_NONNULL_END
