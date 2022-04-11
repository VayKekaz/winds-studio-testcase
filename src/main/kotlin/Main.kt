import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*
import service.DatabaseFactory
import util.disable
import web.handleExceptions
import web.swaggerUi
import web.userRoute

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {

    install(DefaultHeaders)
    install(CallLogging)
    install(DoubleReceive) // only known way to resolve double consume exception thrown by framework in tests

    install(ContentNegotiation) {
        json()
    }

    install(CORS) { disable() } // TODO do i even need that?

    DatabaseFactory.connectAndMigrate()
    if (supposedToMockDb)
        DatabaseFactory.mockDb()

    routing {
        userRoute()
        swaggerUi()
    }
    handleExceptions()

}

/**
 * Wraps `mockDb` HOCON property.
 */
val Application.supposedToMockDb: Boolean
    get() = environment.config.propertyOrNull("ktor.mockDb")?.getString()?.toBooleanStrict() == true
