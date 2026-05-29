# Supabase Auth Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Settings profile authentication with Supabase Auth while keeping provider-specific logic behind HabitBloom-owned interfaces.

**Architecture:** Add a common auth domain contract, implement it with a Supabase gateway, and keep platform token acquisition separate from session creation. Settings observes `AuthRepository` state and renders guest, email, and Google account actions without depending directly on Supabase APIs.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin, kotlinx.coroutines, supabase-kt `auth-kt` and `storage-kt`, common Kotlin tests.

---

## File Structure

- Create `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthModels.kt`: app-owned auth user/session/provider/error models.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthRepository.kt`: repository interface/class behavior that delegates to an injected gateway.
- Create `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthGateway.kt`: provider-agnostic data source contract.
- Create `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthValidation.kt`: email/password validation reducers.
- Replace `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/data/AuthRemoteDataSource.kt` with `SupabaseAuthGateway.kt`: Supabase implementation.
- Create `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/platform/GoogleAuthProvider.kt`: common external Google token interface.
- Create `composeApp/src/androidMain/kotlin/com/horizondev/habitbloom/auth/platform/AndroidGoogleAuthProvider.kt`: Android implementation shell that returns a typed setup error until OAuth client IDs are configured.
- Create `composeApp/src/iosMain/kotlin/com/horizondev/habitbloom/auth/platform/IosGoogleAuthProvider.kt`: iOS unavailable implementation.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/habits/data/remote/SupabaseConfig.kt`: install Supabase Auth.
- Modify `gradle/libs.versions.toml` and `composeApp/build.gradle.kts`: add `auth-kt` dependency and keep Firebase auth available only if still needed elsewhere.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/di/RemoteDataModule.kt`, `DataModule.kt`, and platform modules: wire Supabase auth gateway and Google token provider.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/common/AppViewModel.kt` and `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/app/AppViewModel.kt`: stop forcing remote anonymous auth at startup.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsUiState.kt`: add auth form and profile state.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsViewModel.kt`: handle auth actions.
- Modify `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsScreen.kt`: render auth actions in the profile section.
- Modify `composeApp/src/commonMain/composeResources/values/strings.xml` and `values-uk/strings.xml`: add Settings auth strings.
- Create common tests under `composeApp/src/commonTest/kotlin/com/horizondev/habitbloom/auth` and `.../screens/settings/presentation`.

### Task 1: Auth Domain Models And Validation

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthModels.kt`
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthValidation.kt`
- Create: `composeApp/src/commonTest/kotlin/com/horizondev/habitbloom/auth/domain/AuthValidationTest.kt`

- [ ] **Step 1: Write the failing validation tests**

```kotlin
package com.horizondev.habitbloom.auth.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthValidationTest {
    @Test
    fun validEmailAndPasswordPasses() {
        val result = validateEmailPassword("user@example.com", "strong-password")
        assertEquals(AuthInputValidation.Valid, result)
    }

    @Test
    fun blankEmailFails() {
        val result = validateEmailPassword("", "strong-password")
        assertTrue(result is AuthInputValidation.Invalid)
        assertEquals(AuthInputError.EmailRequired, result.errors.first())
    }

    @Test
    fun shortPasswordFails() {
        val result = validateEmailPassword("user@example.com", "12345")
        assertTrue(result is AuthInputValidation.Invalid)
        assertEquals(AuthInputError.PasswordTooShort, result.errors.first())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.auth.domain.AuthValidationTest`

Expected: fails because `validateEmailPassword` and related types are not defined.

- [ ] **Step 3: Add minimal auth domain models**

