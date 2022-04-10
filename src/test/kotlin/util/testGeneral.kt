package util

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun HttpResponse.expect(status: HttpStatusCode) =
    assert(this.status == status) { "Got status ${this.status}, expected $status." }

suspend inline fun <reified T> HttpResponse.bodyTyped(): T = Json.decodeFromString(bodyAsText())
