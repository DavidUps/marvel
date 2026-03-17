# Android Expert Reference — CLAUDE.md

> Stack técnico · Compose · Clean Architecture · MVI · Coroutines · Flow
> Refleja la arquitectura y convenciones reales del proyecto.

---

## 1. Stack Tecnológico Core

### Lenguaje & Runtime
- **Kotlin 2.x** — lenguaje principal, sin Java nuevo
- **Kotlin Coroutines** — concurrencia estructurada
- **Kotlin Flow** — streams reactivos
- **KSP** (Kotlin Symbol Processing) — en lugar de KAPT

### UI
- **Jetpack Compose** — UI declarativa, sin XML
- **Material Design 3** — sistema de diseño
- **Navigation Compose** — navegación type-safe
- **Adaptive Layouts** — soporte foldables y tablets

### Arquitectura
- **Clean Architecture** — capas Data / Domain / Presentation
- **MVI** — patrón de presentación
- **Multi-module por feature** — `app` · `core` · `features/*`

### Inyección de Dependencias
- **Hilt** — DI sobre Dagger, integrado con Jetpack
- Cada feature expone su propio módulo en `di/`

### Networking
- **Retrofit 2** + **OkHttp 4**
- **Kotlinx Serialization** — en lugar de Gson/Moshi
- **Coil 3** — imágenes asíncronas nativas Compose

### Persistencia
- **Room** — base de datos local (con KSP)
- **DataStore Preferences** — en lugar de SharedPreferences

### Testing
- **JUnit 5** + **Mockk** + **Turbine**
- **Compose UI Testing** + **Hilt Testing**

### CI/CD & Calidad
- **Detekt** + **ktlint**
- **GitHub Actions / Firebase App Distribution**

---

## 2. Estructura de Módulos

```
root/
├── app/                         # Entry point, Application, NavHost principal
│
├── core/
│   ├── functional/              # Either, Failure, extensiones FP
│   ├── extensions/              # Extensiones Kotlin generales
│   ├── network/                 # Retrofit setup, OkHttp, interceptors, DTOs base
│   ├── database/                # Room setup, migrations
│   └── ui/                      # Componentes Compose compartidos, tema, tokens
│
└── features/
    └── login/                   # Módulo autocontenido por feature
        ├── data/
        │   ├── service/         # Retrofit API interface + llamadas remotas
        │   ├── local/           # Room DAOs + DataStore
        │   └── repository/      # Impl del repositorio (mapea DTO → Domain)
        │
        ├── di/                  # Módulo Hilt del feature (bindings + provides)
        │
        └── domain/
            ├── model/           # Entidades de dominio (data class puras, sin Android)
            ├── repository/      # Interface del repositorio
            └── usecase/         # Un fichero por caso de uso
```

> **Nota:** La capa `presentation/` (ViewModels + Screens) vive dentro de `app/`
> o en un módulo `:feature:login:ui` en proyectos grandes. En proyectos medianos,
> dentro de `app/` organizado por feature es suficiente.

### Regla de dependencias
```
Presentation → Domain ← Data
                ↑
              core/*
```
- **Domain** — cero dependencias de Android SDK. Solo Kotlin puro.
- **Data** — implementa interfaces de Domain, conoce Retrofit/Room.
- **Presentation** — consume UseCases de Domain, nunca toca Data directamente.
- **core** — lo usan todos, no depende de ningún feature.

---

## 3. Either — Manejo Funcional de Errores

En lugar de `kotlin.Result`, se usa `Either<L, R>` del paquete `core.functional`.
`Left` representa error, `Right` representa éxito (convención FP right-biased).

### La clase

```kotlin
// core/functional/Either.kt
package com.davidups.core.functional

sealed class Either<out L, out R> {
    data class Left<out L>(val error: L) : Either<L, Nothing>()
    data class Right<out R>(val success: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft  get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)

    fun fold(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Left  -> fnL(error)
            is Right -> fnR(success)
        }
}
```

### Extensiones FP

