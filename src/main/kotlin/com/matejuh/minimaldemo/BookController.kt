package com.matejuh.minimaldemo

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.json
import org.springframework.web.util.UriComponentsBuilder

const val BOOK_URI = "/books"
const val BOOK_ITEM_URI = "/books/{id}"

class BookController(private val service: BookService) {

    suspend fun createBook(req: ServerRequest): ServerResponse =
        req.awaitBody(CreateBook::class)
            .let {
                val id = service.createBook(it)
                ServerResponse.created(UriComponentsBuilder.newInstance().path(BOOK_ITEM_URI).build(id)).buildAndAwait()
            }

    suspend fun getBook(req: ServerRequest): ServerResponse =
        req.pathVariable("id").let { id ->
            val book = service.getBook(BookId.fromString(id))
            book?.let {
                ServerResponse.ok().json().bodyValueAndAwait(ApiBook(book.id, book.name, book.author))
            } ?: ServerResponse.notFound().buildAndAwait()
        }
}
