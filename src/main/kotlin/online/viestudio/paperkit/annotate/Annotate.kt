package online.viestudio.paperkit.annotate

internal object Annotate {

    const val ANNOTATION_PACKAGE = "online.viestudio.paperkit.annotate"

    fun resolve(name: String) = "$ANNOTATION_PACKAGE.$name"
}