```kotlin
// core/extensions/EitherExtensions.kt
package com.davidups.core.extensions

import com.davidups.core.functional.Either

/** Compone 2 funciones: f.c(g) == g(f(x)) */
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = { f(this(it)) }

/** Right-biased flatMap — opera sobre Right, propaga Left sin tocarlo */
inline fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left  -> Either.Left(error)
        is Either.Right -> fn(success)
    }

fun <T, L, R> Either<L, R>.flatMapLeft(fn: (L) -> Either<T, R>): Either<T, R> =
    when (this) {
        is Either.Left  -> fn(error)
        is Either.Right -> Either.Right(success)
    }

fun <T, L, R> Either<L, R>.mapSuccess(fn: (R) -> T): Either<L, T> =
    this.flatMap(fn.c(::right))

fun <T, L, R> Either<L, R>.mapError(fn: (L) -> T): Either<T, R> =
    this.flatMapLeft(fn.c(::left))

/** Right(12).getOrElse(17) → 12  ·  Left(12).getOrElse(17) → 17 */
fun <L, R> Either<L, R>.getOrElse(value: R): R =
    when (this) {
        is Either.Left  -> value
        is Either.Right -> success
    }

/** Ejecuta fn si es Left, devuelve el Either original para seguir encadenando */
inline fun <L, R> Either<L, R>.onFailure(fn: (failure: L) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Left) fn(error) }

/** Ejecuta fn si es Right, devuelve el Either original para seguir encadenando */
inline fun <L, R> Either<L, R>.onSuccess(fn: (success: R) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Right) fn(success) }

/** Si el Right contiene null, delega en el bloque default */
inline fun <L, R, T> Either<L, R?>.leftIfNull(
    default: () -> Either<L, T>,
    mapper: (R) -> T,
): Either<L, T> = flatMap {
    if (it == null) {
        when (val data = default()) {
            is Either.Left<*>  -> Either.Left(data.error as L)
            is Either.Right<*> -> Either.Right(data.success as T)
        }
    } else {
        Either.Right(mapper(it))
    }
}
```

### Failure — jerarquía de errores de dominio

```kotlin
// core/functional/Failure.kt
sealed class Failure {
    data object NetworkError : Failure()
    data object UnauthorizedError : Failure()
    data class ServerError(val code: Int) : Failure()
    data class UnexpectedError(val cause: Throwable? = null) : Failure()
    // Cada feature puede definir sus propios errores de dominio
}
```

### Uso por capas

```kotlin
// Domain — interfaz del repositorio (en features/login/domain/repository/)
interface AuthRepository {
    suspend fun login(email: String, password: String): Either<Failure, User>
    fun observeCurrentUser(): Flow<User?>
}

// Domain — UseCase
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Either<Failure, User> =
        authRepository.login(email, password)
}

// Data — implementación del repositorio
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Either<Failure, User> =
        withContext(ioDispatcher) {
            try {
                val dto = service.login(LoginRequest(email, password))
                Either.Right(dto.toDomain())
            } catch (e: CancellationException) {
                throw e                                    // nunca atrapar CancellationException
            } catch (e: HttpException) {
                Either.Left(Failure.ServerError(e.code()))
            } catch (e: IOException) {
                Either.Left(Failure.NetworkError)
            } catch (e: Exception) {
                Either.Left(Failure.UnexpectedError(e))
            }
        }
}

// Presentation — ViewModel con onSuccess / onFailure encadenados
private fun login() {
    viewModelScope.launch {
        reduce { copy(isLoading = true, error = null) }

        loginUseCase(_uiState.value.email, _uiState.value.password)
            .onSuccess {
                reduce { copy(isLoading = false) }
                _effect.send(LoginEffect.NavigateToHome)
            }
            .onFailure { failure ->
                reduce { copy(isLoading = false, error = failure.toMessage()) }
            }
    }
}
```

---

## 4. MVI — Modelo Vista Intención

### Contrato del patrón

```kotlin
// UiState — inmutable, representa todo lo que ve el usuario
data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val error: String? = null,
)

// UiIntent — lo que el usuario quiere hacer
sealed interface LoginIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object LoginClicked : LoginIntent
    data object ErrorDismissed : LoginIntent
}

// UiEffect — efectos de un solo disparo (navegación, snackbar...)
sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
```

