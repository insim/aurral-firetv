# Aurral TV

A native Android TV app that turns a **Fire TV Stick 4K Max** (Fire OS / Android — not Vega) into a Subsonic player for a self-hosted [Aurral](https://aurral.org) server.

The app is a Subsonic client. **Scrobbling is handled entirely by Aurral**: the app only reports playback through `scrobble.view`, and Aurral records the play and forwards it to your connected Last.fm / ListenBrainz / Koito accounts. The app never talks to those services.

## Features

- **Browse** artists, albums and genres, with Home rails (recently added, recently played, random, starred).
- **Play** with Media3/ExoPlayer: queue, next/previous, seek, shuffle, repeat, background playback and a now-playing notification.
- **Now playing** screen with large artwork, transport controls and a persistent mini player.
- **Search** across artists, albums and tracks.
- **Playlists and flows** — play any playlist, add an album to a playlist, and edit static playlists (rename, delete, remove tracks); Aurral flows are read-only and report error 50.
- **Favorites** — star/unstar songs and browse everything you have starred.
- **Lyrics** via the OpenSubsonic `songLyrics` extension, with timed highlighting.
- **Resume** the saved play queue across restarts (`getPlayQueue` / `savePlayQueue`).
- **Scrobbles** now-playing and completed plays, with the Subsonic 50% / 4-minute rule.

## Requirements

- Fire TV Stick 4K Max (Fire OS 8), or any Android TV / Fire OS device on API 25+.
- A reachable Aurral instance with the Subsonic API enabled at `/rest` (default port `3001`).
- A local Aurral account. For token authentication, the account needs a password stored by Aurral — see the notes below.

## Server setup

1. In Aurral, open the user menu and choose **Connect an app**, create a device/app password, and note it. The Subsonic endpoint is `http://<host>:3001/rest`.
2. Enter the server address, your username and the password in the app.
3. If the app reports **error 41 (Token authentication failed)**, either:
   - sign in to the Aurral web app once so a local password is stored, or
   - turn off **Use token authentication** on the login screen so the app sends the password directly.

## Build and release

Everything is built in GitHub Actions; no local Android SDK is required.

**Every push / PR to `main`** runs unit tests, lint, and produces a debug APK as a workflow artifact.

**Pushing a tag** builds a signed release APK and attaches it to a GitHub Release:

```bash
git tag v0.1.0
git push origin v0.1.0
```

### Signing secrets

Generate a keystore once and add four repository secrets (**Settings → Secrets and variables → Actions**):

```bash
keytool -genkeypair -v -keystore keystore.jks -alias aurraltv \
  -keyalg RSA -keysize 2048 -validity 10000

# key for KEYSTORE_BASE64
base64 -i keystore.jks | pbcopy        # macOS
# base64 -w0 keystore.jks              # Linux
```

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | base64 of `keystore.jks` |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | `aurraltv` |
| `KEY_PASSWORD` | key password |

`keystore.jks` is gitignored and never committed.

## Install on Fire TV

Enable developer options and ADB debugging on the Fire TV: **Settings → My Fire TV → About →** click **Build** seven times, then **Developer Options → ADB debugging → On**. Note the device IP under **Settings → My Fire TV → About → Network**.

```bash
adb connect <fire-tv-ip>:5555
adb install -r aurral-tv-v0.1.0.apk
```

Alternatively download the APK from the GitHub Release on the device with the **Downloader** app and install it.

The app appears on the Fire TV home row with its banner.

## Project layout

```
app/src/main/java/org/aurral/firetv/
  data/
    api/          Subsonic Retrofit interface, auth/URL building, response envelope, interceptors
    api/dto/      One file per wire model
    mapper/       DTO -> domain mappers
    model/        Domain models
    repository/   Auth, Library, Search, Favorites, Playlist, Lyrics, PlayQueue, Scrobble
    session/      SessionManager, credential stores, settings
  playback/       Media3 service, controller, scrobble logic, MediaItem mapping
  ui/             Compose screens, each with its own ViewModel
  di/             Hilt modules, qualifiers, entry points
```

### Technical notes

- **UI toolkit:** Jetpack Compose (Material 3). Since Compose Foundation 1.7, the standard `LazyRow` / `LazyColumn` have built-in TV focus positioning — now the recommended approach for TV catalogs — so the app uses those with custom focus styling for cards, rows and buttons.
- **Playback:** `MediaSessionService` + ExoPlayer, exposing now-playing metadata to the Fire TV UI.
- **Auth:** token (`md5(password + salt)`) by default, legacy password as a fallback. Credentials live in DataStore plus `EncryptedSharedPreferences`.
- **Cleartext HTTP** is permitted (with user trust anchors) so a LAN-only Aurral over `http://` or a self-signed certificate works out of the box.
- No Google Play Services dependencies anywhere, since Fire OS does not ship them.

## Performance and caching

Built for a low-power stick and a bounded amount of storage:

- **Stable session salt.** The Subsonic token salt is generated once per session (not per request), so signed cover-art and stream URLs are deterministic. That is what makes the caches below effective; the salt rotates on sign-in and sign-out.
- **Cover art.** A dedicated Coil `ImageLoader` uses `respectCacheHeaders(false)` (Aurral sends no cache headers) and caches by cover id rather than by URL, so artwork survives salt rotation and restarts. Memory is capped at 15% of available RAM.
- **Audio.** ExoPlayer streams through a media3 `SimpleCache` with an LRU evictor, keyed by song + transcoding rather than URL — replays, seeking back and the previous track are served from disk. A 60 s back buffer keeps "previous" instant.
- **Storage-aware budgets.** Both caches are sized from the device's free space, floored to stay useful and capped (audio 128–384 MB, cover art 32–128 MB) so the app never claims a large fixed slice of a Fire TV's storage. Settings shows the current sizes and can clear them.
- **Large libraries.** The Albums screen pages 100 at a time, loading more as you approach the end of the grid, instead of pulling the entire library at once.
- **Playback state.** The queue list is only rebuilt when the timeline actually changes, not on every player event and 500 ms tick — a 500-track queue is no longer reallocated twice a second.
- **Playback tuning.** ExoPlayer holds a network wake lock (Wi-Fi stays awake during audio-only playback), buffers 20–120 s ahead, and prioritises time over size.
- **Credentials** (Keystore + encrypted prefs) are read and written on `Dispatchers.IO`, keeping first launch and sign-in off the main thread.

## Tests

```bash
./gradlew testDebugUnitTest
```

Covers the Subsonic auth token vector and salt stability, response parsing and error handling, scrobble thresholds, audio cache keys, and duration/byte formatting.