```kotlin
package com.horizondev.habitbloom.auth.domain

data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val provider: AuthProvider
)

data class AuthSession(
    val user: AuthUser?,
    val isAuthenticated: Boolean
) {
    companion object {
        val Guest = AuthSession(user = null, isAuthenticated = false)
    }
}

enum class AuthProvider {
    Guest,
    Email,
    Google,
    Apple,
    Unknown
}

data class ExternalAuthTokens(
    val idToken: String,
    val accessToken: String? = null,
    val provider: AuthProvider
)

sealed interface AuthFailure {
    data object InvalidCredentials : AuthFailure
    data object NetworkUnavailable : AuthFailure
    data object ProviderUnavailable : AuthFailure
    data class Unknown(val message: String) : AuthFailure
}
```

- [ ] **Step 4: Add validation implementation**

```kotlin
package com.horizondev.habitbloom.auth.domain

sealed interface AuthInputValidation {
    data object Valid : AuthInputValidation
    data class Invalid(val errors: List<AuthInputError>) : AuthInputValidation
}

enum class AuthInputError {
    EmailRequired,
    EmailInvalid,
    PasswordRequired,
    PasswordTooShort
}

fun validateEmailPassword(email: String, password: String): AuthInputValidation {
    val errors = buildList {
        val trimmedEmail = email.trim()
        when {
            trimmedEmail.isEmpty() -> add(AuthInputError.EmailRequired)
            "@" !in trimmedEmail || "." !in trimmedEmail.substringAfter("@", "") -> {
                add(AuthInputError.EmailInvalid)
            }
        }
        when {
            password.isEmpty() -> add(AuthInputError.PasswordRequired)
            password.length < 6 -> add(AuthInputError.PasswordTooShort)
        }
    }
    return if (errors.isEmpty()) AuthInputValidation.Valid else AuthInputValidation.Invalid(errors)
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.auth.domain.AuthValidationTest`

Expected: tests in `AuthValidationTest` pass.

### Task 2: Auth Gateway Contract And Repository

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthGateway.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/domain/AuthRepository.kt`
- Create: `composeApp/src/commonTest/kotlin/com/horizondev/habitbloom/auth/domain/AuthRepositoryTest.kt`

- [ ] **Step 1: Write failing repository tests**

```kotlin
package com.horizondev.habitbloom.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRepositoryTest {
    @Test
    fun currentSessionDefaultsToGuestWhenGatewayHasNoUser() = runTest {
        val gateway = FakeAuthGateway(AuthSession.Guest)
        val repository = AuthRepository(gateway)

        assertEquals(AuthSession.Guest, repository.currentSession())
    }

    @Test
    fun signInWithEmailDelegatesToGateway() = runTest {
        val user = AuthUser("user-1", "user@example.com", "User", AuthProvider.Email)
        val gateway = FakeAuthGateway(AuthSession(user, isAuthenticated = true))
        val repository = AuthRepository(gateway)

        val result = repository.signInWithEmail("user@example.com", "strong-password")

        assertEquals(Result.success(AuthSession(user, true)), result)
        assertEquals("user@example.com", gateway.lastEmail)
    }

    private class FakeAuthGateway(initialSession: AuthSession) : AuthGateway {
        private val session = MutableStateFlow(initialSession)
        var lastEmail: String? = null

        override fun observeSession() = session
        override suspend fun currentSession() = session.value
        override suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
            lastEmail = email
            return Result.success(session.value)
        }
        override suspend fun signUpWithEmail(email: String, password: String) = Result.success(session.value)
        override suspend fun resetPassword(email: String) = Result.success(Unit)
        override suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens) = Result.success(session.value)
        override suspend fun signOut() = Result.success(Unit)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.auth.domain.AuthRepositoryTest`

Expected: fails because `AuthGateway` and the new repository API are not defined.

- [ ] **Step 3: Add gateway contract**

```kotlin
package com.horizondev.habitbloom.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthGateway {
    fun observeSession(): Flow<AuthSession>
    suspend fun currentSession(): AuthSession
    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthSession>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens): Result<AuthSession>
    suspend fun signOut(): Result<Unit>
}
```

- [ ] **Step 4: Replace repository implementation**

```kotlin
package com.horizondev.habitbloom.auth.domain

