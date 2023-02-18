package dev.viesoft.paperkit.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeclareFileOrDefaultsConfig(
    val name: String
)
