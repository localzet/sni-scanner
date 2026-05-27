package com.localzet.sniscanner

object ResultAggregator {
    fun sortedForDisplay(results: List<CheckResult>, lowPingOnly: Boolean, lowPingThresholdMs: Int): List<CheckResult> {
        val filtered = if (lowPingOnly) {
            results.filter { result -> result.rttMs != null && result.rttMs!! <= lowPingThresholdMs }
        } else {
            results
        }

        return filtered.sortedWith(
            compareByDescending<CheckResult> { it.tlsOk == true && it.httpOk == true }
                .thenByDescending { it.tlsOk == true }
                .thenBy { it.rttMs ?: Double.MAX_VALUE }
                .thenBy { it.ip }
                .thenBy { it.sni }
        )
    }

    fun workingCount(results: List<CheckResult>): Int =
        results.count { it.tlsOk == true }

    fun uniqueIpCount(results: List<CheckResult>): Int =
        results.map { it.ip }.distinct().size

    fun uniqueSniCount(results: List<CheckResult>): Int =
        results.map { it.sni }.distinct().size

    fun ipSummaries(results: List<CheckResult>): List<IpSummary> =
        results.groupBy { it.ip }
            .map { (ip, ipResults) ->
                val best = sortedForDisplay(ipResults, lowPingOnly = false, lowPingThresholdMs = Int.MAX_VALUE)
                    .firstOrNull()
                IpSummary(
                    ip = ip,
                    total = ipResults.size,
                    working = workingCount(ipResults),
                    bestSni = best?.sni,
                    bestRttMs = best?.rttMs
                )
            }
            .sortedWith(compareByDescending<IpSummary> { it.working }.thenBy { it.bestRttMs ?: Double.MAX_VALUE })

    data class IpSummary(
        val ip: String,
        val total: Int,
        val working: Int,
        val bestSni: String?,
        val bestRttMs: Double?
    )
}