class AuthRepository(
    private val gateway: AuthGateway
) {
    fun observeSession() = gateway.observeSession()

    suspend fun currentSession(): AuthSession = gateway.currentSession()

    suspend fun initUser(): Result<Boolean> = runCatching {
        gateway.currentSession()
        true
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
        return gateway.signInWithEmail(email.trim(), password)
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthSession> {
        return gateway.signUpWithEmail(email.trim(), password)
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return gateway.resetPassword(email.trim())
    }

    suspend fun signInWithGoogle(tokens: ExternalAuthTokens): Result<AuthSession> {
        return gateway.signInWithExternalTokens(tokens.copy(provider = AuthProvider.Google))
    }

    suspend fun signOut(): Result<Unit> = gateway.signOut()
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.auth.domain.AuthRepositoryTest`

Expected: repository tests pass.

### Task 3: Supabase Auth Dependency And Gateway

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `composeApp/build.gradle.kts`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/habits/data/remote/SupabaseConfig.kt`
- Delete or replace: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/data/AuthRemoteDataSource.kt`
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/data/SupabaseAuthGateway.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/di/RemoteDataModule.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/di/DataModule.kt`

- [ ] **Step 1: Add Supabase auth version catalog entry**

Add to `[libraries]`:

```toml
supabase-auth-ktx = { module = "io.github.jan-tennert.supabase:auth-kt", version.ref = "supabaseBom" }
```

- [ ] **Step 2: Add dependency to commonMain**

Add beside the existing storage dependency:

```kotlin
implementation(libs.supabase.auth.ktx)
implementation(libs.supabase.storage.ktx)
```

- [ ] **Step 3: Configure Supabase Auth plugin**

Update `SupabaseConfig.createClient()`:

```kotlin
return createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Storage)
}
```

Use imports:

```kotlin
import io.github.jan.supabase.auth.Auth
```

- [ ] **Step 4: Implement Supabase gateway**

```kotlin
package com.horizondev.habitbloom.auth.data

import com.horizondev.habitbloom.auth.domain.AuthGateway
import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.AuthUser
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.Google
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow

class SupabaseAuthGateway(
    private val supabaseClient: SupabaseClient
) : AuthGateway {
    private val auth = supabaseClient.auth
    private val sessionState = MutableStateFlow(AuthSession.Guest)

    override fun observeSession(): Flow<AuthSession> = sessionState

    override suspend fun currentSession(): AuthSession {
        val session = auth.currentSessionOrNull()?.toDomainSession() ?: AuthSession.Guest
        sessionState.value = session
        return session
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> = runCatching {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        currentSession()
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthSession> = runCatching {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        currentSession()
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.resetPasswordForEmail(email)
    }

    override suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens): Result<AuthSession> = runCatching {
        auth.signInWith(IDToken) {
            provider = Google
            idToken = tokens.idToken
            accessToken = tokens.accessToken
        }
        currentSession()
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
        sessionState.value = AuthSession.Guest
    }
}
```

During implementation, adjust method names to the exact `auth-kt` 3.3.0 API exposed by the compiler while keeping this contract unchanged.

- [ ] **Step 5: Map Supabase session to domain**

Add private mapping in `SupabaseAuthGateway.kt` based on the SDK session and user types:

```kotlin
private fun String?.toProvider(): AuthProvider {
    return when (this?.lowercase()) {
        "email" -> AuthProvider.Email
        "google" -> AuthProvider.Google
        "apple" -> AuthProvider.Apple
        null -> AuthProvider.Unknown
        else -> AuthProvider.Unknown
    }
}
```

Use the compiler to complete SDK field access while preserving the resulting domain shape:

```kotlin
AuthSession(
    user = AuthUser(
        id = user.id,
        email = user.email,
        displayName = user.userMetadata?.get("name")?.toString()?.trim('"'),
        provider = providerName.toProvider()
    ),
    isAuthenticated = true
)
```

- [ ] **Step 6: Wire DI**

Update `RemoteDataModule.kt` to bind `SupabaseAuthGateway` as `AuthGateway`:

```kotlin
single<AuthGateway> { SupabaseAuthGateway(supabaseClient = get()) }
```

Update imports and remove `AuthRemoteDataSource` factory wiring. Keep Firebase Firestore wiring untouched for catalog code that still uses it.

Update `DataModule.kt`:

```kotlin
single { AuthRepository(gateway = get()) }
```

- [ ] **Step 7: Compile to reveal SDK API mismatches**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: either passes or reports concrete `auth-kt` API method/import mismatches. Fix only the gateway/SupabaseConfig code until the task compiles.

### Task 4: Platform Google Token Provider Boundary

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/auth/platform/GoogleAuthProvider.kt`
- Create: `composeApp/src/androidMain/kotlin/com/horizondev/habitbloom/auth/platform/AndroidGoogleAuthProvider.kt`
- Create: `composeApp/src/iosMain/kotlin/com/horizondev/habitbloom/auth/platform/IosGoogleAuthProvider.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/horizondev/habitbloom/platform/PlatformModule.kt`
- Modify: `composeApp/src/iosMain/kotlin/com/horizondev/habitbloom/platform/PlatformModule.kt`

- [ ] **Step 1: Add common provider interface**

```kotlin
package com.horizondev.habitbloom.auth.platform

import com.horizondev.habitbloom.auth.domain.AuthFailure
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens

interface GoogleAuthProvider {
    suspend fun requestTokens(): Result<ExternalAuthTokens>
}
```

- [ ] **Step 2: Add Android implementation shell**

```kotlin
package com.horizondev.habitbloom.auth.platform

import com.horizondev.habitbloom.auth.domain.AuthFailure
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens

class AndroidGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun requestTokens(): Result<ExternalAuthTokens> {
        return Result.failure(IllegalStateException(AuthFailure.ProviderUnavailable.toString()))
    }
}
```

- [ ] **Step 3: Add iOS implementation shell**

```kotlin
package com.horizondev.habitbloom.auth.platform

import com.horizondev.habitbloom.auth.domain.AuthFailure
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens

class IosGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun requestTokens(): Result<ExternalAuthTokens> {
        return Result.failure(IllegalStateException(AuthFailure.ProviderUnavailable.toString()))
    }
}
```

- [ ] **Step 4: Wire platform modules**

In Android platform module:

```kotlin
single<GoogleAuthProvider> { AndroidGoogleAuthProvider() }
```

In iOS platform module:

```kotlin
single<GoogleAuthProvider> { IosGoogleAuthProvider() }
```

- [ ] **Step 5: Compile Android**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: platform modules resolve the provider dependency.

### Task 5: Settings Auth State Reducers

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsUiState.kt`
- Create: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsAuthReducers.kt`
- Create: `composeApp/src/commonTest/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsAuthReducersTest.kt`

- [ ] **Step 1: Write failing reducer tests**

```kotlin
package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.AuthUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAuthReducersTest {
    @Test
    fun guestSessionCreatesGuestProfile() {
        val state = reduceAuthSession(SettingsUiState(), AuthSession.Guest)

        assertFalse(state.authProfile.isAuthenticated)
        assertEquals(AuthProvider.Guest, state.authProfile.provider)
    }

    @Test
    fun authenticatedSessionCreatesEmailProfile() {
        val session = AuthSession(
            user = AuthUser("id-1", "user@example.com", null, AuthProvider.Email),
            isAuthenticated = true
        )

        val state = reduceAuthSession(SettingsUiState(), session)

        assertTrue(state.authProfile.isAuthenticated)
        assertEquals("user@example.com", state.authProfile.title)
        assertEquals(AuthProvider.Email, state.authProfile.provider)
    }
}
```

- [ ] **Step 2: Run reducer tests to verify failure**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsAuthReducersTest`

