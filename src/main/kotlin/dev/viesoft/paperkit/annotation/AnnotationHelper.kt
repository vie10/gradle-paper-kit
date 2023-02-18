package dev.viesoft.paperkit.annotation

internal object AnnotationHelper {

    private const val ANNOTATION_PACKAGE = "dev.viesoft.paperkit.annotation"

    fun resolveAnnotationFQN(name: String) = "$ANNOTATION_PACKAGE.$name"
}
