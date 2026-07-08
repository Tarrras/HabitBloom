# HabitBloom README Refresh Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the stale README and screenshots with an accurate, product-first presentation of the current HabitBloom application.

**Architecture:** Treat the running Android app and repository configuration as authoritative. Capture a coherent six-screen product story from one emulator state, optimize those assets in `screenshots/`, then rewrite `README.md` around verified product capabilities, the current Kotlin Multiplatform stack, and reproducible setup guidance.

**Tech Stack:** Markdown, HTML image layout, Android Debug Bridge, Kotlin Multiplatform, Compose Multiplatform, Gradle.

## Global Constraints

- Documentation and screenshot assets only; do not modify application behavior or secret configuration.
- README language is English and the presentation is product-first.
- Every feature statement must be supported by runtime behavior, source, resources, or Gradle configuration.
- Screenshots must come from the current Android debug build at one consistent emulator size.
- Do not expose credentials or stage `iosApp/Configuration/Secrets.xcconfig`.
- Remove fictional repository owners, placeholder contacts, stale Voyager references, and unsupported license claims.

---

### Task 1: Capture the current product experience

**Files:**
- Create or replace: `screenshots/home.png`
- Create: `screenshots/garden.png`
- Create: `screenshots/calendar.png`
- Create or replace: `screenshots/statistics.png`
- Create or replace: `screenshots/create_habit.png`
- Create: `screenshots/settings.png`
- Remove if redundant: `screenshots/habit_details.png`

**Interfaces:**
- Consumes: Android debug application `com.horizondev.habitbloom` and its persisted emulator data.
- Produces: six consistent, publication-ready screenshots referenced by Task 2.

- [ ] **Step 1: Confirm the Android target and install task**

Run:

```bash
/Users/tarasvovcenko/Library/Android/sdk/platform-tools/adb devices
./gradlew tasks --all --console=plain | rg 'install.*Debug'
```

Expected: one emulator is listed as `device`, and `installDebug` is available for `composeApp`.

- [ ] **Step 2: Build and install the current debug application**

Run:

```bash
./gradlew :composeApp:installDebug --console=plain
```

Expected: `BUILD SUCCESSFUL` and installation on the connected emulator.

- [ ] **Step 3: Launch and inspect the initial UI state**

Run:

```bash
ADB=/Users/tarasvovcenko/Library/Android/sdk/platform-tools/adb
$ADB -s emulator-5554 shell am force-stop com.horizondev.habitbloom
$ADB -s emulator-5554 shell am start -n com.horizondev.habitbloom/.MainActivity
$ADB -s emulator-5554 exec-out uiautomator dump /dev/tty > /tmp/habitbloom-home.xml
python3 /Users/tarasvovcenko/.codex/plugins/cache/openai-curated/test-android-apps/d6169bef/skills/android-emulator-qa/scripts/ui_tree_summarize.py /tmp/habitbloom-home.xml /tmp/habitbloom-home-summary.txt
```

Expected: the app starts without a crash and the summary identifies the current top-level screen and navigation targets.

- [ ] **Step 4: Navigate using UI-tree coordinates and capture six screens**

For each target, dump the UI tree, use `ui_pick.py` to derive the target center, tap it, wait for stable content, and capture:

```bash
$ADB -s emulator-5554 exec-out screencap -p > screenshots/home.png
$ADB -s emulator-5554 exec-out screencap -p > screenshots/garden.png
$ADB -s emulator-5554 exec-out screencap -p > screenshots/calendar.png
$ADB -s emulator-5554 exec-out screencap -p > screenshots/statistics.png
$ADB -s emulator-5554 exec-out screencap -p > screenshots/create_habit.png
$ADB -s emulator-5554 exec-out screencap -p > screenshots/settings.png
```

Expected: every file is a non-empty PNG with the same pixel dimensions, no keyboard, transient dialog, loading state, or debug overlay.

- [ ] **Step 5: Inspect and optimize the assets**

Run:

```bash
file screenshots/*.png
sips -g pixelWidth -g pixelHeight screenshots/home.png screenshots/garden.png screenshots/calendar.png screenshots/statistics.png screenshots/create_habit.png screenshots/settings.png
```

Expected: all six product screenshots have equal dimensions and can be visually inspected as representative stable states. Preserve `screenshots/logo.png` separately.

- [ ] **Step 6: Commit the screenshot set**

```bash
git add screenshots/home.png screenshots/garden.png screenshots/calendar.png screenshots/statistics.png screenshots/create_habit.png screenshots/settings.png
git add -u screenshots
git commit -m "docs: refresh HabitBloom product screenshots"
```

