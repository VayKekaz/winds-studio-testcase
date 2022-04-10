package web

import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.ContentType.Application
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.exception.UnknownFileExtension
import util.getParameter
import util.getResource
import java.net.URL

/**
 * Defines routing responsible for swagger-ui.
 */
fun Route.swaggerUi() {
    static("/static") {
        resources("static")
    }
    get("/swagger-ui") {
        call.respondRedirect("/swagger-ui/index.html")
    }
    get("/swagger-ui/{filename}") {
        val filename = getParameter("filename")
        val file = SwaggerUiWebjar.getFile(filename)
        call.respond(file)
    }
}

private suspend fun ApplicationCall.respond(file: FileResponse) =
    respondBytes(file.bytes, file.type)

/**
 * Object responsible for retrieving swagger-ui files from classpath.
 */
object SwaggerUiWebjar {
    const val version = "4.10.3" // TODO find way to define by dependency version
    const val openApiSpecPath = "/static/swagger.json"

    fun getFile(filename: String): FileResponse {
        val resource = getResource("/META-INF/resources/webjars/swagger-ui/$version/$filename")
            ?: throw NotFoundException("File '$filename' not found inside swagger-ui:$version webjar.")
        return if (filename == "swagger-initializer.js")
            FileResponse(
                resource.readText()
                    .replace( // set default api to ours
                        "url: \"https://petstore.swagger.io/v2/swagger.json\",",
                        "url: \"$openApiSpecPath\","
                    )
                    .toByteArray(),
                Text.JavaScript,
            )
        else
            FileResponse(resource)
    }
}

/**
 * Represents file for sending as a response.
 */
data class FileResponse(val bytes: ByteArray, val type: ContentType) {
    /**
     * Constructs from resource url.
     */
    constructor(url: URL) : this(
        bytes = url.readBytes(),
        type = FileType.fromFilename(url.file).contentType,
    )
}

/**
 * Enum that maps file extensions to corresponding ContentType. Only necessary types defined.
 */
private enum class FileType(val extension: String, val contentType: ContentType) {
    HTML("html", Text.Html),
    CSS("css", Text.CSS),
    JS("js", Text.JavaScript),
    JSON("json", Application.Json.withCharset(Charsets.UTF_8)),
    PNG("png", Image.PNG),
    ;

    companion object {
        fun fromExtension(ext: String): FileType = try {
            values().first { it.extension == ext }
        } catch (e: NoSuchElementException) {
            throw UnknownFileExtension(ext)
        }

        fun fromFilename(filename: String): FileType = fromExtension(
            filename.substring(filename.lastIndexOf('.') + 1) //extract extenstion
        )
    }
}

