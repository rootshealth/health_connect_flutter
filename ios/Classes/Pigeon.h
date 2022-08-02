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

typedef NS_ENUM(NSUInteger, PermissionType) {
  PermissionTypeActivityRecognition = 0,
  PermissionTypeOAuth = 1,
};

typedef NS_ENUM(NSUInteger, WorkoutActivityType) {
  WorkoutActivityTypeAerobics = 0,
  WorkoutActivityTypeArchery = 1,
  WorkoutActivityTypeBadminton = 2,
  WorkoutActivityTypeBaseball = 3,
  WorkoutActivityTypeBasketball = 4,
  WorkoutActivityTypeBiathlon = 5,
  WorkoutActivityTypeBiking = 6,
  WorkoutActivityTypeBikingHand = 7,
  WorkoutActivityTypeBikingRoad = 8,
  WorkoutActivityTypeBikingSpinning = 9,
  WorkoutActivityTypeBikingStationary = 10,
  WorkoutActivityTypeBikingUtility = 11,
  WorkoutActivityTypeBoxing = 12,
  WorkoutActivityTypeCalisthenics = 13,
  WorkoutActivityTypeCircuitTraining = 14,
  WorkoutActivityTypeCrossfit = 15,
  WorkoutActivityTypeCurling = 16,
  WorkoutActivityTypeDancing = 17,
  WorkoutActivityTypeDiving = 18,
  WorkoutActivityTypeElevator = 19,
  WorkoutActivityTypeElliptical = 20,
  WorkoutActivityTypeErgometer = 21,
  WorkoutActivityTypeEscalator = 22,
  WorkoutActivityTypeFencing = 23,
  WorkoutActivityTypeFootballAmerican = 24,
  WorkoutActivityTypeFootballSoccer = 25,
  WorkoutActivityTypeFrisbeeDisc = 26,
  WorkoutActivityTypeGardening = 27,
  WorkoutActivityTypeGolf = 28,
  WorkoutActivityTypeGuidedBreathing = 29,
  WorkoutActivityTypeGymnastics = 30,
  WorkoutActivityTypeHandball = 31,
  WorkoutActivityTypeHighIntensityIntervalTraining = 32,
  WorkoutActivityTypeHiking = 33,
  WorkoutActivityTypeHockey = 34,
  WorkoutActivityTypeHorsebackRiding = 35,
  WorkoutActivityTypeHousework = 36,
  WorkoutActivityTypeIceSkating = 37,
  WorkoutActivityTypeIntervalTraining = 38,
  WorkoutActivityTypeInVehicle = 39,
  WorkoutActivityTypeJumpRope = 40,
  WorkoutActivityTypeKayaking = 41,
  WorkoutActivityTypeKettlebellTraining = 42,
  WorkoutActivityTypeKickboxing = 43,
  WorkoutActivityTypeKickScooter = 44,
  WorkoutActivityTypeKiteSurfing = 45,
  WorkoutActivityTypeMartialArts = 46,
  WorkoutActivityTypeMeditation = 47,
  WorkoutActivityTypeMixedMartialArts = 48,
  WorkoutActivityTypeOther = 49,
  WorkoutActivityTypeP90x = 50,
  WorkoutActivityTypeParagliding = 51,
  WorkoutActivityTypePilates = 52,
  WorkoutActivityTypePolo = 53,
  WorkoutActivityTypeRacquetball = 54,
  WorkoutActivityTypeRockClimbing = 55,
  WorkoutActivityTypeRowing = 56,
  WorkoutActivityTypeRowingMachine = 57,
  WorkoutActivityTypeRugby = 58,
  WorkoutActivityTypeRunning = 59,
  WorkoutActivityTypeRunningJogging = 60,
  WorkoutActivityTypeRunningSand = 61,
  WorkoutActivityTypeRunningTreadmill = 62,
  WorkoutActivityTypeSailing = 63,
  WorkoutActivityTypeScubaDiving = 64,
  WorkoutActivityTypeSkateboarding = 65,
  WorkoutActivityTypeSkating = 66,
  WorkoutActivityTypeSkatingCross = 67,
  WorkoutActivityTypeSkatingIndoor = 68,
  WorkoutActivityTypeSkatingInline = 69,
  WorkoutActivityTypeSkiing = 70,
  WorkoutActivityTypeSkiingBackCountry = 71,
  WorkoutActivityTypeSkiingCrossCountry = 72,
  WorkoutActivityTypeSkiingDownhill = 73,
  WorkoutActivityTypeSkiingKite = 74,
  WorkoutActivityTypeSkiingRoller = 75,
  WorkoutActivityTypeSledding = 76,
  WorkoutActivityTypeSleep = 77,
  WorkoutActivityTypeSleepAwake = 78,
  WorkoutActivityTypeSleepDeep = 79,
  WorkoutActivityTypeSleepLight = 80,
  WorkoutActivityTypeSleepRem = 81,
  WorkoutActivityTypeSnowboarding = 82,
  WorkoutActivityTypeSnowmobile = 83,
  WorkoutActivityTypeSnowshoeing = 84,
  WorkoutActivityTypeSoftball = 85,
  WorkoutActivityTypeSquash = 86,
  WorkoutActivityTypeStairClimbing = 87,
  WorkoutActivityTypeStairClimbingMachine = 88,
  WorkoutActivityTypeStandUpPaddleBoarding = 89,
  WorkoutActivityTypeStill = 90,
  WorkoutActivityTypeStrengthTraining = 91,
  WorkoutActivityTypeSurfing = 92,
  WorkoutActivityTypeSwimming = 93,
  WorkoutActivityTypeSwimmingOpenWater = 94,
  WorkoutActivityTypeSwimmingPool = 95,
  WorkoutActivityTypeTableTennis = 96,
  WorkoutActivityTypeTeamSports = 97,
  WorkoutActivityTypeTennis = 98,
  WorkoutActivityTypeTilting = 99,
  WorkoutActivityTypeTreadMeal = 100,
  WorkoutActivityTypeUnknown = 101,
  WorkoutActivityTypeVolleyball = 102,
  WorkoutActivityTypeVolleyballBeach = 103,
  WorkoutActivityTypeVolleyballIndoor = 104,
  WorkoutActivityTypeWakeBoarding = 105,
  WorkoutActivityTypeWalking = 106,
  WorkoutActivityTypeWalkingFitness = 107,
  WorkoutActivityTypeWalkingNordic = 108,
  WorkoutActivityTypeWalkingPaced = 109,
  WorkoutActivityTypeWalkingStroller = 110,
  WorkoutActivityTypeWalkingTreadmill = 111,
  WorkoutActivityTypeWaterPolo = 112,
  WorkoutActivityTypeWeightlifting = 113,
  WorkoutActivityTypeWheelchair = 114,
  WorkoutActivityTypeWindsurfing = 115,
  WorkoutActivityTypeYoga = 116,
  WorkoutActivityTypeZumba = 117,
};

