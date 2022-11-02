package com.matejuh.minimaldemo

import org.springframework.stereotype.Service

@Service
class BookService(
    private val repository: BookRepository
) {
    suspend fun createBook(createBook: CreateBook): BookId =
        repository.create(createBook)

    suspend fun getBook(id: BookId): Book? =
        repository.get(id)
}
