package util

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import model.ServerErrorResponse

/**
 * Defines much cleaner DSL for `StatusPages` ktor plugin.
 */

/**
 * Default handler used for exceptions that fully describe themselves.
 */
suspend fun ApplicationCall.defaultExceptionHandler(
    exception: Throwable,
    status: HttpStatusCode = BadRequest,
) {
    respond(status, ServerErrorResponse(exception))
}

/**
 * Default handler used for exceptions that fully describe themselves.
 */
inline fun <reified E : Throwable> StatusPagesConfig.defaultException(status: HttpStatusCode = BadRequest) {
    onException<E> { defaultExceptionHandler(it, status) }
}

/**
 * Handler that can be customized in any ways.
 */
inline fun <reified E : Throwable> StatusPagesConfig.onException(crossinline handler: suspend ApplicationCall.(E) -> Unit) =
    this.exception<E> { call: ApplicationCall, exception: E ->
        call.handler(exception)
    }