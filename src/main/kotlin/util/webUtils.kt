package util

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import model.exception.RequiredParameterNotProvided

typealias CallContext = PipelineContext<out Any, ApplicationCall>

/**
 * Returns optional parameter or calculated default.
 */
fun CallContext.getParameter(name: String, lazyDefault: CallContext.() -> String): String = try {
    getParameter(name)
} catch (optionalParamException: RequiredParameterNotProvided) {
    lazyDefault()
}

/**
 * Returns required parameter or throws `RequiredParameterNotProvided` exception.
 */
fun CallContext.getParameter(name: String): String =
    call.parameters[name]
        ?: throw RequiredParameterNotProvided(name)
