package online.viestudio.paperkit.annotate

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeclareFileOrDefaultsConfig(
    val name: String
)