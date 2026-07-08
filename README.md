# tickle

tickle is a local-first Android bookkeeping app for fast manual logging.

The product direction is deliberately small: a 3-second ledger for quick daily
expense and income records, with home-screen widgets as a first-class entry
point and the app screens for editing, history, statistics, backup, restore,
and onboarding.

This repository is the first public source snapshot. Screenshots and the final
visual tutorial will be added after the current artwork pass is finished.

## Product Principles

- Manual entry and widget entry are first-class.
- No screenshot/OCR or fragile background capture in the current product path.
- Data stays local by default in Room.
- Export and restore use plain CSV files under `Download/tickle`.
- The visual language is quiet, black/white, minimal, and touch-friendly.

## Current Features

- Compose app shell with a compact onboarding guide.
- Manual bookkeeping inside the app with date selection for backfilling.
- Expense and income records with category selection and custom categories.
- Quick category management with fixed home categories and an expanded "other"
  category picker.
- Ledger grouped by month/date, with expense/income filtering, edit, and delete.
- Statistics by day, month, and year, with explicit expense/income mode instead
  of net-flow reporting.
- Minimal date/time picker sheets for quick entry, ledger editing, and period
  navigation.
- Lightweight motion and haptic feedback for app buttons, category switching,
  amount changes, save success, and zero-amount warnings.
- CSV export and restore from the latest exported backup.
- Home-screen widgets in six variants:
  - 4x2 dark
  - 4x2 light
  - 4x3 dark
  - 4x3 light
  - 4x4 dark
  - 4x4 light
- Widget direct entry, undo, expense/income switching, and midnight/day-change
  refresh.

## Android Modules

The project is intentionally one Android app module for now, with clear package
boundaries inside `app/src/main/java/com/lightledger/app`.

- `data` - Room database, DAO, repository, CSV import/export.
- `data/model` - database entities and enums.
- `domain` - pure formatting, parsing, and category helper logic.
- `ui` - Compose screens, onboarding, dialogs, motion helpers, and ViewModel.
- `widget` - Android AppWidget providers, RemoteViews updates, and day-change
  refresh receiver.

More detail is in `docs/PROJECT_STRUCTURE.md`.

## Privacy

tickle does not use an account system, remote backend, analytics SDK, network
API, screenshot capture, OCR, or notification reading in the current product
path. Bookkeeping data is stored locally in Room. CSV export and restore are
user-triggered and use files under `Download/tickle`.

## Export Format

Exports are written to:

```text
Download/tickle
```

Each export creates a transaction CSV with date, direction, amount, unit, type,
and note fields. The column contract is documented in `docs/DATA_EXPORT.md`.

## Local Development

Open this path in Android Studio:

```text
E:\AppDev\LightLedger
```

This machine currently uses:

```powershell
$env:JAVA_HOME='D:\Andriod Studio\jbr'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

Build the normal app:

```powershell
.\gradlew.bat :app:assembleMainlineDebug
```

Build the friend-test app without replacing the mainline install:

```powershell
.\gradlew.bat :app:assembleFriendsDebug
```

Run unit tests:

```powershell
.\gradlew.bat :app:testMainlineDebugUnitTest
.\gradlew.bat :app:testFriendsDebugUnitTest
```

Latest local debug APK copies are written to:

```text
dist\tickle-mainline-debug.apk
dist\tickle-friends-debug.apk
```

## Release Notes

Current debug flavors:

- `mainline`: `com.lightledger.app`, app name `tickle`
- `friends`: `com.lightledger.app.friends`, app name `tickle beta`

Before public store release, create a signed release build, bump
`versionCode`/`versionName`, prepare store screenshots and privacy copy, and
repeat the widget/date/statistics/manual-entry regression checklist in
`docs/RELEASE_CHECKLIST.md`.

## License

Apache License 2.0.

## Local-Only Folders

- `dist/` - locally copied APKs for testing.
- `artifacts/` - screenshots, UI dumps, test exports, old OCR/screenshot lab
  materials, and other review evidence.

Both folders are ignored by Git. Keep release artifacts and private samples out
of the public repository unless they are intentionally sanitized.
