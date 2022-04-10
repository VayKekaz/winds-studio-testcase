package service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import model.Page
import model.User
import model.UserDto
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import util.logger
import javax.sql.DataSource

object DatabaseFactory {

    private val logger = logger()

    val mockUsers = mutableListOf<User>()

    fun connectAndMigrate() {
        logger.info("Initialising database")
        Database.connect(hikariDataSource)
        runFlyway(hikariDataSource)
    }

    private val hikariDataSource: HikariDataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            driverClassName = org.h2.Driver::class.qualifiedName
            jdbcUrl = "jdbc:h2:mem:test"
            maximumPoolSize = 3
            isAutoCommit = false
            // https://docs.oracle.com/javadb/10.8.3.0/devguide/cdevconcepts15366.html
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

    private fun runFlyway(datasource: DataSource) {
        val flyway = Flyway.configure()
            .locations("classpath:/db/migrations")
            .dataSource(datasource)
            .load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            logger.error("Exception running flyway migration", e)
            throw e
        }
        logger.info("Flyway migration has finished")
    }

    fun mockDb(): Unit = transaction {
        mockUsers.add(User.new {
            email = "example@email.mail"
            firstName = "george"
            lastName = "floyd"
            patronymic = "example"
            phoneNumber = "+1 1234 4567 789"
        })
    }
}