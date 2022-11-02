package com.matejuh.minimaldemo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import net.javacrumbs.jsonunit.JsonMatchers
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.PathContainer
import org.springframework.web.util.pattern.PathPatternParser
import java.net.ServerSocket

class MinimalDemoApplicationTests {

	init {
		val port = ServerSocket(0).use { it.localPort }
		server(port).bindNow()
		RestAssured.port = port
		RestAssured.requestSpecification = RequestSpecBuilder()
			.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build()
	}

	@Test
	internal fun `should create and get a book`() {
		val location = Given {
			body(createBook)
		} When {
			post(BOOK_URI)
		} Then {
			statusCode(HttpStatus.CREATED.value())
		} Extract {
			header(HttpHeaders.LOCATION)
		}
		val id = PathPatternParser()
			.parse(BOOK_ITEM_URI)
			.matchAndExtract(PathContainer.parsePath(location))
			?.uriVariables
			?.get("id")
		id.shouldNotBeNull()

		When {
			get(location)
		} Then {
			body(
				JsonMatchers.jsonStringEquals(
					"""{
                       "id":"$id",
                       "name":"${createBook.name}",
                       "author":"${createBook.author}"
                    }
                    """.trimIndent()
				)
			)
		}
	}

}
