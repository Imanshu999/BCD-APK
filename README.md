# Player Hub (Android)

Player Hub is an Android-first game library and file-launcher prototype.

## Included
- Player Hub branding with a dark glass-style UI
- Library, Controllers and Settings bottom navigation
- Android system document picker for selecting files without blanket storage access
- Imported-file list and APK hand-off to Android's package installer
- Open other selected files with a compatible installed app
- GitHub profile and repository links in Settings

## Compatibility limits
- APK installation is handed to Android's package installer and requires user confirmation. Android may also ask the user to allow installs from this source.
- ISO, ZIP, OBB, XAPK, APKS and PC game files may be selected/imported, but importing does not make them executable.
- Windows PC games such as GTA need a compatible runtime/emulator, graphics translation, game-specific setup and device testing. This repository does not yet contain that runtime.
- Player Hub does not get unrestricted access to all storage. Android's system picker grants access only to files the user selects.
- Controller mapping, touch controls and per-game optimization are planned, not yet implemented.

## Build
Push to `main` or run **Build Android APK** from the Actions tab. Download the debug APK artifact after a successful workflow run.
