package model.exception

import java.lang.RuntimeException

class RequiredParameterNotProvided(val parameterName: String) :
    RuntimeException("Required parameter '$parameterName' not provided.")
