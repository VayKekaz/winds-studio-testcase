package util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

fun Any.logger(): Logger = LoggerFactory.getLogger(this::class.java)

fun Any.getResource(name: String): URL? = this::class.java.getResource(name)

infix fun String.zipLinesCompare(other: String): String {
    val maxLength = this.lines().maxOf { it.length }
    return (this.lines() zip other.lines()).joinToString("\n") {
        // format: '   "name": "user1"  !!  "name": "user2"'
        "${" ".repeat(maxLength - it.first.length)}${it.first} ${if (it.first == it.second) "  " else "!!"} ${it.second}"
    }
}
