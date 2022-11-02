package com.matejuh.minimaldemo

import com.matejuh.demo.generated.tables.Books.Companion.BOOKS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val jooq: DSLContext) {
    suspend fun create(createBook: CreateBook): BookId =
        jooq.insertInto(
            BOOKS,
            BOOKS.NAME,
            BOOKS.AUTHOR
        ).values(
            createBook.name,
            createBook.author
        ).returningResult(BOOKS.ID)
            .fetchAsync()
            .await()
            .getValues(BOOKS.ID)
            .first()!!

    suspend fun get(id: BookId): Book? =
        runBlocking(Dispatchers.IO) {
            jooq.selectFrom(BOOKS)
                .where(BOOKS.ID.eq(id))
                .fetchOne()
                ?.let { r -> Book(r[BOOKS.ID]!!, r[BOOKS.NAME]!!, r[BOOKS.AUTHOR]!!) }
        }
}
