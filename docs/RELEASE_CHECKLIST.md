# Release checklist

This checklist is for small friend testing first, then GitHub and app-store
preparation later.

## Before every shared APK

- Build the intended flavor:

```powershell
.\gradlew.bat :app:assembleFriendsDebug
```

- Install on a clean or secondary device when possible.
- Open the app once and confirm the visual guide appears cleanly.
- Add each widget size/style that is included in the build:
  - 4x2 dark
  - 4x2 light
  - 4x3 dark
  - 4x3 light
  - 4x4 dark
  - 4x4 light
- From a widget, add one expense and one income.
- Reopen the app and confirm ledger and statistics update.
- Edit a ledger entry:
  - amount
  - date
  - time
  - expense/income type
  - category
  - note
- Export data and confirm files appear under `Download/tickle`.
- Restore from the latest export on a test install.

## Before GitHub

- Confirm `artifacts/` is ignored and does not contain tracked private data.
- Confirm `dist/` is ignored and APKs are not committed.
- Remove or redact personal screenshots before turning any artifact into docs.
- Keep `README.md`, `docs/PROJECT_STRUCTURE.md`, and `docs/DATA_EXPORT.md`
  current with the actual product behavior.
- Decide whether Illustrator `.ai` files should be committed, moved to Git LFS,
  or kept local only.
- Add a license before making the repository public.

## Before app store review

- Create a signed release build.
- Bump `versionCode` and `versionName`.
- Decide final package id and app name.
- Prepare privacy policy text:
  - local-first data storage
  - export/restore behavior
  - no account system
  - no screenshot/OCR capture in current version
- Prepare store screenshots for:
  - widget quick entry
  - in-app manual entry
  - ledger
  - statistics
  - export/restore
- Test widget availability on Samsung, Xiaomi, OPPO, vivo, and a stock Android
  launcher if possible.

## Friend-test flavor

The `friends` product flavor uses an application id suffix so it can coexist
with the mainline app:

```text
com.lightledger.app.friends
```

Use this flavor when sending APKs to friends so their test app does not replace
your main local version.
