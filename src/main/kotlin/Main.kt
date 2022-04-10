import io.bkbn.kompendium.swagger.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.ServerErrorResponse
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import service.DatabaseFactory
import util.defaultException
import util.defaultExceptionHandler
import util.disable
import util.onException
import web.swaggerUi
import web.userRoute

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {

    /*
    install(OpenAPIGen) {
        info { title = "winds studio testcase" }
        serveSwaggerUi = true
        swaggerUiPath = "/swagger-ui/"
        swaggerUiVersion = "4.10.3"
    }

     */

    /*
    install(Kompendium) {
        spec = OpenApiSpec(
            info = Info(
                title = "Simple Demo API",
                version = "1.33.7",
                description = "Wow isn't this cool?",
            ),
            servers = mutableListOf(
                Server(
                    url = URI("http://localhost:8080"),
                    description = "Production instance of my API"
                ),
            ),
        )
    }


    install(SwaggerUI) {
        swaggerUrl = "/swagger-ui"
        jsConfig = JsConfig(
            specs = mapOf(
                "My API v1" to URI("/openapi.json"),
                "My API v2" to URI("/openapi.json")
            ),
            // This part will be inserted after Swagger UI is loaded in Browser.
            // Example is prepared according to this documentation: https://swagger.io/docs/open-source-tools/swagger-ui/usage/oauth2/
            jsInit = {
                """
  window.ui.initOAuth({
      clientId: 'CLIENT_ID',
      clientSecret: 'CLIENT_SECRET',
      realm: 'MY REALM',
      appName: 'TEST APP',
      useBasicAuthenticationWithAccessCodeGrant: true
  });  
      """
            }
        )
    }
     */
    install(DefaultHeaders)
    install(CallLogging)
    install(DoubleReceive) // only known way to resolve double consume exception thrown by framework in tests

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        disable()
    }

    DatabaseFactory.connectAndMigrate()
    if (supposedToMockDb)
        DatabaseFactory.mockDb()

    routing {
        userRoute()
        swaggerUi()
    }
    install(StatusPages) {
        defaultException<NumberFormatException>()
        defaultException<EntityNotFoundException>(HttpStatusCode.NotFound)
        onException<ExposedSQLException> { // bad idea to share sql queries
            respond(ServerErrorResponse("Duplicate email.", it::class.simpleName))
        }
    }
}

val Application.supposedToMockDb: Boolean
    get() = environment.config.propertyOrNull("ktor.mockDb")?.getString()?.toBooleanStrict() == true
