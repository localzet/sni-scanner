package com.localzet.sniscanner

import org.junit.Assert.assertEquals
import org.junit.Test

class ScanInputParserTest {
    @Test
    fun parseAddresses_acceptsMultipleDelimitersAndComments() {
        val raw = """
            1.1.1.1, 8.8.8.8
            9.9.9.9 # dns
            [2606:4700:4700::1111]
            https://invalid.example
            1.1.1.1
        """.trimIndent()

        assertEquals(
            listOf("1.1.1.1", "8.8.8.8", "9.9.9.9", "2606:4700:4700::1111"),
            ScanInputParser.parseAddresses(raw)
        )
    }

    @Test
    fun parseSnis_keepsOneSniPerLineAndRemovesComments() {
        val raw = """
            example.com
            vk.com # local note

            example.com
        """.trimIndent()

        assertEquals(listOf("example.com", "vk.com"), ScanInputParser.parseSnis(raw))
    }

    @Test
    fun buildTargetsCreatesIpBySniMatrixInStableOrder() {
        val targets = ScanInputParser.buildTargets(
            addresses = listOf("1.1.1.1", "8.8.8.8"),
            snis = listOf("a.example", "b.example")
        )

        assertEquals(
            listOf(
                CheckTarget("1.1.1.1", "a.example"),
                CheckTarget("1.1.1.1", "b.example"),
                CheckTarget("8.8.8.8", "a.example"),
                CheckTarget("8.8.8.8", "b.example")
            ),
            targets
        )
    }
}
