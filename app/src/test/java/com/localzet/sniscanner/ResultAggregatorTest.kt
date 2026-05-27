package com.localzet.sniscanner

import org.junit.Assert.assertEquals
import org.junit.Test

class ResultAggregatorTest {
    @Test
    fun sortedForDisplayPrioritizesHttpThenTlsThenRtt() {
        val blocked = result("1.1.1.1", "blocked.example", tlsOk = false, httpOk = false, rttMs = 20.0)
        val tlsOnly = result("1.1.1.1", "tls.example", tlsOk = true, httpOk = false, rttMs = 40.0)
        val full = result("8.8.8.8", "full.example", tlsOk = true, httpOk = true, rttMs = 80.0)

        val sorted = ResultAggregator.sortedForDisplay(
            listOf(blocked, tlsOnly, full),
            lowPingOnly = false,
            lowPingThresholdMs = 150
        )

        assertEquals(listOf(full, tlsOnly, blocked), sorted)
    }

    @Test
    fun ipSummariesSelectBestSniPerIp() {
        val results = listOf(
            result("1.1.1.1", "slow.example", tlsOk = true, httpOk = false, rttMs = 90.0),
            result("1.1.1.1", "fast.example", tlsOk = true, httpOk = true, rttMs = 30.0),
            result("8.8.8.8", "blocked.example", tlsOk = false, httpOk = false, rttMs = null)
        )

        val summaries = ResultAggregator.ipSummaries(results)

        assertEquals("1.1.1.1", summaries[0].ip)
        assertEquals(2, summaries[0].working)
        assertEquals("fast.example", summaries[0].bestSni)
        assertEquals("8.8.8.8", summaries[1].ip)
        assertEquals(0, summaries[1].working)
    }

    private fun result(
        ip: String,
        sni: String,
        tlsOk: Boolean,
        httpOk: Boolean,
        rttMs: Double?
    ): CheckResult =
        CheckResult(ip = ip, sni = sni, port = 443, timeout = 5f).also {
            it.tlsOk = tlsOk
            it.httpOk = httpOk
            it.rttMs = rttMs
        }
}
