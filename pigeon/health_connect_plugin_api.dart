import 'package:pigeon/pigeon.dart';

enum PermissionStatus { granted, denied }

enum PermissionType { activityRecognition, bodySensors, oAuth }

enum WorkoutActivityType {
  aerobics("aerobics"),
  archery("archery"),
  badminton("badminton"),
  baseball("baseball"),
  basketball("basketball"),
  biathlon("biathlon"),
  biking("biking"),
  bikingHand("biking.hand"),
  bikingRoad("biking.road"),
  bikingSpinning("biking.spinning"),
  bikingStationary("biking.stationary"),
  bikingUtility("biking.utility"),
  boxing("boxing"),
  calisthenics("calisthenics"),
  circuitTraining("circuit_training"),
  crossfit("crossfit"),
  curling("curling"),
  dancing("dancing"),
  diving("diving"),
  elevator("elevator"),
  elliptical("elliptical"),
  ergometer("ergometer"),
  escalator("escalator"),
  fencing("fencing"),
  footballAmerican("football.american"),
  footballSoccer("football.soccer"),
  frisbeeDisc("frisbee_disc"),
  gardening("gardening"),
  golf("golf"),
  guidedBreathing("guided_breathing"),
  gymnastics("gymnastics"),
  handball("handball"),
  highIntensityIntervalTraining("interval_training.high_intensity"),
  hiking("hiking"),
  hockey("hockey"),
  horsebackRiding("horseback_riding"),
  housework("housework"),
  iceSkating("ice_skating"),
  intervalTraining("interval_training"),
  inVehicle("in_vehicle"),
  jumpRope("jump_rope"),
  kayaking("kayaking"),
  kettlebellTraining("kettlebell_training"),
  kickboxing("kickboxing"),
  kickScooter("kick_scooter"),
  kiteSurfing("kitesurfing"),
  martialArts("martial_arts"),
  meditation("meditation"),
  mixedMartialArts("martial_arts.mixed"),
  other("other"),
  p90x("p90x"),
  paragliding("paragliding"),
  pilates("pilates"),
  polo("polo"),
  racquetball("racquetball"),
  rockClimbing("rock_climbing"),
  rowing("rowing"),
  rowingMachine("rowing.machine"),
  rugby("rugby"),
  running("running"),
  runningJogging("running.jogging"),
  runningSand("running.sand"),
  runningTreadmill("running.treadmill"),
  sailing("sailing"),
  scubaDiving("scuba_diving"),
  skateboarding("skateboarding"),
  skating("skating"),
  skatingCross("skating.cross"),
  skatingIndoor("skating.indoor"),
  skatingInline("skating.inline"),
  skiing("skiing"),
  skiingBackCountry("skiing.back_country"),
  skiingCrossCountry("skiing.cross_country"),
  skiingDownhill("skiing.downhill"),
  skiingKite("skiing.kite"),
  skiingRoller("skiing.roller"),
  sledding("sledding"),
  sleep("sleep"),
  sleepAwake("sleep.awake"),
  sleepDeep("sleep.deep"),
  sleepLight("sleep.light"),
  sleepRem("sleep.rem"),
  snowboarding("snowboarding"),
  snowmobile("snowmobile"),
  snowshoeing("snowshoeing"),
  softball("softball"),
  squash("squash"),
  stairClimbing("stair_climbing"),
  stairClimbingMachine("stair_climbing.machine"),
  standUpPaddleBoarding("standup_paddleboarding"),
  still("still"),
  strengthTraining("strength_training"),
  surfing("surfing"),
  swimming("swimming"),
  swimmingOpenWater("swimming.open_water"),
  swimmingPool("swimming.pool"),
  tableTennis("table_tennis"),
  teamSports("team_sports"),
  tennis("tennis"),
  tilting("tilting"),
  treadMeal("treadmeal"),
  unknown("unknown"),
  volleyball("volleyball"),
  volleyballBeach("volleyball.beach"),
  volleyballIndoor("volleyball.indoor"),
  wakeBoarding("wakeboarding"),
  walking("walking"),
  walkingFitness("walking.fitness"),
  walkingNordic("walking.nordic"),
  walkingPaced("walking.paced"),
  walkingStroller("walking.stroller"),
  walkingTreadmill("walking.treadmill"),
  waterPolo("water_polo"),
  weightlifting("weightlifting"),
  wheelchair("wheelchair"),
  windsurfing("windsurfing"),
  yoga("yoga"),
  zumba("zumba");

  const WorkoutActivityType(this.value);

  final String value;
}

class PermissionResult {
  final PermissionType permissionType;

  final PermissionStatus permissionStatus;

  const PermissionResult(this.permissionType, this.permissionStatus);
}

class HealthConnectData {
  final double? weight;
  final double? height;

  const HealthConnectData({
    required this.height,
    required this.weight,
  });
}

/// A time interval predicate
/// [startDateInMsSinceEpoch] - the starting point of the time interval
/// [endDateInMsSinceEpoch] - the end point of the time interval
///
class Predicate {
  final int startDateInMsSinceEpoch;
  final int endDateInMsSinceEpoch;

  const Predicate({
    required this.startDateInMsSinceEpoch,
    required this.endDateInMsSinceEpoch,
  });
}

/// startTimestamp, endTimestamp end duration are represented in seconds
class HealthConnectWorkoutData {
  final String? identifier;
  final String? name;
  final String? description;
  final WorkoutActivityType? activityType;
  final int? startTimestamp;
  final int? endTimestamp;
  final int? duration;

  const HealthConnectWorkoutData({
    required this.identifier,
    required this.name,
    required this.description,
    required this.activityType,
    required this.startTimestamp,
    required this.endTimestamp,
    required this.duration,
  });
}

/// flutter call native
@HostApi()
abstract class HealthConnectHostApi {
  @async
  PermissionResult requestPermissions();

  bool hasActivityRecognitionPermission();

  bool hasBodySensorsPermission();

  @async
  PermissionResult requestOAuthPermission();

  bool hasOAuthPermission();

  void openSettings();

  @async
  bool disconnect();

  @async
  HealthConnectData getHealthConnectData();

  @async
  List<HealthConnectWorkoutData> getHealthConnectWorkoutsData(Predicate predicate);

  void subscribeToHealthConnectWorkoutsData();
}

/// native call flutter
@FlutterApi()
abstract class HealthConnectFlutterApi {
  void onWorkoutDataUpdated(HealthConnectWorkoutData healthConnectWorkoutData);
}
