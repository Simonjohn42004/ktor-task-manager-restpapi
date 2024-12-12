package com.example

import com.example.model.Priority
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationJsonPathTest {

    @Test
    fun taskCanBeFound() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response.status)

        val jsonBody = response.bodyAsText()

        val jsonResponse = JsonPath.parse(jsonBody)
        val result = jsonResponse.read<List<String>>("$[*].name")

        assertEquals("cleaning", result[0])
        assertEquals("gardening", result[1])
        assertEquals("shopping", result[2])
    }


    @Test
    fun taskCanBeFoundByPriority() = testApplication {
        application {
            module()
        }
        val priority = Priority.Medium

        val jsonDoc = client.getAsJsonPath("/tasks/byPriority/$priority")

        val result = jsonDoc.read<List<String>>("$[?(@.priority == '$priority')].name")

        assertEquals(2,result.size)

        assertEquals("gardening", result[0])
        assertEquals("painting", result[1])


    }


    private suspend fun HttpClient.getAsJsonPath(url: String):DocumentContext {
        val response = this.get(url){
            accept(ContentType.Application.Json)
        }

        return JsonPath.parse(response.bodyAsText())
    }
}