### ViewModel MVI

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged    -> reduce { copy(email = intent.email) }
            is LoginIntent.PasswordChanged -> reduce { copy(password = intent.password) }
            is LoginIntent.LoginClicked    -> login()
            is LoginIntent.ErrorDismissed  -> reduce { copy(error = null) }
        }
    }

    private fun login() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, error = null) }

            loginUseCase(_uiState.value.email, _uiState.value.password)
                .onSuccess {
                    reduce { copy(isLoading = false) }
                    _effect.send(LoginEffect.NavigateToHome)
                }
                .onFailure { failure ->
                    reduce { copy(isLoading = false, error = failure.toMessage()) }
                }
        }
    }

    private inline fun reduce(block: LoginUiState.() -> LoginUiState) {
        _uiState.update { it.block() }
    }
}
```

### Screen + Content separados

```kotlin
// Screen — sabe del ViewModel, gestiona efectos, no tiene lógica de layout
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is LoginEffect.NavigateToHome -> onNavigateToHome()
            is LoginEffect.ShowSnackbar  -> { /* snackbarHostState.showSnackbar(effect.message) */ }
        }
    }

    LoginContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

// Content — sin ViewModel, 100% previewable con @Preview
@Composable
internal fun LoginContent(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            EmailField(
                value = uiState.email,
                onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
            )
            PasswordField(
                value = uiState.password,
                onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            )
            LoginButton(
                isLoading = uiState.isLoading,
                onClick = { onIntent(LoginIntent.LoginClicked) },
            )
            AnimatedVisibility(visible = uiState.error != null) {
                ErrorBanner(
                    message = uiState.error.orEmpty(),
                    onDismiss = { onIntent(LoginIntent.ErrorDismissed) },
                )
            }
        }
    }
}
```

---

## 5. DI por Feature — Módulo `di/`

Cada feature expone su propio módulo Hilt dentro de `features/login/di/`.
El módulo `app/` solo instala módulos de `core/`. Los features se instalan solos.

```kotlin
// features/login/di/LoginModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
object LoginNetworkModule {

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)
}
```

```kotlin
// core/di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
}

// core/di/DispatcherModule.kt
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class IoDispatcher
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides @IoDispatcher fun provideIo(): CoroutineDispatcher = Dispatchers.IO
    @Provides @DefaultDispatcher fun provideDefault(): CoroutineDispatcher = Dispatchers.Default
}
```

---

## 6. Data Layer — Service y Local

### Service (Retrofit)

```kotlin
// features/login/data/service/AuthService.kt
interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): TokenResponse
}
```

### Local (Room)

```kotlin
// features/login/data/local/UserDao.kt
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUser(id: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
```

### Repository — mapea y encapsula todo en Either

```kotlin
// features/login/data/repository/AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val userDao: UserDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Either<Failure, User> =
        withContext(ioDispatcher) {
            try {
                val dto = service.login(LoginRequest(email, password))
                val user = dto.toDomain()
                userDao.saveUser(user.toEntity())
                Either.Right(user)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                Either.Left(Failure.ServerError(e.code()))
            } catch (e: IOException) {
                Either.Left(Failure.NetworkError)
            } catch (e: Exception) {
                Either.Left(Failure.UnexpectedError(e))
            }
        }

    override fun observeCurrentUser(): Flow<User?> =
        userDao.observeUser(SessionManager.userId)
            .map { entity -> entity?.toDomain() }
            .flowOn(ioDispatcher)
}
```

---

## 7. Domain Layer — UseCases

```kotlin
// features/login/domain/usecase/LoginUseCase.kt
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Either<Failure, User> {
        if (!email.isValidEmail()) return Either.Left(Failure.UnexpectedError())
        if (password.length < 8)  return Either.Left(Failure.UnexpectedError())
        return authRepository.login(email, password)
    }
}
```

Reglas de los UseCases:
- Un fichero, una responsabilidad.
- `operator fun invoke()` — se llama como función: `loginUseCase(email, pass)`.
- Sin imports de Android SDK. Solo Kotlin + Domain models.
- Devuelven `Either<Failure, T>`, nunca lanzan excepciones.

---

## 8. Gestión del Ciclo de Vida en Compose

### collectAsStateWithLifecycle — siempre, nunca collectAsState

```kotlin
// ✅ Deja de colectar cuando la app va a background
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ Sigue colectando aunque la app esté en background
val uiState by viewModel.uiState.collectAsState()
```

### Efectos de Compose

```kotlin
// LaunchedEffect — coroutina al entrar en composición, cancelada al salir
LaunchedEffect(userId) {
    viewModel.loadUser(userId)          // se relanza si cambia userId
}

// DisposableEffect — registrar/desregistrar recurso con limpieza garantizada
val lifecycleOwner = LocalLifecycleOwner.current
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> viewModel.onResume()
            Lifecycle.Event.ON_PAUSE  -> viewModel.onPause()
            else                      -> Unit
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
}