@class PermissionResult;
@class HealthConnectData;
@class Predicate;
@class HealthConnectWorkoutData;

@interface PermissionResult : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithPermissionType:(PermissionType)permissionType
    permissionStatus:(PermissionStatus)permissionStatus;
@property(nonatomic, assign) PermissionType permissionType;
@property(nonatomic, assign) PermissionStatus permissionStatus;
@end

@interface HealthConnectData : NSObject
+ (instancetype)makeWithWeight:(nullable NSNumber *)weight
    height:(nullable NSNumber *)height;
@property(nonatomic, strong, nullable) NSNumber * weight;
@property(nonatomic, strong, nullable) NSNumber * height;
@end

@interface Predicate : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithStartDateInMsSinceEpoch:(NSNumber *)startDateInMsSinceEpoch
    endDateInMsSinceEpoch:(NSNumber *)endDateInMsSinceEpoch;
@property(nonatomic, strong) NSNumber * startDateInMsSinceEpoch;
@property(nonatomic, strong) NSNumber * endDateInMsSinceEpoch;
@end

@interface HealthConnectWorkoutData : NSObject
+ (instancetype)makeWithUuid:(nullable NSString *)uuid
    identifier:(nullable NSString *)identifier
    name:(nullable NSString *)name
    description:(nullable NSString *)description
    activityType:(WorkoutActivityType)activityType
    startTimestamp:(nullable NSNumber *)startTimestamp
    endTimestamp:(nullable NSNumber *)endTimestamp
    duration:(nullable NSNumber *)duration;
@property(nonatomic, copy, nullable) NSString * uuid;
@property(nonatomic, copy, nullable) NSString * identifier;
@property(nonatomic, copy, nullable) NSString * name;
@property(nonatomic, copy, nullable) NSString * description;
@property(nonatomic, assign) WorkoutActivityType activityType;
@property(nonatomic, strong, nullable) NSNumber * startTimestamp;
@property(nonatomic, strong, nullable) NSNumber * endTimestamp;
@property(nonatomic, strong, nullable) NSNumber * duration;
@end

/// The codec used by HealthConnectHostApi.
NSObject<FlutterMessageCodec> *HealthConnectHostApiGetCodec(void);

@protocol HealthConnectHostApi
- (void)requestActivityRecognitionPermissionWithCompletion:(void(^)(PermissionResult *_Nullable, FlutterError *_Nullable))completion;
/// @return `nil` only when `error != nil`.
- (nullable NSNumber *)hasActivityRecognitionPermissionWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)requestOAuthPermissionWithCompletion:(void(^)(PermissionResult *_Nullable, FlutterError *_Nullable))completion;
/// @return `nil` only when `error != nil`.
- (nullable NSNumber *)hasOAuthPermissionWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)openSettingsWithError:(FlutterError *_Nullable *_Nonnull)error;
- (void)disconnectWithCompletion:(void(^)(NSNumber *_Nullable, FlutterError *_Nullable))completion;
- (void)getHealthConnectDataWithCompletion:(void(^)(HealthConnectData *_Nullable, FlutterError *_Nullable))completion;
- (void)getHealthConnectWorkoutsDataPredicate:(Predicate *)predicate completion:(void(^)(NSArray<HealthConnectWorkoutData *> *_Nullable, FlutterError *_Nullable))completion;
- (void)subscribeToHealthConnectWorkoutsDataWithError:(FlutterError *_Nullable *_Nonnull)error;
@end

extern void HealthConnectHostApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<HealthConnectHostApi> *_Nullable api);

/// The codec used by HealthConnectFlutterApi.
NSObject<FlutterMessageCodec> *HealthConnectFlutterApiGetCodec(void);

@interface HealthConnectFlutterApi : NSObject
- (instancetype)initWithBinaryMessenger:(id<FlutterBinaryMessenger>)binaryMessenger;
- (void)onWorkoutDataUpdatedHealthConnectWorkoutData:(HealthConnectWorkoutData *)healthConnectWorkoutData completion:(void(^)(NSError *_Nullable))completion;
@end
NS_ASSUME_NONNULL_END
