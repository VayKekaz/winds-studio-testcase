package util

import io.ktor.server.application.*

/**
 * Contains wrappers for HOCON-based configuration.
 */

/**
 * `mockDb`
 */
val Application.supposedToMockDb: Boolean
    get() = environment.config.propertyOrNull("ktor.mockDb")?.getString()?.toBooleanStrict() == true
