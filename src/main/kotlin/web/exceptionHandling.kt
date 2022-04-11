package web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import model.ServerErrorResponse
import model.exception.RequiredParameterNotProvided
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import util.defaultException
import util.onException

fun Application.handleExceptions() {
    install(StatusPages) {
        defaultException<ContentTransformationException>()
        defaultException<NumberFormatException>()
        defaultException<RequiredParameterNotProvided>()
        defaultException<EntityNotFoundException>(HttpStatusCode.NotFound)
        onException<ExposedSQLException> { // bad idea to share sql queries
            respond(ServerErrorResponse("Duplicate email.", it::class.simpleName))
        }
    }
}