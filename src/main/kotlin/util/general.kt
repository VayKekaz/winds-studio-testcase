package util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*
import io.ktor.util.pipeline.*
import model.exception.RequiredParameterNotProvided
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

fun Any.logger(): Logger = LoggerFactory.getLogger(this::class.java)

fun Any.getResource(name: String): URL? = this::class.java.getResource(name)

fun PipelineContext<out Any, ApplicationCall>.getParameter(name: String): String =
    call.parameters[name]
        ?: throw RequiredParameterNotProvided(name)

infix fun String.zipLinesCompare(other: String): String {
    val maxLength = this.lines().maxOf { it.length }
    return (this.lines() zip other.lines()).joinToString("\n") {
        // format: '   "name": "user1"  !!  "name": "user2"'
        "${" ".repeat(maxLength - it.first.length)}${it.first} ${if (it.first == it.second) "  " else "!!"} ${it.second}"
    }
}

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

