package online.viestudio.paperkit.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import online.viestudio.paperkit.annotate.Annotate
import online.viestudio.paperkit.annotate.DeclareFileOrDefaultsConfig
import online.viestudio.paperkit.annotate.Export
import online.viestudio.paperkit.common.Fun
import online.viestudio.paperkit.common.Type
import online.viestudio.paperkit.utils.findAnnotation

internal class PaperKitProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private val builder = PaperKitPluginBuilder()

    override fun finish() {
        builder.write(codeGenerator)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> = with(resolver) {
        processAnnotation<KSFunctionDeclaration>(
            "PluginStartHook",
            StartHookVisitor()
        ) + processAnnotation<KSFunctionDeclaration>(
            "PluginStopHook",
            StopHookVisitor()
        ) + processAnnotation<KSFunctionDeclaration>(
            "PluginReloadHook",
            ReloadHookVisitor()
        ) + processAnnotation<KSClassDeclaration>(
            "Export",
            ExportVisitor()
        ) + processAnnotation<KSClassDeclaration>(
            "DeclareCommand",
            RegisterCommandVisitor()
        ) + processAnnotation<KSClassDeclaration>(
            "DeclareFileOrDefaultsConfig",
            ConfigVisitor()
        ) + processAnnotation<KSClassDeclaration>(
            "Service",
            ServiceVisitor()
        )
    }

    private inner class ServiceVisitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val isListener = classDeclaration.getAllSuperTypes().any { it.toTypeName() == Type.KIT_LISTENER }
            if (!isListener) return
            builder.export(classDeclaration, classDeclaration.asStarProjectedType())
            builder.callOnStart {
                addStatement(
                    "%M<%T>().%M()",
                    Fun.KOIN_GET,
                    classDeclaration.toClassName(),
                    Fun.KOIN_BIND,
                    Fun.KIT_LISTENER_REGISTER
                )
            }.callOnStop {
                addStatement(
                    "%M<%T>().%M()",
                    Fun.KOIN_GET,
                    classDeclaration.toClassName(),
                    Fun.KOIN_BIND,
                    Fun.KIT_LISTENER_UNREGISTER
                )
            }
        }
    }

    private inner class ConfigVisitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val annotation = classDeclaration.findAnnotation<DeclareFileOrDefaultsConfig>()
            val name = annotation.arguments.first().value.toString()
            builder.declareConfigFileOrDefaults(classDeclaration, name)
        }
    }

    private inner class RegisterCommandVisitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val primaryConstructor = classDeclaration.primaryConstructor ?: return
            if (primaryConstructor.parameters.isNotEmpty()) {
                logger.warn("Unable to register command class $classDeclaration with non-empty constructor.")
                return
            }
            builder.declareCommand(classDeclaration)
        }
    }

    private inner class ExportVisitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val primaryConstructor = classDeclaration.primaryConstructor ?: return
            if (primaryConstructor.parameters.isNotEmpty()) {
                logger.warn("Unable to export class $classDeclaration with non-empty constructor.")
                return
            }
            val annotation = classDeclaration.findAnnotation<Export>()
            val ksType = annotation.arguments.first().value as KSType
            builder.export(classDeclaration, ksType)
        }
    }

    private inner class ReloadHookVisitor : KSVisitorVoid() {

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            if (function.parameters.isNotEmpty()) return
            val receiver = function.extensionReceiver
            if (receiver != null && receiver.toTypeName().toString() != Type.KIT_PLUGIN.toString()) return
            builder.callOnReload(function)
        }
    }

    private inner class StopHookVisitor : KSVisitorVoid() {

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            if (function.parameters.isNotEmpty()) return
            val receiver = function.extensionReceiver
            if (receiver != null && receiver.toTypeName().toString() != Type.KIT_PLUGIN.toString()) return
            builder.callOnStop(function)
        }
    }

    private inner class StartHookVisitor : KSVisitorVoid() {

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            if (function.parameters.isNotEmpty()) return
            val receiver = function.extensionReceiver
            if (receiver != null && receiver.toTypeName().toString() != Type.KIT_PLUGIN.toString()) return
            builder.callOnStart(function)
        }
    }

    private inline fun <reified T> Resolver.processAnnotation(
        name: String,
        visitor: KSVisitor<Unit, Unit>,
    ): List<KSAnnotated> {
        val symbols = getSymbolsWithAnnotation(Annotate.resolve(name))
        symbols.filter { it is T && it.validate() }.forEach {
            it.accept(visitor, Unit)
        }
        return symbols.filter { !it.validate() }.toList()
    }
}