# EV_SUM_2

📌 **Descripción general del proyecto**
Aplicación móvil Android desarrollada con Kotlin y Jetpack Compose. Implementa funcionalidades en la nube utilizando Firebase (Authentication y Firestore) y aprovecha sensores del dispositivo para entrada de datos por voz y geolocalización avanzada. La aplicación está diseñada bajo el patrón Service-Repository para una clara separación de responsabilidades y un fácil mantenimiento.

🏗 **Arquitectura del proyecto**
El proyecto sigue una arquitectura funcional organizada en capas, asegurando bajo acoplamiento:

- **data**: Contiene los `Repositories` (`AuthRepository`, `PhraseRepository`, `LocationRepository`) encargados de interactuar de forma asíncrona con Firebase y los servicios de Google Cloud (FMS).
- **domain**: Contiene los modelos de negocio puros (`AppUser`, `Phrase`, `DeviceLocation`) y la lógica de validación desacoplada del framework (`SpeechNormalization.kt`, `Validators.kt`).
- **services**: Actúa como delegador y envoltorio del hardware del dispositivo. Contiene clases de control como `LocationService`, `SpeechController` y `TextToSpeechController`.
- **ui**: Toda la capa visual creada con **Jetpack Compose**. Contiene las pantallas agrupadas lógicamente (`auth`, `home`, `location`) y el enrutamiento principal ubicado en `AppNavGraph.kt` y `RouterScreen.kt`.

📦 **Tecnologías utilizadas**

- **Kotlin 1.9+** & **Jetpack Compose** (Material Design 3).
- **Firebase Authentication** (Gestión de usuarios y flujos de sesión).
- **Firebase Firestore** (Base de datos NoSQL escalable).
- **Google Play Services Location** (`FusedLocationProviderClient` y `Geocoder`).
- **Jetpack Navigation Compose** (`NavGraph` y ciclo de vida de navegación).
- **Kotlinx Coroutines** & **Flow** (Programación asíncrona estructurada y estado reactivo sin bloqueos del hilo UI).

🔐 **Autenticación**
El proyecto incluye un flujo de seguridad administrado integralmente desde `AuthRepository`:

- Inicio de sesión, creación de cuentas y flujos para recuperación de contraseñas.
- Persistencia mediante `AuthStateListener` encapsulado en un `Flow` (`authStateFlow`), por lo cual la sesión se mantiene activa a lo largo de reinicios de la aplicación.
- Redirección automática: El componente raíz `RouterScreen` evalúa en tiempo real si existe sesión, llevando al usuario automáticamente hacia la ruta de Login (si no hay sesión) o Home (si está activo).

🎤 **Dictado por voz y normalización (Speech-to-Text)**
Integración en la interfaz que permite al usuario dictar datos complejos como el correo y la contraseña.

- **Normalización lingüística**: Algoritmos interceptan la cadena hablada en tiempo real. Textos como "arroba", "punto", "guion bajo" o "y griega" se traducen como `@`, `.`, `_`, `y`. Las palabras numéricas habladas (ej. "uno") son convertidas fielmente a `1`.
- Para lograr un alto desempeño, los métodos normales han sido abstraídos a la capa `domain.validators` favoreciendo el testing rápido.

☁ **Firestore (Persistencia de frases)**
Implementación funcional con el servicio Cloud Firestore para mantener una biblioteca de "frases rápidas":

- **CRUD Completo**: Capacidad para crear, leer, editar y eliminar frases (`add`, `get`, `update`, `delete`).
- **Consultas Ordenadas**: Listado automático ordenado inversamente (de la frase más reciente a la más vieja) mediante timestamps milisegundos (`createdAt`).

📍 **Geolocalización y Reverse Geocoding**

