package util

import io.ktor.http.*
import io.ktor.server.plugins.cors.*

/**
 * Disables all CORS security.
 */
fun CORSConfig.disable() {
    anyHost()
    anyMethod()
    anyHeader()
}

fun CORSConfig.anyMethod() =
    HttpMethod.DefaultMethods.forEach(::allowMethod)

fun CORSConfig.anyHeader() =
    allowHeaders { true }
