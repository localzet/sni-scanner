package com.localzet.sniscanner

object ScanInputParser {
    private val addressTokenDelimiters = Regex("[,;\\s]+")
    private val forbiddenAddressChars = Regex("[/?#@]")

    fun parseAddresses(raw: String): List<String> =
        raw.lineSequence()
            .map { it.substringBefore('#') }
            .flatMap { it.split(addressTokenDelimiters).asSequence() }
            .map { normalizeAddress(it) }
            .filter { isValidAddressToken(it) }
            .distinct()
            .toList()

    fun parseSnis(raw: String): List<String> =
        raw.lineSequence()
            .map { it.substringBefore('#').trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()

    fun buildTargets(addresses: List<String>, snis: List<String>): List<CheckTarget> =
        addresses.flatMap { ip -> snis.map { sni -> CheckTarget(ip, sni) } }

    private fun normalizeAddress(value: String): String =
        value.trim().trim('[', ']')

    private fun isValidAddressToken(value: String): Boolean =
        value.isNotEmpty() &&
            value.length <= 253 &&
            !value.contains(forbiddenAddressChars) &&
            !value.contains("://")
}
