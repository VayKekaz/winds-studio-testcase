package model

import kotlinx.serialization.Serializable

@Serializable
data class ServerErrorResponse(val message: String?, val exceptionName: String?) {
    constructor(exception: Throwable) : this(exception.message, exception::class.simpleName)
}
