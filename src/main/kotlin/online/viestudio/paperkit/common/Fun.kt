package online.viestudio.paperkit.common

import com.squareup.kotlinpoet.MemberName

internal object Fun {

    val KOIN_BIND = MemberName("org.koin.dsl", "bind", true)
    val KOIN_GET = MemberName("org.koin.core.component", "get", true)
    val KIT_LISTENER_REGISTER = MemberName("online.viestudio.paperkit.listener", "register", true)
    val KIT_LISTENER_UNREGISTER = MemberName("online.viestudio.paperkit.listener", "unregister", true)
}