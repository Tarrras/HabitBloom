# Modern Android/KMP Playbook

## Module Blueprint

- Organize by feature and layer, not by technical type alone.
- Keep `commonMain` focused on domain rules, use cases, and shared repository contracts.
- Place network/database implementations in platform-specific or shared data modules depending on
  library support.

Example layout:

- `:composeApp` (Android app shell + navigation host)
- `:shared:core:domain` (entities, use cases, repository interfaces)
- `:shared:core:data` (repository implementations, mappers, DTO adapters)
- `:shared:feature:<name>` (feature presentation/domain/data as needed)
- `:shared:core:testing` (test fixtures, fake factories, shared test utilities)

## Dependency Injection with Koin

- Define one Koin module per feature plus core modules.
- Bind interfaces to implementations at module boundaries.
- Inject dispatchers and clocks to improve deterministic tests.
- Keep DI graphs explicit and small; avoid wildcard module includes without purpose.

## UI and State

- Model screen state with immutable `UiState` classes.
- Expose `StateFlow<UiState>` from `ViewModel`/presenter.
- Keep Composables stateless when possible:
  pass state in, callbacks out.
- Hoist state to the nearest stable owner.
- Use `derivedStateOf` and memoization only when profiling indicates real recomposition cost.

## Coroutines and Flow

- Use structured concurrency with lifecycle-aware scopes.
- Prefer cold streams for data pipelines and `StateFlow` for UI state exposure.
- Handle loading/error/success as explicit state, not ad hoc booleans.
- Avoid blocking calls in shared code; abstract platform IO behind suspending interfaces.

## Data Layer

- Keep DTO/entity/domain models separate when API or storage models diverge.
- Use mapper functions/extensions at boundaries.
- Prefer repository contracts in domain and implementations in data.
- Use idempotent operations for sync and retry-safe updates.

## KMP Boundary Decisions

- Share in `commonMain`:
  domain rules, validation, use cases, repository contracts, and pure mapping logic.
- Keep platform-specific:
  permissions, notifications, deep links, secure storage, and OS-specific background work.
- Use `expect/actual` only for real platform divergence. Avoid creating expect/actual for trivial
  wrappers.

## Testing Strategy

- Unit tests:
  use case logic, reducers/state producers, repository behavior with fakes.
- Integration tests:
  data source + serialization + persistence paths where regressions are likely.
- UI tests:
  key user flows and state rendering transitions.
- KMP parity tests:
  validate shared business logic consistency across targets.

## Modern Library Choices

- UI: Jetpack Compose / Compose Multiplatform
- DI: Koin
- Async: kotlinx.coroutines, Flow
- Serialization: kotlinx.serialization
- Networking: Ktor client or codebase-standard modern alternative
- Date/time: kotlinx-datetime
- Local persistence:
  SQLDelight for shared DB use cases, Room for Android-only needs

Always prefer latest stable versions compatible with the project and verify compatibility before
bumping.

## Migration Rules for Legacy Code

- Do not rewrite the whole app at once.
- Apply strangler pattern per feature:
  add modern modules and route new behavior through them.
- Keep interop seams clear when old XML/UI frameworks coexist with Compose during transition.
- Replace direct singleton usage with Koin-backed injection incrementally.
