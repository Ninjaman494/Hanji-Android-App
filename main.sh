./gradlew assembleDebug
wget https://dl.google.com/appcrawler/beta1/app-crawler.zip
unzip app-crawler.zip
cd app-crawler
java -jar crawl_launcher.jar --apk-file ../app/build/outputs/apk/debug/app-debug.apk --android-sdk $ANDROID_HOME
# java -jar crawl_launcher.jar --app-package-name com.a494studios.koreanconjugator --android-sdk C:/Users/akash/AppData/Local/Android/Sdk                                                         