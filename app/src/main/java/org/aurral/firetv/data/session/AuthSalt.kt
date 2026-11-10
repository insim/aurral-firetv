package org.aurral.firetv.data.session

import java.util.concurrent.ThreadLocalRandom

/**
 * Generates the Subsonic token salt.
 *
 * The salt is generated once per session and reused for every signed URL, which
 * keeps those URLs stable so Coil, OkHttp and the ExoPlayer cache can actually
 * reuse responses. It is rotated on sign-in and sign-out.
 */
object AuthSalt {

    private const val LENGTH = 12
    private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"

    fun generate(): String {
        val random = ThreadLocalRandom.current()
        return buildString(LENGTH) {
            repeat(LENGTH) { append(ALPHABET[random.nextInt(ALPHABET.length)]) }
        }
    }
}
