# Player Hub (Android)

Android game/file library prototype with its own internal tool registry.

## Included in this milestone
- Startup loading screen and saved file library
- File cards showing extension, size and assigned internal tool
- Built-in WebView for HTML and built-in text reader for text-like formats
- Separate Run and Install actions for APK files; Android package installation still requires Android's system installer and user approval
- Player Hub launcher icon and Library / Controllers / Settings navigation

## Runtime limitations
A normal Android app cannot execute an arbitrary APK as an in-app process without installing it. That requires a virtual Android/container runtime, which is not included.
HTML and text formats can open inside Player Hub. PDF/image viewing, archive extraction, APK virtual execution, ISO mounting and Windows game execution are not implemented yet. Importing a file does not make it executable.
The system file picker grants access only to files selected by the user; no broad storage permission is requested.

## Build
Run **Build Android APK** in GitHub Actions and download the debug APK artifact after a successful build.


## Development roadmap
See [docs/ROADMAP.md](docs/ROADMAP.md) for the planned 30-feature roadmap. Planned features are clearly distinguished from implemented functionality.
