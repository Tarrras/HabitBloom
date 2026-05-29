# Supabase Authentication Design

## Context

HabitBloom is a Kotlin Multiplatform Compose app with Android and iOS targets. The Settings screen already has a visual profile card, but it has no account behavior. The app currently initializes Firebase anonymous auth through `AuthRepository`, while Supabase is already present for storage through `SupabaseConfig`.

The approved direction is to move account authentication to Supabase Auth while keeping the app flexible enough to replace Supabase with another provider or custom backend later.

## Goals

- Add real account functionality to the Settings profile section.
- Use Supabase Auth for the first implementation.
- Support guest usage, email/password sign in, email/password account creation, password reset, Google sign in, and sign out.
- Keep authentication behind app-owned interfaces so UI and ViewModels do not depend directly on Supabase APIs.
- Preserve a path for future providers, especially Sign in with Apple before iOS App Store submission if Google sign-in remains enabled.
- Avoid service-role keys or privileged secrets in the client app.

## Non-Goals

- Build a custom backend auth service now.
- Move all habit data to Supabase tables in this feature.
- Implement Sign in with Apple in the first pass.
- Implement account deletion in the first pass.
- Build a full profile editing screen beyond authentication state and account actions.

## Architecture

The auth feature will be split into domain, data, and platform boundaries.

`auth/domain` will define the app-facing contract:

- `AuthRepository`
- `AuthGateway`
- `AuthSession`
- `AuthUser`
- `AuthProvider`
- `AuthError`
- input models for email/password and provider tokens

`auth/data` will contain `SupabaseAuthGateway`, which maps Supabase Auth sessions, users, providers, and failures into HabitBloom domain models.

`auth/platform` will contain platform-specific provider token acquisition:

- `GoogleAuthProvider` common interface
- Android implementation using native Google sign-in or Credential Manager to obtain Google tokens
- iOS and desktop implementations return a typed unavailable result until those flows are implemented

The rest of the app will depend on `AuthRepository`, not on `SupabaseClient`, Supabase GoTrue/Auth APIs, or platform-specific Google APIs.

## Supabase Client

`SupabaseConfig` will install Supabase Auth in addition to Storage. The existing Supabase URL and anon key can remain client-side configuration. No service-role key will be added to the app.

The auth gateway will use Supabase Kotlin Auth APIs for:

- current session lookup
- auth state observation where supported by the SDK
- email/password sign up
- email/password sign in
- password reset email
- Google ID token sign in
- sign out

## Guest Mode

Guest mode means the user can keep using local HabitBloom features without an account. Unlike the current Firebase anonymous auth, this design will not require creating a remote anonymous Supabase user at app start.

The Settings profile card will show a guest state when there is no Supabase session. Guest progress remains local. If later synchronization is added, a separate migration/linking design will decide how local data is associated with a new account.

## Settings UX

The existing profile card will become interactive.

For a guest user, the card will show:

- title: guest profile
- subtitle: local progress / sign in to protect account
- actions: Continue with Google, Sign in with email, Create account

For an authenticated user, the card will show:

- display name if available, otherwise email
- provider label such as Google or Email
- actions: Account details and Sign out

Email sign in and account creation will be implemented as Settings-owned dialog or bottom sheet state, not as a separate top-level navigation flow. Validation errors, loading states, and auth failures will be owned by `SettingsViewModel`.

## ViewModel Behavior

`SettingsViewModel` will observe auth state through `AuthRepository` and include account state in `SettingsUiState`.

New events will include:

- open auth sheet
- close auth sheet
- update email
- update password
- submit sign in
- submit sign up
- reset password
- sign in with Google
- sign out

The ViewModel will translate repository results into stable UI states:

- idle
- loading
- authenticated
- guest
- recoverable error

## Provider Flexibility

Provider-specific token acquisition is separate from session creation. This keeps the system replaceable:

- Supabase can be replaced by a custom backend by implementing `AuthGateway`.
- Google can be replaced or supplemented by adding another `ExternalAuthProvider`.
- Apple sign-in can be added by implementing the same token provider path and passing Apple ID tokens to the gateway.

The app should not store provider-specific objects in UI state.

## Supabase Setup Requirements

Google sign-in requires external setup:

- Create Android OAuth client ID with the package name and SHA-1 fingerprints for debug and release.
- Register the Google client IDs in the Supabase Dashboard Google provider settings.
- Configure OAuth consent screen branding, privacy policy, and required scopes.
- For iOS support later, create an iOS OAuth client and configure bundle ID, URL schemes, and Supabase provider settings.

Email/password requires enabling the email provider in Supabase Auth settings. Password reset requires a valid redirect URL configuration if deep-link completion is added.

## Security

- Use only Supabase anon or publishable keys in the app.
- Never ship service-role or secret keys in client code.
- Do not make authorization decisions from user-editable metadata.
- If user-owned Supabase tables are introduced later, enable RLS and use policies based on `auth.uid()`.
- Keep auth errors user-safe in UI while logging technical details through Napier where appropriate.
- Keep session persistence inside the Supabase SDK/session storage path rather than manually storing tokens in app settings.

## Testing

Common tests will cover:

- auth domain mapping from session/user into UI-safe models
- Settings profile state for guest users
- Settings profile state for authenticated users
- email validation and password validation
- loading and error transitions for sign in, sign up, reset password, Google sign in, and sign out

Platform Google sign-in will need Android integration verification on an emulator or device after OAuth client IDs are configured.

Build verification will include the closest available Gradle compile task, expected to be `./gradlew :composeApp:compileDebugKotlinAndroid`.

## Open Operational Requirements

The code can be implemented before all external Supabase/Google dashboard settings are complete, but Google sign-in cannot succeed at runtime until OAuth provider configuration is finished.

The implementation should include clear typed errors for unavailable provider setup so the Settings UI can fail gracefully during development.
