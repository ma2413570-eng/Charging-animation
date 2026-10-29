# Moto Charge Display

A no-notification charging-screen preview for Android. It reads battery level/status and Android's `BatteryManager.computeChargeTimeRemaining()` estimate.

## Honest limitation
This APK displays a full-screen charging UI while the app is open. It does not replace the Motorola lock screen or show over it after the screen is locked. Doing that requires system-level integration / a ROM or privileged SystemUI change. Android may return no estimate on some devices, in which case the screen says the estimate is unavailable.

## Build on your phone using GitHub
1. Create a repository at https://github.com/new.
2. Upload all project files from this ZIP, preserving folders.
3. Open Actions -> Build APK -> Run workflow (or push to main).
4. Download the `MotoChargeDisplay-debug-apk` artifact from the completed run and extract `app-debug.apk`.
5. Install it on your phone.
