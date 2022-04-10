package model

import com.papsign.ktor.openapigen.annotations.Response
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Users : IntIdTable() {
    val email = varchar("email", 50).uniqueIndex()
    val firstName = varchar("firstName", 50)
    val lastName = varchar("lastName", 50)
    val patronymic = varchar("patronymic", 50)
    val phoneNumber = varchar("phoneNumber", 16)
}

class User(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, User>(Users) {
        fun new(dto: UserDto) = User.new {
            email = dto.email
            firstName = dto.firstName
            lastName = dto.lastName
            patronymic = dto.patronymic
            phoneNumber = dto.phoneNumber
        }
    }

    var email by Users.email
    var firstName by Users.firstName
    var lastName by Users.lastName
    var patronymic by Users.patronymic
    var phoneNumber by Users.phoneNumber
}

@Response
@Serializable
data class UserDto(
    val id: Int? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phoneNumber: String,
) {
    constructor(entity: User) : this(
        entity.id.value,
        entity.email,
        entity.firstName,
        entity.lastName,
        entity.patronymic,
        entity.phoneNumber,
    )

    companion object {
        val example = UserDto(
            email = "example@email.mail",
            firstName = "george",
            lastName = "floyd",
            patronymic = "example",
            phoneNumber = "+1 1234 4567 789",
        )
    }
}