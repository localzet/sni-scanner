package com.localzet.sniscanner

data class CheckTarget(
    val ip: String,
    val sni: String
) {
    val label: String
        get() = "$ip / $sni"
}