// SideEffect — sincronizar con sistema externo en cada recomposición exitosa
SideEffect {
    analytics.setCurrentScreen(screenName)
}

// rememberUpdatedState — lambda actualizado dentro de efecto de larga vida
@Composable
fun Timer(onTimeout: () -> Unit) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        delay(30_000L)
        currentOnTimeout()     // siempre llama a la versión más reciente
    }
}

// rememberCoroutineScope — coroutinas desde callbacks de UI (onClick...)
val scope = rememberCoroutineScope()
Button(onClick = {
    scope.launch { snackbarHostState.showSnackbar("Guardado") }
}) { Text("Guardar") }
```

### ObserveEffect — helper reutilizable en core/ui

```kotlin
@Composable
fun <T> ObserveEffect(
    flow: Flow<T>,
    onEffect: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(onEffect)
        }
    }
}
```

### Tabla resumen de efectos

| Efecto                   | Cuándo usarlo                                         | Se relanza si…     |
|--------------------------|-------------------------------------------------------|--------------------|
| `LaunchedEffect(key)`    | Coroutina al entrar en composición                    | Cambia la key      |
| `DisposableEffect(key)`  | Registrar/desregistrar recurso con limpieza           | Cambia la key      |
| `SideEffect`             | Sincronizar con sistema externo en cada recomposición | Siempre            |
| `rememberUpdatedState`   | Capturar valor actualizado en efecto de larga vida    | No aplica          |
| `rememberCoroutineScope` | Coroutinas desde eventos de usuario                   | No (scope estable) |
| `derivedStateOf`         | Estado derivado, evitar recomposiciones extra         | No aplica          |

### derivedStateOf — evitar recomposiciones innecesarias

```kotlin
// ❌ Recalcula en cada cambio de scroll aunque el booleano no cambie
val showButton = listState.firstVisibleItemIndex > 0

// ✅ Solo recompone cuando cambia el resultado booleano
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}
```

### Antipatrones a evitar

```kotlin
// ❌ Coroutina suelta en el cuerpo — se ejecuta en cada recomposición
@Composable fun Bad() {
    CoroutineScope(Dispatchers.IO).launch { fetchData() }
}

// ❌ Estado sin remember — se resetea en cada recomposición
@Composable fun Bad() {
    var count = 0
    Button(onClick = { count++ }) { Text("$count") }
}

// ✅ Estado correcto
@Composable fun Good() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) { Text("$count") }
}

// ❌ GlobalScope en cualquier punto
GlobalScope.launch { /* nunca */ }
```

---

## 9. Coroutinas — Gestión Correcta

### Scopes

| Contexto             | Scope                     | Nota                              |
|----------------------|---------------------------|-----------------------------------|
| ViewModel            | `viewModelScope`          | Se cancela con el ViewModel       |
| Repository / UseCase | Dispatcher inyectado      | Nunca crear scope propio          |
| WorkManager          | `CoroutineWorker`         | Scope gestionado por WorkManager  |
| Service              | `CoroutineScope` propio   | Cancelar en `onDestroy`           |
| Test                 | `runTest`                 | Con `UnconfinedTestDispatcher`    |

### Dispatchers

```kotlin
withContext(Dispatchers.IO)      { service.login(request) }    // red, disco, BD
withContext(Dispatchers.Default) { processBitmap(bitmap) }     // CPU-intensivo
// Main solo si es estrictamente necesario — StateFlow ya emite en Main
```

### Reglas críticas

```kotlin
// ✅ Siempre relanzar CancellationException
try {
    repository.getData()
} catch (e: CancellationException) {
    throw e                             // obligatorio
} catch (e: IOException) {
    Either.Left(Failure.NetworkError)
}

// ✅ Paralelismo estructurado con async
suspend fun loadDashboard() = coroutineScope {
    val user  = async { userRepository.getUser() }
    val stats = async { statsRepository.getStats() }
    Dashboard(user.await(), stats.await())
}

// ✅ Timeout explícito
withTimeout(10_000L) { api.fetchData() }
```

---

## 10. Flow

```kotlin
// Repository — stream offline-first con Either
fun observeProducts(): Flow<Either<Failure, List<Product>>> =
    localDataSource.observeProducts()
        .map { entities ->
            if (entities.isEmpty()) Either.Left(Failure.NetworkError)
            else Either.Right(entities.map(mapper::toDomain))
        }
        .catch { emit(Either.Left(Failure.UnexpectedError(it))) }
        .flowOn(Dispatchers.IO)

