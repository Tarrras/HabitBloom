# HabitBloom README Refresh Design

## Goal

Turn the repository README into an accurate, product-first presentation of HabitBloom while retaining enough technical detail for developers and recruiters to evaluate and run the project.

## Audience and language

- Primary audience: potential users, portfolio reviewers, and recruiters.
- Secondary audience: Kotlin Multiplatform developers evaluating the codebase.
- Language: English.
- Tone: concise, polished, and factual. Avoid marketing claims that cannot be verified from the application or repository.

## Content structure

1. **Hero**
   - Existing HabitBloom logo.
   - Product name and a short value proposition centered on turning consistent habits into a growing digital garden.
   - Compact platform and technology badges only when they are accurate.
2. **Product overview**
   - A short explanation of the motivation loop: plan habits, complete them, grow flowers, review progress.
   - Three or four differentiators, emphasizing the visual garden, flexible schedules, progress insights, and cross-platform implementation.
3. **Product gallery**
   - Current Android emulator screenshots for the strongest implemented states.
   - Target screens: Home, Garden, Calendar, Statistics, habit creation or details, and Settings.
   - Arrange screenshots in small themed groups with one-line captions rather than one undifferentiated strip.
4. **Feature set**
   - Habit creation from templates and custom habits.
   - Morning, afternoon, and evening organization.
   - Flexible duration and repeat schedules.
   - Daily completion, streaks, and habit details.
   - Flower garden and growth stages.
   - Calendar history and statistics.
   - Reminders, themes, image support, and English/Ukrainian localization.
   - Offline-first local persistence and cloud-backed account data only where verified in code.
5. **Engineering overview**
   - Kotlin Multiplatform and Compose Multiplatform.
   - Cleanly separated presentation, domain, and data responsibilities.
   - Current libraries and services derived from Gradle files and source, not from the stale README.
   - Compact repository structure matching the current `screens/*` layout.
6. **Getting started**
   - Verified prerequisites and commands.
   - Android and iOS setup at a useful level without exposing secrets.
   - Reference existing configuration file locations rather than fictional repository URLs or hard-coded credentials.
7. **Project status and contribution**
   - Clearly identify the project as actively developed if repository evidence supports it.
   - Keep contribution guidance brief.
   - Remove placeholder contact details and unsupported license claims unless the corresponding files exist.

## Screenshot policy

- Build and install the current Android debug variant on the available emulator.
- Capture screenshots directly from the running app at a consistent device size.
- Use representative populated states where available; do not fabricate data outside normal app interactions.
- Keep system bars consistent and avoid transient dialogs, keyboards, loading indicators, or debug overlays.
- Store optimized PNG or WebP assets under `screenshots/` with descriptive lowercase names.
- Inspect each image after capture and ensure every README image path resolves.

## Accuracy rules

- Feature claims must be supported by current UI, source code, resources, or runtime behavior.
- Stack and version claims must be supported by Gradle configuration.
- Do not retain stale references to Voyager if Navigation Compose is the current implementation.
- Do not claim a license when no `LICENSE` file exists.
- Do not include example GitHub owners, email addresses, API keys, or secret values.

## Verification

- Build and install the Android app successfully.
- Exercise the target flows through the emulator and inspect all final screenshots.
- Check README image references and local links.
- Review the rendered Markdown structure for readable hierarchy and screenshot sizing.
- Inspect the final diff to confirm only intended documentation and screenshot assets changed.

## Scope

This change updates documentation and screenshot assets only. It does not redesign application screens, add features, publish the app, or modify secret configuration.
