./gradlew assembleDebug
ls -l
java -jar crawl_launcher.jar --apk-file ../app/build/outputs/apk/debug/app-debug.apk --android-sdk $ANDROID_HOME