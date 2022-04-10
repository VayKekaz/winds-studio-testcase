package web

import util.findAll
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import util.getParameter

/**
 * Defines routing responsible for `User` operations.
 */
fun Route.userRoute() = route("/users") {
    get {
        val pageNumber = getParameter("pageNumber") { "0" }.toInt()
        val pageSize = getParameter("pageSize") { "10" }.toInt()
        val users = newSuspendedTransaction { User.findAll(Pagination(pageNumber, pageSize)) }.map(::UserDto)
        call.respond(OK, users)
    }

    get("/{id}") {
        val id = getParameter("id").toInt()
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