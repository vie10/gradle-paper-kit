package online.viestudio.paperkit.common

import com.squareup.kotlinpoet.TypeVariableName

internal object Type {

    val KIT_PLUGIN = TypeVariableName("online.viestudio.paperkit.plugin.KitPlugin")
    val BASE_KIT_PLUGIN = TypeVariableName("online.viestudio.paperkit.plugin.BaseKitPlugin")
    val MODULES_CONTAINER = TypeVariableName("online.viestudio.paperkit.koin.KoinModulesContainer")
    val COMMANDS_DECLARATION = TypeVariableName("online.viestudio.paperkit.command.CommandsDeclaration")
    val CONFIG_DECLARATION = TypeVariableName("online.viestudio.paperkit.config.ConfigurationDeclaration")
}