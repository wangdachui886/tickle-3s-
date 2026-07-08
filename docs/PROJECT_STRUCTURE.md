# Project structure

tickle is organized as a small Android app with a strict split between product
source, design assets, local evidence, and distributable builds.

## Workspace level

```text
E:\AppDev
  README.md
  .gitignore
  LightLedger\
```

`E:\AppDev` is a local workspace. The app project root is `LightLedger/`.

## App project level

```text
LightLedger\
  app\          Android source, resources, manifest, Gradle config
  design\       public design notes; source/process artwork is local by default
  docs\         project, release, and data-format documentation
  gradle\       Gradle wrapper files
  artifacts\    local review evidence and historical experiments
  dist\         local APK copies for sharing/testing
```

Only `app/`, `docs/`, Gradle files, top-level documentation, and deliberate
public design notes should be considered normal project source. `artifacts/`,
`dist/`, and process design source files are local-only by default.

## App package boundaries

```text
com.lightledger.app
  data\
    model\
  domain\
  ui\
    theme\
  widget\
```

### `data`

Owns persistence and import/export.

- Room database setup.
- DAO queries.
- Repository APIs consumed by UI and widgets.
- CSV export and restore logic.

Do not put Compose UI or Android widget layout decisions here.

### `data/model`

Owns database entities and enums.

Schema changes should be made deliberately because they affect installed users
and future backups.

### `domain`

Owns pure Kotlin logic that can be tested without Android UI.

Examples:

- money formatting
- time formatting
- category suggestions
- text parsing helpers

New reusable rules should usually start here before they touch UI.

### `ui`

Owns Compose screens, dialogs, onboarding, and the ViewModel.

The UI should talk to app data through `LightLedgerViewModel` and repository
methods, not directly through DAO objects.

### `widget`

Owns Android AppWidget providers and RemoteViews rendering.

Widget actions should be small and reliable: update temporary widget state,
write confirmed transactions through the repository, then refresh widgets and
app-visible data.

## Design assets

The Android app includes the currently used onboarding images in:

```text
app/src/main/res/drawable-nodpi/
```

Illustrator files, proposal boards, and intermediate widget explorations are
kept out of the public source snapshot unless they are intentionally promoted to
final public assets.

If the tutorial artwork is revised, export the final PNG cards and copy them to:

```text
app/src/main/res/drawable-nodpi/
```

## Local-only artifacts

`artifacts/` contains review screenshots, UI dumps, historical OCR/screenshot
experiments, and sample data. This folder is intentionally ignored by Git.

If a file from `artifacts/` becomes product documentation or a public demo asset,
copy a sanitized version into `docs/` or `design/` instead of linking directly to
the local artifact.

## Adding future modules

Keep the project one Android app module until there is real pressure to split.
When the codebase grows, the first useful extraction points are:

- `:core-model` for data classes and pure domain logic.
- `:core-export` for CSV import/export contracts.
- `:widget` only if widget code becomes independently testable and large.

Do not split modules just to look mature. The current priority is a simple,
readable app that friends can install and that future contributors can
understand quickly.