Expected: fails because reducer and auth UI state do not exist.

- [ ] **Step 3: Add Settings auth UI state**

Add to `SettingsUiState.kt`:

```kotlin
data class SettingsAuthProfileUiState(
    val isAuthenticated: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val provider: AuthProvider = AuthProvider.Guest
)

enum class SettingsAuthMode {
    SignIn,
    SignUp
}
```

Extend `SettingsUiState`:

```kotlin
val authProfile: SettingsAuthProfileUiState = SettingsAuthProfileUiState(),
val authMode: SettingsAuthMode = SettingsAuthMode.SignIn,
val showAuthSheet: Boolean = false,
val authEmail: String = "",
val authPassword: String = "",
val authError: String? = null,
val isAuthLoading: Boolean = false
```

- [ ] **Step 4: Add reducer implementation**

```kotlin
package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession

fun reduceAuthSession(
    state: SettingsUiState,
    session: AuthSession
): SettingsUiState {
    val user = session.user
    val profile = if (session.isAuthenticated && user != null) {
        SettingsAuthProfileUiState(
            isAuthenticated = true,
            title = user.displayName ?: user.email ?: "HabitBloom account",
            subtitle = when (user.provider) {
                AuthProvider.Google -> "Signed in with Google"
                AuthProvider.Email -> "Signed in with email"
                AuthProvider.Apple -> "Signed in with Apple"
                AuthProvider.Guest -> "Guest profile"
                AuthProvider.Unknown -> "Signed in"
            },
            provider = user.provider
        )
    } else {
        SettingsAuthProfileUiState(
            isAuthenticated = false,
            title = "Guest profile",
            subtitle = "Sign in to protect your progress",
            provider = AuthProvider.Guest
        )
    }
    return state.copy(authProfile = profile, isAuthLoading = false)
}
```

