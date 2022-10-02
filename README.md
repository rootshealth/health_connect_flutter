# Health Connect Flutter

## About
alpha-stage health_connect plugin for flutter

# Setup
Uses pigeon

To update the generated code, run

```
flutter pub get;

flutter pub run pigeon --input pigeon/health_connect_plugin_api.dart \
--dart_out lib/health_connect_plugin.dart \
--objc_header_out ios/Classes/Pigeon.h \
--objc_source_out ios/Classes/Pigeon.m \
--java_package "com.helloinside.health_connect_flutter" \
--java_out android/src/main/java/com/helloinside/health_connect_flutter/Pigeon.java

or

/Users/dominik.koscica/fvm/versions/3.3.3/bin/flutter pub run pigeon --input pigeon/health_connect_plugin_api.dart \
--dart_out lib/health_connect_plugin.dart \
--objc_header_out ios/Classes/Pigeon.h \
--objc_source_out ios/Classes/Pigeon.m \
--java_package "com.helloinside.health_connect_flutter" \
--java_out android/src/main/java/com/helloinside/health_connect_flutter/Pigeon.java
```