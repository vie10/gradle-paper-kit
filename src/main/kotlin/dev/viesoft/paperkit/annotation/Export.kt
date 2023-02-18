package dev.viesoft.paperkit.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Export(
    val clazz: KClass<out Any>
)
