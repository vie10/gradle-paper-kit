package online.viestudio.paperkit.ksp

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import online.viestudio.paperkit.common.Fun
import online.viestudio.paperkit.common.Type

private const val CLASS_NAME = "AnnotationDrivenPlugin"
private const val CLASS_PACKAGE = "online.viestudio.paperkit.plugin"

internal class PaperKitPluginBuilder {

    private val classType
        get() = TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(OPEN)
            .superclass(Type.BASE_KIT_PLUGIN)
            .addFunction(onStart.build())
            .addFunction(onStop.build())
            .addFunction(onReload.build())
            .addFunction(export.build())
            .addFunction(commandsDeclaration.build())
            .addFunction(configDeclaration.build())
            .build()
    private val onStart = FunSpec.builder("onStart")
        .addModifiers(OVERRIDE, FINAL, SUSPEND, PROTECTED)
    private val onStop = FunSpec.builder("onStop")
        .addModifiers(OVERRIDE, FINAL, SUSPEND, PROTECTED)
    private val onReload = FunSpec.builder("onReloadResources")
        .addModifiers(OVERRIDE, FINAL, SUSPEND, PROTECTED)
    private val commandsDeclaration = FunSpec.builder("declareCommands")
        .receiver(Type.COMMANDS_DECLARATION)
        .addModifiers(OVERRIDE, FINAL, PROTECTED)
    private val configDeclaration = FunSpec.builder("declareConfiguration")
        .receiver(Type.CONFIG_DECLARATION)
        .addModifiers(OVERRIDE, FINAL, PROTECTED)
    private val export
        get() = FunSpec.builder("export")
            .receiver(Type.MODULES_CONTAINER)
            .addModifiers(OVERRIDE, FINAL, PROTECTED)
            .addStatement("module {")
            .addCode(exportModule.build())
            .addStatement("}")
    private val exportModule = CodeBlock.builder()
    private val origins: MutableSet<KSFile> = mutableSetOf()

    private fun origin(ksFile: KSFile) = apply {
        origins.add(ksFile)
    }

    private fun origin(function: KSFunctionDeclaration) = apply {
        origin(function.containingFile ?: return@apply)
    }

    private fun origin(clazz: KSClassDeclaration) = apply {
        origin(clazz.containingFile ?: return@apply)
    }

    fun declareConfigFileOrDefaults(configClass: KSClassDeclaration, name: String) {
        val className = configClass.toClassName()
        configDeclaration.addStatement(
            "%T::class loadFrom (file(%S) or defaults(%T::class))",
            className,
            name,
            className
        )
    }

    fun declareCommand(commandClass: KSClassDeclaration) {
        commandsDeclaration.addStatement("register { %T() }", commandClass.toClassName())
        origin(commandClass)
    }

    fun callOnStart(function: KSFunctionDeclaration) = apply {
        call(function)(onStart)
    }

    fun callOnStop(function: KSFunctionDeclaration) = apply {
        call(function)(onStop)
    }

    fun callOnReload(function: KSFunctionDeclaration) = apply {
        call(function)(onReload)
    }

    private fun call(function: KSFunctionDeclaration): FunSpec.Builder.() -> Unit = {
        val clazz = function.closestClassDeclaration()
        if (clazz != null) {
            addStatement("%M<%T>().${function.simpleName.asString()}()", Fun.KOIN_GET, clazz.toClassName())
        } else {
            addStatement(
                "%M()",
                MemberName(
                    function.packageName.asString(),
                    function.simpleName.asString(),
                    function.extensionReceiver != null
                )
            )
        }
        origin(function)
    }

    fun export(clazz: KSClassDeclaration, type: KSType) {
        exportModule.addStatement("single { %T() } %M %T::class", clazz.toClassName(), Fun.KOIN_BIND, type.toTypeName())
        origin(clazz)
    }

    fun write(codeGenerator: CodeGenerator) {
        build().writeTo(codeGenerator, true, origins)
    }

    private fun build() = FileSpec.builder(CLASS_PACKAGE, CLASS_NAME).addType(classType).build()
}