- [ ] **Step 5: Run reducer tests to verify pass**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsAuthReducersTest`

Expected: reducer tests pass.

### Task 6: Settings ViewModel Auth Actions

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsViewModel.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsUiState.kt`
- Create: `composeApp/src/commonTest/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsViewModelAuthTest.kt`

- [ ] **Step 1: Add Settings events**

Extend `SettingsUiEvent`:

```kotlin
data object OpenSignIn : SettingsUiEvent
data object OpenSignUp : SettingsUiEvent
data object CloseAuthSheet : SettingsUiEvent
data class UpdateAuthEmail(val email: String) : SettingsUiEvent
data class UpdateAuthPassword(val password: String) : SettingsUiEvent
data object SubmitEmailAuth : SettingsUiEvent
data object ResetPassword : SettingsUiEvent
data object SignInWithGoogle : SettingsUiEvent
```

Keep existing `Logout` and make it call Supabase sign out.

- [ ] **Step 2: Write failing ViewModel tests**

Create fakes for `AuthRepository`, `ProfileRepository`, `ThemeUseCase`, `TimeFormatUseCase`, and `GoogleAuthProvider` only as needed by constructor signatures. The first tests should verify reducer-level behavior through events:

```kotlin
@Test
fun openSignInShowsSheetInSignInMode() = runTest {
    val viewModel = createViewModel()

    viewModel.handleUiEvent(SettingsUiEvent.OpenSignIn)

    assertTrue(viewModel.state.value.showAuthSheet)
    assertEquals(SettingsAuthMode.SignIn, viewModel.state.value.authMode)
}
```

```kotlin
@Test
fun invalidEmailAuthDoesNotCallRepository() = runTest {
    val authRepository = FakeAuthRepository()
    val viewModel = createViewModel(authRepository = authRepository)

    viewModel.handleUiEvent(SettingsUiEvent.UpdateAuthEmail("bad"))
    viewModel.handleUiEvent(SettingsUiEvent.UpdateAuthPassword("12345"))
    viewModel.handleUiEvent(SettingsUiEvent.SubmitEmailAuth)

    assertEquals(0, authRepository.signInCalls)
    assertEquals("Enter a valid email and password.", viewModel.state.value.authError)
}
```

