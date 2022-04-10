package db.migration

import model.Users
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ClassName") class V1__create_user_table : BaseJavaMigration() {
    override fun migrate(context: Context) {
        transaction {
            SchemaUtils.create(Users)
        }
    }
}
