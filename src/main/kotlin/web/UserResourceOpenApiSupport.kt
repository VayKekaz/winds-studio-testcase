package web

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.status
import com.papsign.ktor.openapigen.route.throws
import findAll
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


// tupoe govno tupogo govna
fun NormalOpenAPIRoute.userRouteOpenApiSupport() = route("/users") {

    data class GetPaginatedParams(
        @QueryParam("Number of page", allowEmptyValues = true) val pageNumber: Int = 0,
        @QueryParam("Number of elements per page", allowEmptyValues = true) val pageSize: Int = 10,
    )
    throws<NormalOpenAPIRoute, NumberFormatException, ServerErrorResponse>(
        BadRequest,
        gen = ::ServerErrorResponse
        // TODO String -> Page<UserDto>
    ).get<GetPaginatedParams, Any> { (pageNumber, pageSize) ->
        val users = newSuspendedTransaction { User.findAll(Pagination(pageNumber, pageSize)) }
        respond(Json.encodeToString(users.map(::UserDto)))
    }

    @Path("{id}")
    data class GetByIdParams(@PathParam("Id of desired user.") val id: Int)
    throws<NormalOpenAPIRoute, EntityNotFoundException, ServerErrorResponse>(
        NotFound,
        gen = ::ServerErrorResponse
    ).throws<NormalOpenAPIRoute, NumberFormatException, ServerErrorResponse>(
        BadRequest,
        gen = ::ServerErrorResponse
    ).get<GetByIdParams, UserDto> { (id) ->
        val user = UserDto(
            newSuspendedTransaction { User.findById(id) }
                ?: throw EntityNotFoundException(EntityID(id, Users), User)
        )
        respond(user)
    }

    throws<NormalOpenAPIRoute, ExposedSQLException, ServerErrorResponse>(
        BadRequest,
        gen = { // not good idea to share sql queries
            ServerErrorResponse(
                "Probably duplicate email.",
                ExposedSQLException::class.simpleName
            )
        }
    ).post<Unit, UserDto, UserDto> { _, user ->
        val createdEntity = newSuspendedTransaction {
            User.new(user)
        }
        status(Created)
        respond(UserDto(createdEntity))
    }
}