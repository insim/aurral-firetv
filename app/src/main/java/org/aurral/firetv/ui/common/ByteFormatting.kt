package org.aurral.firetv.ui.common

/** Human-readable byte size, e.g. `12.4 MB`. */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 MB"
    val mebibytes = bytes / (1024.0 * 1024.0)
    return if (mebibytes < 1.0) {
        "%.0f KB".format(bytes / 1024.0)
    } else {
        "%.1f MB".format(mebibytes)
    }
}
