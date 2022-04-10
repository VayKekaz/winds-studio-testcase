package web

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Page
import model.UserDto
import org.junit.jupiter.api.Test
import util.bodyTyped
import util.expect
import util.zipLinesCompare

class UserOperationsTest {

    private val testUser = UserDto(
        email = "test@env.create",
        firstName = "test",
        lastName = "environment",
        patronymic = "creation",
        phoneNumber = "+1 1134 134654"
    )

    @Test fun `get users, should be empty`(): Unit = testApplication {
        client.get("/users") {
            accept(Application.Json)
        }.apply {
            expect(HttpStatusCode.OK)
            val users: Page<UserDto> = bodyTyped()
            assert(users.isEmpty()) { "Users size = ${users.size}, expected 0." }
        }
    }

    @Test fun `post user, should appear in db`(): Unit = testApplication {
        var userToCreate = testUser
        val creationResponse: UserDto
        client.post("/users") {
            accept(Application.Json)
            contentType(Application.Json)
            this.setBody(Json.encodeToString(userToCreate))
        }.apply {
            expect(HttpStatusCode.Created)
            creationResponse = bodyTyped()
            userToCreate = userToCreate.copy(id = creationResponse.id)
            assert(creationResponse == userToCreate) {
                Json.encodeToString(userToCreate) zipLinesCompare Json.encodeToString(creationResponse)
            }
        }
        // get all and check
        client.get("/users") {
            accept(Application.Json)
        }.apply {
            val users: Page<UserDto> = bodyTyped()
            assert(users.size == 1) { "Users size = ${users.size}, expected 1." }
            val userFromDb = users.content.first()
            assert(userFromDb == userToCreate) {
                Json.encodeToString(userFromDb) zipLinesCompare Json.encodeToString(userToCreate)
            }
        }
        // get by id and check
        client.get("/users/${userToCreate.id}") {
            accept(Application.Json)
        }.apply {
            val userFromDb: UserDto = bodyTyped()
            assert(userFromDb == userToCreate) {
                Json.encodeToString(userFromDb) zipLinesCompare Json.encodeToString(userToCreate)
            }
        }
    }
}