- Extracción precisa de latitud y longitud a través de `LocationRepository` (el cual delega al `FusedLocationProviderClient`).
- **Reverse Geocoding**: El servicio convierte las coordenadas espaciales puras en un formato comprensible (por ejemplo, calle y ciudad) utilizando el componente `Geocoder` con mitigación para la API nivel 33 o superior.
- **Copiado al portapapeles**: Posibilidad expedita para extraer la coordenada o su dirección escrita.

🗣 **Text-to-Speech (TTS)**

- Controlador propio e independiente (`TextToSpeechController`) diseñado para acoplarse y sintetizar la voz.
- Pensado para la accesibilidad y proactividad temporal, con lenguaje configurado primordialmente a "es-CL". Las frases en el panel pueden ser ejecutadas directamente permitiendo al dispositivo recitarlas y vaciar colas antiguas para evitar enredos de audio (`QUEUE_FLUSH`).

🧪 **Testing**

- Pruebas unitarias desarrolladas bajo **JUnit**.
- La cobertura actual recae en garantizar el 100% de eficacia en la evaluación oral: `SpeechNormalizersTest.kt`, probando combinaciones fonéticas que un usuario podría utilizar al dictar sin un teclado manual, asegurando un pase limpio a los repositorios.

📲 **Permisos requeridos**
Todas las protecciones se controlan en tiempo de ejecución tras ser declaradas en el `AndroidManifest.xml`:

- `RECORD_AUDIO`: Habilitación indispensable para captar la entrada mediante micrófono en el dictado.
- `ACCESS_COARSE_LOCATION` / `ACCESS_FINE_LOCATION`: Autorización de GPS (preciso/aproximado) de Google Play Services antes de emitir un lat/lng exitoso.

🚀 **Cómo ejecutar el proyecto**

1. Clona el repositorio.
2. Ábrelo utilizando **Android Studio**.
3. (Prerrequisito) Asegúrate de tener válido e incorporado el archivo `google-services.json` que conecte a un clúster válido de Firebase.
4. Sincroniza Gradle (`Sync Project with Gradle Files`).
5. Debido a los servicios de Speech-to-Text y dictado, **se recomienda el uso de un celular físico** en vez de un emulador.
6. Haz clic sobre **Run 'app'** en la barra superior.

⚠ **Limitaciones conocidas**

- El motor de reconocimiento dictado de Google es susceptible al ruido ambiente extremo. Para emuladores, comúnmente lanza fallas u omisiones (`RecognitionListener` err) si el Play Store carece de actualizaciones internas.

📁 **Estructura del proyecto (Resumen)**

```
app/src/main/java/com/demodogo/ev_sum_2/
├── data/
│   ├── firebase/ (Configuración inicial)
│   ├── repositories/ (Coroutines y accesos)
│   └── session/ (Almacenamiento genérico DataStore)
├── domain/
│   ├── errors/ (Dominio puro)
│   ├── models/ (Clases de recolección temporal AppUser, DeviceLocation)
│   └── validators/ (Normalizadores orales)
├── services/ (Capa interconexión - Auth, Speech, TTS, Locations)
└── ui/
    ├── auth/ (Interfaces de control general de puerta al sistema)
    ├── home/ (Gestión y tableros CRUD centralizados)
    ├── location/ (Feedback para el componente FusedLocation)
    ├── nav/ (NavHost y RouterScreen)
    └── theme/ (Configurador Material3)
```

🧾 **Definition of Done (Alcance final implementado)**

- [x] Construcción final de módulos bajo el patrón _Service - Repository_ asegurados frente al hilo principal (Coroutines dispatcher implementado).
- [x] Autenticación validada exitosamente con Firestore y AuthStateListener, suprimiendo necesidad de relogging molesto.
- [x] CRUD de textos/frases probado en Firebase bajo aislamiento por documento local a `FirebaseAuth` UID.
- [x] Controladores nativos (TextToSpeech y FusedLocation) operando bajo la encapsulación Try-Catch requerida contra `SecurityException` y bloqueos hardware.
- [x] Tests unitarios (`SpeechNormalizersTest.kt`) sobre casos críticos de formato y espacios de dictado.
