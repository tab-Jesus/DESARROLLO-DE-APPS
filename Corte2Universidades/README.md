# Corte 2 – Universidades App
**Estudiante:** JESUS DAVID CAMPO YUNES  
**Código:** 7502410028  
**Ejercicio:** #2 – Tabla Universidades  
**Tecnología:** Android Studio (Kotlin) + Supabase + EmailJS

---

## Descripción
Aplicación Android para gestionar Universidades y Carreras con:
- Login con sesión persistente (SharedPreferences)
- Recuperación de contraseña por email (EmailJS)
- CRUD completo de Universidades y Carreras
- 4 Reportes parametrizados (2 por tabla)
- 2 Sensores: Acelerómetro (agitar = recargar lista) y Proximidad (mano cerca = mostrar total)

---

## PASO 1 – Configurar Supabase

1. Ve a https://supabase.com y crea una cuenta gratuita.
2. Clic en **"New project"** → dale un nombre (ej: `corte2-universidades`) → elige región → crea.
3. Ve a **SQL Editor** (panel izquierdo) → pega el contenido del archivo `supabase_setup.sql` → clic **Run**.
4. Ve a **Settings > API** y copia:
   - **Project URL** → será algo como `https://abcdefgh.supabase.co`
   - **anon public key** → clave larga que empieza con `eyJ...`
5. Abre el archivo:
   ```
   app/src/main/java/com/tuuniversidad/corte2universidades/utils/Constants.kt
   ```
   Y reemplaza:
   ```kotlin
   const val SUPABASE_URL = "https://TU_PROYECTO.supabase.co/rest/v1/"
   const val SUPABASE_ANON_KEY = "TU_ANON_KEY_AQUI"
   ```
   con tus valores reales. **Asegúrate de que la URL termine en `/rest/v1/`**

---

## PASO 2 – Configurar EmailJS

1. Ve a https://www.emailjs.com y crea una cuenta gratuita (plan Free: 200 emails/mes).
2. **Conectar servicio de email:**
   - Ve a **Email Services** → **Add New Service**
   - Elige **Gmail** (o cualquier proveedor) → conecta tu cuenta → anota el **Service ID** (ej: `service_abc123`)
3. **Crear plantilla:**
   - Ve a **Email Templates** → **Create New Template**
   - Diseña el email así:
     ```
     Subject: Tu nueva contraseña - {{app_name}}
     
     Hola {{to_name}},
     
     Tu nueva contraseña es: {{new_password}}
     
     Ingresa con tu email y esta contraseña.
     Por seguridad, cámbiala después de iniciar sesión.
     ```
   - En el campo **To Email** escribe: `{{to_email}}`
   - Guarda y anota el **Template ID** (ej: `template_xyz789`)
4. **Obtener Public Key:**
   - Ve a **Account > General** → copia tu **Public Key** (ej: `user_XXXXXXXXXX`)
5. Actualiza `Constants.kt`:
   ```kotlin
   const val EMAILJS_SERVICE_ID  = "service_abc123"
   const val EMAILJS_TEMPLATE_ID = "template_xyz789"
   const val EMAILJS_USER_ID     = "user_XXXXXXXXXX"
   ```

---

## PASO 3 – Configurar el proyecto en Android Studio

1. Abre **Android Studio** (versión Hedgehog 2023.1.1 o superior recomendada).
2. **File > Open** → selecciona la carpeta `Corte2Universidades`.
3. Espera que Gradle sincronice (puede tardar 2–5 minutos en la primera vez).
4. Si hay error de Gradle, ve a **File > Invalidate Caches > Invalidate and Restart**.
5. Asegúrate de tener internet activo (necesita descargar dependencias).

---

## PASO 4 – Crear emulador y ejecutar

1. Ve a **Device Manager** (ícono de teléfono en la barra derecha) → **Create Device**.
2. Elige **Pixel 6** (o similar) → Next → descarga **Android 13 (API 33)** → Next → Finish.
3. Inicia el emulador con el botón ▶.
4. Cuando el emulador esté encendido, presiona **Run 'app'** (Shift+F10).

---

## PASO 5 – Probar los sensores en el emulador

