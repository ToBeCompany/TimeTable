package com.example.plugins;

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

public class RoutingKtTest {

    @Test
    fun testGetSiginin() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/siginin/0").apply {

            }
        }
    }
}