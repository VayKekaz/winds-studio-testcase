package web

import findAll
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Route.userRoute() = route("/users") {
    get {
        val pageNumber = call.parameters["pageNumber"]?.toInt() ?: 0
        val pageSize = call.parameters["pageSize"]?.toInt() ?: 10
        val users = newSuspendedTransaction { User.findAll(Pagination(pageNumber, pageSize)) }.map(::UserDto)
        call.respond(OK, users)
    }

    get("/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val user = UserDto(
            newSuspendedTransaction { User.findById(id) }
                ?: throw EntityNotFoundException(EntityID(id, Users), User)
        )
        call.respond(OK, user)
    }


    post<UserDto> {
        val body: UserDto = call.receive()
        val createdEntity = newSuspendedTransaction {
            User.new(body)
        }
        call.respond(Created, UserDto(createdEntity))
    }
}