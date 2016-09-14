adb uninstall study.wallet
gradle build
adb install build/outputs/apk/Wallet-release.apk
adb shell am start -n study.wallet/.Wallet
