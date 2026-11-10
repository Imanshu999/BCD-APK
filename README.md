# BCD Android

An Android-first launcher prototype inspired by the BCD Games project. This repository is an independent Android implementation; the original iOS runtime cannot be copied directly to Android.

## Current milestone
- Android launcher shell with a glass-style dark UI
- Game library placeholder and import entry point
- GitHub Actions workflow to build a debug APK

## Build
Push to `main` or run **Build Android APK** from the Actions tab. Download the `bcd-android-debug-apk` artifact from the workflow run.

## Important
This first milestone is a launcher prototype, not a Windows-game compatibility runtime. Running Windows PC games requires substantial Android-native integration (for example Wine/Box64 and device-specific graphics support), testing, and license review. No proprietary binaries or API keys are included.