- [ ] **Step 3: Run tests to verify failure**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsViewModelAuthTest`

Expected: fails because ViewModel auth events are not implemented.

- [ ] **Step 4: Update ViewModel constructor**

```kotlin
class SettingsViewModel(
    private val repository: ProfileRepository,
    private val themeUseCase: ThemeUseCase,
    private val timeFormatUseCase: TimeFormatUseCase,
    private val authRepository: AuthRepository,
    private val googleAuthProvider: GoogleAuthProvider
) : BloomViewModel<SettingsUiState, SettingsUiIntent>(SettingsUiState()), KoinComponent
```

- [ ] **Step 5: Observe auth session in init**

```kotlin
authRepository.observeSession().onEach { session ->
    updateState { reduceAuthSession(it, session) }
}.launchIn(viewModelScope)

viewModelScope.launch {
    updateState { reduceAuthSession(it, authRepository.currentSession()) }
}
```

- [ ] **Step 6: Handle email auth events**

```kotlin
SettingsUiEvent.SubmitEmailAuth -> {
    launch {
        val current = state.value
        if (validateEmailPassword(current.authEmail, current.authPassword) !is AuthInputValidation.Valid) {
            updateState { it.copy(authError = "Enter a valid email and password.") }
            return@launch
        }
        updateState { it.copy(isAuthLoading = true, authError = null) }
        val result = when (current.authMode) {
            SettingsAuthMode.SignIn -> authRepository.signInWithEmail(current.authEmail, current.authPassword)
            SettingsAuthMode.SignUp -> authRepository.signUpWithEmail(current.authEmail, current.authPassword)
        }
        result
            .onSuccess { session -> updateState { reduceAuthSession(it, session).copy(showAuthSheet = false, authPassword = "") } }
            .onFailure { error -> updateState { it.copy(isAuthLoading = false, authError = error.message ?: "Authentication failed.") } }
    }
}
```

- [ ] **Step 7: Handle Google and sign out events**

```kotlin
SettingsUiEvent.SignInWithGoogle -> {
    launch {
        updateState { it.copy(isAuthLoading = true, authError = null) }
        googleAuthProvider.requestTokens()
            .fold(
                onSuccess = { tokens ->
                    authRepository.signInWithGoogle(tokens)
                        .onSuccess { session -> updateState { reduceAuthSession(it, session) } }
                        .onFailure { error -> updateState { it.copy(isAuthLoading = false, authError = error.message ?: "Google sign in failed.") } }
                },
                onFailure = { error ->
                    updateState { it.copy(isAuthLoading = false, authError = error.message ?: "Google sign in is not configured.") }
                }
            )
    }
}

is SettingsUiEvent.Logout -> {
    launch {
        authRepository.signOut()
        updateState { reduceAuthSession(it, AuthSession.Guest) }
    }
}
```

- [ ] **Step 8: Run ViewModel tests**

Run: `./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsViewModelAuthTest`

Expected: Settings ViewModel auth tests pass.

### Task 7: Settings Profile UI

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/screens/settings/presentation/SettingsScreen.kt`
- Modify: `composeApp/src/commonMain/composeResources/values/strings.xml`
- Modify: `composeApp/src/commonMain/composeResources/values-uk/strings.xml`

- [ ] **Step 1: Add strings**

Add English strings:

```xml
<string name="settings_guest_profile">Guest profile</string>
<string name="settings_guest_profile_subtitle">Sign in to protect your progress</string>
<string name="settings_continue_with_google">Continue with Google</string>
<string name="settings_sign_in_email">Sign in with email</string>
<string name="settings_create_account">Create account</string>
<string name="settings_sign_out">Sign out</string>
<string name="settings_email">Email</string>
<string name="settings_password">Password</string>
<string name="settings_reset_password">Reset password</string>
```

Add Ukrainian translations with equivalent account language.

