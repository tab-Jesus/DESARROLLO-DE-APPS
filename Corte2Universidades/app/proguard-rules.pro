# Archivo: app/proguard-rules.pro
-keep class com.tuuniversidad.corte2universidades.models.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn retrofit2.**
