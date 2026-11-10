# Player Hub — Feature Roadmap

This roadmap is a plan, not a claim that these features already work. Features are grouped so each release can be built and tested before the next one.

## Library and file management
1. Remove one item from the library without deleting the original device file.
2. Clear the library with confirmation.
3. Search by filename and extension.
4. Sort by name, file type, size and recently added.
5. Show file type, MIME type, size and source location.
6. Detect missing or revoked file access and offer re-linking.
7. Show thumbnails and format-specific icons.
8. Multi-select files for batch removal and categorization.
9. Create collections, favourites and custom tags.
10. Import/export a library backup.

## Built-in file tools
11. Internal HTML viewer with safe browsing controls.
12. Text, JSON, XML, Markdown and CSV reader with syntax-aware formatting.
13. Image viewer with zoom, rotate and slideshow.
14. PDF reader with page navigation and search.
15. Archive browser for ZIP and supported formats.
16. Audio player with playlists and background playback.
17. Video player with subtitles and playback controls.
18. Metadata inspector and file integrity/hash checker.
19. Download manager with pause/resume and checksums.
20. Local file transfer and sharing tools.

## Gaming and controls
21. Game profiles with per-game settings.
22. Custom touch-control layouts.
23. Bluetooth/USB controller detection and mapping.
24. Save controller layouts and per-game presets.
25. Performance overlay for FPS, frame time and memory where Android permits.
26. Game library metadata, artwork and descriptions.
27. Runtime/plugin registry that reports exactly which engine supports each format.
28. Virtual Android runtime research for isolated APK execution (separate security and compatibility project).
29. PC game compatibility runtime research (Wine/translation layers, device and GPU dependent).
30. Per-game launch logs, crash reports and compatibility notes.

## Quality, privacy and accessibility
- Automated build, unit tests, lint and dependency checks on every change.
- Clear permission explanations and least-privilege file access.
- No silent external-app hand-offs; unsupported formats must say why they cannot run.
- Accessibility labels, scalable text, keyboard/controller navigation and localization.
- Never label a planned or partially supported format as runnable until tested on real devices.

## Delivery order
**Milestone A:** library management, robust file metadata/icons, delete/clear, search/sort, error recovery.
**Milestone B:** internal image/PDF/archive/audio/video tools.
**Milestone C:** controller profiles, performance diagnostics and game metadata.
**Milestone D:** investigate virtual Android and PC-game runtimes as separate prototypes, with explicit compatibility and security tests.
