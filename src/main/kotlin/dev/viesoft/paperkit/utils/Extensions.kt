package dev.viesoft.paperkit.utils

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

inline fun <reified T> KSClassDeclaration.findAnnotation(): KSAnnotation = annotations.firstOrNull {
    it.annotationType.toTypeName() == T::class.asTypeName()
}!!

inline fun <reified T> KSClassDeclaration.findAnnotationOrNull(): KSAnnotation? = annotations.firstOrNull {
    it.annotationType.toTypeName() == T::class.asTypeName()
}
