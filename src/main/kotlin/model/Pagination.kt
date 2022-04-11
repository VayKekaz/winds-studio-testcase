package model

import kotlinx.serialization.Serializable
import model.SortDirection.ASCENDING
import kotlin.math.ceil

@Serializable
data class Page<T>
constructor(
    val content: List<T>,
    val pagination: Pagination,
    val totalElements: Int,
) : Collection<T> {

    constructor(
        content: Iterable<T>,
        pagination: Pagination,
        totalElements: Int,
    ) : this(
        content.toList(),
        pagination,
        totalElements
    )

    override val size: Int = content.size
    val totalPages = ceil(totalElements.toDouble() / pagination.pageSize.toDouble()).toInt()
    val isLast: Boolean = pagination.pageNumber + 1 == totalPages
    val isOutOfBounds: Boolean = pagination.offset + size > totalElements


    fun <newT> map(transform: (T) -> newT): Page<newT> =
        Page(content.map(transform), pagination, totalElements)

    override fun iterator(): Iterator<T> = content.iterator()
    override fun contains(element: T) = content.contains(element)
    override fun containsAll(elements: Collection<T>) = content.containsAll(elements)
    override fun isEmpty() = content.isEmpty()
}

@Serializable
data class Pagination(val pageNumber: Int, val pageSize: Int, val order: SortDirection = ASCENDING) {

    init {
        require(pageSize > 0) { "Page size can 't be less than 1 (got pageSize=$pageSize)." }
    }

    // getter-only properties will not be serialized
    val offset
        get() = pageSize * pageNumber
}

enum class SortDirection {
    ASCENDING,
    DESCENDING,
    ;
}
