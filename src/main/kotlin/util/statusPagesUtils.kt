package util

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import model.ServerErrorResponse

suspend fun ApplicationCall.defaultExceptionHandler(
    exception: Throwable,
    status: HttpStatusCode = BadRequest,
) {
    respond(status, ServerErrorResponse(exception))
}

inline fun <reified E : Throwable> StatusPagesConfig.defaultException(status: HttpStatusCode = BadRequest) {
    onException<E> { defaultExceptionHandler(it, status) }
}

inline fun <reified E : Throwable> StatusPagesConfig.onException(crossinline handler: suspend ApplicationCall.(E) -> Unit) =
    this.exception<E> { call: ApplicationCall, exception: E ->
        call.handler(exception)
    }