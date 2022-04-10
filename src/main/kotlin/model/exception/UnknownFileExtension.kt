package model.exception

class UnknownFileExtension(val stringRepresentation: String) : Exception("Unknown extension: .$stringRepresentation")