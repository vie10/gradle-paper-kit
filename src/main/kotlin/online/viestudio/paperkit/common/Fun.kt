package online.viestudio.paperkit.common

import com.squareup.kotlinpoet.MemberName

internal object Fun {

    val KOIN_BIND = MemberName("org.koin.dsl", "bind", true)
    val KOIN_GET = MemberName("org.koin.core.component", "get", true)
}