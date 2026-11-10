# Keep Retrofit service interface and its method annotations for runtime reflection.
-keep,allowobfuscation interface org.aurral.firetv.data.api.SubsonicApi
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepclasseswithmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# kotlinx.serialization keeps generated serializers (bundled rules cover most cases).
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class **$$serializer {
    *** INSTANCE;
}

# Media3
-dontwarn androidx.media3.**
