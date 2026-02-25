---
name: android-kmp-modern
description: Expert implementation and code-review guidance for modern Android and Kotlin Multiplatform projects. Use when building, refactoring, or reviewing Kotlin-based mobile code with Kotlin 2.x, Jetpack Compose or Compose Multiplatform UI, Koin dependency injection, coroutines/Flow, Gradle module architecture, and modern testing.
---

# Android KMP Modern

Use this skill to produce production-grade Android/KMP solutions with modern tools only and to avoid
reintroducing legacy Android patterns.

## Core Defaults

- Prefer Kotlin 2.x language/compiler features and Kotlin DSL (`*.gradle.kts`).
- Prefer Jetpack Compose (Android) and Compose Multiplatform (shared UI) over XML views.
- Prefer Koin for dependency injection over manual service locators or Dagger/Hilt in new code.
- Use coroutines + `Flow` for async and stream state handling.
- Model UI state as immutable data classes and expose it from `ViewModel`/presenter layers.
- Keep business logic out of Composables; Composables should render state and emit intents.

## Delivery Workflow

1. Classify scope first: Android-only feature, shared KMP business logic, or shared UI.
2. Define module boundaries before coding:
   Android app module, shared `:core`/`:feature`/`:data` modules, platform-specific adapters.
3. Implement by layers:
   UI -> presentation -> domain -> data.
4. Wire dependencies with Koin modules per feature/domain, not one global mega module.
5. Add tests with each change:
   domain and data unit tests, plus UI tests where behavior changed.
6. Run project checks and fix warnings in changed code paths.

## Implementation Rules

- Use `suspend` and `Flow` APIs with explicit dispatcher handling; inject dispatchers where needed.
- Keep side effects at boundaries (repositories, use cases, platform services), not inside
  Composables.
- Use `StateFlow` for persistent UI state and one-off event channels/shared flow only when needed.
- Treat nullability deliberately; avoid force unwraps (`!!`) in production code.
- Serialize network/data contracts with `kotlinx.serialization` unless the codebase already
  standardizes another modern approach.
- Favor feature-based packages/modules over type-based dumping grounds.
- Prefer unidirectional data flow (intent -> reducer/use case -> new state).

## Android Native Guidance

- Use Navigation Compose for screen transitions in Compose-based apps.
- Use lifecycle-aware collection (`collectAsStateWithLifecycle`) for flows in UI.
- Use `rememberSaveable` for user-entered transient state that should survive process recreation.
- Follow Material 3 tokens/theming from a single design system entry point.

## KMP Guidance

- Keep pure business rules in `commonMain` whenever platform APIs are not required.
- Use `expect/actual` for platform-specific capabilities (storage, secure settings, notifications,
  etc.).
- Keep platform entry points thin; delegate shared behavior to common modules.
- When Compose Multiplatform is not practical for a feature, share state/domain and keep UI
  platform-native.

## Quality Bar

- Add or update tests for each behavioral change.
- Preserve deterministic behavior across Android/iOS for shared logic.
- Keep APIs stable and explicit; avoid leaking data-layer types into UI layer.
- Minimize tech debt in touched areas by opportunistic cleanup (small, safe refactors).

## Review Checklist

- Is the change using Kotlin 2.x idioms and modern coroutine patterns?
- Is UI declarative and state-driven (Compose), without business logic in Composables?
- Are dependencies provided through Koin with clear module ownership?
- Is KMP sharing done at the correct layer (domain/data first, UI only when justified)?
- Are tests meaningful and covering changed behavior?

## References

- Read [Modern Android KMP Playbook](references/modern-android-kmp-playbook.md) for detailed
  architecture, module templates, dependency decisions, and test strategy.
- Apply the playbook selectively to fit the codebase while keeping modern defaults.
