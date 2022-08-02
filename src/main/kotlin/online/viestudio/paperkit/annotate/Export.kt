package online.viestudio.paperkit.annotate

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Export(
    val clazz: KClass<out Any>
)
