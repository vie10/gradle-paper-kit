package dev.viesoft.paperkit.common

import com.squareup.kotlinpoet.TypeVariableName

internal object Type {

    val KIT_PLUGIN = TypeVariableName("dev.viesoft.paperkit.plugin.KitPlugin")
    val KIT_LISTENER = TypeVariableName("dev.viesoft.paperkit.listener.KitListener")
    val BASE_KIT_PLUGIN = TypeVariableName("dev.viesoft.paperkit.plugin.BaseKitPlugin")
    val MODULES_CONTAINER = TypeVariableName("dev.viesoft.paperkit.koin.KoinModulesContainer")
    val COMMANDS_DECLARATION = TypeVariableName("dev.viesoft.paperkit.command.CommandsDeclaration")
    val CONFIG_DECLARATION = TypeVariableName("dev.viesoft.paperkit.config.ConfigurationDeclaration")
}
