package org.aurral.firetv.data.session

/** Adds a scheme when missing and removes a trailing slash. */
object ServerUrlNormalizer {

    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return trimmed
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "http://$trimmed"
        }
        return withScheme.trimEnd('/')
    }
}