- [ ] **Step 2: Change ProfileCard parameters**

```kotlin
private fun ProfileCard(
    profile: SettingsAuthProfileUiState,
    onGoogleClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSignOutClick: () -> Unit
)
```

- [ ] **Step 3: Render guest actions**

Inside `ProfileCard`, if `profile.isAuthenticated` is false, render three buttons using existing Bloom buttons:

```kotlin
BloomPrimaryFilledButton(
    text = stringResource(Res.string.settings_continue_with_google),
    onClick = onGoogleClick
)
BloomPrimaryOutlinedButton(
    text = stringResource(Res.string.settings_sign_in_email),
    onClick = onSignInClick
)
BloomPrimaryOutlinedButton(
    text = stringResource(Res.string.settings_create_account),
    onClick = onSignUpClick
)
```

- [ ] **Step 4: Render signed-in actions**

If authenticated, show the profile title/subtitle and a sign out button:

```kotlin
BloomPrimaryOutlinedButton(
    text = stringResource(Res.string.settings_sign_out),
    onClick = onSignOutClick
)
```

- [ ] **Step 5: Add auth sheet/dialog**

Use the existing dialog pattern and text fields to collect email/password. The dialog submit button dispatches `SettingsUiEvent.SubmitEmailAuth`, reset dispatches `SettingsUiEvent.ResetPassword`, and close dispatches `SettingsUiEvent.CloseAuthSheet`.

- [ ] **Step 6: Compile UI**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: Settings UI compiles with generated resources.

### Task 8: App Startup And Firebase Auth Removal

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/common/AppViewModel.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/app/AppViewModel.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/horizondev/habitbloom/di/RemoteDataModule.kt`

- [ ] **Step 1: Stop treating auth init as anonymous sign-in**

Keep startup session load but no longer create a remote user:

```kotlin
runCatching {
    authRepository.currentSession()
}.onFailure {
    Napier.e("Failed to load auth session", it, tag = TAG)
}
```

- [ ] **Step 2: Remove Firebase Auth remote data source wiring**

Remove imports and bindings for:

```kotlin
AuthRemoteDataSource
FirebaseAuth
Firebase.auth
```

Keep Firebase Firestore if current habit catalog code still needs it.

- [ ] **Step 3: Compile**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: app startup compiles without Firebase auth dependency injection.

### Task 9: Verification

**Files:**
- No new files.

- [ ] **Step 1: Run focused auth tests**

Run:

```bash
./gradlew :composeApp:allTests --tests com.horizondev.habitbloom.auth.domain.AuthValidationTest --tests com.horizondev.habitbloom.auth.domain.AuthRepositoryTest --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsAuthReducersTest --tests com.horizondev.habitbloom.screens.settings.presentation.SettingsViewModelAuthTest
```

Expected: all focused auth tests pass.

- [ ] **Step 2: Run Android compile**

Run:

```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```

Expected: compile succeeds.

- [ ] **Step 3: Run broader common tests**

Run:

```bash
./gradlew :composeApp:allTests
```

Expected: existing common tests continue to pass.

- [ ] **Step 4: Manual runtime check**

Run the Android app and open Settings. Verify:

- guest profile appears when no session exists
- email sign-in sheet opens
- invalid email/password shows validation error
- Google action reports setup/unavailable error until OAuth implementation is completed
- sign out returns to guest profile

## Spec Coverage Review

- Supabase Auth replaces Firebase as the planned implementation: Tasks 3 and 8.
- Flexible provider boundary: Tasks 2 and 4.
- Guest mode without forced remote anonymous auth: Task 8.
- Email/password, reset password, Google, sign out actions: Tasks 3, 6, and 7.
- Settings profile functionality: Tasks 5, 6, and 7.
- Security constraints around client keys and RLS are documented in the spec; no database tables are created in this implementation plan.
- Testing and verification paths are covered in Tasks 1, 2, 5, 6, and 9.
