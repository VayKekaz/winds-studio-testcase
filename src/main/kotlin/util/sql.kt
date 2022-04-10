package util

import model.Page
import model.Pagination
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <ID : Comparable<ID>, T : Entity<ID>> EntityClass<ID, T>.findAll(pagination: Pagination): Page<T> {
    val content = newSuspendedTransaction {
        all()
            .limit(
                pagination.pageSize,
                pagination.offset.toLong()
            )
            .sortedBy { it.id }
    }
    return Page(
        content,
        pagination,
        this.count().toInt()
    )
}