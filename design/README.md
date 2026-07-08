# Design notes

This folder is intentionally light in the public repository.

The current public source snapshot keeps process artwork local by default:

- Illustrator source files.
- Widget proposal boards.
- Historical redesign explorations.
- Raw review screenshots and UI dumps.

The app assets that are actually used at runtime live in Android resources,
especially:

```text
app/src/main/res/drawable-nodpi/
```

Public screenshots, logo assets, and the current README tutorial image live in:

```text
docs/
```

Keep source files and process drafts local unless they are deliberately promoted
to public-facing assets.