### Acelerómetro (agitar para recargar lista):
1. Con la app corriendo en el emulador, ve a la pantalla de **Universidades**.
2. En Android Studio, abre el panel **Extended Controls** del emulador (ícono `...` en la barra lateral del emulador).
3. Ve a **Virtual sensors > Accelerometer**.
4. Mueve los sliders de X/Y/Z rápidamente hacia valores extremos (ej: de 0 a 20 y de vuelta).
5. Verás el Toast **"¡Agitación detectada! Recargando lista..."** y la lista se actualizará.

### Proximidad (mostrar total de universidades):
1. En el panel **Extended Controls** del emulador.
2. Ve a **Virtual sensors > Additional sensors > Proximity**.
3. Mueve el slider a un valor **menor a 5** (simula la mano cerca).
4. Verás el Toast **"Total de universidades registradas: X"**.

### En dispositivo físico:
- **Agitar:** simplemente sacude el teléfono.
- **Proximidad:** cubre el sensor de proximidad (esquina superior del teléfono) con la mano.

---

## PASO 6 – Subir a GitHub (comandos Git)

```bash
# Desde la carpeta del proyecto
git init
git add .
git commit -m "Corte 2 - App Universidades con Kotlin y Supabase"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/corte2-universidades.git
git push -u origin main
```

Para generar el APK:
- **Build > Generate Signed Bundle/APK > APK** → sigue el asistente.
- El APK queda en `app/release/app-release.apk`.
- Agréga lo al repo: `git add app/release/app-release.apk && git commit -m "APK" && git push`

---

## Estructura del proyecto

```
Corte2Universidades/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/tuuniversidad/corte2universidades/
│   │   ├── api/
│   │   │   ├── ApiService.kt          ← Endpoints Supabase (Retrofit)
│   │   │   └── RetrofitClient.kt      ← Configuración HTTP
│   │   ├── models/
│   │   │   ├── Usuario.kt
│   │   │   ├── Universidad.kt
│   │   │   └── Carrera.kt
│   │   ├── adapters/
│   │   │   ├── UniversidadAdapter.kt  ← RecyclerView universidades
│   │   │   └── CarreraAdapter.kt      ← RecyclerView carreras
│   │   ├── activities/
│   │   │   ├── LoginActivity.kt       ← Login + recuperar clave
│   │   │   ├── MenuActivity.kt        ← Menú principal
│   │   │   ├── UniversidadesActivity.kt ← Lista + sensores
│   │   │   ├── UniversidadFormActivity.kt ← Formulario crear/editar
│   │   │   ├── CarrerasActivity.kt    ← Lista carreras
│   │   │   ├── CarreraFormActivity.kt ← Formulario carreras
│   │   │   └── ReportesActivity.kt    ← 4 reportes parametrizados
│   │   └── utils/
│   │       ├── Constants.kt           ← ⚠️ CONFIGURA AQUÍ TUS KEYS
│   │       └── EmailJSHelper.kt       ← Envío de email
│   └── res/layout/                    ← Todas las pantallas XML
├── supabase_setup.sql                 ← SQL para crear tablas
└── README.md                          ← Este archivo
```

---

## Cómo funciona el código (para tu video)

### Login
`LoginActivity` toma email/password, hace un GET a Supabase con filtros `eq.valor` y verifica si existe ese usuario. Si existe, guarda la sesión en `SharedPreferences` y navega al menú.

### Recuperar contraseña
Genera una clave de 6 dígitos con `Random`, hace PATCH en Supabase para actualizar el campo `password`, luego llama a la API REST de EmailJS con un POST JSON incluyendo `service_id`, `template_id` y los parámetros del template.

### CRUD Universidades/Carreras
Usa **Retrofit** para hacer llamadas HTTP a la API REST de Supabase. Los filtros de Supabase funcionan como query params: `?id=eq.5`, `?pais=eq.Colombia`, `?numeroCarreras=gte.10`. Para crear usa POST, editar usa PATCH, eliminar usa DELETE.

### Sensores
`SensorManager` registra listeners en `onResume()` y los desregistra en `onPause()`. El acelerómetro calcula la magnitud del vector de aceleración y si supera el umbral de 15 m/s², ejecuta el reload. El sensor de proximidad dispara el Toast cuando la distancia < 5 cm.

### Reportes
Los 4 reportes reutilizan los mismos adapters del CRUD pero con endpoints filtrados. El layout muestra/oculta secciones según el reporte seleccionado.

---

## Datos de prueba por defecto (creados por el SQL)
- **Email:** admin@test.com  
- **Password:** 123456