// ViewModel — combinar múltiples flows en un único StateFlow
val uiState: StateFlow<HomeUiState> = combine(
    userRepository.observeCurrentUser(),
    settingsRepository.observeTheme(),
) { user, theme ->
    HomeUiState(user = user, theme = theme)
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = HomeUiState(),
)
```

---

## 11. Principios SOLID aplicados

| Principio | Aplicación concreta                                                                  |
|-----------|--------------------------------------------------------------------------------------|
| **S**RP   | Un UseCase = una acción. Un Composable = un propósito. Un Repository = un dominio   |
| **O**CP   | Interfaces en `domain/repository/`; nuevas impl sin tocar contratos existentes       |
| **L**SP   | `AuthRepositoryImpl` y `FakeAuthRepository` (tests) son intercambiables              |
| **I**SP   | Interfaces de repositorio granulares, no una única megainterfaz                      |
| **D**IP   | ViewModel → UseCase (interfaz). Hilt resuelve la impl. Data nunca expuesta directa  |

---

## 12. Testing

### UseCase con Either

```kotlin
class LoginUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val sut = LoginUseCase(authRepository)

    @Test
    fun `given valid credentials, returns Right with User`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Either.Right(fakeUser)

        val result = sut("user@test.com", "password123")

        assertTrue(result.isRight)
        assertEquals(fakeUser, (result as Either.Right).success)
    }

    @Test
    fun `given network error, returns Left with NetworkError`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Either.Left(Failure.NetworkError)

        val result = sut("user@test.com", "password123")

        assertTrue(result.isLeft)
        assertEquals(Failure.NetworkError, (result as Either.Left).error)
    }
}
```

### ViewModel con Turbine

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class LoginViewModelTest {
    private val loginUseCase = mockk<LoginUseCase>()
    private lateinit var viewModel: LoginViewModel

    @BeforeEach fun setup() { viewModel = LoginViewModel(loginUseCase) }

    @Test
    fun `on login success, emits NavigateToHome effect`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Either.Right(fakeUser)

        viewModel.effect.test {
            viewModel.onIntent(LoginIntent.LoginClicked)
            assertEquals(LoginEffect.NavigateToHome, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on network error, uiState shows error message`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Either.Left(Failure.NetworkError)

        viewModel.uiState.test {
            awaitItem()                            // estado inicial
            viewModel.onIntent(LoginIntent.LoginClicked)
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## 13. Versiones de Referencia (2025)

```toml
# libs.versions.toml
[versions]
kotlin                = "2.1.0"
ksp                   = "2.1.0-1.0.29"
compose-bom           = "2025.02.00"
hilt                  = "2.55"
room                  = "2.7.0"
retrofit              = "2.11.0"
okhttp                = "4.12.0"
kotlinx-serialization = "1.8.0"
coroutines            = "1.10.1"
lifecycle             = "2.9.0"
navigation            = "2.9.0"
datastore             = "1.1.3"
coil                  = "3.1.0"
turbine               = "1.2.0"
mockk                 = "1.14.0"
detekt                = "1.23.8"

[libraries]
compose-bom           = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
hilt-android          = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler         = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
room-runtime          = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx              = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler         = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
retrofit-core         = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
turbine               = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
mockk                 = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
```

---

## 14. Checklist Pre-PR

- [ ] Repositorio devuelve `Either<Failure, T>`, nunca lanza excepciones
- [ ] `CancellationException` siempre re-lanzada en catch genéricos
- [ ] `flowOn(Dispatchers.IO)` en DataSource/Repository, no en ViewModel
- [ ] Estados UI inmutables (`data class` + `reduce { copy(...) }`)
- [ ] UiEffects en `Channel`, nunca en `StateFlow`
- [ ] `collectAsStateWithLifecycle()` en todos los Composables
- [ ] Screen y Content separados — Content previewable sin ViewModel
- [ ] UseCases sin imports de Android SDK
- [ ] Módulo `di/` dentro del feature, no en `app/`
- [ ] Test por cada rama del UseCase (Right + cada Left posible)
- [ ] Sin lógica de negocio en Composables ni en ViewModels (solo orquestar)
- [ ] Sin `GlobalScope`

---

*Arquitectura personal de David · Clean Architecture · MVI · Either · Jetpack Compose*
