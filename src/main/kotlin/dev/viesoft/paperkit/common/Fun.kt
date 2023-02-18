package dev.viesoft.paperkit.common

import com.squareup.kotlinpoet.MemberName

internal object Fun {

    val KOIN_BIND = MemberName("org.koin.dsl", "bind", true)
    val KOIN_GET = MemberName("org.koin.core.component", "get", true)
    val KIT_LISTENER_REGISTER = MemberName("dev.viesoft.paperkit.listener", "register", true)
    val KIT_LISTENER_UNREGISTER = MemberName("dev.viesoft.paperkit.listener", "unregister", true)
}