Expected: only intended screenshot assets are included in the commit.

### Task 2: Rewrite the README as a product-first presentation

**Files:**
- Modify: `README.md`

**Interfaces:**
- Consumes: the six screenshot paths produced by Task 1 and verified repository facts.
- Produces: the repository landing page and all local image references checked by Task 3.

- [ ] **Step 1: Inventory authoritative product and engineering facts**

Run:

```bash
rg -n 'data object (Home|Statistics|Calendar|Settings)|Garden|Reminder|Theme|Language|Notification' composeApp/src/commonMain/kotlin composeApp/src/commonMain/composeResources/values/strings.xml
rg -n 'kotlin =|compose =|koin|sqldelight|firebase|supabase|ktor|coil|navigation-compose' gradle/libs.versions.toml composeApp/build.gradle.kts
test -f LICENSE && echo license-present || echo no-license-file
```

Expected: evidence for the feature list and stack is recorded; the license section is included only if `LICENSE` exists.

- [ ] **Step 2: Replace the README structure and copy**

Write `README.md` with these exact sections:

```markdown
# HabitBloom

<!-- centered logo, concise value proposition, accurate badges -->

## Build habits that grow
## See HabitBloom in action
## What you can do
## How it works
## Built with Kotlin Multiplatform
## Project structure
## Getting started
## Contributing
```

The gallery must reference:

```text
screenshots/home.png
screenshots/garden.png
screenshots/calendar.png
screenshots/statistics.png
screenshots/create_habit.png
screenshots/settings.png
```

Expected: product value and screenshots appear before technical details; all copy follows the global accuracy constraints.

- [ ] **Step 3: Verify setup instructions against the repository**

Run:

```bash
rg -n 'google-services|GoogleService-Info|Secrets.xcconfig|SUPABASE|FIREBASE' . --glob '!**/build/**' --glob '!iosApp/Configuration/Secrets.xcconfig'
./gradlew :composeApp:tasks --console=plain | rg 'assembleDebug|installDebug'
xcodebuild -list -project iosApp/iosApp.xcodeproj
```

Expected: README file locations and Android/iOS commands match the current project; secret values are never printed or copied.

- [ ] **Step 4: Review the README diff**

Run:

```bash
git diff --check -- README.md
git diff -- README.md
```

Expected: no whitespace errors, placeholders, fictional URLs, stale technology claims, or unsupported product claims.

- [ ] **Step 5: Commit the README rewrite**

```bash
git add README.md
git commit -m "docs: present HabitBloom product experience"
```

Expected: the commit contains `README.md` only.

### Task 3: Validate the complete documentation package

**Files:**
- Verify: `README.md`
- Verify: `screenshots/*.png`

**Interfaces:**
- Consumes: the final README and screenshot set.
- Produces: evidence that the requested documentation is complete, accurate, and renderable.

- [ ] **Step 1: Validate every local README link**

Run:

```bash
python3 - <<'PY'
from pathlib import Path
import re

readme = Path("README.md").read_text()
paths = re.findall(r'(?:src="|!\[[^\]]*\]\()([^")#]+)', readme)
missing = [path for path in paths if not path.startswith(("http://", "https://")) and not Path(path).exists()]
assert not missing, f"Missing local README assets: {missing}"
print(f"Validated {len(paths)} README image/link targets")
PY
```

Expected: the script exits successfully with no missing local assets.

- [ ] **Step 2: Scan for stale and placeholder content**

Run:

```bash
rg -n 'yourusername|your\.email|Voyager|TODO|TBD|YOUR_|MIT License' README.md
```

Expected: no matches.

- [ ] **Step 3: Confirm screenshot integrity**

Run:

```bash
file screenshots/home.png screenshots/garden.png screenshots/calendar.png screenshots/statistics.png screenshots/create_habit.png screenshots/settings.png
sips -g pixelWidth -g pixelHeight screenshots/home.png screenshots/garden.png screenshots/calendar.png screenshots/statistics.png screenshots/create_habit.png screenshots/settings.png
```

Expected: six valid PNG files with identical dimensions.

- [ ] **Step 4: Perform the final repository audit**

Run:

```bash
git status --short
git log -3 --oneline
git diff --check HEAD~2..HEAD
```

Expected: documentation commits are present, there are no unintended tracked changes, and the pre-existing untracked `iosApp/Configuration/Secrets.xcconfig` remains untouched.
